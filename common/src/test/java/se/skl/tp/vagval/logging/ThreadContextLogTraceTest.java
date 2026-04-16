package se.skl.tp.vagval.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static se.skl.tp.vagval.logging.ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE;
import static se.skl.tp.vagval.logging.ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE;

class ThreadContextLogTraceTest {

  @Test
  void testClear() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    ThreadContextLogTrace.clear();
    assertNull(ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  void testGetOnKey() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    assertEquals("test", ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  void testUsingTwoKeysGiveDifferentResult() {
    ThreadContextLogTrace.put(ROUTER_RESOLVE_VAGVAL_TRACE, "test");
    ThreadContextLogTrace.put(ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE, "test2");
    assertEquals("test", ThreadContextLogTrace.get(ROUTER_RESOLVE_VAGVAL_TRACE));
    assertEquals("test2", ThreadContextLogTrace.get(ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }

}