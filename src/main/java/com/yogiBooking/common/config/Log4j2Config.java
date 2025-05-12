package com.yogiBooking.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.logging.log4j.Level.*;

@Component
@Slf4j
public class Log4j2Config {

    @Value("${environment:local}")
    private String environment;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String LOCAL = "local";

    @Bean
    public Logger log4j2Logger() {
        String logDir = getLogDirectory();

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        // Create Pattern Layout
        PatternLayout patternLayout = createPatternLayout();

        // Create and register appenders
        RollingFileAppender rollingFileAppender = createRollingFileAppender(logDir, patternLayout);
        rollingFileAppender.start(); // Explicitly start the RollingFileAppender

        ConsoleAppender consoleAppender = createConsoleAppender(patternLayout);
        consoleAppender.start(); // Explicitly start the ConsoleAppender

        config.addAppender(rollingFileAppender);
        config.addAppender(consoleAppender);

        // Configure loggers
        configureLogger(config, "org.springframework", WARN, rollingFileAppender);
        configureLogger(config, "org.springframework.boot", WARN, rollingFileAppender);
        configureLogger(config, "org.hibernate", ERROR, rollingFileAppender);
        configureLogger(config, "com.yogiBooking", DEBUG, rollingFileAppender);
        configureLogger(config, LogManager.ROOT_LOGGER_NAME, INFO, rollingFileAppender);

        // Apply changes and ensure the context is started
        context.updateLoggers();
        context.start();

        return LogManager.getLogger(Log4j2Config.class);
    }

    public String getLogDirectory() {
        String logDirectoryPath = resolveLogPath();
        try {
            Files.createDirectories(Paths.get(logDirectoryPath));
        } catch (IOException e) {
            log.error("Failed to create log directory: {}", e.getMessage());
            logDirectoryPath = "logs";
            new File(logDirectoryPath).mkdirs();
        }
        return logDirectoryPath;
    }

    private String resolveLogPath() {
        if (LOCAL.equals(environment)) {
            // For local environment, save in user's Documents folder
            return Paths.get(USER_HOME, "Documents", applicationName, "logs").toString();
        } else {
            // For non-local environments, save in server's logs folder
            return "/var/log/" + applicationName;
        }
    }

    private PatternLayout createPatternLayout() {
        String pattern = LOCAL.equals(environment)
                ? "%d{yyyy-MM-dd'T'HH:mm:ss.SSSZ} %highlight{%-5level} %pid --- [%t] [%logger] : %msg%n"
                : "%d{yyyy-MM-dd'T'HH:mm:ss.SSSZ} %-5level %pid --- [%t] [%logger] : %msg%n";
        
        return PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();
    }

    private RollingFileAppender createRollingFileAppender(String logDir, PatternLayout layout) {
        return RollingFileAppender.newBuilder()
                .setName("RollingFileAppender")
                .withFileName(logDir + "/app.log")
                .withFilePattern(logDir + "/app-%d{yyyy-MM-dd}.%i.log.gz")
                .setLayout(layout)
                .withPolicy(CompositeTriggeringPolicy.createPolicy(
                        SizeBasedTriggeringPolicy.createPolicy("10MB"),
                        TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build()
                ))
                .withStrategy(DefaultRolloverStrategy.newBuilder().withMax("30").build()) // Keep 30 days of logs
                .build();
    }

    private ConsoleAppender createConsoleAppender(PatternLayout layout) {
        return ConsoleAppender.newBuilder()
                .setName("ConsoleAppender")
                .setLayout(layout)
                .build();
    }

    private void configureLogger(Configuration config, String loggerName, Level level, RollingFileAppender appender) {
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        if (!loggerConfig.getName().equals(loggerName)) {
            loggerConfig = new LoggerConfig(loggerName, level, true);
            config.addLogger(loggerName, loggerConfig);
        }
        loggerConfig.setLevel(level);
        loggerConfig.addAppender(appender, null, null);
    }
}
