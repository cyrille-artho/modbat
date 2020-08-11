package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}
import modbat.log.Log

abstract class PathVisualizer(val mbt: MBT) {
  val typeName: String
  val graphInitNode: String
  var out: PrintStream = null
  val outFile = mbt.modelClass.getName + "-" + graphInitNode + "-" + typeName + "Graph.dot"
  val fullOutFile = mbt.config.dotDir + File.separatorChar + outFile
  try {
    out = new PrintStream(new FileOutputStream(fullOutFile), false, "UTF-8")
  } catch {
    case ioe: IOException => {
      mbt.log.error("Cannot open file " + fullOutFile + ":")
      mbt.log.error(ioe.getMessage)
      //System.exit(1)
    }
  }
  def dotify(): (Int, Int, Int, Int, Int, Int, Int)
}
