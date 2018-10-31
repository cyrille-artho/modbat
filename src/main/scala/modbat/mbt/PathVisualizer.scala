package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}
import modbat.log.Log

trait PathVisualizer {

  val shape: String
  var out: PrintStream = null
  val outFile = MBT.modelClass.getName + "-" + shape + "Graph.dot"
  val fullOutFile = Main.config.dotDir + File.separatorChar + outFile
  try {
    out = new PrintStream(new FileOutputStream(fullOutFile), false, "UTF-8")
  } catch {
    case ioe: IOException => {
      Log.error("Cannot open file " + fullOutFile + ":")
      Log.error(ioe.getMessage)
      //System.exit(1)
    }
  }
  def dotify(): Unit
}
