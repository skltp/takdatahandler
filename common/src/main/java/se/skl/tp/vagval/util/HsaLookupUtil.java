package se.skl.tp.vagval.util;

import se.skl.tp.HsaLookupConfiguration;

public class HsaLookupUtil {

    private HsaLookupUtil() {
        //  Static utility
    }

    public static boolean isHsaLookupEnabled(Object hsaCache, HsaLookupConfiguration configuration, String namespace) {
        if (hsaCache == null) return false;
        boolean defaultSetting = configuration.getDefaultEnabled();
        for(String exceptedNamespace : configuration.getExceptedNamespaces()) {
            if (exceptedNamespace == null || exceptedNamespace.isBlank()) continue;
            if (namespace.startsWith(exceptedNamespace)) return !defaultSetting;
        }
        return defaultSetting;
    }
}
