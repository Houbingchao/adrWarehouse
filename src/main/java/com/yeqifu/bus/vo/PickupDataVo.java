package com.yeqifu.bus.vo;

import com.yeqifu.bus.entity.PickupData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 侯炳超
 * @date 2023-10-29 21:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PickupDataVo extends PickupData {
    /**
     * 分页参数，当前是第一页，每页10条数据
     */
    private Integer page=1;
    private Integer limit=10;
}
