package se.skl.tp.behorighet;

public interface BehorighetHandler {
  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId);
}
