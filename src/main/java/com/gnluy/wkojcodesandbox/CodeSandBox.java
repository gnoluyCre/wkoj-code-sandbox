package com.gnluy.wkojcodesandbox;

import com.gnluy.wkojcodesandbox.controller.model.ExecuteCodeRequest;
import com.gnluy.wkojcodesandbox.controller.model.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

