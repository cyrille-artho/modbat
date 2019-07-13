/*** AIST confidential ***/

import modbat.mbt.MBT;
import modbat.mbt.after;
import modbat.mbt.before;
import modbat.mbt.init;
import modbat.mbt.shutdown;
import modbat.log.Log;
import modbat.offline.ReplayRandom;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestSimpleModel {
  @Test public void test1() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test2() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test3() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test4() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test5() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test6() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test7() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test8() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test9() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(1));
    // counter.toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test10() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(4));
    // counter.inc

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @Test public void test11() {
    MBT.clearLaunchedModels();
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // counter = new SimpleCounter()

    m0.executeTransition(m0.getTransition(2));
    // counter.inc2

    m0.executeTransition(m0.getTransition(3));
    // assert (counter.value == 2)
  }

  @BeforeClass static public void init() {
    System.out.println("*** AIST confidential ***");
    MBT.setRethrowExceptions(true);
    MBT.configClassLoader(".");
    MBT.loadModelClass ("SimpleModel");
  }

}
