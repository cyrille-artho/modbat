package modbat.examples;

public class SimpleCounter {
  int count = 0;
  boolean flag = true;

  public void toggleSwitch() {
    flag = !flag;
  }

  public void inc() {
    if (flag) {
      count += 1;
    }
  }

  public void inc2() {
    count +=2;
  }

  public int value() {
    return count;
  }
}
