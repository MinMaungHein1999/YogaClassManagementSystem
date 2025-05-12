package com.yogiBooking.common.service.initializer;

import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.entity.constants.Status;
import com.yogiBooking.common.repository.*;
import com.yogiBooking.common.utils.FileUtils;
import com.yogiBooking.common.repository.ServiceCategoryRepository;
import com.yogiBooking.common.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
public class CommonInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final RegionRepository regionRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final TransactionTemplate transactionTemplate;

    public CommonInitializer(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager, CountryRepository countryRepository,
                             RegionRepository regionRepository, ServiceCategoryRepository serviceCategoryRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.regionRepository = regionRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    public void initializeData() {
        initializeEntity("countries", this::parseCountries);
        initializeEntity("regions", this::parseRegion);
        initializeEntity("cities", this::parseCity);
        initializeEntity("nrc-codes", this::parseNrcCode);
        initializeEntity("levels", this::parseLevel);
        initializeEntity("service-categories", this::parseServiceCategory);
        initializeEntity("yoga-teaching-services", this::parseYogaTeachingService);
    }

    protected <T> void initializeEntity(String entityName, Function<String, T> parseFunction) {
        transactionTemplate.execute(status -> {
            List<T> entities = readCsvFile(entityName + ".csv", parseFunction);
            if (entities.isEmpty()) {
                log.warn("No entities found to initialize from {}.", entityName);
                return null;
            }

            String tableName = switch (entities.getFirst().getClass().getSimpleName()) {
                case "Region" -> "regions";
                case "City" -> "cities";
                case "Country" -> "countries";
                case "NrcCode" -> "nrc_codes";
                case "Level" -> "levels";
                case "ServiceCategory" -> "service_categories";
                case "YogaTeachingService" -> "yoga_teaching_services";
                default -> throw new IllegalArgumentException("Unknown entity class: " + entities.getFirst().getClass().getSimpleName());
            };

            // Retrieve existing IDs
            List<Long> existingIds = jdbcTemplate.queryForList("SELECT id FROM %s".formatted(tableName), Long.class);

            // Filter new entities
            entities = entities.stream()
                    .filter(e -> {
                        if (e instanceof MasterData data) {
                            return !existingIds.contains(data.getId());
                        }
                        return false;
                    }).toList();

            if (entities.isEmpty()) {
                log.info("No new entities to insert for {}", entityName);
                updateSequence(tableName);
                return null;
            }

            int count = 0;
            List<Object[]> batchArgs = new ArrayList<>();
            String sql = "";

            for (T entity : entities) {
                try {
                    if (entity instanceof Region region) {
                        sql = "INSERT INTO regions (id, name_en, name_mm, code, created_at) VALUES (?, ?, ?, ?, ?)";
                        batchArgs.add(new Object[]{
                                region.getId(), region.getNameEn(), region.getNameMm(), region.getCode(), LocalDateTime.now()
                        });
                    } else if (entity instanceof NrcCode nrcCode) {
                        sql = "INSERT INTO nrc_codes (id, details, name_mm, name_en, prefix_code, created_at) VALUES (?, ?, ?, ?, ?, ?)";
                        batchArgs.add(new Object[]{
                                nrcCode.getId(), nrcCode.getDetails(), nrcCode.getNameMm(), nrcCode.getNameEn(),
                                nrcCode.getPrefixCode(), LocalDateTime.now()
                        });
                    } else if (entity instanceof Level level) {
                        sql = "INSERT INTO levels (id, name, created_at) VALUES (?, ?, ?)";
                        batchArgs.add(new Object[]{
                                level.getId(), level.getName(), LocalDateTime.now()
                        });
                    } else if (entity instanceof ServiceCategory category) {
                        sql = "INSERT INTO service_categories (id, name, status, created_at) VALUES (?, ?, ?, ?)";
                        batchArgs.add(new Object[]{
                                category.getId(), category.getName(), 1, LocalDateTime.now()
                        });
                    } else if (entity instanceof City city) {
                        sql = "INSERT INTO cities (id, name_mm, region_id, created_at) VALUES (?, ?, ?, ?)";
                        batchArgs.add(new Object[]{
                                city.getId(), city.getNameMm(), city.getRegion().getId(), LocalDateTime.now()
                        });
                    } else if (entity instanceof YogaTeachingService yogaTeachingService) {
                        sql = "INSERT INTO yoga_teaching_services (id, name, service_category_id, code, created_at) VALUES (?, ?, ?, ?, ?)";
                        batchArgs.add(new Object[]{
                                yogaTeachingService.getId(), yogaTeachingService.getName(), yogaTeachingService.getServiceCategory().getId(),
                                yogaTeachingService.getCode(), LocalDateTime.now()
                        });
                    } else if (entity instanceof Country country) {
                        sql = "INSERT INTO countries (name, code) VALUES (?, ?)";
                        batchArgs.add(new Object[]{
                                country.getName(),
                                country.getCode() });
                    }
                } catch (Exception e) {
                    log.error("Error preparing entity for batch insert: {}", e.getMessage(), e);
                }
            }

            // Execute batch insert
            if (!batchArgs.isEmpty()) {
                try {
                    int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);
                    for (int result : results) {
                        if (result > 0) {
                            count++;
                        }
                    }

                    // Update the sequence to the maximum ID + 1
                    updateSequence(tableName);

                } catch (DataAccessException e) {
                    status.setRollbackOnly(); // Rollback transaction on failure
                    log.error("Batch insert failed for {}: {}", entityName, e.getMessage(), e);
                }
            }
            log.info("Successfully initialized {} {} entities.", count, entityName);
            return null;
        });
    }

    // Method to update the sequence for a given table
    private void updateSequence(String tableName) {
        try {
            // Query to find the maximum ID in the table
            String maxIdQuery = "SELECT COALESCE(MAX(id), 0) FROM %s".formatted(tableName);
            Long maxId = jdbcTemplate.queryForObject(maxIdQuery, Long.class);

            // Assume the sequence name follows the convention: <table_name>_id_seq
            String sequenceName = tableName + "_id_seq";

            // Set the sequence to max(id) + 1
            String setvalQuery = "SELECT setval('%s', %d, true)".formatted(sequenceName, maxId + 1);
            jdbcTemplate.execute(setvalQuery);

            log.info("Updated sequence {} to next value: {}", sequenceName, maxId + 1);
        } catch (Exception e) {
            log.error("Failed to update sequence for table {}: {}", tableName, e.getMessage(), e);
        }
    }

    private <T> List<T> readCsvFile(String fileName, Function<String, T> parseFunction) {
        InputStream inputStream = FileUtils.getResourceAsStream("initial-data/" + fileName);

        if (inputStream == null) {
            log.error("File not found: initial-data/{}", fileName);
            return Collections.emptyList();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .skip(1) // Skip header
                    .map(parseFunction)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to read CSV file: initial-data/{}", fileName, e);
            return Collections.emptyList();
        }
    }

    private Level parseLevel(String line) {
        String[] data = line.split(",");
        Level level = new Level();
        level.setId(Long.parseLong(data[0].trim()));
        level.setName(data[1]);
        return level;
    }

    private ServiceCategory parseServiceCategory(String line) {
        String[] data = line.split(",", -1);
        ServiceCategory serviceCategory = new ServiceCategory();
        serviceCategory.setId(Long.parseLong(data[0]));
        serviceCategory.setName(data[1]);
        serviceCategory.setStatus(Status.ACTIVE);

        return serviceCategory;
    }

    private YogaTeachingService parseYogaTeachingService(String line) {
        String[] data = line.split(",");
        ServiceCategory serviceCategory = this.serviceCategoryRepository.findById(Long.parseLong(data[2])).orElseThrow();
        YogaTeachingService yogaTeachingService = new YogaTeachingService();
        yogaTeachingService.setId(Long.parseLong(data[0]));
        yogaTeachingService.setName(data[1]);
        yogaTeachingService.setServiceCategory(serviceCategory);
        yogaTeachingService.setCode(data[3]);
        return yogaTeachingService;
    }

    private NrcCode parseNrcCode(String line) {
        String[] data = line.split(",");
        NrcCode nrcCode = new NrcCode();
        nrcCode.setId(Long.parseLong(data[0].trim()));
        nrcCode.setDetails(data[1].trim());
        nrcCode.setNameMm(data[2].trim());
        nrcCode.setNameEn(data[3].trim());
        nrcCode.setPrefixCode(Integer.parseInt(data[4]));
        return nrcCode;
    }

    private City parseCity(String line) {
        String[] data = line.split(",");
        City city = new City();
        Region region = this.regionRepository.findById(Long.parseLong(data[3])).orElseThrow();
        city.setId(Long.parseLong(data[0].trim()));
        city.setNameMm(data[1]);
        city.setRegion(region);
        return city;
    }

    private Region parseRegion(String line) {
        String[] data = line.split(",");
        Region region = new Region();
        region.setId(Long.parseLong(data[0].trim()));
        region.setNameEn(data[1].trim());
        region.setNameMm(data[2].trim());
        region.setCode(data[3].trim());
        return region;
    }

    private Country parseCountries(String line) {
        String[] data = line.split(",");
        Country country = new Country();
        country.setName(data[0].trim());
        country.setCode(data[1].trim());
        return country;
    }
}