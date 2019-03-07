package modbat.examples;

import randoop.CheckRep;

public class ControlCounter {

  int count = 0;
  int trueCount = 0;

  boolean flag = true;

  public void toggleSwitch() {
    flag = !flag;
  }

  public void inc() {

    trueCount += 1;

    if (flag) {
      count += 1;
    }
  }

  public void inc2() {
    count +=2;
    trueCount +=2;
  }

  public int value() {
    return count;
  }

  @CheckRep
  public boolean isValid() {
    return count == trueCount;
  }
}
