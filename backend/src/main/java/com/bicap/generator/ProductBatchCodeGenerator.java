package com.bicap.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProductBatchCodeGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * Generate Product Batch Code.
     *
     * Format:
     * PB-YYYYMMDD-000001
     *
     * Example:
     * PB-20260808-000001
     */
    public String generate(Long batchId) {

        String date =
                LocalDate.now().format(DATE_FORMAT);

        return String.format(
                "PB-%s-%06d",
                date,
                batchId
        );
    }

}