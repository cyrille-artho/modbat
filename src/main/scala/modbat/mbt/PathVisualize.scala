package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}
import modbat.cov.{Trie, TrieNode}
import modbat.log.Log

class PathVisualize(trie:Trie, shape:String) {
  require(shape == "Box" || shape == "Point", "the input of path visualizer must be either Box or Point")
  var out: PrintStream = null
  val outFile = "pathInfoIn" + shape + "Graph.dot"
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

  def dotify() {
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"0.3\", nodesep=\"0.2\" ];")
    out.println("  node [ shape=\""+ shape.toLowerCase +"\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println("  edge [ fontname = \"Helvetica\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")
    display(trie.root, 0)
    out.println("}")
  }

  def display(root:TrieNode, level:Int = 0) {
    if (root.isLeaf) return
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      val modelName = node.modelInfo._1.toString
      val modelID = node.modelInfo._2.toString
      val transName = node.transitionInfo._1.toString
      val transID = node.transitionInfo._2.toString
      val transExecutionCounter = node.transitionInfo._3.toString
      val selfTransCounter = "(Transition Self-execution Times:"+ node.selfTransCounter +")"
      val newlabel =  "[label = \"" + "Model Name:" + modelName  + "\\n" +
        "Model ID:" + modelID + "\\n" +
        "Transition Name:" + transName  + "\\n" +
        "Transition ID:" + transID  + "\\n" +
        "Transition Execution Counter:" + transExecutionCounter  + "\\n" +
        selfTransCounter + "\"];"
      shape.toLowerCase match {
        case "box" => val nodeIDFrom = node.transitionInfo._1.split("=>")(0)
                      val nodeIDTo = node.transitionInfo._1.split("=>")(1)
                      out.println("  " + nodeIDFrom + "->" + nodeIDTo  + newlabel)
        case "point" => val nodeIDFrom = node.transitionInfo._2 -1
                        val nodeIDTo = node.transitionInfo._2
                        if (level == 0) out.println("  \"\"" + "->" + node.transitionInfo._2 + newlabel)
                        else out.println("  " + nodeIDFrom + "->" + nodeIDTo + newlabel)
      }
      display(node,level+1)
    }
  }
}