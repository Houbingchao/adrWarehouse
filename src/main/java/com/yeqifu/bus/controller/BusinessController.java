package com.yeqifu.bus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 业务管理的路由器
 * @Author:
 * @Date: 2019/12/5 9:33
 */
@Controller
@RequestMapping("bus")
public class BusinessController {

    /**
     * 跳转到取件登记页面
     * @return
     */
    @RequestMapping("toCustomerManager")
    public String toCustomerManager(){
        return "business/customer/customerManager";
    }


    /**
     * 跳转到物品管理页面
     * @return
     */
    @RequestMapping("toGoodsManager")
    public String toGoodsManager(){
        return "business/goods/goodsManager";
    }

    /**
     * 跳转到取件记录页面
     * @return
     */
    @RequestMapping("toPickupManager")
    public String toPickupManager(){
        return "business/pickup/pickupManager";
    }

    /**
     * 跳转到取件记录导出页面
     * @return
     */
    @RequestMapping("toExportPickupRecordsManager")
    public String toExportPickupRecordsManager(){
        return "business/ExportPickupRecords/exportPickupRecords";
    }

    /**
     * 跳转到入库记录导出页面
     * @return
     */
    @RequestMapping("toExportWarehousingRecordsManager")
    public String toExportWarehousingRecordsManager(){
        return "business/ExportPickupRecords/exportWarehousingRecords";
    }




}
