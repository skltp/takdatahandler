package se.skl.tp;

import java.util.ArrayList;
import java.util.List;

public class HsaLookupConfigurationImpl implements HsaLookupConfiguration {

    boolean defaultEnabled = true;
    List<String> exceptedNamespaces = new ArrayList<>();

    @Override
    public boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public void setDefaultEnabled(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    @Override
    public List<String> getExceptedNamespaces() {
        return exceptedNamespaces;
    }

    @Override
    public void setExceptedNamespaces(List<String> exceptedNamespaces) {
        this.exceptedNamespaces = exceptedNamespaces;
    }
}
