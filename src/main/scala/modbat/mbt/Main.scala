package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log

object Main {
  class TestData {
    var modbat: Modbat = _
    // store reference to Modbat instance for final callback
    // from test harness in case of test failure
  }

  def main(args: Array[String]): Unit = {
    Modbat.isUnitTest = false
    val config = new Configuration()
    val log = new Log(Console.out, Console.err)
    try {
      run(args, config, log)
    } catch {
      case e: Exception => System.exit(1)
    }
    System.exit(0)
  }

  def run(args: Array[String], config: Configuration,
          log: Log,
          testData: TestData = new TestData()): Unit = {
    var modelClassName: String = null
    val c = new ConfigMgr("scala modbat.jar",
                          "CLASSNAME",
                          config,
                          new Version("modbat.mbt"),
                          /* test = */false,
                          log.out)
    /* delegate parsing args to config library */
    try {
      val remainder = c.parseArgs(args)
      remainder match {
        case Some(remainingArgs) => {
          if (!remainingArgs.hasNext) {
            log.error(c.header)
            log.error("Model class argument missing. Try --help.")
            throw new NoModelClassException(c.header)
          }
          modelClassName = remainingArgs.next()
          if (remainingArgs.hasNext) {
            val remaining = remainingArgs.next()
            log.error(
              "Extra arguments starting at \"" + remaining +
                "\" are not supported.")
            throw new ExtraArgumentsException(remaining)
          }
        }
        case None => // nothing
      }
    } catch {
      case e: IllegalArgumentException => {
        log.error(e.getMessage())
        throw e
      }
    }

    val mbt = new MBT(config, log)
    log.setLevel(config.logLevel)
    setup(config, mbt, modelClassName) // TODO: refactor into case code below once needed

    val modbat = new Modbat(mbt)
    testData.modbat = modbat
    /* execute */
    config.mode match {
      case "dot" =>
        new Dotify(config, mbt.launch(null), modelClassName + ".dot").dotify()
      case _ => modbat.explore(config.nRuns)
    }
  }

  def setup(config: Configuration, mbt: MBT, modelClassName: String): Unit = {
    /* configure components */
    MBT.isOffline = false
    mbt.loadModelClass(modelClassName)
    mbt.setRNG(config.randomSeed)
  }
}
