package se.skl.tp.vagval;

import java.util.List;
import se.skltp.takcache.RoutingInfo;

public interface VagvalHandler {
  public List<RoutingInfo> getRoutingInfo(String tjanstegranssnitt, String receiverAddress);
}
