package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log

object Main {
  val config = new Configuration()

  def main(args: Array[String]) {
    var modelClassName: String = null
    val c = new ConfigMgr("scala modbat.jar",
                          "CLASSNAME",
                          config,
                          new Version("modbat.mbt"))
    /* delegate parsing args to config library */
    try {
      val remainingArgs = c.parseArgs(args)
      if (!remainingArgs.hasNext) {
        Log.error(c.header)
        Log.error("Model class argument missing. Try --help.")
        System.exit(1)
      }
      modelClassName = remainingArgs.next
      if (remainingArgs.hasNext) {
        Log.error(
          "Extra arguments starting at \"" + remainingArgs.next() +
            "\" are not supported.")
        System.exit(1)
      }
    } catch {
      case e: IllegalArgumentException => {
        Log.error(e.getMessage())
        System.exit(1)
      }
    }

    setup(modelClassName) // TODO: refactor into case code below once needed

    /* execute */
    config.mode match {
      case "dot" =>
        new Dotify(MBT.launch(null), modelClassName + ".dot").dotify()
      case _ => Modbat.explore(config.nRuns)
    }
  }

  def setup(modelClassName: String) {
    /* configure components */
    Log.setLevel(config.logLevel)
    MBT.enableStackTrace = config.printStackTrace
    MBT.maybeProbability = config.maybeProbability
    MBT.configClassLoader(config.classpath)
    MBT.loadModelClass(modelClassName)
    MBT.setRNG(config.randomSeed)
    MBT.isOffline = false
    MBT.runBefore = config.setup
    MBT.runAfter = config.cleanup
    MBT.precondAsFailure = config.precondAsFailure
  }
}
