package se.skl.tp.vagval.logging;

import static se.skl.tp.vagval.logging.ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE;
import static se.skl.tp.vagval.logging.ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE;

import org.junit.Assert;
import org.junit.Test;

public class ThreadContextLogTraceTest {

  @Test
  public void testClear() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    ThreadContextLogTrace.clear();
    Assert.assertNull(ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  public void testGetOnKey() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    Assert.assertEquals("test", ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  public void testUsingTwoKeysGiveDifferentResult() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    ThreadContextLogTrace.put(ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE, "test2");
    Assert.assertEquals("test", ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
    Assert.assertEquals("test2", ThreadContextLogTrace.get(ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }

}