package com.mata.config;

import com.mata.dto.Code;
import com.mata.dto.Result;
import com.mata.exception.BusinessException;
import com.mata.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    //其他异常
    @ExceptionHandler(Exception.class)
    public Result doException(Exception exception){
        return new Result(null, Code.SYSTEM_UNKNOW_ERR,"系统异常");
    }
    //系统异常
    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException systemException){
        return new Result(null,systemException.getCode(),systemException.getMessage());
    }
    //业务异常
    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException businessException){
        return new Result(null,businessException.getCode(),businessException.getMessage());
    }

}
