package modbat.mbt

import org.scalatest._

class PrecondFalse extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "PrecondFalse1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","modbat.test.PrecondFalse"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
