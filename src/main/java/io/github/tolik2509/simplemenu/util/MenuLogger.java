package io.github.tolik2509.simplemenu.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public final class MenuLogger {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("simplemenu_messages", Locale.getDefault());
    private static Logger pluginLogger;

    public MenuLogger() {
    }

    public static void init(Logger logger) {
        pluginLogger = logger;
    }

    public static void warning(String key, Object... args) {
        String pattern = bundle.getString(key);
        String message = MessageFormat.format(pattern, args);
        if (pluginLogger != null) {
            pluginLogger.warning(message);
        }

    }

    public static void info(String key, Object... args) {
        if (pluginLogger != null) {
            String pattern = bundle.getString(key);
            String finalMessage = MessageFormat.format(pattern, args);
            pluginLogger.info(finalMessage);
        }
    }
}