package com.yogiBooking.common.utils;

import lombok.extern.slf4j.Slf4j;
import java.io.*;

@Slf4j
public class FileUtils {
    /**
     * Get InputStream from classpath resource.
     * @param filename The file path inside `resources/`
     * @return InputStream of the file
     */
    public static InputStream getResourceAsStream(String filename) {
        return FileUtils.class.getClassLoader().getResourceAsStream(filename);
    }

}
