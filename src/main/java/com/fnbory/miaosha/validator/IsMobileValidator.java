package com.fnbory.miaosha.validator;

import com.fnbory.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: fnbory
 * @Date: 2019/6/14 20:52
 */
public class IsMobileValidator implements  ConstraintValidator<IsMobile,String>{

    private  boolean required=true;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.required=constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext Context) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }
        else {
            if(StringUtils.isEmpty(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
