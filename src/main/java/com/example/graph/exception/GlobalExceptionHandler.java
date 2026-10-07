package com.example.graph.exception;

import com.example.graph.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常，请求地址：{}，异常信息：{}", request.getRequestURI(), message);
        return Result.fail(400, message);
    }

    /**
     * 普通表单参数绑定校验异常
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常，请求地址：{}，异常信息：{}", request.getRequestURI(), message);
        return Result.fail(400, message);
    }

    /**
     * 兜底：捕获所有其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        log.error("系统未知异常，请求地址：{}", request.getRequestURI(), e);
        return Result.fail(500, "服务器内部错误");
    }
}
