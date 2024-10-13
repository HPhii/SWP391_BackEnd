package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.model.entity.SubscriptionPlan;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import com.example.koifishfengshui.model.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PaymentService {

    public boolean processPayment(Advertisement ad, SubscriptionPlan plan, User user) {
        // Thực hiện logic thanh toán
        // Ví dụ: gọi API đến cổng thanh toán, kiểm tra giao dịch thành công hay không
        boolean paymentSuccess = callPaymentGateway(ad, plan, user);

        return paymentSuccess;
    }

    private boolean callPaymentGateway(Advertisement ad, SubscriptionPlan plan, User user) {
        // Đây là ví dụ mô phỏng việc gọi đến cổng thanh toán và nhận kết quả
        // Trong thực tế, bạn sẽ phải tích hợp với API của dịch vụ thanh toán
        // Giả sử thanh toán thành công trong ví dụ này
        return true;  // hoặc trả về false nếu thanh toán thất bại
    }

    public String createUrl(TransactionHistory transaction, SubscriptionPlan plan) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        String tmnCode = "8161QF3U";
        String secretKey = "5PQCLJTUF77QT32AC6KWMJ2IQ52OB5BC";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//        String returnUrl = "https://blearning.vn/guide/swp/docker-local?orderId=" + transaction.getTransactionId();
        String returnUrl = "http://localhost:8080/api/ads/vn-pay-callback";
        String currCode = "VND";

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", transaction.getTransactionId().toString());
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + transaction.getTransactionId());
        vnpParams.put("vnp_OrderType", "other");

        String amount = String.valueOf((long) (plan.getPrice() * 100));
        vnpParams.put("vnp_Amount", amount);

        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23"); // Có thể lấy IP thực từ request

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Bỏ ký tự '&' cuối cùng

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString();
    }


    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

