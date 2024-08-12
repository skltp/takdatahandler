package se.skl.tp;

import java.util.List;

public interface HsaLookupConfiguration {
    boolean isDefaultEnabled();

    void setDefaultEnabled(boolean defaultEnabled);

    List<String> getExceptedNamespaces();

    void setExceptedNamespaces(List<String> exceptedNamespaces);
}
