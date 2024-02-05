package com.sheldon.springbootinit.service.impl;
import java.util.Date;

import com.sheldon.springbootinit.model.entity.Chart;
import com.sheldon.springbootinit.service.ChartInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ClassName ChartInfoServiceImplTest
 * @Author 26483
 * @Date 2024/2/5 17:03
 * @Version 1.0
 * @Description TODO
 */
@SpringBootTest
class ChartInfoServiceImplTest {

    @Autowired
    private ChartInfoService chartInfoService;

    @Test
    void genderChartInfo() {

        Chart chart = new Chart();
        chart.setId(0L);
        chart.setUserId(0L);
        chart.setName("");
        chart.setGoal("");
        chart.setChartType("");
        chart.setChartData("");
        chart.setStatus(0);
        chart.setExecMessage("");
        chart.setGenChart("");
        chart.setGenResult("");
        chart.setCreateTime(new Date());
        chart.setUpdateTime(new Date());
        chart.setIsDelete(0);

        chartInfoService.genderChartInfo(chart);


    }
}