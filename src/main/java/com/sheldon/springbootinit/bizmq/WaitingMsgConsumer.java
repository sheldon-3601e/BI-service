package com.sheldon.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
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
        log.info("接收等待分析图表信息：{}", message);
        if (!StringUtils.isBlank(message)){
            if ("succeed".equals(message)) {
                log.info("succeed");
                try {
                    msgProducer.sendSucceedMsg(message);
                    channel.basicAck(deliveryTag, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    log.error("failed");
                    channel.basicNack(deliveryTag, false, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
   /*     try {
            // 校验消息
            if (StringUtils.isBlank(message)) {
                // 如果更新失败，拒绝当前消息，让消息重新进入队列
                log.error("消息确认失败：{}", "message is blank");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
            }
            Long chartId = Long.parseLong(message);
            Chart chart = chartService.getById(chartId);
            if (chart == null) {
                log.error("消息确认失败：{}", "chart is null");
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表不存在");
            }

            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus(ChartStatueEnum.WORKING.getValue());
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                // 如果更新图表执行中状态失败，拒绝消息并处理图表更新错误
                log.error("消息确认失败：{}", "更新图表执行中状态失败");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新图表执行中状态失败");
            }
            // 分析图表数据，并保存到数据库
            chartInfoService.genderChartInfo(chart);

            // 消息确认
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("消息确认失败：{}", e.getMessage());
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("消息拒绝失败：{}", ex.getMessage());
            }
        }*/
    }

}
