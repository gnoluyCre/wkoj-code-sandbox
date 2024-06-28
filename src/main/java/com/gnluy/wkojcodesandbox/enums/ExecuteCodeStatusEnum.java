package com.gnluy.wkojcodesandbox.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ExecuteCodeStatusEnum {
    SUCCESS("执行成功", 1),
    CODE_SANDBOX("代码沙箱错误", 2),
    CODE_ERROR("用户代码执行失败", 3);

    private final String text;
    private final Integer value;

    private ExecuteCodeStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static List<Integer> getValues() {
        return (List) Arrays.stream(values()).map((item) -> {
            return item.value;
        }).collect(Collectors.toList());
    }

    public static ExecuteCodeStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        } else {
            ExecuteCodeStatusEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                ExecuteCodeStatusEnum anEnum = var1[var3];
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
