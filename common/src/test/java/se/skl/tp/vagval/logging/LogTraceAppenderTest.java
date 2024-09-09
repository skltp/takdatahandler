package se.skl.tp.vagval.logging;

import org.junit.Assert;
import org.junit.Test;

public class LogTraceAppenderTest {

  @Test
  public void testAppendFunctionality() {
    LogTraceAppender logTraceAppender = new LogTraceAppender();
    logTraceAppender.append("add1", "add2");
    Assert.assertEquals("add1,add2",logTraceAppender.toString());
  }


}