package com.mata.exception;

//建立自定义异常 业务异常
public class BusinessException extends RuntimeException {
    //继承RuntimeException（运行时异常）
    private Integer code;

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause, Integer code) {
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
