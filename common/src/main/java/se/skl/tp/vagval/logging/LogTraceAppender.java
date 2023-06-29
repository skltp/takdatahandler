package se.skl.tp.vagval.logging;

import java.util.ArrayList;
import java.util.List;

public class LogTraceAppender {
  List<String> logTrace = new ArrayList<>();

  public LogTraceAppender() {}

  public LogTraceAppender(String... toAppend) {
    append(toAppend);
  }

  public void append(String... arguments){
    for(String argument: arguments){
      logTrace.add(argument);
    }
  }

  @Override
  public String toString(){
    return String.join(",", logTrace);
  }
}
