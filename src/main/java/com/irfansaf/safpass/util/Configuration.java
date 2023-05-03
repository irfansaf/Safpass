package com.irfansaf.safpass.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
    public static final String PURCHASE_CODE_KEY = "purchaseCode";
    private static Configuration instance;
    private Properties properties = new Properties();

    private Configuration() {
        try {
            File filePath = new File(getConfigurationFolderPath(), "safpass.properties");
            if (filePath.exists() && filePath.isFile()) {
                InputStream is = new FileInputStream(filePath);
                properties.load(is);
                is.close();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "An error occurred during loading configuration.", e);
        }
    }

    private File getConfigurationFolderPath() {
        File configurationFolderPath = null;
        try {
            configurationFolderPath = new File(Configuration.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).getParentFile();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not determine configuration folder path.", e);
        }
        return configurationFolderPath;
    }

    private <T> T getValue(String key, T defaultValue, Class<T> type) {
        T value = defaultValue;
        String prop = properties.getProperty(key);
        if (prop != null) {
            try {
                value = type.getConstructor(String.class).newInstance(prop);
            } catch (Exception e) {
                LOG.log(Level.WARNING, String.format("Could not parse value as [%s] for key [%s]", type.getName(), key));
            }
        }
        return value;
    }

    public void savePurchaseCode(String purchaseCode) throws BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(Configuration.class);
        prefs.put(PURCHASE_CODE_KEY, purchaseCode);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {

        }
    }

    public Boolean hasSavedPurchaseCode() {
        Preferences prefs = Preferences.userNodeForPackage(Configuration.class);
        String savedPurchaseCode = prefs.get(PURCHASE_CODE_KEY, null);
        return savedPurchaseCode != null;
    }

    public Boolean is(String key, Boolean defaultValue) {
        return getValue(key, defaultValue, Boolean.class);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return getValue(key, defaultValue, Integer.class);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String[] getArray(String key, String[] defaultValue) {
        String prop = properties.getProperty(key);
        if (prop != null) {
            return prop.split(",");
        }
        return defaultValue;
    }

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
}
