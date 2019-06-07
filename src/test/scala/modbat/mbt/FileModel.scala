package modbat.mbt

import org.scalatest._

class FileModel extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "FileModel1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=500","modbat.test.FileModel","--stop-on-failure"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
