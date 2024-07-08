package com.gnluy.wkojcodesandbox.controller.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private Long time;

    private Long memory;
}
