package se.skl.tp.vagval.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogTraceAppenderTest {

  @Test
  void testAppendFunctionality() {
    LogTraceAppender logTraceAppender = new LogTraceAppender();
    logTraceAppender.append("add1", "add2");
    assertEquals("add1,add2",logTraceAppender.toString());
  }


}