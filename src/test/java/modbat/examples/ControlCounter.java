package modbat.examples;

import randoop.CheckRep;
import randoop.org.apache.commons.lang3.builder.HashCodeBuilder;

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

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ControlCounter c = (ControlCounter) o;

    return count == c.count && trueCount == c.trueCount &&
            flag == c.flag;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
            .append(count)
            .append(trueCount)
            .append(flag)
            .toHashCode();
  }

}
