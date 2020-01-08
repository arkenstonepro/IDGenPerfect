package com.newbee;

import org.junit.Test;

/**
 * Created by Aaron Kuai on 2020/1/8.
 */
public class IDGenTest {

    @Test
    public void nextId() {

        IDGen gen =
                new IDGen(
                        42, 8, 10, new IP4WorkIdPicker().workId("Local Host", "Test", (int) (-1L ^ -1L << 8)));

        System.out.println(gen);
    }
}
