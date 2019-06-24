package com.fnbory.miaosha.exception;

import com.fnbory.miaosha.result.CodeMsg;
import com.fnbory.miaosha.result.Result;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/6/15 14:20
 */
@ControllerAdvice
@ResponseBody
public class GlobalExcepetionHandler {
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception e){
        if(e instanceof GlobalException){
            return Result.error(((GlobalException) e).getCm());
        }
        else if(e instanceof BindException){
            List<ObjectError> errors=((BindException) e).getAllErrors();
            String msg=errors.get(0).getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillargs(msg));
        }
        else {
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
