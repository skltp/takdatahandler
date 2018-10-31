package se.skl.tp.behorighet;

import static se.skl.tp.hsa.cache.HsaCache.DEFAUL_ROOTNODE;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.vagval.logging.LogTraceAppender;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skl.tp.vagval.util.DefaultRoutingUtil;
import se.skltp.takcache.TakCache;


@Service
public class BehorighetHandlerImpl implements BehorighetHandler {

  public static final String DEFAULT_RECEIVER_ADDRESS = "*";

  private HsaCache hsaCache;
  private TakCache takCache;
  private final String oldStyleDefaultRoutingAddressDelimiter;


  @Autowired
  public BehorighetHandlerImpl(HsaCache hsaCache, TakCache takCache, @Value("${vagvalrouter.default.routing.address.delimiter}") String delimiter) {
    this.hsaCache = hsaCache;
    this.takCache = takCache;

    oldStyleDefaultRoutingAddressDelimiter = delimiter;
  }

  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId) {
    LogTraceAppender logTrace = new LogTraceAppender();

    boolean isAuthorized = isAuthorized(senderId, servicecontractNamespace, receiverId, logTrace);

    logTrace.deleteCharIfLast(',');
    ThreadContextLogTrace
        .put(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE, logTrace.toString());
    return isAuthorized;
  }

  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId,
      LogTraceAppender logTrace) {

    if (DefaultRoutingUtil.useOldStyleDefaultRouting(receiverId,
        oldStyleDefaultRoutingAddressDelimiter)) {
      return isAuthorizedUsingDefaultRouting(senderId, servicecontractNamespace, receiverId,
          logTrace);
    }

    logTrace.append(receiverId);
    if (takCache.isAuthorized(senderId, servicecontractNamespace, receiverId)) {
      return true;
    }

    if (isAuthorizedByClimbingHsaTree(senderId, servicecontractNamespace, receiverId, logTrace)) {
      return true;
    }

    logTrace.append("(default)",DEFAULT_RECEIVER_ADDRESS);
    return takCache.isAuthorized(senderId, servicecontractNamespace, DEFAULT_RECEIVER_ADDRESS);
  }

  private boolean isAuthorizedUsingDefaultRouting(String senderId, String servicecontractNamespace,
      String receiverId, LogTraceAppender logTrace) {
    logTrace.append("(leaf)");
    List<String> receiverAddresses = DefaultRoutingUtil
        .extractReceiverAdresses(receiverId, oldStyleDefaultRoutingAddressDelimiter);
    for (String receiverAddressTmp : receiverAddresses) {
      logTrace.append(receiverAddressTmp, ',');
      if (takCache.isAuthorized(senderId, servicecontractNamespace, receiverAddressTmp)) {
        return true;
      }
    }
    return false;
  }

  private boolean isAuthorizedByClimbingHsaTree(String senderId, String servicecontractNamespace,
      String receiverId, LogTraceAppender logTrace) {
    logTrace.append(",(parent)");
    while (receiverId != DEFAUL_ROOTNODE) {
      receiverId = getHsaParent(receiverId);
      logTrace.append(receiverId, ',');
      if (takCache.isAuthorized(senderId, servicecontractNamespace, receiverId)) {
        return true;
      }
    }
    return false;
  }

  private String getHsaParent(String receiverId) {
    return hsaCache.getParent(receiverId);
  }

}
