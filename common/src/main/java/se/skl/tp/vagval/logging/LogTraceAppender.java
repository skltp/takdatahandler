package se.skl.tp.vagval.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogTraceAppender {
  List<String> logTrace = new ArrayList<>();

  public LogTraceAppender() {}

  public void append(String... arguments){
      Collections.addAll(logTrace, arguments);
  }

  @Override
  public String toString(){
    return String.join(",", logTrace);
  }
}
