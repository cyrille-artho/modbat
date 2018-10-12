package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}
import modbat.cov.{Trie, TrieNode}
import modbat.log.Log

class PathVisualize(trie:Trie, shape:String = "") {
  var out: PrintStream = null
  assert (shape != "")
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
    out.println("  edge [ fontname = \"Helvetica\", fontsize=\"8.0\"," + " margin=\"0.05\" ];")
    display(trie.root, 0)
    out.println("}")
  }

  def display(root:TrieNode, level:Int = 0):Unit = {
    if (root.isLeaf) return
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      val newLabelModelInfo = node.modelInfo.toString()
      val newLabelTransInfo = node.transitionInfo.toString()
      val newLabelCounterInfo = "("+ node.selfTransCounter +")"
      shape.toLowerCase match {
        case "box" => val nodeIDFrom = node.transitionInfo._1.split("=>")(0)
                      val nodeIDTo = node.transitionInfo._1.split("=>")(1)
                      out.println("  " + nodeIDFrom + "->" + nodeIDTo  + "[label = \"" + newLabelModelInfo + "\\n" + newLabelTransInfo + "\\n" + newLabelCounterInfo + "\"];")
        case "point" => val nodeIDFrom = node.transitionInfo._2 -1
                        val nodeIDTo = node.transitionInfo._2
                        if (level == 0) out.println("  \"\"" + "->" + node.transitionInfo._2 + "[label = \"" + newLabelModelInfo + "\\n" + newLabelTransInfo + "\\n" + newLabelCounterInfo + "\"];")
                        else out.println("  " + nodeIDFrom + "->" + nodeIDTo + "[label = \"" + newLabelModelInfo + "\\n" + newLabelTransInfo + "\\n" + newLabelCounterInfo + "\"];")
      }
      display(node,level+1)
    }
  }
}
