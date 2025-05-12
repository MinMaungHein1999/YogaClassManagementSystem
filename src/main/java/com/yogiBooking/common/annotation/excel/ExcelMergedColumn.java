package com.yogiBooking.common.annotation.excel;

import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMergedColumn {
    String[] headerNames();
    int order();
    CellType cellType() default CellType.STRING;
}