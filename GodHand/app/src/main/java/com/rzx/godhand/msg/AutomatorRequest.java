package com.rzx.godhand.msg;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/24/024.
 */
public class AutomatorRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String method;
    private Object[] args;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "AutomatorRequest{" +
                "method='" + method + '\'' +
                ", args='" + args + '\'' +
                '}';
    }
}
