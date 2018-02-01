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
public class TestSimpleLaunch {
  @Test public void test402b73cd0066eaea() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-1: skip

    m0.executeTransition(m0.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-1: assert

    MBT m1 = MBT.getLaunchedModel(1);
    m1.executeTransition(m1.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-2: skip

    m1.executeTransition(m1.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m1.executeTransition(m1.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    MBT m2 = MBT.getLaunchedModel(2);
    m2.executeTransition(m2.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-3: skip

    m1.executeTransition(m1.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m2.executeTransition(m2.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-3: assert

    MBT m3 = MBT.getLaunchedModel(3);
    m3.executeTransition(m3.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-4: skip

    m3.executeTransition(m3.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-4: assert

    m1.executeTransition(m1.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m1.executeTransition(m1.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    MBT m6 = MBT.getLaunchedModel(6);
    m6.executeTransition(m6.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-7: skip

    MBT m5 = MBT.getLaunchedModel(5);
    m5.executeTransition(m5.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-6: skip

    m6.executeTransition(m6.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-7: assert

    m5.executeTransition(m5.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-6: assert

    MBT m4 = MBT.getLaunchedModel(4);
    m4.executeTransition(m4.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-5: skip

    m1.executeTransition(m1.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-2: assert

    m4.executeTransition(m4.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-5: launch

    m4.executeTransition(m4.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-5: assert

    MBT m7 = MBT.getLaunchedModel(7);
    m7.executeTransition(m7.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-8: skip

    m7.setExpectedException("java.lang.AssertionError");
    m7.executeTransition(m7.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-8: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @Test public void test41530f4040856586() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    m0.executeTransition(m0.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-1: skip

    m0.executeTransition(m0.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-1: assert

    MBT m1 = MBT.getLaunchedModel(1);
    m1.executeTransition(m1.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-2: skip

    m1.executeTransition(m1.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-2: assert

    MBT m2 = MBT.getLaunchedModel(2);
    m2.executeTransition(m2.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-3: skip

    m2.executeTransition(m2.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-3: launch

    MBT m3 = MBT.getLaunchedModel(3);
    m3.executeTransition(m3.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-4: skip

    m3.executeTransition(m3.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-4: launch

    m2.executeTransition(m2.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-3: launch

    m3.executeTransition(m3.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-4: assert

    MBT m5 = MBT.getLaunchedModel(5);
    m5.executeTransition(m5.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-6: skip

    m5.executeTransition(m5.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-6: launch

    MBT m6 = MBT.getLaunchedModel(6);
    m6.executeTransition(m6.getTransition(0));
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-7: skip

    m5.executeTransition(m5.getTransition(1));
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-6: launch

    m6.setExpectedException("java.lang.AssertionError");
    m6.executeTransition(m6.getTransition(2));
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-7: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @BeforeClass static public void init() {
    Log.setLevel(3);
    MBT.setMaybeProbability(0.5);
    MBT.setRethrowExceptions(true);
    MBT.configClassLoader("build/modbat-test.jar");
    MBT.loadModelClass ("modbat.test.SimpleLaunch");
    MBT.invokeAnnotatedStaticMethods(Init.class, null);
  }

  @AfterClass static public void shutdown() {
    MBT.invokeAnnotatedStaticMethods(Shutdown.class, null);
  }
}
