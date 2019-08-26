package modbat.mbt

import org.scalatest._

class ChooseInBefore extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ChooseInBefore1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=3","modbat.test.ChooseInBefore","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
