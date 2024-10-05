package com.example.koifishfengshui.enums;

public enum AdStatus {
    PENDING,
    APPROVED,
    PENDING_PAYMENT,   // Quảng cáo đang chờ thanh toán
    PAYMENT_FAILED,    // Thanh toán thất bại
    QUEUED_FOR_POST,
    PUBLISHED,
    REJECTED
}
