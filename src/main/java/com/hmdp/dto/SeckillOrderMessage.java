package com.hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderMessage {
    // 订单id
    private Long orderId;
    // 用户id
    private Long userId;
    // 代金券id
    private Long voucherId;
    // 订单创建时间
    private Long eventTime;
}
