package modbat.mbt

import org.scalatest._

class SUTReq extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SUTReq1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--precond-as-failure","--no-redirect-out","--log-level=fine","modbat.test.SUTReq"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "SUTReq2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","modbat.test.SUTReq"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
