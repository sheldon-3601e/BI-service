package com.sheldon.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.sheldon.springbootinit.common.ErrorCode;
import com.sheldon.springbootinit.constant.AiConstant;
import com.sheldon.springbootinit.exception.BusinessException;
import com.sheldon.springbootinit.manager.AiManager;
import com.sheldon.springbootinit.mapper.ChartInfoMapper;
import com.sheldon.springbootinit.mapper.ChartMapper;
import com.sheldon.springbootinit.model.entity.Chart;
import com.sheldon.springbootinit.model.enums.ChartStatueEnum;
import com.sheldon.springbootinit.service.ChartInfoService;
import com.sheldon.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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

    // 添加重试机制
    Retryer<String[]> retryer = RetryerBuilder.<String[]>newBuilder()
            .retryIfResult(res -> res.length != 3)  // 设置根据结果重试，当split长度不等于3时重试
            .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS)) // 设置等待间隔时间
            .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 设置最大重试次数
            .build();

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

            // 调用 AI 服务进行图表分析
            String[] split = null;
            try {
                split = retryer.call(() -> aiManager.doChart(AiConstant.MODEL_ID, userInputString).split("【【【【【"));
            } catch (ExecutionException | RetryException e) {
                // 重试失败，抛出异常
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI服务异常");
            }

            String genCart = split[1];
            String genResult = split[2];

            // 创建模拟数据
            /*String genCart = ("{\n" +
                    "    \"title\": {\n" +
                    "        \"text\": \"网站用户人数趋势\",\n" +
                    "        \"subtext\": \"数据来源：Raw data\"\n" +
                    "    },\n" +
                    "    \"xAxis\": {\n" +
                    "        \"type\": \"category\",\n" +
                    "        \"data\": [\"1\", \"2\", \"3\"]\n" +
                    "    },\n" +
                    "    \"yAxis\": {\n" +
                    "        \"type\": \"value\"\n" +
                    "    },\n" +
                    "    \"series\": [\n" +
                    "        {\n" +
                    "            \"name\": \"用户人数\",\n" +
                    "            \"type\": \"bar\",\n" +
                    "            \"data\": [10, 20, 30]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            String genResult = ("网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多," +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多," +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多，" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多，" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多，" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多，" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多，" +
                    "网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多网站用户数量逐渐增长，周六和周日用户数量较多，周一用户数量较少，周二到周五用户数量逐渐增多");
*/
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


    // 从单表中获取图表数据
    public String getChartDataById(Long chartId, String dataKeys) {

        // 获得图表数据的 key
        String[] split1 = dataKeys.split(",");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split1);
        // 获取图表数据的 value
        List<Map<String, Object>> chartDataList = this.getChartInfoById(chartId);
        // 将图表数据转换为 csv 格式
        return convert(chartDataList, list);
    }

    // 将图表数据转换为 csv 格式
    public static String convert(List<Map<String, Object>> dataList, List<String> keys) {
        String header = String.join(",", keys);

        return dataList.stream()
                .map(data -> keys.stream()
                        .map(data::get)
                        .map(value -> value != null ? value.toString() : "")
                        .collect(Collectors.joining(",")))
                .collect(Collectors.joining("\n", header + "\n", ""));
    }

}




