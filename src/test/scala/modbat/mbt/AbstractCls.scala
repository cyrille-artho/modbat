package modbat.mbt

import org.scalatest._

class AbstractCls extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "AbstractCls1" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","--no-redirect-out","modbat.test.AbstractCls"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
