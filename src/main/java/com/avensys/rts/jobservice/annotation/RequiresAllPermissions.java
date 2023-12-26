package com.avensys.rts.jobservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.avensys.rts.jobservice.enums.Permission;

/**
 * Author: Rahul Sahu This annotation is used to check if the user has all the
 * permissions specified in the annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAllPermissions {
    Permission[] value() default {};
}