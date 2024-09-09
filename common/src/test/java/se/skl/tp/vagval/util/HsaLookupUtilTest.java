package se.skl.tp.vagval.util;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import se.skl.tp.HsaLookupConfiguration;
import se.skl.tp.HsaLookupConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;

public class HsaLookupUtilTest {

    HsaCache hsaCache = new HsaCacheImpl();

    @Test
    public void hsaCacheNullShouldGiveFalse() {
    Assert.assertFalse(
        HsaLookupUtil.isHsaLookupEnabled(null, new HsaLookupConfigurationImpl(), "a:b:c"));
    }

    @Test
    public void defaultDisabledNoExceptionsShouldGiveFalse() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setDefaultEnabled(false);
        Assert.assertFalse(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    public void defaultDisabledWithMatchingExceptionShouldGiveTrue() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setDefaultEnabled(false);
        configuration.setExceptedNamespaces(Arrays.asList("a:b:c"));
        Assert.assertTrue(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    public void defaultEnabledWithStartMatchingExceptionShouldGiveFalse() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setExceptedNamespaces(Arrays.asList("x:y:z", "a:b"));
        Assert.assertFalse(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }

    @Test
    public void defaultEnabledWithNoMatchingExceptionShouldGiveTrue() {
        HsaLookupConfiguration configuration = new HsaLookupConfigurationImpl();
        configuration.setExceptedNamespaces(Arrays.asList("", "a:b:c:d", null));
        Assert.assertTrue(
                HsaLookupUtil.isHsaLookupEnabled(hsaCache, configuration, "a:b:c"));
    }
}
