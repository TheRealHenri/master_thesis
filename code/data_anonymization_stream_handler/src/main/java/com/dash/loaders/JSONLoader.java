package com.dash.loaders;

import com.dash.configs.SystemConfiguration;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JSONLoader {

    private static final String JSON_FILE_PATH = "/tmp/default_dash_config.json";
    //private static final String JSON_FILE_PATH = "/Users/allgower/Uni/TUB/MA/master_thesis/code/dash_config/default_dash_config.json";
    private static SystemConfiguration cachedConfig;
    private static String lastChecksum;
    private final static Logger log = LoggerFactory.getLogger(JSONLoader.class);

    public static SystemConfiguration loadConfig() {
        String currentChecksum = calculateChecksum();

        if (cachedConfig != null && currentChecksum.equals(lastChecksum)) {
            log.info("No changes to configuration detected. Using cached configuration.");
            return cachedConfig;
        }

        log.info("Loading new configuration.");

        SystemConfiguration newConfig = parseJson();

        log.info("New configuration loaded successfully!");

        cachedConfig = newConfig;
        lastChecksum = currentChecksum;

        return newConfig;
    }

    private static String calculateChecksum() {
        String checksum = null;
        try {
            byte[] data = Files.readAllBytes(Paths.get(JSON_FILE_PATH));
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            checksum = new BigInteger(1, hash).toString(16);
        } catch (IOException e) {
            log.error("Error reading configuration file {}. File might not exist.", JSON_FILE_PATH, e.fillInStackTrace());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return checksum;
    }

    private static SystemConfiguration parseJson() {
        SystemConfiguration config;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            config = objectMapper.readValue(new File(JSON_FILE_PATH), SystemConfiguration.class);
        } catch (JsonMappingException e){
            log.error("Error parsing configuration file {}. File is not structured correctly.", JSON_FILE_PATH, e.fillInStackTrace());
            log.error("Expected JSON structure: {}", getExpectedJSONStructure());
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            log.error("Error reading configuration file {}. File might not exist.", JSON_FILE_PATH, e.fillInStackTrace());
            throw new IllegalArgumentException(e);
        }
        return config;
    }

    private static String getExpectedJSONStructure() {
        return "{\n" +
               "    \"globalConfig\": {\n" +
               "        \"bootstrapServer\": \"STRING\",\n" +
               "        \"topic\": \"STRING\",\n" +
               "        \"dataSchema\": \"STRING\"\n" +
               "    },\n" +
               "    \"streamProperties\": [\n" +
               "        {\n" +
               "            \"applicationId\": \"STRING\",\n" +
               "            \"category\": \"ENUM (VALUE_BASED, TUPLE_BASED, ATTRIBUTE_BASED, TABLE_BASED)\",\n" +
               "            \"anonymizers\": [\n" +
               "                {\n" +
               "                    \"anonymizer\": \"STRING (suppression, substitution, etc.)\",\n" +
               "                    \"parameters\": [\n" +
               "                        {\n" +
               "                            \"keys\": [\n" +
               "                                 \"key\": \"STRING\",\n" +
               "                                 // ... other keys\n" +
                "                            ],\n" +
               "                            // Other parameters like: \"buckets\", \"windowSize\", \"k\", \"l\"\n" +
               "                        }\n" +
               "                    ]\n" +
               "                },\n" +
               "                // ... other anonymizers\n" +
               "            ]\n" +
               "        },\n" +
               "        // ... other stream configs\n" +
               "    ]\n" +
               "}\n";
    }

    private JSONLoader() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
