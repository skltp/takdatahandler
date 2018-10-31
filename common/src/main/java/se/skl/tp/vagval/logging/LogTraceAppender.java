package se.skl.tp.vagval.logging;

public class LogTraceAppender {
  StringBuilder logTrace;

  public LogTraceAppender() {
    this.logTrace = new StringBuilder();
  }

  public LogTraceAppender(String toAppend) {
    this.logTrace = new StringBuilder();
    append(toAppend);
  }

  public void append(Object... arguments){
    for(Object argument: arguments){
      logTrace.append(argument);
    }
  }

  public void deleteLastChar(){
    if(logTrace.length() > 0) {
      logTrace.deleteCharAt(logTrace.length() - 1);
    }
  }

  public void deleteCharIfLast(char ch){
    if(logTrace.length() > 0 && logTrace.charAt(logTrace.length() - 1)==ch) {
      logTrace.deleteCharAt(logTrace.length() - 1);
    }
  }

  @Override
  public String toString(){
    return logTrace.toString();
  }
}
