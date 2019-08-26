package modbat.mbt

import org.scalatest._

class NullaryCons extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "NullaryCons1" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons"), ModbatTestHarness.setTestJar, td)
    result._1 should be(1)
    result._3 should not be empty
  }

  "NullaryCons2" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons","--print-stack-trace"), ModbatTestHarness.setTestJar, td)
    result._1 should be(1)
    result._3 should not be empty
  }
}
