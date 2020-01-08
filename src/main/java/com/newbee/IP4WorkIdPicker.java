package com.newbee;

import java.net.InetAddress;
import java.util.Random;

public class IP4WorkIdPicker implements WorkerPicker {
  @Override
  public int workId(final String host, final String instanceName, final int mask) {
    try {
      byte[] bytes = InetAddress.getLocalHost().getAddress();
      return Integer.valueOf(bytes[3] & mask); // 192.168.0.123
    } catch (Exception e) {
      return new Random().nextInt(mask);
    }
  }
}
