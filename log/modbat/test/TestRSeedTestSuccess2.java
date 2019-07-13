package modbat.test;

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
public class TestRSeedTestSuccess2 {
  @Test public void test3ba471c1785a0175() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test89a82931be29e65() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test27fe2d950fc74b21() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test19b6aa1b14a29017() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test85780661bf76533() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test486e122d1bcbade7() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(0));
    // modbat/test/RSeedTestSuccess2.scala:21: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @BeforeClass static public void init() {
    Log.setLevel(3);
    MBT.setMaybeProbability(0.5);
    if (System.getProperty("rethrow-exceptions") != null) {
      MBT.setRethrowExceptions(true);
    }
    MBT.configClassLoader("build/modbat-test.jar");
    MBT.loadModelClass ("modbat.test.RSeedTestSuccess2");
    MBT.invokeAnnotatedStaticMethods(Init.class, null);
  }

  @AfterClass static public void shutdown() {
    MBT.invokeAnnotatedStaticMethods(Shutdown.class, null);
  }
}
