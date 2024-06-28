package com.gnluy.wkojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.gnluy.wkojcodesandbox.enums.ProcessExitValueEnum;
import com.gnluy.wkojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * @author IGR
 * @version 1.0
 * @description: Java执行命令工具类 编译&运行
 * @date 28/6/2024 下午11:37
 */
public class ProcessUtil {

    /**
     * 执行process 返回ExecuteMessage
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        int exitValue = 0;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            BufferedReader bufferedReader;
            ArrayList cmdOutputStrList;
            String cmdOutputLine;
            if (exitValue == ProcessExitValueEnum.SUCCESS.getValue()) {
                System.out.println(opName + "成功");

                //逐行获取控制台输出信息
                bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                cmdOutputStrList = new ArrayList();

                while((cmdOutputLine = bufferedReader.readLine()) != null) {
                    cmdOutputStrList.add(cmdOutputLine);
                }

                executeMessage.setMessage(StringUtils.join(cmdOutputStrList, "\n"));
            } else {
                System.out.println(opName + "失败 错误码：" + exitValue);

                //逐行获取控制台输出信息
                bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                cmdOutputStrList = new ArrayList();

                while((cmdOutputLine = bufferedReader.readLine()) != null) {
                    cmdOutputStrList.add(cmdOutputLine);
                }

                executeMessage.setMessage(StringUtils.join(cmdOutputStrList, "\n"));
            }

            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            return executeMessage;
        } catch (IOException | InterruptedException var8) {
            throw new RuntimeException(var8);
        }
    }

    /**
     * 交互式执行（模拟控制台输入）
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            outputStreamWriter.flush();
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();

            String compileOutputLine;
            while((compileOutputLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileOutputLine);
            }

            executeMessage.setMessage(compileOutputStringBuilder.toString());
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        System.out.println(executeMessage);
        return executeMessage;
    }
}