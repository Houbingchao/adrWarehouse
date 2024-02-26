package com.yeqifu.bus.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeqifu.bus.entity.Goods;
import com.yeqifu.bus.service.IExportService;
import com.yeqifu.bus.service.IGoodsService;
import com.yeqifu.bus.vo.ExportVo;
import com.yeqifu.bus.vo.GoodsVo;
import com.yeqifu.sys.common.AppFileUtils;
import com.yeqifu.sys.common.Constast;
import com.yeqifu.sys.common.DataGridView;
import com.yeqifu.sys.common.ResultObj;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`) 前端控制器
 * </p>
 *
 * @author luoyi-
 * @since 2019-12-06
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IExportService exportService;


    /**
     * 查询商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("loadAllGoods")
    public DataGridView loadAllGoods(GoodsVo goodsVo){
        IPage<Goods> page = new Page<Goods>(goodsVo.getPage(),goodsVo.getLimit());
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        //queryWrapper.eq(goodsVo.getProviderid()!=null&&goodsVo.getProviderid()!=0,"providerid",goodsVo.getProviderid());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getGoodsname()),"goodsname",goodsVo.getGoodsname());
        //queryWrapper.like(StringUtils.isNotBlank(goodsVo.getProductcode()),"productcode",goodsVo.getProductcode());
        //queryWrapper.like(StringUtils.isNotBlank(goodsVo.getPromitcode()),"promitcode",goodsVo.getPromitcode());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getBrand()),"brand",goodsVo.getBrand());
        //queryWrapper.like(StringUtils.isNotBlank(goodsVo.getSize()),"size",goodsVo.getSize());

        //queryWrapper.orderByAsc("id");
        goodsService.page(page,queryWrapper);
        List<Goods> records = page.getRecords();
        // for (Goods goods : records) {
        //     Provider provider = providerService.getById(goods.getProviderid());
        //     if (null!=provider){
        //         goods.setProvidername(provider.getProvidername());
        //     }
        // }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 添加商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("addGoods")
    public ResultObj addGoods(GoodsVo goodsVo){
        try {
            //System.out.println("====================================");
            //System.out.println(goodsVo.getGoodsimg());
            // if (goodsVo.getGoodsimg()!=null&&goodsVo.getGoodsimg().endsWith("_temp")){
            //     String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
            //     goodsVo.setGoodsimg(newName);
            // }
            goodsService.save(goodsVo);


//            ----
            // 添加入库信息到bus_export表中
            ExportVo export = new ExportVo();
            export.setGoodsname(goodsVo.getGoodsname());
            export.setBrand(goodsVo.getBrand());
            export.setPerson(goodsVo.getPerson());
            export.setNumber(goodsVo.getNumber());
            export.setTime(goodsVo.getTime());
            export.setProjectid(goodsVo.getProjectid());
            exportService.save(export);


            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("updateGoods")
    public ResultObj updateGoods(GoodsVo goodsVo){
        try {
            // 获取编辑前的数量
            Goods oldGoods = goodsService.getById(goodsVo.getId());
            Integer oldNumber = oldGoods.getNumber();
            // 获取编辑后的数量
            Integer newNumber = goodsVo.getNumber();

            // 更新商品信息
            goodsService.updateById(goodsVo);

            // 判断新数量是否大于旧数量，如果是，则表示入库操作
            if (newNumber > oldNumber) {
                // 计算入库数量
                Integer exportNumber = newNumber - oldNumber;

                // 创建入库信息对象
                ExportVo exportVo = new ExportVo();
                exportVo.setGoodsname(goodsVo.getGoodsname());
                exportVo.setBrand(goodsVo.getBrand());
                exportVo.setPerson(goodsVo.getPerson());
                exportVo.setNumber(exportNumber); // 入库数量为变化的数量差
                exportVo.setTime(DateUtil.formatDateTime(new Date())); // 获取当前时间作为入库时间
                exportVo.setProjectid(goodsVo.getProjectid());

                // 保存入库信息到 bus_export 表中
                exportService.save(exportVo);
            }


            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除商品
     * @param id 商品id
     * @return
     */
    @RequestMapping("deleteGoods")
    public ResultObj deleteGoods(Integer id){
        try {
            //删除商品的图片
            //AppFileUtils.removeFileByPath(goodsimg);
//            goodsService.removeById(id);
            goodsService.deleteGoodsById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 加载所有商品 下拉
     * @return
     */
    @RequestMapping("loadAllGoodsForSelect")
    public DataGridView loadAllGoodsForSelect(){
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        //queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Goods> list = goodsService.list(queryWrapper);
        // for (Goods goods : list) {
        //     Provider provider = providerService.getById(goods.getProviderid());
        //     if (null!=provider){
        //         goods.setProvidername(provider.getProvidername());
        //     }
        // }
        return new DataGridView(list);
    }

    /**
     * 检查是否重名
     * checkGoodsName
     *
     */
    @PostMapping("/checkGoodsName")
    public ResultObj checkGoodsName(@RequestParam String goodsName) {
        try {
            // 根据物品名查询是否存在相同名称的物品
            Goods existingGoods = goodsService.getByGoodsName(goodsName);

            if (existingGoods == null) {
                // 物品名可用
                return ResultObj.CHECKNAME_SUCCESS;
            } else {
                // 物品名重复
                return ResultObj.CHECKNAME_ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CHECKNAMEING_ERROR;
        }
    }

    //库存预警
    @RequestMapping("loadAllWarningGoods")
    public DataGridView loadAllWarningGoods(){
        List<Goods> goods = goodsService.loadAllWarning();
        return new DataGridView((long) goods.size(),goods);
    }






}

