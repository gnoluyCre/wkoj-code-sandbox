package com.gnluy.wkojcodesandbox;

import com.gnluy.wkojcodesandbox.model.ExecuteCodeRequest;
import com.gnluy.wkojcodesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

