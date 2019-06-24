package com.fnbory.miaosha.access;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: fnbory
 * @Date: 2019/6/22 17:26
 */

@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
    int seconds();
    int maxCount() ;
    boolean needLogin() default  true;
}
