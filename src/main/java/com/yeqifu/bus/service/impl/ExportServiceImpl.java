package com.yeqifu.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeqifu.bus.entity.Export;
import com.yeqifu.bus.entity.Goods;
import com.yeqifu.bus.mapper.ExportMapper;
import com.yeqifu.bus.mapper.GoodsMapper;
import com.yeqifu.bus.service.IExportService;
import com.yeqifu.bus.service.IGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 侯炳超
 * @date 2024-02-18 14:02
 */
@Service
@Transactional
public class ExportServiceImpl extends ServiceImpl<ExportMapper, Export> implements IExportService {

}
