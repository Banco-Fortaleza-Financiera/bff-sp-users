package com.bancofortaleza.users.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AppLoggerTest {

    @AfterEach
    void clearMdc() {
        AppLogger.clearContext();
    }

    @Test
    void putContextShouldStoreOnlyPresentValues() {
        AppLogger.putContext(AppLogger.REQUEST_ID, "req-123");
        AppLogger.putContext(AppLogger.METHOD, "");
        AppLogger.putContext(AppLogger.PATH, null);

        assertThat(AppLogger.getContext(AppLogger.REQUEST_ID)).isEqualTo("req-123");
        assertThat(AppLogger.getContext(AppLogger.METHOD)).isNull();
        assertThat(AppLogger.getContext(AppLogger.PATH)).isNull();
    }

    @Test
    void putContextShouldStoreMapEntriesAndRemoveThem() {
        AppLogger.putContext(Map.of(
                AppLogger.REQUEST_ID, "req-123",
                AppLogger.DEVICE_IP, "127.0.0.1"));

        assertThat(AppLogger.getContext(AppLogger.DEVICE_IP)).isEqualTo("127.0.0.1");

        AppLogger.removeContext(AppLogger.DEVICE_IP);

        assertThat(AppLogger.getContext(AppLogger.DEVICE_IP)).isNull();
    }

    @Test
    void loggingMethodsShouldBeCallable() {
        RuntimeException exception = new RuntimeException("boom");

        AppLogger.trace(AppLoggerTest.class, "trace {}", "value");
        AppLogger.debug(AppLoggerTest.class, "debug {}", "value");
        AppLogger.info(AppLoggerTest.class, "info {}", "value");
        AppLogger.warn(AppLoggerTest.class, "warn {}", "value");
        AppLogger.warn(AppLoggerTest.class, "warn", exception);
        AppLogger.error(AppLoggerTest.class, "error {}", "value");
        AppLogger.error(AppLoggerTest.class, "error", exception);
    }

    @Test
    void requestLoggingMethodsShouldBeCallable() {
        AppLogger.requestStarted(AppLoggerTest.class, "GET", "/users");
        AppLogger.requestCompleted(AppLoggerTest.class, "GET", "/users", 200, 15L);
        AppLogger.requestFailed(AppLoggerTest.class, "GET", "/users", 15L, new RuntimeException("boom"));
    }
}
