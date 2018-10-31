package se.skl.tp.vagval;

import static se.skl.tp.hsa.cache.HsaCache.DEFAUL_ROOTNODE;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.vagval.logging.LogTraceAppender;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skl.tp.vagval.util.DefaultRoutingUtil;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.TakCache;

@Service
public class VagvalHandlerImpl implements VagvalHandler {

  public static final String DEFAULT_RECEIVER_ADDRESS = "*";

  private HsaCache hsaCache;
  private TakCache takCache;
  private final String oldStyleDefaultRoutingAddressDelimiter;

  @Autowired
  public VagvalHandlerImpl(HsaCache hsaCache, TakCache takCache, @Value("${vagvalrouter.default.routing.address.delimiter}") String delimiter) {
    this.hsaCache = hsaCache;
    this.takCache = takCache;
    oldStyleDefaultRoutingAddressDelimiter = delimiter;
  }

  public List<RoutingInfo> getRoutingInfo(String tjanstegranssnitt, String receiverAddress){

    List<RoutingInfo> routingInfos;
    LogTraceAppender logTrace = new LogTraceAppender();

    routingInfos = getRoutingInfo(tjanstegranssnitt, receiverAddress, logTrace);
    logTrace.deleteCharIfLast(',');
    ThreadContextLogTrace.put(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE, logTrace.toString());

    return routingInfos;
  }

  private List<RoutingInfo> getRoutingInfo(String tjanstegranssnitt, String receiverAddress,
      LogTraceAppender logTrace) {

    if( DefaultRoutingUtil.useOldStyleDefaultRouting(receiverAddress,
        oldStyleDefaultRoutingAddressDelimiter) ){
      return getRoutingInfoUseOldStyleDefaultRouting(tjanstegranssnitt, receiverAddress, logTrace);
    }

    logTrace.append(receiverAddress);
    List<RoutingInfo> routingInfos = getRoutingInfoFromTakCache(tjanstegranssnitt, receiverAddress);
    if (!routingInfos.isEmpty()) {
      return routingInfos;
    }

    routingInfos = getRoutingInfoByClimbingHsaTree(tjanstegranssnitt, receiverAddress, logTrace);
    if (!routingInfos.isEmpty()) {
      return routingInfos;
    }

    logTrace.append("(default)",DEFAULT_RECEIVER_ADDRESS);
    return getRoutingInfoFromTakCache(tjanstegranssnitt, DEFAULT_RECEIVER_ADDRESS);
  }

  private List<RoutingInfo> getRoutingInfoByClimbingHsaTree(String tjanstegranssnitt, String receiverAddress, LogTraceAppender logTrace) {
    logTrace.append(",(parent)");
    while (receiverAddress != DEFAUL_ROOTNODE) {
      receiverAddress = getHsaParent(receiverAddress);
      logTrace.append(receiverAddress, ',');
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
        oldStyleDefaultRoutingAddressDelimiter);

    for(String receiverAddressTmp : receiverAddresses){
      logTrace.append(receiverAddressTmp,',');
      List<RoutingInfo> routingInfoList = getRoutingInfoFromTakCache(tjanstegranssnitt, receiverAddressTmp);
      if(!routingInfoList.isEmpty()){
        return routingInfoList;
      }
    }

    return Collections.<RoutingInfo>emptyList();
  }

  private List<RoutingInfo> getRoutingInfoFromTakCache(String tjanstegranssnitt, String receiverAddress) {
    return takCache.getRoutingInfo(tjanstegranssnitt, receiverAddress);
  }
}
