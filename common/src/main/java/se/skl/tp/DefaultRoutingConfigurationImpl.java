package se.skl.tp;

import java.util.Arrays;
import java.util.List;

public class DefaultRoutingConfigurationImpl implements DefaultRoutingConfiguration {

  protected String oldStyleDefaultRoutingAddressDelimiter;
  protected List<String> defaultRoutingAllowedContracts;
  protected List<String> defaultRoutingAllowedSenderIds;

  @Override
  public String getDelimiter() {
    return oldStyleDefaultRoutingAddressDelimiter;
  }

  @Override
  public void setDelimiter(String oldStyleDefaultRoutingAddressDelimiter) {
    this.oldStyleDefaultRoutingAddressDelimiter = oldStyleDefaultRoutingAddressDelimiter;
  }

  @Override
  public List<String> getAllowedContracts() {
    return defaultRoutingAllowedContracts;
  }

  @Override
  public void setAllowedContracts(List<String> defaultRoutingAllowedContracts) {
    this.defaultRoutingAllowedContracts = defaultRoutingAllowedContracts;
  }

  @Override
  public List<String> getAllowedSenderIds() {
    return defaultRoutingAllowedSenderIds;
  }

  @Override
  public void setAllowedSenderIds(List<String> defaultRoutingAllowedSenderIds) {
    this.defaultRoutingAllowedSenderIds = defaultRoutingAllowedSenderIds;
  }

  public void setAllowedContractsAsString(String defaultRoutingAllowedContracts) {
    if(defaultRoutingAllowedContracts==null || defaultRoutingAllowedContracts.isEmpty()){
      this.defaultRoutingAllowedContracts = null;
    }else{
      this.defaultRoutingAllowedContracts = Arrays.asList(defaultRoutingAllowedContracts.split(","));
    }
  }

  public void setAllowedSenderIdsAsString(String defaultRoutingAllowedSenderIds) {
    if(defaultRoutingAllowedSenderIds==null || defaultRoutingAllowedSenderIds.isEmpty()){
      this.defaultRoutingAllowedSenderIds = null;
    }else{
      this.defaultRoutingAllowedSenderIds = Arrays.asList(defaultRoutingAllowedSenderIds.split(","));
    }
  }

}
