package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}

import modbat.cov.{Trie, TrieNode}
import modbat.log.Log

import scala.collection.mutable.ListBuffer

case class LabelInfo (label:String, self:Boolean = false)

class PathVisualizer(trie:Trie, shape:String) {
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
    out.println("  node [ fontname = \"Helvetica\", fontsize=\"6.0\", shape=\""+ shape.toLowerCase +"\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println("  edge [ fontname = \"Helvetica\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")
    val stack:ListBuffer[LabelInfo] = new ListBuffer[LabelInfo]
    display(trie.root, 0, 0, stack)
    out.println("}")
  }

  def display(root:TrieNode, level:Int = 0, nodeNumber:Int = 0, stack:ListBuffer[LabelInfo] = null):(Int,ListBuffer[LabelInfo]) = {
    var newNodeNumber = nodeNumber
    var newStack = stack
    if (root.isLeaf) {
      if (newStack != null) {
        for (i <- newStack.indices){
          newNodeNumber = newNodeNumber + 1
          if (i == 0) out.println(i + "->" + newNodeNumber + newStack(i).label)
          else if (newStack(i).self){
            newNodeNumber = newNodeNumber -1
            out.println(newNodeNumber + "->" + newNodeNumber + newStack(i).label)
          }
          else out.println(newNodeNumber -1 + "->" + newNodeNumber + newStack(i).label)
        }
      }
      if (newStack != null){
        newStack.trimEnd(1)
      }
      return (newNodeNumber, newStack)
    }
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      val modelName = node.modelInfo.modelName
      val modelID = node.modelInfo.modelID.toString
      val transName = node.transitionInfo.transitionName.stripSuffix(" (1)")
      val transID = node.transitionInfo.transitionID.toString
      val transExecutionCounter = node.transitionInfo.transCounter.toString
      val selfTransCounter = "(Transition Self-execution Times:"+ node.selfTransCounter +")"
      val newlabel =  "[label = \"" + "Model Name:" + modelName  + "\\n" +
        "Model ID:" + modelID + "\\n" +
        "Transition Name:" + transName  + "\\n" +
        "Transition ID:" + transID  + "\\n" +
        "Transition Execution Counter:" + transExecutionCounter  + "\\n" +
        selfTransCounter + "\"];"
      shape.toLowerCase match {
        case "box" => val nodeIDFrom = transName.split(" => ")(0)
                      val nodeIDTo = transName.split(" => ")(1).stripSuffix(" (1)")
                      out.println("  " + nodeIDFrom + "->" + nodeIDTo  + newlabel)
                       display(node,level+1)
        case "point" =>
                        val newlabelInfo = if (transName.split(" => ")(0) == transName.split(" => ")(1).stripSuffix(" (1)")) LabelInfo(newlabel,true)
                                           else LabelInfo(newlabel)
                        newStack += newlabelInfo
                       // Log.info("new stack:" + newStack)
                        val result = display(node,level+1, newNodeNumber, newStack)
                        newNodeNumber = result._1
                        newStack = result._2
      }
    }
    if (newStack != null){
      newStack.trimEnd(1)
    }
    (newNodeNumber, newStack)
  }
}