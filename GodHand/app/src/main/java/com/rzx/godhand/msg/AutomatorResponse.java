package com.rzx.godhand.msg;

import java.io.Serializable;

/**
 * Created by rzx on 2016/7/25.
 */
public class AutomatorResponse implements Serializable{
    private static final long serialVersionUID = 2L;
    private Object response;

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return response.toString();
    }
}
