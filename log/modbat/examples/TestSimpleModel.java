package modbat.examples;

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT;
import modbat.log.Log;
import modbat.offline.ReplayRandom;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestSimpleModel {
  @Test public void test49a846e52b813972() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x49a846e52b813972L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1));
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3));
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(5));
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test486e122d1bcbade7() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1));
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3));
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(5));
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test1690fa4e0c732f7d() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1));
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3));
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(5));
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test636e772f0798297b() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x636e772f0798297bL, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1));
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2));
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3));
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(5));
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @BeforeClass static public void init() {
    Log.setLevel(3);
    MBT.setMaybeProbability(0.5);
    MBT.setRethrowExceptions(true);
    MBT.configClassLoader("build/modbat-examples.jar");
    MBT.loadModelClass ("modbat.examples.SimpleModel");
    MBT.invokeAnnotatedStaticMethods(Init.class, null);
  }

  @AfterClass static public void shutdown() {
    MBT.invokeAnnotatedStaticMethods(Shutdown.class, null);
  }
}
