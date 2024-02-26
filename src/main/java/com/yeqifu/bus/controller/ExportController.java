package com.yeqifu.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeqifu.bus.entity.Customer;
import com.yeqifu.bus.entity.Export;
import com.yeqifu.bus.service.IExportService;
import com.yeqifu.sys.common.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 侯炳超
 * @date 2024-02-18 15:25
 */
@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private IExportService exportService;

    /**
     * 根据各种条件查询入库记录
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return
     */
    @GetMapping("loadExportByConditions")
    public DataGridView loadCustomerByConditions(@RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate,
                                                 @RequestParam(required = false) String person,
                                                 @RequestParam(required = false) String goodsname,
                                                 @RequestParam(required = false) String projectid) {
        // 封装查询条件
        QueryWrapper<Export> queryWrapper = new QueryWrapper<>();

        // 判断是否所有查询条件都为空
        boolean allConditionsEmpty = StringUtils.isBlank(startDate)
                && StringUtils.isBlank(endDate)
                && StringUtils.isBlank(person)
                && StringUtils.isBlank(goodsname)
                && StringUtils.isBlank(projectid);

        // 添加非空判断并设置查询条件
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            queryWrapper.between("time", startDate, endDate);
        }

        if (StringUtils.isNotBlank(person)) {
            queryWrapper.like("person", person);
        }

        if (StringUtils.isNotBlank(goodsname)) {
            queryWrapper.like("goodsname", goodsname);
        }

        if (StringUtils.isNotBlank(projectid)) {
            queryWrapper.like("projectid", projectid);
        }

        // 如果所有查询条件都为空，按照取件时间的降序排列，并查询排序后的前50条数据
        if (allConditionsEmpty) {
            queryWrapper.orderByDesc("time").last("LIMIT 50");
        }

        // 查询
        List<Export> exportList = exportService.list(queryWrapper);
        return new DataGridView((long) exportList.size(), exportList);
    }


}
