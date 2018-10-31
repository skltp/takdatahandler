package se.skl.tp.vagval.logging;

import org.junit.Assert;
import org.junit.Test;

public class LogTraceAppenderTest {

  @Test
  public void testAppendFunctionality() {
    LogTraceAppender logTraceAppender = new LogTraceAppender();
    logTraceAppender.append("add1", ",add2");
    Assert.assertEquals("add1,add2",logTraceAppender.toString());
  }

  @Test
  public void testDeleteLastChar() {
    LogTraceAppender logTraceAppender = new LogTraceAppender("start1");
    logTraceAppender.deleteLastChar();
    Assert.assertEquals("start",logTraceAppender.toString());
  }

  @Test
  public void testDeleteLastCharOnEmptyStringShouldNotCauseChrash() {
    LogTraceAppender logTraceAppender = new LogTraceAppender("");
    logTraceAppender.deleteLastChar();
    Assert.assertEquals("",logTraceAppender.toString());
  }

  @Test
  public void testDeleteCharIfLastShouldDeleteMatch() {
    LogTraceAppender logTraceAppender = new LogTraceAppender("start1");
    logTraceAppender.deleteCharIfLast('1');
    Assert.assertEquals("start",logTraceAppender.toString());
  }

  @Test
  public void testDeleteCharIfLastOnEmptyStringShouldNotCauseCrash() {
    LogTraceAppender logTraceAppender = new LogTraceAppender("");
    logTraceAppender.deleteCharIfLast('1');
    Assert.assertEquals("",logTraceAppender.toString());
  }

  @Test
  public void testDeleteCharIfLastShouldNotDeleteIfNotMatch() {
    LogTraceAppender logTraceAppender = new LogTraceAppender("start1");
    logTraceAppender.deleteCharIfLast('2');
    Assert.assertEquals("start1",logTraceAppender.toString());
  }

}