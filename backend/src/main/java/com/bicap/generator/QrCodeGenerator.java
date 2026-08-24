package com.bicap.generator;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QrCodeGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }

}