package se.skl.tp.vagval.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DefaultRoutingUtil {

  private DefaultRoutingUtil() {
    //  Static utility
  }

  public static boolean useOldStyleDefaultRouting(String receiverAddress, String addressDelimiter){
    // Determine if delimiter is set and present in request logical address.
    // Delimiter is used in deprecated default routing (VG#VE).
    if( receiverAddress!=null && addressDelimiter!=null && !addressDelimiter.isEmpty() ){
      return receiverAddress.contains(addressDelimiter);
    }
    return false;
  }

  public static List<String> extractReceiverAdresses(String recieverAddressesString, String addressDelimiter) {
    List<String> receiverAddresses = new ArrayList<>();

    StringTokenizer strToken = new StringTokenizer(recieverAddressesString, addressDelimiter);
    while (strToken.hasMoreTokens()) {
      String tempAddress = (String) strToken.nextElement();
      if (!receiverAddresses.contains(tempAddress)) {
        receiverAddresses.add(0, tempAddress);
      }
    }
    return receiverAddresses;
  }

  public static boolean isParameterAllowed(String parameter, List<String> allowedParameters){
    return allowedParameters==null || allowedParameters.isEmpty() || allowedParameters.contains(parameter);
  }

}