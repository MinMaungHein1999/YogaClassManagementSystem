package com.yogiBooking.common.annotation.excel;

import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDateColumn {
    // Names for a single date column
    String[] dateColumnNames() default {};
    
    // Names for day column
    String[] dayColumnNames() default {};
    
    // Names for month column
    String[] monthColumnNames() default {};
    
    // Names for year column
    String[] yearColumnNames() default {};
    
    // Date format for parsing/formatting
    String dateFormat() default "dd/MM/yyyy";
    
    // Whether the date is always stored as separate day/month/year values
    boolean alwaysSeparateValues() default false;
    
    // Day offset from the header column (if using single header with separate values)
    int dayOffset() default 0;
    
    // Month offset from the header column (if using single header with separate values)
    int monthOffset() default 1;
    
    // Year offset from the header column (if using single header with separate values)
    int yearOffset() default 2;
    
    // Cell type for writing
    CellType cellType() default CellType.STRING;
}
