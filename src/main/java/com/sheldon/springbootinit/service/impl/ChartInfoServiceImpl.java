package com.sheldon.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheldon.springbootinit.common.ErrorCode;
import com.sheldon.springbootinit.exception.BusinessException;
import com.sheldon.springbootinit.manager.AiManager;
import com.sheldon.springbootinit.mapper.ChartInfoMapper;
import com.sheldon.springbootinit.mapper.ChartMapper;
import com.sheldon.springbootinit.model.entity.Chart;
import com.sheldon.springbootinit.model.enums.ChartStatueEnum;
import com.sheldon.springbootinit.service.ChartInfoService;
import com.sheldon.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 26483
 * @description 针对表【chart(图表)】的数据库操作Service实现
 * @createDate 2024-01-18 01:58:17
 */
@Service
@Slf4j
public class ChartInfoServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartInfoService {

    @Resource
    private ChartInfoMapper chartInfoMapper;

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @Override
    public List<Map<String, Object>> getChartInfoById(Long chartId) {

        String tableName = "chart_" + chartId;
        String sql = "select * from " + tableName;
        List<Map<String, Object>> chartInfoList = chartInfoMapper.getChartInfoById(sql);
        return chartInfoList;
    }

    @Override
    public void genderChartInfo(Chart chart) {

        Long chartId = chart.getId();
        String name = chart.getName();
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String dataKeys = chart.getChartData();

        String chartData = this.getChartDataById(chartId, dataKeys);

        // 构建 AI 服务需要的输入参数
        StringBuilder userInput = new StringBuilder();
        if (StrUtil.isNotEmpty(name)) {
            userInput.append("图表名称为").append(chartType).append("\n");
        }
        if (StrUtil.isNotEmpty(chartType)) {
            userInput.append("，请使用").append(chartType).append("\n");
        }
        userInput.append("'Analysis goal:").append("\n");
        userInput.append(goal);
        userInput.append("\n");
        userInput.append("Raw data：").append("\n");
        userInput.append(chartData).append("'").append("\n");
        String userInputString = userInput.toString();

        // 异步执行图表分析任务
        try {
            // 更新图表状态为“执行中”
            Chart updateChart = new Chart();
            updateChart.setId(chartId);
            updateChart.setStatus(ChartStatueEnum.WORKING.getValue());
            boolean res = chartService.updateById(updateChart);
            if (!res) {
                log.warn("图表分析保存失败");
            }
//
//            // 调用 AI 服务进行图表分析
//            String result = aiManager.doChart(AiConstant.MODEL_ID, userInputString);
//            if (StrUtil.isEmpty(result)) {
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI服务异常");
//            }
//
//            // 解析结果
//            String[] split = result.split("【【【【【");
//            if (split.length != 3) {
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI服务异常");
//            }
//
//            String genCart = split[1];
//            String genResult = split[2];

            // 创建模拟数据
            String genCart = ("\n" +
                    "{\n" +
                    "  \"title\": {\n" +
                    "    \"text\": \"每天人数占比\",\n" +
                    "    \"x\": \"center\"\n" +
                    "  },\n" +
                    "  \"tooltip\": {\n" +
                    "    \"trigger\": \"item\",\n" +
                    "    \"formatter\": \"{a} <br/>{b} : {c} ({d}%)\"\n" +
                    "  },\n" +
                    "  \"legend\": {\n" +
                    "    \"orient\": \"vertical\",\n" +
                    "    \"left\": \"left\",\n" +
                    "    \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\", \"8号\"]\n" +
                    "  },\n" +
                    "  \"series\": [\n" +
                    "    {\n" +
                    "      \"name\": \"人数占比\",\n" +
                    "      \"type\": \"pie\",\n" +
                    "      \"radius\": \"55%\",\n" +
                    "      \"center\": [\"50%\", \"60%\"],\n" +
                    "      \"data\": [\n" +
                    "        { \"value\": 10, \"name\": \"1号\" },\n" +
                    "        { \"value\": 20, \"name\": \"2号\" },\n" +
                    "        { \"value\": 30, \"name\": \"3号\" },\n" +
                    "        { \"value\": 20, \"name\": \"4号\" },\n" +
                    "        { \"value\": 2, \"name\": \"5号\" },\n" +
                    "        { \"value\": 32, \"name\": \"6号\" },\n" +
                    "        { \"value\": 20, \"name\": \"7号\" },\n" +
                    "        { \"value\": 10, \"name\": \"8号\" }\n" +
                    "      ],\n" +
                    "      \"itemStyle\": {\n" +
                    "        \"emphasis\": {\n" +
                    "          \"shadowBlur\": 10,\n" +
                    "          \"shadowOffsetX\": 0,\n" +
                    "          \"shadowColor\": \"rgba(0, 0, 0, 0.5)\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n");
            String genResult = ("\n" +
                    "根据提供的原始数据，我们可以得出以下结论：\n" +
                    "- 在1号，10名用户占总人数的10%。\n" +
                    "- 在2号，20名用户占总人数的20%。\n" +
                    "- 在3号，30名用户占总人数的30%。\n" +
                    "- 在4号，20名用户占总人数的20%。\n" +
                    "- 在5号，2名用户占总人数的2%。\n" +
                    "- 在6号，32名用户占总人数的32%。\n" +
                    "- 在7号，20名用户占总人数的20%。\n" +
                    "- 在8号，10名用户占总人数的10%。\n" +
                    "通过这个饼图，我们可以直观地了解每天人数占总人数的比例。");

            Chart updateChartResult = new Chart();
            updateChartResult.setId(chartId);
            updateChartResult.setStatus(ChartStatueEnum.SUCCEED.getValue());
            updateChartResult.setGenChart(genCart);
            updateChartResult.setGenResult(genResult);
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                log.error("图表分析保存失败");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表分析保存失败");
            }

        } catch (Exception e) {
            // 处理图表分析过程中的异常
            log.error("图表分析失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表分析失败");
        }
    }

    public String getChartDataById(Long chartId, String dataKeys) {

        // 获得图表数据的 key
        String[] split1 = dataKeys.split(",");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split1);
        // 获取图表数据的 value
        List<Map<String, Object>> chartDataList = this.getChartInfoById(chartId);
        // 将图表数据转换为 csv 格式
        String convert = convert(chartDataList, list);
        return convert;
    }

    public static String convert(List<Map<String, Object>> dataList, List<String> keys) {
        String header = String.join(",", keys);

        String result = dataList.stream()
                .map(data -> keys.stream()
                        .map(data::get)
                        .map(value -> value != null ? value.toString() : "")
                        .collect(Collectors.joining(",")))
                .collect(Collectors.joining("\n", header + "\n", ""));

        return result;
    }

}




