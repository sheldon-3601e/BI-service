package com.sheldon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sheldon.springbootinit.model.entity.Chart;

import java.util.List;
import java.util.Map;

/**
* @author 26483
* @description 针对表【chart(图表)】的数据库操作Service
* @createDate 2024-01-18 01:58:17
*/
public interface ChartInfoService extends IService<Chart> {

    /**
     * 根据图表 Id 动态查询数据库表
     * @param chartId
     * @param chartId
     */
    List<Map<String, Object>> getChartInfoById(Long chartId);


    /**
     * 异步请求 AI 生成结果
     * @param chart
     * @param data
     */
    void genderChartInfo(Chart chart);

}
