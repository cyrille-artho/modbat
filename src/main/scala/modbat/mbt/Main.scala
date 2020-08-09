package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log

object Main {
  def main(args: Array[String]): Unit = {
    Modbat.isUnitTest = false
    val config = new Configuration()
    try {
      run(args, config) // TODO: do not call exit once exceptions are used
    } catch {
      case e: Exception => System.exit(1)
    }
    System.exit(0)
  }

  def run(args: Array[String], config: Configuration): Unit = {
    var modelClassName: String = null
    val c = new ConfigMgr("scala modbat.jar",
                          "CLASSNAME",
                          config,
                          new Version("modbat.mbt"))
    /* delegate parsing args to config library */
    try {
      val remainder = c.parseArgs(args)
      remainder match {
        case Some(remainingArgs) => {
          if (!remainingArgs.hasNext) {
            Log.error(c.header)
            Log.error("Model class argument missing. Try --help.")
            throw new NoModelClassException(c.header)
          }
          modelClassName = remainingArgs.next()
          if (remainingArgs.hasNext) {
            val remaining = remainingArgs.next()
            Log.error(
              "Extra arguments starting at \"" + remaining +
                "\" are not supported.")
            throw new ExtraArgumentsException(remaining)
          }
        }
        case None => // nothing
      }
    } catch {
      case e: IllegalArgumentException => {
        Log.error(e.getMessage())
        throw e
      }
    }

    val mbt = new MBT(config)
    setup(config, mbt, modelClassName) // TODO: refactor into case code below once needed

    val modbat = new Modbat(mbt)
    /* execute */
    config.mode match {
      case "dot" =>
        new Dotify(config, mbt.launch(null), modelClassName + ".dot").dotify()
      case _ => modbat.explore(config.nRuns)
    }
  }

  def setup(config: Configuration, mbt: MBT, modelClassName: String): Unit = {
    /* configure components */
    Log.setLevel(config.logLevel)
    MBT.isOffline = false
    MBT.configClassLoader(config.classpath)
    mbt.loadModelClass(modelClassName)
    mbt.setRNG(config.randomSeed)
  }
}
