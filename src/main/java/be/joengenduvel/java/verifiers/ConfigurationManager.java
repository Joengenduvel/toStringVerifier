package be.joengenduvel.java.verifiers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationManager {
    private static final String CONFIG_FILE_NAME = "/be.joengenduvel.tostring.properties";
    private static final String FIELDS_TO_ALWAYS_IGNORE_KEY = "fieldToIgnore.Always";
    private static ConfigurationManager configurationManager;
    private final Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
    }

    static synchronized ConfigurationManager getInstance() {
        if (configurationManager == null) {
            configurationManager = new ConfigurationManager();
        }
        return configurationManager;
    }

    private void loadProperties() throws IOException {
        InputStream resourceAsStream = ConfigurationManager.class.getResourceAsStream(CONFIG_FILE_NAME);
        if (resourceAsStream != null && resourceAsStream.available() > 0) {
            properties.load(resourceAsStream);
        } else {
            System.err.println(String.format("No configuration file found with name %s", CONFIG_FILE_NAME));
        }
    }

    public List<String> getFieldsToIgnore() {
        List<String> fieldsToIgnore = new ArrayList<>();
        try {
            loadProperties();
            String propertyValue = properties.getProperty(FIELDS_TO_ALWAYS_IGNORE_KEY);
            if (propertyValue != null) {
                String[] splittedValue = propertyValue.split(",");
                fieldsToIgnore.addAll(Arrays.asList(splittedValue));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fieldsToIgnore;
    }
}
