package com.mata.exception;

//建立自定义异常 系统异常
public class SystemException extends RuntimeException{
    //继承RuntimeException（运行时异常）
    private Integer code;

    public SystemException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public SystemException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
