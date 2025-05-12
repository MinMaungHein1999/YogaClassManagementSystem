package com.yogiBooking.common.utils.excel;

import com.yogiBooking.common.annotation.excel.ExcelColumn;
import com.yogiBooking.common.annotation.excel.ExcelDateColumn;
import com.yogiBooking.common.annotation.excel.ExcelMergedColumn;
import com.yogiBooking.common.annotation.excel.ExcelRelationColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExcelMapper {
    // Maximum depth for nested relations
    private static final int MAX_RELATION_DEPTH = 3;
    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";

    private ExcelMapper() {
        // Prevent instantiation
    }

    // Main public methods
    public static <T> T mapRowToEntity(Row row, Class<T> clazz, int headerIndex) throws ReflectiveOperationException {
        return mapRowToEntity(row, clazz, headerIndex, 0);
    }

    public static <T> void mapEntityToRow(T entity, Row row, int headerIndex) throws ReflectiveOperationException {
        mapEntityToRow(entity, row, headerIndex, 0);
    }

    // Core mapping methods with depth tracking
    private static <T> T mapRowToEntity(Row row, Class<T> clazz, int headerIndex, int depth) throws ReflectiveOperationException {
        T entity = clazz.getDeclaredConstructor().newInstance();
        Map<String, Integer> columnMapping = getColumnMapping(row.getSheet(), headerIndex);
    
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            mapFieldFromRow(row, entity, field, columnMapping, headerIndex, depth);
        }
        return entity;
    }
    
    private static <T> void mapEntityToRow(T entity, Row row, int headerIndex, int depth) throws ReflectiveOperationException {
        Class<?> clazz = entity.getClass();
        Map<String, Integer> columnMapping = getColumnMapping(row.getSheet(), headerIndex);
    
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            mapFieldToRow(entity, row, field, columnMapping, headerIndex, depth);
        }
    }

    // Field mapping dispatcher methods
    private static <T> void mapFieldFromRow(Row row, T entity, Field field, 
            Map<String, Integer> columnMapping, int headerIndex, int depth) throws ReflectiveOperationException {
        if (field.isAnnotationPresent(ExcelColumn.class)) {
            mapExcelColumnToField(row, entity, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelMergedColumn.class)) {
            mapExcelMergedColumnToField(row, entity, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelDateColumn.class) && field.getType().equals(LocalDate.class)) {
            mapExcelDateColumnToField(row, entity, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelRelationColumn.class)) {
            if (depth < MAX_RELATION_DEPTH) {
                mapExcelRelationColumnToField(row, entity, field, columnMapping, headerIndex, depth);
            } else {
                log.warn("Maximum relation depth reached for field: {}", field.getName());
            }
        }
    }
    
    private static <T> void mapFieldToRow(T entity, Row row, Field field, 
            Map<String, Integer> columnMapping, int headerIndex, int depth) throws ReflectiveOperationException {
        if (field.isAnnotationPresent(ExcelColumn.class)) {
            writeExcelColumnFromField(entity, row, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelMergedColumn.class)) {
            writeExcelMergedColumnFromField(entity, row, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelDateColumn.class) && field.getType().equals(LocalDate.class)) {
            writeExcelDateColumnFromField(entity, row, field, columnMapping);
        } else if (field.isAnnotationPresent(ExcelRelationColumn.class)) {
            if (depth < MAX_RELATION_DEPTH) {
                writeExcelRelationColumnFromField(entity, row, field, columnMapping, headerIndex, depth);
            } else {
                log.warn("Maximum relation depth reached for field: {}", field.getName());
            }
        }
    }

    // Specific field mapping implementations
    private static <T> void mapExcelColumnToField(Row row, T entity, Field field, Map<String, Integer> columnMapping) 
            throws IllegalAccessException {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        Integer columnIndex = getColumnIndex(columnMapping, excelColumn.names());

        if (columnIndex != null) {
            Cell cell = getOrCreateCell(row, columnIndex);
            String cellValue = getCellValue(cell);
            field.set(entity, cellValue);
        }
    }

    private static <T> void mapExcelMergedColumnToField(Row row, T entity, Field field, Map<String, Integer> columnMapping) 
            throws IllegalAccessException {
        ExcelMergedColumn mergedColumn = field.getAnnotation(ExcelMergedColumn.class);
        Integer columnIndex = getColumnIndex(columnMapping, mergedColumn.headerNames());

        if (columnIndex != null) {
            int order = mergedColumn.order();
            Cell cell = getOrCreateCell(row, columnIndex + order);
            String cellValue = getCellValue(cell);
            field.set(entity, cellValue);
        }
    }

    private static <T> void mapExcelDateColumnToField(Row row, T entity, Field field, Map<String, Integer> columnMapping) 
            throws IllegalAccessException {
        ExcelDateColumn dateColumn = field.getAnnotation(ExcelDateColumn.class);
        String dateFormat = dateColumn.dateFormat();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        
        // If alwaysSeparateValues is true, we'll always use the day/month/year approach
        if (dateColumn.alwaysSeparateValues()) {
            // First try with separate column names
            boolean mappedWithSeparateNames = tryMapDateFromSeparateColumnNames(row, entity, field, columnMapping, dateColumn);
            
            // If that fails, try with offsets from a single header
            if (!mappedWithSeparateNames) {
                tryMapDateFromSingleHeaderWithOffsets(row, entity, field, columnMapping, dateColumn);
            }
            return;
        }
        
        // Try to map from a single date column first
        Integer dateColumnIndex = getColumnIndex(columnMapping, dateColumn.dateColumnNames());
        if (dateColumnIndex != null) {
            Cell cell = row.getCell(dateColumnIndex);
            if (cell != null) {
                String dateStr = getCellValue(cell);
                try {
                    if (dateStr != null && !dateStr.isEmpty()) {
                        LocalDate date = LocalDate.parse(dateStr, formatter);
                        field.set(entity, date);
                        return;
                    }
                } catch (DateTimeParseException e) {
                    log.debug("Failed to parse date from single column. Trying day/month/year columns.");
                }
            }
        }
        
        // If single column mapping failed or not provided, try day/month/year columns
        boolean mappedWithSeparateNames = tryMapDateFromSeparateColumnNames(row, entity, field, columnMapping, dateColumn);
        
        // If that fails, try with offsets from a single header
        if (!mappedWithSeparateNames) {
            tryMapDateFromSingleHeaderWithOffsets(row, entity, field, columnMapping, dateColumn);
        }
    }
    
    private static <T> boolean tryMapDateFromSeparateColumnNames(Row row, T entity, Field field,
            Map<String, Integer> columnMapping, ExcelDateColumn dateColumn) throws IllegalAccessException {
        Integer dayColumnIndex = getColumnIndex(columnMapping, dateColumn.dayColumnNames());
        Integer monthColumnIndex = getColumnIndex(columnMapping, dateColumn.monthColumnNames());
        Integer yearColumnIndex = getColumnIndex(columnMapping, dateColumn.yearColumnNames());
        
        if (dayColumnIndex != null && monthColumnIndex != null && yearColumnIndex != null) {
            String day = getCellValue(row.getCell(dayColumnIndex));
            String month = getCellValue(row.getCell(monthColumnIndex));
            String year = getCellValue(row.getCell(yearColumnIndex));
            
            return tryParseAndSetDate(entity, field, day, month, year);
        }
        return false;
    }
    
    private static <T> void tryMapDateFromSingleHeaderWithOffsets(Row row, T entity, Field field,
            Map<String, Integer> columnMapping, ExcelDateColumn dateColumn) throws IllegalAccessException {
        // Try to find the base column index from dateColumnNames
        Integer baseColumnIndex = getColumnIndex(columnMapping, dateColumn.dateColumnNames());
        
        if (baseColumnIndex != null) {
            int dayColumnIndex = baseColumnIndex + dateColumn.dayOffset();
            int monthColumnIndex = baseColumnIndex + dateColumn.monthOffset();
            int yearColumnIndex = baseColumnIndex + dateColumn.yearOffset();
            
            String day = getCellValue(row.getCell(dayColumnIndex));
            String month = getCellValue(row.getCell(monthColumnIndex));
            String year = getCellValue(row.getCell(yearColumnIndex));
            
            tryParseAndSetDate(entity, field, day, month, year);
        }
    }
    
    private static <T> boolean tryParseAndSetDate(T entity, Field field, String day, String month, String year) 
            throws IllegalAccessException {
        try {
            if (day != null && month != null && year != null) {
                // Handle potential formatting issues
                day = day.replaceAll("[^0-9]", "");
                month = month.replaceAll("[^0-9]", "");
                year = year.replaceAll("[^0-9]", "");
                
                if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                    return false;
                }
                
                // Ensure proper padding
                day = day.length() == 1 ? "0" + day : day;
                month = month.length() == 1 ? "0" + month : month;
                
                // Handle 2-digit years
                if (year.length() == 2) {
                    int yearNum = Integer.parseInt(year);
                    year = (yearNum > 50 ? "19" : "20") + year; // Assume 19xx for years > 50, else 20xx
                }
                
                String dateStr = day + "/" + month + "/" + year;
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
                field.set(entity, date);
                return true;
            }
        } catch (Exception e) {
            log.debug("Failed to parse date from values: day={}, month={}, year={}, error={}", 
                    day, month, year, e.getMessage());
        }
        return false;
    }

    // Update the writeExcelDateColumnFromField method to handle the new approach
    private static <T> void writeExcelDateColumnFromField(T entity, Row row, Field field, Map<String, Integer> columnMapping) 
            throws IllegalAccessException {
        ExcelDateColumn dateColumn = field.getAnnotation(ExcelDateColumn.class);
        LocalDate date = (LocalDate) field.get(entity);
        
        if (date == null) return;
        
        // If alwaysSeparateValues is true, we'll always use the day/month/year approach
        if (dateColumn.alwaysSeparateValues()) {
            // First try with separate column names
            boolean writtenWithSeparateNames = tryWriteDateToSeparateColumnNames(row, date, columnMapping, dateColumn);
            
            // If that fails, try with offsets from a single header
            if (!writtenWithSeparateNames) {
                tryWriteDateToSingleHeaderWithOffsets(row, date, columnMapping, dateColumn);
            }
            return;
        }
        
        // Try to write to a single date column first
        Integer dateColumnIndex = getColumnIndex(columnMapping, dateColumn.dateColumnNames());
        if (dateColumnIndex != null) {
            Cell cell = getOrCreateCell(row, dateColumnIndex);
            String formattedDate = date.format(DateTimeFormatter.ofPattern(dateColumn.dateFormat()));
            setCellValue(cell, formattedDate, dateColumn.cellType());
            return;
        }
        
        // If single column not available, try day/month/year columns
        boolean writtenWithSeparateNames = tryWriteDateToSeparateColumnNames(row, date, columnMapping, dateColumn);
        
        // If that fails, try with offsets from a single header
        if (!writtenWithSeparateNames) {
            tryWriteDateToSingleHeaderWithOffsets(row, date, columnMapping, dateColumn);
        }
    }
    
    private static boolean tryWriteDateToSeparateColumnNames(Row row, LocalDate date, 
            Map<String, Integer> columnMapping, ExcelDateColumn dateColumn) {
        Integer dayColumnIndex = getColumnIndex(columnMapping, dateColumn.dayColumnNames());
        Integer monthColumnIndex = getColumnIndex(columnMapping, dateColumn.monthColumnNames());
        Integer yearColumnIndex = getColumnIndex(columnMapping, dateColumn.yearColumnNames());
        
        boolean hasWritten = false;
        
        if (dayColumnIndex != null) {
            Cell dayCell = getOrCreateCell(row, dayColumnIndex);
            setCellValue(dayCell, String.valueOf(date.getDayOfMonth()), dateColumn.cellType());
            hasWritten = true;
        }
        
        if (monthColumnIndex != null) {
            Cell monthCell = getOrCreateCell(row, monthColumnIndex);
            setCellValue(monthCell, String.valueOf(date.getMonthValue()), dateColumn.cellType());
            hasWritten = true;
        }
        
        if (yearColumnIndex != null) {
            Cell yearCell = getOrCreateCell(row, yearColumnIndex);
            setCellValue(yearCell, String.valueOf(date.getYear()), dateColumn.cellType());
            hasWritten = true;
        }
        
        return hasWritten && dayColumnIndex != null && monthColumnIndex != null && yearColumnIndex != null;
    }
    
    private static boolean tryWriteDateToSingleHeaderWithOffsets(Row row, LocalDate date, 
            Map<String, Integer> columnMapping, ExcelDateColumn dateColumn) {
        // Try to find the base column index from dateColumnNames
        Integer baseColumnIndex = getColumnIndex(columnMapping, dateColumn.dateColumnNames());
        
        if (baseColumnIndex != null) {
            int dayColumnIndex = baseColumnIndex + dateColumn.dayOffset();
            int monthColumnIndex = baseColumnIndex + dateColumn.monthOffset();
            int yearColumnIndex = baseColumnIndex + dateColumn.yearOffset();
            
            Cell dayCell = getOrCreateCell(row, dayColumnIndex);
            setCellValue(dayCell, String.valueOf(date.getDayOfMonth()), dateColumn.cellType());
            
            Cell monthCell = getOrCreateCell(row, monthColumnIndex);
            setCellValue(monthCell, String.valueOf(date.getMonthValue()), dateColumn.cellType());
            
            Cell yearCell = getOrCreateCell(row, yearColumnIndex);
            setCellValue(yearCell, String.valueOf(date.getYear()), dateColumn.cellType());
            
            return true;
        }
        return false;
    }

    private static <T> void mapExcelRelationColumnToField(Row row, T entity, Field field,
                                                          Map<String, Integer> columnMapping, int headerIndex, int depth) throws ReflectiveOperationException {
        ExcelRelationColumn relationColumn = field.getAnnotation(ExcelRelationColumn.class);
        String prefix = relationColumn.prefix();

        // Get the related entity type
        Class<?> relatedEntityType = field.getType();

        // Create a new instance of the related entity
        if (relationColumn.createIfNotFound()) {
            Object relatedEntity = relatedEntityType.getDeclaredConstructor().newInstance();
            Map<String, Integer> prefixedColumnMapping = createPrefixedColumnMapping(columnMapping, prefix);

            // Map fields of the related entity
            for (Field relatedField : relatedEntityType.getDeclaredFields()) {
                relatedField.setAccessible(true);
                mapFieldFromRow(row, relatedEntity, relatedField, prefixedColumnMapping, headerIndex, depth + 1);
            }

            // Set the related entity to the field
            field.set(entity, relatedEntity);
        }
    }

    private static <T> void writeExcelColumnFromField(T entity, Row row, Field field, Map<String, Integer> columnMapping)
            throws IllegalAccessException {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        Integer columnIndex = getColumnIndex(columnMapping, excelColumn.names());

        if (columnIndex != null) {
            Object value = field.get(entity);
            Cell cell = getOrCreateCell(row, columnIndex);
            setCellValue(cell, value, excelColumn.cellType());
        }
    }

    private static <T> void writeExcelMergedColumnFromField(T entity, Row row, Field field, Map<String, Integer> columnMapping)
            throws IllegalAccessException {
        ExcelMergedColumn mergedColumn = field.getAnnotation(ExcelMergedColumn.class);
        Integer columnIndex = getColumnIndex(columnMapping, mergedColumn.headerNames());

        if (columnIndex != null) {
            Object value = field.get(entity);
            int order = mergedColumn.order();
            Cell cell = getOrCreateCell(row, columnIndex + order);
            setCellValue(cell, value, mergedColumn.cellType());
        }
    }

    private static <T> void writeExcelRelationColumnFromField(T entity, Row row, Field field,
            Map<String, Integer> columnMapping, int headerIndex, int depth) throws ReflectiveOperationException {
        ExcelRelationColumn relationColumn = field.getAnnotation(ExcelRelationColumn.class);
        String prefix = relationColumn.prefix();
        
        Object relatedEntity = field.get(entity);
        if (relatedEntity == null) {
            return;
        }
        
        Class<?> relatedEntityType = relatedEntity.getClass();
        Map<String, Integer> prefixedColumnMapping = createPrefixedColumnMapping(columnMapping, prefix);
        
        // Write fields of the related entity
        for (Field relatedField : relatedEntityType.getDeclaredFields()) {
            relatedField.setAccessible(true);
            mapFieldToRow(relatedEntity, row, relatedField, prefixedColumnMapping, headerIndex, depth + 1);
        }
    }

    // Utility methods
    private static Map<String, Integer> createPrefixedColumnMapping(Map<String, Integer> columnMapping, String prefix) {
        Map<String, Integer> prefixedColumnMapping = new HashMap<>();
        for (Map.Entry<String, Integer> entry : columnMapping.entrySet()) {
            String key = entry.getKey();
            if (prefix.isEmpty() || key.startsWith(prefix)) {
                String newKey = prefix.isEmpty() ? key : key.substring(prefix.length());
                prefixedColumnMapping.put(newKey.trim(), entry.getValue());
            }
        }
        return prefixedColumnMapping;
    }

    private static Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    private static Integer getColumnIndex(Map<String, Integer> columnMapping, String[] columnNames) {
        for (String columnName : columnNames) {
            if (columnMapping.containsKey(columnName)) {
                return columnMapping.get(columnName);
            }
        }
        return null;
    }

    private static Map<String, Integer> getColumnMapping(Sheet sheet, int headerIndex) {
        Map<String, Integer> columnMapping = new HashMap<>();
        Row headerRow = sheet.getRow(headerIndex);

        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = getOrCreateCell(headerRow, i);
            columnMapping.put(cell.getStringCellValue().trim(), i);
        }
        return columnMapping;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> convertMyanmarToEnglishNumbers(cell.getStringCellValue());
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                String value = (numericValue == (long) numericValue)
                        ? String.valueOf((long) numericValue)  // Integer case
                        : String.valueOf(numericValue);       // Decimal case
                yield convertMyanmarToEnglishNumbers(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private static void setCellValue(Cell cell, Object value, CellType cellType) {
        if (value == null) return;

        if (value instanceof String strValue) {
            if (cellType.equals(CellType.NUMERIC) && strValue.matches("\\d+")) {
                var style = cell.getCellStyle();
                style.setAlignment(HorizontalAlignment.RIGHT);
                cell.setCellStyle(style);
                cell.setCellValue(Double.parseDouble(strValue));
                return;
            }
            cell.setCellValue(strValue);
        } else if (value instanceof Number numValue) {
            var style = cell.getCellStyle();
            style.setAlignment(HorizontalAlignment.RIGHT);
            cell.setCellStyle(style);
            cell.setCellValue(numValue.doubleValue());
        } else if (value instanceof Boolean boolValue) {
            cell.setCellValue(boolValue);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    // String utility methods
    private static String convertMyanmarToEnglishNumbers(String input) {
        return trimSpaces(input)
                .replace("၀", "0")
                .replace("၁", "1")
                .replace("၂", "2")
                .replace("၃", "3")
                .replace("၄", "4")
                .replace("၅", "5")
                .replace("၆", "6")
                .replace("၇", "7")
                .replace("၈", "8")
                .replace("၉", "9");
    }

    private static String trimSpaces(String input) {
        if (input == null) return "";
        return input.trim();
    }
}