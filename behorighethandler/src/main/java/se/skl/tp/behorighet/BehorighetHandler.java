package se.skl.tp.behorighet;

public interface BehorighetHandler {
  boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId);
}
