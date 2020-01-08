package com.newbee;

public interface WorkerPicker {

  int workId(final String host, final String instanceName, final int mask);
}
