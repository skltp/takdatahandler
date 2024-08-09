package se.skl.tp.behorighet.util;

public class TestTakDataDefines {

  public static final String NAMNRYMD_1 = "riv:abc:namnrymd-1";
  public static final String NAMNRYMD_2 = "riv:abc:namnrymd-2";
  public static final String DOMAIN_1 = "riv:abc";
  public static final String RECEIVER_1 = "receiver-1";
  public static final String RECEIVER_2 = "receiver-2";
  public static final String RECEIVER_3 = "receiver-3";
  public static final String RECEIVER_4 = "receiver-4";
  public static final String DEFAULT_RECEIVER = "*";
  public static final String SENDER_1 = "sender-1";
  public static final String SENDER_2 = "sender-2";

  public static final String RECEIVER_1_DEFAULT_RECEIVER_2 = String.format("%s#%s", RECEIVER_1, RECEIVER_2);
  public static final String RECEIVER_1_DEFAULT_RECEIVER_1 = String.format("%s#%s", RECEIVER_1, RECEIVER_1);
  public static final String RECEIVER_2_DEFAULT_RECEIVER_3 = String.format("%s#%s", RECEIVER_2, RECEIVER_3);
  public static final String RECEIVER_2_RECEIVER_3_RECIEVER_4 = String.format("%s#%s#%s", RECEIVER_2, RECEIVER_3, RECEIVER_4);
  public static final String RECEIVER_4_RECEIVER_3_RECIEVER_2 = String.format("%s#%s#%s", RECEIVER_4, RECEIVER_3, RECEIVER_2);
  public static final String RECEIVER_3_DEFAULT_RECEIVER_4 = String.format("%s#%s", RECEIVER_3, RECEIVER_4);

  public static final String AUTHORIZED_RECEIVER_IN_HSA_TREE = "SE0000000003-1234";
  public static final String CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE = "SE0000000001-1234";
  public static final String PARENT_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE = "SE0000000004-1234";

}
