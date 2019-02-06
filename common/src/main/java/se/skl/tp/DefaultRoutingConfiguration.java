package se.skl.tp;

import java.util.List;

public interface DefaultRoutingConfiguration {
    String getDelimiter();

    void setDelimiter(String oldStyleDefaultRoutingAddressDelimiter);

    List<String> getAllowedContracts();

    void setAllowedContracts(List<String> defaultRoutingAllowedContracts);

    List<String> getAllowedSenderIds();

    void setAllowedSenderIds(List<String> defaultRoutingAllowedSenderIds);
}
