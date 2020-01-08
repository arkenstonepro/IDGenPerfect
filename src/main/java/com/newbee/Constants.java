package com.newbee;

import java.util.concurrent.TimeUnit;

public interface Constants {
  /** Since Tue Mar 12 17:09:02 CST 2019 */
  long ID_EPOCH = 1552381742292L; //

  /** Mill seconds per year. */
  long YEAR_MILLS = TimeUnit.DAYS.toMillis(365);
}
