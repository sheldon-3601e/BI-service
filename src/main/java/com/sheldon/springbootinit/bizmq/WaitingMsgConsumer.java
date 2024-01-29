package com.sheldon.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.sheldon.springbootinit.common.ErrorCode;
import com.sheldon.springbootinit.exception.BusinessException;
import com.sheldon.springbootinit.model.entity.Chart;
import com.sheldon.springbootinit.model.enums.ChartStatueEnum;
import com.sheldon.springbootinit.service.ChartInfoService;
import com.sheldon.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @ClassName MyMessageProducer
 * @Author 26483
 * @Date 2024/1/26 16:48
 * @Version 1.0
 * @Description TODO
 */
@Component
@Slf4j
public class WaitingMsgConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private ChartInfoService chartInfoService;

    @Resource
    private MsgProducer msgProducer;

    @RabbitListener(queues = {BiMqConstant.QUEUE_WAITING_NAME}, ackMode = "MANUAL")
    public void sendMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        log.info("MQ:接收等待分析图表信息：{}", message);
        try {
            // 校验消息
            if (StringUtils.isBlank(message)) {
                // 如果更新失败，拒绝当前消息，让消息重新进入队列
                log.error("消息确认失败：{}", "");
                channel.basicNack(deliveryTag, false, true);
            }
            Long chartId = Long.parseLong(message);
            Chart chart = chartService.getById(chartId);
            if (chart == null) {
                log.error("消息确认失败：{}", "图表不存在");
                channel.basicNack(deliveryTag, false, false);
            }

            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus(ChartStatueEnum.WORKING.getValue());
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                // 如果更新图表执行中状态失败，拒绝消息并处理图表更新错误
                log.error("消息确认失败：{}", "更新图表执行中状态失败");
                channel.basicNack(deliveryTag, false, false);
            }
            // 分析图表数据，并保存到数据库
            chartInfoService.genderChartInfo(chart);

            // 消息确认
            channel.basicAck(deliveryTag, false);
            msgProducer.sendSucceedMsg(message);
        } catch (Exception e) {
            log.error("消息确认失败：{}", e.getMessage());
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("消息拒绝失败：{}", ex.getMessage());
            }
        }
    }

}
