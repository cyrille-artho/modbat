package modbat.genran

import modbat.mbt.Main
import org.scalatest.FunSuite

class RandoopManagerTest extends FunSuite {

  test("testRun") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.examples.SimpleRandomModel", "--no-redirect-out"))

  }

  test("runArrayList") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.ArrayListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02" ))

  }

  test("runLinkedList") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.LinkedListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))

  }

  test("runSimpleLIst") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.SimpleListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))

  }

  test("runRandomSimpleListModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.genran.RandomSimpleListModel", "--no-redirect-out", "-s=7", "-n=30", "--abort-probability=0.02"))

  }



}
