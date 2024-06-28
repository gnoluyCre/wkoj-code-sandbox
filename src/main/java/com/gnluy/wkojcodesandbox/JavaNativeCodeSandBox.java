package com.gnluy.wkojcodesandbox;

import cn.hutool.core.io.FileUtil;
import com.gnluy.wkojcodesandbox.enums.ExecuteCodeStatusEnum;
import com.gnluy.wkojcodesandbox.model.ExecuteCodeRequest;
import com.gnluy.wkojcodesandbox.model.ExecuteCodeResponse;
import com.gnluy.wkojcodesandbox.model.ExecuteMessage;
import com.gnluy.wkojcodesandbox.model.JudgeInfo;
import com.gnluy.wkojcodesandbox.utils.ProcessUtil;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandBox implements CodeSandBox {
    private final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> inputList = executeCodeRequest.getInputList();
        JudgeInfo judgeInfo = new JudgeInfo();
        List<String> outputList = new ArrayList();

        //1. 把用户的代码保存为文件
        String code = executeCodeRequest.getCode();
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        //2. 编译用户代码
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        String runCmd;
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            Integer exitValue = executeMessage.getExitValue();
            if (exitValue != 0) {
                executeCodeResponse = this.getErrorExecuteCodeResponse(executeMessage.getMessage(), ExecuteCodeStatusEnum.CODE_SANDBOX.getValue(), judgeInfo);
                return executeCodeResponse;
            }
        } catch (Exception e) {
            executeCodeResponse = this.getErrorExecuteCodeResponse(e.getMessage(), ExecuteCodeStatusEnum.CODE_SANDBOX.getValue(), judgeInfo);
            return executeCodeResponse;
        }

        //3, 循环执行输入用例 编译后代码
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(runProcess, "运行");
                Integer exitValue = executeMessage.getExitValue();
                if (exitValue == 0 && !ObjectUtils.isEmpty(exitValue)) {
                    executeMessageList.add(executeMessage);
                    continue;
                }
                return this.getErrorExecuteCodeResponse(executeMessage.getMessage(), ExecuteCodeStatusEnum.CODE_ERROR.getValue(), judgeInfo);
            } catch (Exception e) {
                executeCodeResponse = this.getErrorExecuteCodeResponse(e.getMessage(), ExecuteCodeStatusEnum.CODE_SANDBOX.getValue(), judgeInfo);
                return executeCodeResponse;
            }
        }

        //4. 整理输出结果
        long maxExecuteTime = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {
            maxExecuteTime = Math.max(executeMessage.getTime(), maxExecuteTime);
            outputList.add(executeMessage.getMessage());
        }
        executeCodeResponse.setOutputList(outputList);
        judgeInfo.setTime(maxExecuteTime);
        judgeInfo.setMemory(0L);
        executeCodeResponse.setStatus(ExecuteCodeStatusEnum.SUCCESS.getValue());
        executeCodeResponse.setJudgeInfo(judgeInfo);

        //5. 删除文件
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除文件夹" + userCodeParentPath + (del ? "成功" : "失败"));
        }
        return executeCodeResponse;

}

    /**
     * 封装获取错误输出response
     * @param message
     * @param status
     * @param judgeInfo
     * @return
     */
    private ExecuteCodeResponse getErrorExecuteCodeResponse(String message, Integer status, JudgeInfo judgeInfo) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        judgeInfo.setMessage(message);
        executeCodeResponse.setOutputList(new ArrayList());
        executeCodeResponse.setMessage(message);
        executeCodeResponse.setStatus(status);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        System.out.println("错误响应: " + executeCodeResponse);
        return executeCodeResponse;
    }
}