package se.skl.tp.vagval;

import static se.skl.tp.hsa.cache.HsaCache.DEFAUL_ROOTNODE;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skl.tp.DefaultRoutingConfiguration;
import se.skl.tp.DefaultRoutingConfigurationImpl;
import se.skl.tp.HsaLookupConfiguration;
import se.skl.tp.HsaLookupConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.vagval.logging.LogTraceAppender;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skl.tp.vagval.util.DefaultRoutingUtil;
import se.skl.tp.vagval.util.HsaLookupUtil;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.VagvalCache;

@Service
public class VagvalHandlerImpl implements VagvalHandler {

  public static final String DEFAULT_RECEIVER_ADDRESS = "*";

  private HsaCache hsaCache;
  private VagvalCache vagvalCache;
  DefaultRoutingConfiguration defaultRoutingConfiguration;
  HsaLookupConfiguration hsaLookupConfiguration;

  @Autowired
  public VagvalHandlerImpl(HsaCache hsaCache, VagvalCache vagvalCache, DefaultRoutingConfiguration defaultRoutingConfiguration,
                           HsaLookupConfiguration hsaLookupConfiguration) {
    this.hsaCache = hsaCache;
    this.vagvalCache = vagvalCache;
    this.defaultRoutingConfiguration = defaultRoutingConfiguration;
    this.hsaLookupConfiguration = hsaLookupConfiguration;
  }

  public VagvalHandlerImpl(HsaCache hsaCache, VagvalCache vagvalCache, DefaultRoutingConfiguration defaultRoutingConfiguration) {
    this(hsaCache, vagvalCache, defaultRoutingConfiguration, new HsaLookupConfigurationImpl());
  }

  public VagvalHandlerImpl(HsaCache hsaCache, VagvalCache vagvalCache) {
    this( hsaCache, vagvalCache, new DefaultRoutingConfigurationImpl());
  }

  public VagvalHandlerImpl(VagvalCache vagvalCache) {
    this( null, vagvalCache, new DefaultRoutingConfigurationImpl());
  }

  public List<RoutingInfo> getRoutingInfo(String tjanstegranssnitt, String receiverAddress){

    List<RoutingInfo> routingInfos;
    LogTraceAppender logTrace = new LogTraceAppender();

    routingInfos = getRoutingInfo(tjanstegranssnitt, receiverAddress, logTrace);

    ThreadContextLogTrace.put(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE, logTrace.toString());

    return routingInfos;
  }

  private List<RoutingInfo> getRoutingInfo(String tjanstegranssnitt, String receiverAddress,
      LogTraceAppender logTrace) {

    if( DefaultRoutingUtil.useOldStyleDefaultRouting(receiverAddress,
        defaultRoutingConfiguration.getDelimiter()) ){
      return getRoutingInfoUseOldStyleDefaultRouting(tjanstegranssnitt, receiverAddress, logTrace);
    }

    logTrace.append(receiverAddress);
    List<RoutingInfo> routingInfos = getRoutingInfoFromTakCache(tjanstegranssnitt, receiverAddress);
    if (!routingInfos.isEmpty()) {
      return routingInfos;
    }

    if(HsaLookupUtil.isHsaLookupEnabled(hsaCache, hsaLookupConfiguration, tjanstegranssnitt)) {
      routingInfos = getRoutingInfoByHsaLookup(tjanstegranssnitt, receiverAddress, logTrace);
      if (!routingInfos.isEmpty()) {
        return routingInfos;
      }
    }

    logTrace.append("(default)",DEFAULT_RECEIVER_ADDRESS);
    return getRoutingInfoFromTakCache(tjanstegranssnitt, DEFAULT_RECEIVER_ADDRESS);
  }

  private List<RoutingInfo> getRoutingInfoByHsaLookup(String tjanstegranssnitt, String receiverAddress, LogTraceAppender logTrace) {
    logTrace.append("(parent)");
    while (receiverAddress != DEFAUL_ROOTNODE) {
      receiverAddress = getHsaParent(receiverAddress);
      logTrace.append(receiverAddress);
      List<RoutingInfo> routingInfoList = getRoutingInfoFromTakCache(tjanstegranssnitt, receiverAddress);
      if(!routingInfoList.isEmpty()){
        return routingInfoList;
      }
    }
    return  Collections.<RoutingInfo>emptyList();
  }

  private String getHsaParent(String receiverId) {
    return hsaCache.getParent(receiverId);
  }

  private List<RoutingInfo> getRoutingInfoUseOldStyleDefaultRouting(String tjanstegranssnitt, String receiverAddress, LogTraceAppender logTrace) {
    logTrace.append("(leaf)");

    List<String> receiverAddresses = DefaultRoutingUtil.extractReceiverAdresses(receiverAddress,
        defaultRoutingConfiguration.getDelimiter());

    for(String receiverAddressTmp : receiverAddresses){
      logTrace.append(receiverAddressTmp);
      List<RoutingInfo> routingInfoList = getRoutingInfoFromTakCache(tjanstegranssnitt, receiverAddressTmp);
      if(!routingInfoList.isEmpty()){
        return routingInfoList;
      }
    }

    return Collections.<RoutingInfo>emptyList();
  }

  private List<RoutingInfo> getRoutingInfoFromTakCache(String tjanstegranssnitt, String receiverAddress) {
    return vagvalCache.getRoutingInfo(tjanstegranssnitt, receiverAddress);
  }
}
