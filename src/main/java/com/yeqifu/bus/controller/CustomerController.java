package com.yeqifu.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeqifu.bus.entity.Customer;
import com.yeqifu.bus.entity.Goods;
import com.yeqifu.bus.entity.PickupData;
import com.yeqifu.bus.service.ICustomerService;
import com.yeqifu.bus.service.IGoodsService;
import com.yeqifu.bus.vo.CustomerVo;
import com.yeqifu.bus.vo.PickupDataVo;
import com.yeqifu.sys.common.Constast;
import com.yeqifu.sys.common.DataGridView;
import com.yeqifu.sys.common.ResultObj;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB 前端控制器
 * </p>
 *
 * @author luoyi-
 * @since 2019-12-05
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;
    @Autowired
    private IGoodsService goodsService;


    /**
     * 根据日期区间查询取件记录
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return
     */
    @GetMapping("loadCustomerByConditions")
    public DataGridView loadCustomerByConditions(@RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate,
                                                 @RequestParam(required = false) String recipient,
                                                 @RequestParam(required = false) String itemname,
                                                 @RequestParam(required = false) String projectid) {
        // 封装查询条件
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();

        // 判断是否所有查询条件都为空
        boolean allConditionsEmpty = StringUtils.isBlank(startDate)
                && StringUtils.isBlank(endDate)
                && StringUtils.isBlank(recipient)
                && StringUtils.isBlank(itemname)
                && StringUtils.isBlank(projectid);

        // 添加非空判断并设置查询条件
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            queryWrapper.between("time", startDate, endDate);
        }

        if (StringUtils.isNotBlank(recipient)) {
            queryWrapper.like("recipient", recipient);
        }

        if (StringUtils.isNotBlank(itemname)) {
            queryWrapper.like("itemname", itemname);
        }

        if (StringUtils.isNotBlank(projectid)) {
            queryWrapper.like("projectid", projectid);
        }

        // 如果所有查询条件都为空，按照取件时间的降序排列，并查询排序后的前50条数据
        if (allConditionsEmpty) {
            queryWrapper.orderByDesc("time").last("LIMIT 50");
        }

        // 查询
        List<Customer> customerList = customerService.list(queryWrapper);
        return new DataGridView((long) customerList.size(), customerList);
    }

    /**
     * 查询所有取件记录
     * @param customerVo
     * @return
     */
     @RequestMapping("loadAllCustomer")
     public DataGridView loadAllCustomer(CustomerVo customerVo){
         //1.声明一个分页page对象
         IPage<Customer> page = new Page<Customer>(customerVo.getPage(),customerVo.getLimit());

         //2.声明一个queryWrapper
         QueryWrapper<Customer> queryWrapper = new QueryWrapper<Customer>();

         // 设置排序规则，按 createTime 字段降序排序
         queryWrapper.orderByDesc("time");
         customerService.page(page,queryWrapper);
         return new DataGridView(page.getTotal(),page.getRecords());
     }



    // 检查库存是否足够
    @GetMapping("/checkInventory/{itemId}/{number}")
    public ResultObj checkInventory(@PathVariable int itemId, @PathVariable int number) {
        Goods goods = goodsService.getById(itemId);
        if (goods != null && goods.getNumber() >= number) {
            return ResultObj.CHECK_SUCCESS;
        } else {
            return ResultObj.CHECK_ERROR;
        }
    }


    /**
     * 添加一条取出信息
     * @param pickupData
     * @return
     */
    @RequestMapping("registerPickup")
    public ResultObj registerPickup(@RequestBody  PickupData pickupData){// @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        try {


            // 插入物品取走登记信息
            CustomerVo customerVo = new CustomerVo();
            customerVo.setId(pickupData.getId());
            customerVo.setItemname(pickupData.getItemname());
            customerVo.setRecipient(pickupData.getRecipient());
            customerVo.setNumber(pickupData.getNumber());
            customerVo.setTime(pickupData.getTime());
            customerVo.setProjectid(pickupData.getProjectid());


            customerService.save(customerVo);

            // 更新对应物品的数量
            Integer itemId = pickupData.getId();
            int withdrawalQuantity = pickupData.getNumber();
            Goods goods = goodsService.getById(itemId);
            if (goods != null) {
                int currentQuantity = goods.getNumber();
                if (currentQuantity >= withdrawalQuantity) {
                    goods.setNumber(currentQuantity - withdrawalQuantity);
                    goodsService.updateById(goods);
                    return ResultObj.ADD_SUCCESS;
                } else {
                    return ResultObj.ADD_ERROR; // 物品数量不足，返回失败
                }
            } else {
                return ResultObj.ADD_ERROR; // 物品不存在，返回失败
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


}

