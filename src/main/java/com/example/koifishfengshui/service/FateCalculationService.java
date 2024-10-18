package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.FateType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FateCalculationService {

    // Can: Giáp - Ất = 1, Bính - Đinh = 2, Mậu - Kỷ = 3, Canh - Tân = 4, Nhâm - Quý = 5
    private static final int[] CAN_VALUES = {4, 4, 5, 5, 1, 1, 2, 2, 3, 3};

    // Chi: Tý - Sửu - Ngọ - Mùi = 0, Dần - Mão - Thân - Dậu = 1, Thìn - Tỵ - Tuất - Hợi = 2
    private static final int[] CHI_VALUES = {1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 0, 0};

    public FateType calculateFate(LocalDate birthdate) {
        int year = birthdate.getYear();

        int can = calculateCan(year);
        int chi = calculateChi(year);

        int fateValue = can + chi;

        // Nếu tổng Can + Chi lớn hơn 6, trừ đi 5
        if (fateValue > 5) {
            fateValue -= 5;
        }

        return determineFate(fateValue);
    }

    private int calculateCan(int year) {
        int lastDigit = year % 10;
        return CAN_VALUES[lastDigit];
    }

    private int calculateChi(int year) {
        int twoLastDigits = year % 12;
        return CHI_VALUES[twoLastDigits];
    }

    private FateType determineFate(int fateValue) {
        switch (fateValue) {
            case 1:
                return FateType.METAL;  // Kim
            case 2:
                return FateType.WATER;  // Thủy
            case 3:
                return FateType.FIRE;   // Hỏa
            case 4:
                return FateType.EARTH;  // Thổ
            case 5:
                return FateType.WOOD;   // Mộc
            default:
                return null;
        }
    }
}

