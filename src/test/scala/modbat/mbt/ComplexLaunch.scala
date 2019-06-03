package modbat.mbt

import org.scalatest._

class ComplexLaunch extends FlatSpec with Matchers {
  "ComplexLaunch1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--log-level=debug","--no-redirect-out","modbat.test.ComplexLaunch"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=50","--log-level=fine","--print-stack-trace","--no-redirect-out","modbat.test.ComplexLaunch"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch3" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.test.ComplexLaunch"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch4" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","modbat.test.ComplexLaunch2"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch5" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","modbat.test.ComplexLaunch3"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch6" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","modbat.test.ComplexLaunch4"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch7" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","modbat.test.ComplexLaunch5"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ComplexLaunch8" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","modbat.test.ComplexLaunch6"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}