package modbat.genran

import modbat.mbt.Main
import org.scalatest.FunSuite

/**
  * Tmp class for debugging
  */
class RandoopManagerTest extends FunSuite {

  test("testSimpleRandomModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.model.SimpleRandomModel", "--no-redirect-out"))

  }

  test("runArrayListModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.ArrayListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02" ))

  }

  test("runLinkedListModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.LinkedListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))

  }

  test("runSimpleListModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.containers.SimpleListModel", "--no-redirect-out", "-s=7", "-n=1000", "--abort-probability=0.02"))

  }

  test("runRandomSimpleListModel") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.model.RandomSimpleListModel", "--no-redirect-out", "-s=7", "-n=30", "--abort-probability=0.02"))

  }

  test("runRandomSimpleListModelWrapper") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.genran.model.RandomSimpleListModelWrapper", "--no-redirect-out", "-s=7", "-n=30", "--abort-probability=0.02"))

  }


}
