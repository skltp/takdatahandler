package se.skl.tp.vagval.util;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import se.skl.tp.HsaLookupConfiguration;
import se.skl.tp.HsaLookupConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HsaLookupUtilTest {

    HsaCache hsaCache = new HsaCacheImpl();

    @Test
    void hsaCacheNullShouldGiveFalse() {
    assertFalse(
        HsaLookupUtil.isHsaLookupEnabled(null, new HsaLookupConfigurationImpl(), "a:b:c"));
    }

    @Test
    void defaultDisabledNoExceptionsShouldGiveFalse() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setDefaultEnabled(false);
        assertFalse(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    void defaultDisabledWithMatchingExceptionShouldGiveTrue() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setDefaultEnabled(false);
        configuration.setExceptedNamespaces(List.of("a:b:c"));
        assertTrue(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    void defaultEnabledWithStartMatchingExceptionShouldGiveFalse() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setExceptedNamespaces(Arrays.asList("x:y:z", "a:b"));
        assertFalse(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    void defaultEnabledWithNoMatchingExceptionShouldGiveTrue() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setExceptedNamespaces(Arrays.asList("", "a:b:c:d", null));
        assertTrue(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }
}
