package com.hmdp.mq;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.SeckillOrderMessage;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SeckillOrderConsumer {

    @Resource
    private IVoucherOrderService voucherOrderService; // 注入优惠券订单服务

    // Kafka消息监听器，监听秒杀订单主题的消息
    @RetryableTopic( // 配置重试主题
            attempts = "${app.kafka.retry.max-attempts}", // 最大重试次数
            // 退避策略，指数退避
            backoff = @Backoff(
                    delayExpression = "${app.kafka.retry.base-delay-ms}",
                    multiplier = 2.0,
                    maxDelayExpression = "${app.kafka.retry.max-delay-ms}"
            ),
            autoCreateTopics = "${app.kafka.retry.auto-create-topics}", // 是否自动创建重试主题
            exclude = {IllegalArgumentException.class} // 排除非法参数异常，不进行重试
    )
    @KafkaListener(topics = "${app.kafka.seckill-topic}")
    public void onMessage(String message) {
        SeckillOrderMessage orderMessage;
        try {
            orderMessage = JSONUtil.toBean(message, SeckillOrderMessage.class);
        } catch (Exception e) {
            log.error("Kafka消息解析失败，payload={}", message, e);
            throw new IllegalArgumentException("invalid payload", e);
        }
        if (orderMessage.getUserId() == null || orderMessage.getVoucherId() == null || orderMessage.getOrderId() == null) {
            log.error("Kafka消息字段不完整，payload={}", message);
            throw new IllegalArgumentException("invalid payload");
        }

        // 创建优惠券订单
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderMessage.getOrderId());
        voucherOrder.setUserId(orderMessage.getUserId());
        voucherOrder.setVoucherId(orderMessage.getVoucherId());
        voucherOrderService.createVoucherOrder(voucherOrder);
        log.info("Kafka消费成功，orderId={}", orderMessage.getOrderId());
    }

    @DltHandler
    public void onDlt(String message) {
        log.error("DLT告警：payload={}", message);
    }
}
