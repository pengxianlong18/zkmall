package com.pxl.common.valid;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ValidListConstraintValidator implements ConstraintValidator<ValidList, Integer> {

    private Set<Integer> set=new HashSet<>();
    // 初始化方法
    @Override
    public void initialize(ValidList constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        if (vals != null && vals.length > 0) {
            for (int val : vals) {
                set.add(val);
            }
        }
    }


    //判断是否校验成功
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(value);
    }
}
