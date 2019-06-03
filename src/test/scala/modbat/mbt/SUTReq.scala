package modbat.mbt

import org.scalatest._

class SUTReq extends FlatSpec with Matchers {
  "SUTReq1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--precond-as-failure","--no-redirect-out","--log-level=fine","modbat.test.SUTReq"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SUTReq2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","modbat.test.SUTReq"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}