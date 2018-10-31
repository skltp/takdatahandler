package se.skl.tp.vagval.logging;

import org.apache.logging.log4j.ThreadContext;

public class ThreadContextLogTrace {

  private ThreadContextLogTrace() {
    // Static utility class
  }

  /**
   * The hsaId used for routing and a breadcrumb from the hsa-tree traversal
   * (if the tree was traversed).
   */
  public static final String ROUTER_RESOLVE_VAGVAL_TRACE = "routerVagvalTrace";
  /**
   * The hsaId used for behorighet (authorization) and a breadcrumb from the
   * hsa-tree traversal (if the tree was traversed).
   */
  public static final String ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE = "routerBehorighetTrace";


  public static void clear() {
    ThreadContext.clearAll();
  }

  public static void put(String key, String val) {
    ThreadContext.put(key, val);
  }

  public static String get(String key) {
    return ThreadContext.get(key);
  }
}
