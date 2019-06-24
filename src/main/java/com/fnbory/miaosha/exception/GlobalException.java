package com.fnbory.miaosha.exception;

import com.fnbory.miaosha.result.CodeMsg;

/**
 * @Author: fnbory
 * @Date: 2019/6/15 14:37
 */
public class GlobalException extends   RuntimeException {
    private CodeMsg cm;

    public  GlobalException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
