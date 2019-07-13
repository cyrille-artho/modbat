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
public class TestChooseTest {
  @Test public void test782de49f112cb820() {
    MBT.clearLaunchedModels();
    MBT.setRNG (new ReplayRandom(0x782de49f112cb820L, new int[]{}, 0));
    MBT m0 = MBT.launch(null);

    MBT.setRNG (new ReplayRandom(0x782de49f112cb820L, new int[]{0x0, 0x0}, 0));
    m0.setExpectedException("java.lang.AssertionError");
    m0.executeTransition(m0.getTransition(2));
    // modbat/test/ChooseTest.scala:15: assert
    MBT.setTestFailed(true);
    MBT.cleanup();

  }

  @BeforeClass static public void init() {
    Log.setLevel(2);
    MBT.setMaybeProbability(0.5);
    MBT.setRethrowExceptions(true);
    MBT.configClassLoader("build/modbat-test.jar");
    MBT.loadModelClass ("modbat.test.ChooseTest");
    MBT.invokeAnnotatedStaticMethods(Init.class, null);
  }

  @AfterClass static public void shutdown() {
    MBT.invokeAnnotatedStaticMethods(Shutdown.class, null);
  }
}
