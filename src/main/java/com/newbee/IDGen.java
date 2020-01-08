package com.newbee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

import static java.lang.System.currentTimeMillis;

public class IDGen {

  protected final int timestampBit;

  protected final int workBit;

  protected final int sequenceBit;

  protected final long maxSequence;

  private long lastTimestamp = -1L;

  protected final long epochSince = Constants.ID_EPOCH;

  private long sequence = 0L;

  private final long workIdOffed;

  private final int reserveBit;

  private final int timestampBitOffset;

  public IDGen(final int timestampBit, final int workBit, final int sequenceBit, final int workId) {

    this.timestampBit = timestampBit;
    this.workBit = workBit;
    this.sequenceBit = sequenceBit;
    maxSequence = -1L ^ -1L << this.sequenceBit;

    this.reserveBit = 63 - timestampBit - workBit - sequenceBit;
    this.workIdOffed = workId << workBit;

    timestampBitOffset = workBit + sequenceBit;
  }

  public synchronized long nextId() {

    long timestamp = currentTimeMillis();

    if (this.lastTimestamp == timestamp) {

      sequence = sequence + 1 & maxSequence;

      if (this.sequence == 0) {
        // Exceed the sequenceBit
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      this.sequence = 0;
    }

    // can not adjust the clock in runtime!!
    if (timestamp < this.lastTimestamp) {
      System.err.println(
          String.format(
              "clock moved backwards.Refusing to generate id for %d milliseconds",
              (this.lastTimestamp - timestamp)));

      // TODO I do not know how to handle this, this may fucked us
      return currentTimeMillis() - epochSince << timestampBitOffset
          | workIdOffed
          | new Random().nextInt((int) maxSequence);
    }

    this.lastTimestamp = timestamp;
    return timestamp - epochSince << timestampBitOffset | workIdOffed | sequence;
  }

  private long tilNextMillis(long lastTimestamp) {
    long timestamp = currentTimeMillis();
    while (timestamp <= lastTimestamp) {
      timestamp = currentTimeMillis();
      try {
        LockSupport.parkNanos(71);
      } catch (Throwable throwable) {
        // TODO Ignore, I do not know neither why do this?
      }
    }
    return timestamp;
  }

  @Override
  public String toString() {

    final StringBuffer stringBuffer = new StringBuffer();

    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:MM");

    stringBuffer
        .append(
            "\n\nID_GEN_CONFIG\n==============================================================================================\n")
        .append(String.format("%-30s %63s%n", "MaxID(Value)", Long.MAX_VALUE + ""))
        .append(
            String.format(
                "%-30s %63s%n", "MaxID(Hex)", Long.toHexString(Long.MAX_VALUE).toUpperCase()))
        .append(String.format("%-30s %63s%n", "MaxID(63)", Long.toBinaryString(Long.MAX_VALUE)))
        .append(
            String.format(
                "%-30s %63s%n",
                "Rsv(" + reserveBit + ")",
                Long.toBinaryString((-1L ^ -1L << reserveBit) << (63 - reserveBit))))
        .append(
            String.format(
                "%-30s %63s%n",
                "Time(" + timestampBit + ")",
                Long.toBinaryString((-1L ^ -1L << timestampBit) << (timestampBitOffset))))
        .append(
            String.format(
                "%-30s %63s%n",
                "Work(" + workBit + ")",
                Long.toBinaryString((-1L ^ -1L << workBit) << (sequenceBit))))
        .append(
            String.format(
                "%-30s %63s%n",
                "Seq(" + sequenceBit + ")", Long.toBinaryString((-1L ^ -1L << sequenceBit))))
        .append(
            "______________________________________________________________________________________________\n\n")
        .append(
            String.format(
                "%-30s %63s%n", "MaxWorkId(" + workBit + ")", (-1L ^ -1L << workBit) + ""))
        .append(
            String.format(
                "%-30s %63s%n", "MaxSeq(" + sequenceBit + ")", (-1L ^ -1L << sequenceBit) + ""))
        .append(String.format("%-30s %63s%n", "Seq/Second", (-1L ^ -1L << sequenceBit) * 1000 + ""))
        .append(
            "______________________________________________________________________________________________\n\n")
        .append(String.format("%-30s %63s%n", "EpochSince", epochSince + ""))
        .append(
            String.format(
                "%-30s %63.1f%n",
                "Afford(Years)", (-1L ^ -1L << timestampBit) * 1d / Constants.YEAR_MILLS))
        .append(
            String.format("%-30s %63s%n", "EpochSince(DT)", formatter.format(new Date(epochSince))))
        .append(
            String.format(
                "%-30s %63s%n",
                "EpochTill(DT)",
                formatter.format(new Date(epochSince + (-1L ^ -1L << timestampBit)))))
        .append(
            "==============================================================================================");

    return stringBuffer.toString();
  }
}
