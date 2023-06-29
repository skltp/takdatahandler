package se.skl.tp.behorighet;

import static se.skl.tp.hsa.cache.HsaCache.DEFAUL_ROOTNODE;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skl.tp.DefaultRoutingConfiguration;
import se.skl.tp.DefaultRoutingConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.vagval.logging.LogTraceAppender;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skl.tp.vagval.util.DefaultRoutingUtil;
import se.skltp.takcache.BehorigheterCache;


@Service
public class BehorighetHandlerImpl implements BehorighetHandler {

  public static final String DEFAULT_RECEIVER_ADDRESS = "*";

  private HsaCache hsaCache;
  private BehorigheterCache behorigheterCache;

  DefaultRoutingConfiguration defaultRoutingConfiguration;

  @Autowired
  public BehorighetHandlerImpl(HsaCache hsaCache, BehorigheterCache behorigheterCache, DefaultRoutingConfiguration defaultRoutingConfiguration) {
    this.hsaCache = hsaCache;
    this.behorigheterCache = behorigheterCache;
    this.defaultRoutingConfiguration = defaultRoutingConfiguration;
  }

  public BehorighetHandlerImpl(HsaCache hsaCache, BehorigheterCache behorigheterCache) {
    this(hsaCache, behorigheterCache, new DefaultRoutingConfigurationImpl());
  }

  public BehorighetHandlerImpl(BehorigheterCache behorigheterCache) {
    this(null, behorigheterCache, new DefaultRoutingConfigurationImpl());
  }

  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId) {
    LogTraceAppender logTrace = new LogTraceAppender();


    boolean isAuthorized = isAuthorized(senderId, servicecontractNamespace, receiverId, logTrace);

    ThreadContextLogTrace
        .put(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE, logTrace.toString());
    return isAuthorized;
  }

  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId,
      LogTraceAppender logTrace) {

    if (DefaultRoutingUtil.useOldStyleDefaultRouting(receiverId,defaultRoutingConfiguration.getDelimiter())) {
      return isAuthorizedUsingDefaultRouting(senderId, servicecontractNamespace, receiverId,
          logTrace);
    }

    logTrace.append(receiverId);
    if (behorigheterCache.isAuthorized(senderId, servicecontractNamespace, receiverId)) {
      return true;
    }

    if (hsaCache != null && isAuthorizedByClimbingHsaTree(senderId, servicecontractNamespace, receiverId, logTrace)) {
      return true;
    }

    logTrace.append("(default)",DEFAULT_RECEIVER_ADDRESS);
    if (behorigheterCache.isAuthorized(senderId, servicecontractNamespace, DEFAULT_RECEIVER_ADDRESS)) {
      return true;
    }

    return false;
  }

  private boolean isAuthorizedUsingDefaultRouting(String senderId, String servicecontractNamespace,
      String receiverId, LogTraceAppender logTrace) {
    logTrace.append("(leaf)");

    List<String> receiverAddresses = DefaultRoutingUtil
        .extractReceiverAdresses(receiverId, defaultRoutingConfiguration.getDelimiter());

    if(isParametersValidForDefaultRouting(receiverAddresses, senderId, servicecontractNamespace)){
      for (String receiverAddressTmp : receiverAddresses) {
        logTrace.append(receiverAddressTmp);
        if (behorigheterCache.isAuthorized(senderId, servicecontractNamespace, receiverAddressTmp)) {
          return true;
        }
      }
    } else{
      logTrace.append("Invalid parameters");
    }


    return false;
  }

  private boolean isParametersValidForDefaultRouting(List<String> receiverAddresses, String senderId, String servicecontractNamespace) {
    return receiverAddresses.size()<=2
        && DefaultRoutingUtil.isParameterAllowed(servicecontractNamespace, defaultRoutingConfiguration.getAllowedContracts())
        && DefaultRoutingUtil.isParameterAllowed(senderId, defaultRoutingConfiguration.getAllowedSenderIds());
  }

  private boolean isAuthorizedByClimbingHsaTree(String senderId, String servicecontractNamespace,
      String receiverId, LogTraceAppender logTrace) {
    logTrace.append("(parent)");
    while (receiverId != DEFAUL_ROOTNODE) {
      receiverId = getHsaParent(receiverId);
      logTrace.append(receiverId);
      if (behorigheterCache.isAuthorized(senderId, servicecontractNamespace, receiverId)) {
        return true;
      }
    }
    return false;
  }

  private String getHsaParent(String receiverId) {
    return hsaCache.getParent(receiverId);
  }

}
