package com.gnluy.wkojcodesandbox.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProcessExitValueEnum {
    SUCCESS("正常退出", 0),
    ERROR("异常退出", 1);

    private final String text;
    private final Integer value;

    private ProcessExitValueEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static List<Integer> getValues() {
        return (List) Arrays.stream(values()).map((item) -> {
            return item.value;
        }).collect(Collectors.toList());
    }

    public static ProcessExitValueEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        } else {
            ProcessExitValueEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                ProcessExitValueEnum anEnum = var1[var3];
                if (anEnum.value.equals(value)) {
                    return anEnum;
                }
            }

            return null;
        }
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }
}

