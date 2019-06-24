package com.fnbory.miaosha.result;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Result<T> {
    private  int code;
    private  String msg;
    private  T data;

    public  static <T>  Result success(T data){return new Result<>(data); }

    public  static Result error(CodeMsg codeMsg){return new Result(codeMsg);}

    private  Result(T data){
        this.code=0;
        this.msg="success";
        this.data=data;
    }

    private  Result(CodeMsg codeMsg){
        if(codeMsg==null) {
            return;
        }
        else {
            this.code=codeMsg.getCode();
            this.msg=codeMsg.getMsg();
        }
    }


}
