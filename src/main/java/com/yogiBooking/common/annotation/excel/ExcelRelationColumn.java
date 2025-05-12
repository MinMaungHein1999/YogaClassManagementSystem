package com.yogiBooking.common.annotation.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelRelationColumn {
    // Prefix for column names that belong to this relation
    String prefix() default "";

    // Whether to create a new entity if not found
    boolean createIfNotFound() default true;
}
