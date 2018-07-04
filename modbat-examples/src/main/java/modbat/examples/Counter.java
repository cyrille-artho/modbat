package modbat.examples;

public class Counter {
  int count = 0;
  boolean flag = true;

  public void toggleSwitch() {
    flag = !flag;
  }

  public void inc(int i) {
    if (!flag) {
      i &= ~0x1; // bit flip
    }
    count += i;
  }

  public int value() {
    return count;
  }
}
