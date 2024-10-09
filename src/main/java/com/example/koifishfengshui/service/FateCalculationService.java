package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.FateType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FateCalculationService {

    private static final int[] CAN_VALUES = {4, 4, 5, 5, 1, 1, 2, 2, 3, 3};

    private static final int[] CHI_VALUES = {0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2};

    public FateType calculateFate(LocalDate birthdate) {
        int year = birthdate.getYear();

        int can = calculateCan(year);
        int chi = calculateChi(year);

        int fateValue = can + chi;

        if (fateValue > 6) {
            fateValue -= 5;
        }

        return determineFate(fateValue);
    }

    private int calculateCan(int year) {
        int lastDigit = year % 10;
        return CAN_VALUES[lastDigit];
    }

    private int calculateChi(int year) {
        int chi = year % 12;
        return CHI_VALUES[chi];
    }

    private FateType determineFate(int fateValue) {
        switch (fateValue) {
            case 1:
                return FateType.METAL;
            case 2:
                return FateType.WOOD;
            case 3:
                return FateType.WATER;
            case 4:
                return FateType.FIRE;
            case 5:
                return FateType.EARTH;
            default:
                return null;
        }
    }
}
