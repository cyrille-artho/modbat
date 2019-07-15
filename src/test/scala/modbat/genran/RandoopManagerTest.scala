package modbat.genran

import modbat.mbt.{Main, ModbatTestHarness}
import org.scalatest._

/**
  * TMP class for debugging / testing
  *
  */
class RandoopManagerTest extends FlatSpec with Matchers {

  "SimpleRandomModel" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=7", "-n=100", "--no-redirect-out", "--classpath=build/modbat-test.jar", "-s",  "modbat.genran.model.SimpleRandomModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._2.filter(_.contains("[INFO] [Random Testing]")) should not be empty
    result._3.filter(_.contains("java.lang.Object obj2 = modbat.genran.ObjectHolder.pick")) should not be empty
  }

  "RandomTCPProtocolModel" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=7", "-n=100", "--no-redirect-out", "--classpath=build/modbat-test.jar", "-s",  "modbat.genran.RandomTCPProtocolModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._2.filter(_.contains("[INFO] [Random Testing]")) should not be empty
    result._3.filter(_.contains("java.lang.Object obj2 = modbat.genran.ObjectHolder.pick")) should not be empty
  }

  "SimpleTCPProtocol" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=7", "-n=100", "--no-redirect-out", "--classpath=build/modbat-test.jar", "-s",  "modbat.genran.SimpleTCPProtocol"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._2.filter(_.contains("assertion failed at active => connectionError")) should not be empty
    result._3.filter(_.contains("modbat.genran.RandomTCPProtocolModel.assert")) should not be empty
  }

  "Test123" should "display output" in {

//
//    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.model.RandomSimpleListModel", "--no-redirect-out", "-s=7", "-n=30", "--abort-probability=0.02"))
//
//    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.model.RandomSimpleListModelWrapper", "--no-redirect-out", "-s=7", "-n=30", "--abort-probability=0.02"))
//
//    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.SimpleTCPProtocol", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))
//
//    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.RandomTCPProtocolModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))
  }
}
