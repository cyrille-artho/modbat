package modbat.mbt

import org.scalatest._

class CrashSoon extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "CrashSoon1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=200","--no-redirect-out","--stop-on-failure","modbat.test.CrashSoon"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
