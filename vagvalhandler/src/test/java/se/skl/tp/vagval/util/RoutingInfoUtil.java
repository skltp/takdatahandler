package se.skl.tp.vagval.util;

import se.skltp.takcache.RoutingInfo;

public class RoutingInfoUtil {

  private RoutingInfoUtil() {
    // Utility class
  }

  public static RoutingInfo createRoutingInfo(String address, String rivProfile){
    RoutingInfo routingInfo = new RoutingInfo();
    routingInfo.setAddress(address);
    routingInfo.setRivProfile(rivProfile);
    return routingInfo;
  }

}
