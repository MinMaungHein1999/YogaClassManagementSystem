package com.yogiBooking.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class CommonExcelUtils {

    public static Optional<Workbook> getYogiTemplateWorkbook() {
        String filePath = "templates/yogi_register_form_v4.6.xlsx";
        InputStream inputStream = FileUtils.getResourceAsStream(filePath);

        if (inputStream == null) {
            log.error("Template file not found: {}", filePath);
            return Optional.empty();
        }

        try {
            return Optional.of(new XSSFWorkbook(inputStream));
        } catch (IOException e) {
            log.error("Failed to load Excel template: {}", filePath, e);
            return Optional.empty();
        }

    }
}
