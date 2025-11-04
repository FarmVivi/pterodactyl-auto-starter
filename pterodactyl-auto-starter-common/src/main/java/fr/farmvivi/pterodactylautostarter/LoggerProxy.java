package fr.farmvivi.pterodactylautostarter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerProxy extends java.util.logging.Logger {
    private final java.util.logging.Logger logger;

    public LoggerProxy(java.util.logging.Logger logger) {
        super(logger.getName(), logger.getResourceBundleName());
        this.logger = logger;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return logger.getResourceBundle();
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        logger.setResourceBundle(bundle);
    }

    @Override
    public String getResourceBundleName() {
        return logger.getResourceBundleName();
    }

    @Override
    public Filter getFilter() {
        return logger.getFilter();
    }

    @Override
    public void setFilter(Filter newFilter) throws SecurityException {
        logger.setFilter(newFilter);
    }

    @Override
    public void log(LogRecord record) {
        logger.log(record);
    }

    @Override
    public void log(Level level, String msg) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg);
        }
    }

    public void log(Level level, Component component) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void log(Level level, Supplier<String> msgSupplier) {
        if (logger.isLoggable(level)) {
            logger.log(level, msgSupplier);
        }
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg, param1);
        }
    }

    public void log(Level level, Component component, Object param1) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertComponentToLogacyComponent(component), param1);
        }
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg, params);
        }
    }

    public void log(Level level, Component component, Object[] params) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertComponentToLogacyComponent(component), params);
        }
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg, thrown);
        }
    }

    public void log(Level level, Component component, Throwable thrown) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertComponentToLogacyComponent(component), thrown);
        }
    }

    @Override
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        if (logger.isLoggable(level)) {
            logger.log(level, thrown, msgSupplier);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        logger.logp(level, sourceClass, sourceMethod, msg);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, Component component) {
        logger.logp(level, sourceClass, sourceMethod, convertComponentToLogacyComponent(component));
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
        logger.logp(level, sourceClass, sourceMethod, msgSupplier);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        logger.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, Component component, Object param1) {
        logger.logp(level, sourceClass, sourceMethod, convertComponentToLogacyComponent(component), param1);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        logger.logp(level, sourceClass, sourceMethod, msg, params);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, Component component, Object[] params) {
        logger.logp(level, sourceClass, sourceMethod, convertComponentToLogacyComponent(component), params);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        logger.logp(level, sourceClass, sourceMethod, msg, thrown);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, Component component, Throwable thrown) {
        logger.logp(level, sourceClass, sourceMethod, convertComponentToLogacyComponent(component), thrown);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
        if (logger.isLoggable(level)) {
            logger.logp(level, sourceClass, sourceMethod, thrown, msgSupplier);
        }
    }

    @Override
    public void entering(String sourceClass, String sourceMethod) {
        logger.entering(sourceClass, sourceMethod);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object param1) {
        logger.entering(sourceClass, sourceMethod, param1);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        logger.entering(sourceClass, sourceMethod, params);
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod) {
        logger.exiting(sourceClass, sourceMethod);
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod, Object result) {
        logger.exiting(sourceClass, sourceMethod, result);
    }

    @Override
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        logger.throwing(sourceClass, sourceMethod, thrown);
    }

    @Override
    public void severe(String msg) {
        logger.severe(msg);
    }

    public void severe(Component component) {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void warning(String msg) {
        logger.warning(msg);
    }

    public void warning(Component component) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.warning(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    public void info(Component component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void config(String msg) {
        logger.config(msg);
    }

    public void config(Component component) {
        if (logger.isLoggable(Level.CONFIG)) {
            logger.config(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void fine(String msg) {
        logger.fine(msg);
    }

    public void fine(Component component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void finer(String msg) {
        logger.finer(msg);
    }

    public void finer(Component component) {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void finest(String msg) {
        logger.finest(msg);
    }

    public void finest(Component component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(convertComponentToLogacyComponent(component));
        }
    }

    @Override
    public void severe(Supplier<String> msgSupplier) {
        logger.severe(msgSupplier);
    }

    @Override
    public void warning(Supplier<String> msgSupplier) {
        logger.warning(msgSupplier);
    }

    @Override
    public void info(Supplier<String> msgSupplier) {
        logger.info(msgSupplier);
    }

    @Override
    public void config(Supplier<String> msgSupplier) {
        logger.config(msgSupplier);
    }

    @Override
    public void fine(Supplier<String> msgSupplier) {
        logger.fine(msgSupplier);
    }

    @Override
    public void finer(Supplier<String> msgSupplier) {
        logger.finer(msgSupplier);
    }

    @Override
    public void finest(Supplier<String> msgSupplier) {
        logger.finest(msgSupplier);
    }

    @Override
    public Level getLevel() {
        return logger.getLevel();
    }

    @Override
    public void setLevel(Level newLevel) throws SecurityException {
        logger.setLevel(newLevel);
    }

    @Override
    public boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void addHandler(Handler handler) throws SecurityException {
        logger.addHandler(handler);
    }

    @Override
    public void removeHandler(Handler handler) throws SecurityException {
        logger.removeHandler(handler);
    }

    @Override
    public Handler[] getHandlers() {
        return logger.getHandlers();
    }

    @Override
    public boolean getUseParentHandlers() {
        return logger.getUseParentHandlers();
    }

    @Override
    public void setUseParentHandlers(boolean useParentHandlers) {
        logger.setUseParentHandlers(useParentHandlers);
    }

    @Override
    public java.util.logging.Logger getParent() {
        return logger.getParent();
    }

    @Override
    public void setParent(java.util.logging.Logger parent) {
        logger.setParent(parent);
    }

    public String convertComponentToLogacyComponent(Component component) {
        return ANSIComponentSerializer.ansi().serialize(component);
    }

    @Override
    public int hashCode() {
        return logger.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return logger.equals(obj);
    }

    @Override
    public String toString() {
        return logger.toString();
    }
}
