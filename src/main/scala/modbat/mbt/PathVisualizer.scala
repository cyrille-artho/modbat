package modbat.mbt

import java.io.{File, FileOutputStream, IOException, PrintStream}

import modbat.cov.{Trie, TrieNode}
import modbat.log.Log

import scala.collection.mutable.ListBuffer

case class LabelInfo (label:String, self:Boolean = false) // LabelInfo is used for record the label information used for "point" output
case class NodeInfo (node:TrieNode, var transCounter:String) // NodeInfo is used for recode  the node information used for "box" output

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
    val labelRecoderStack:ListBuffer[LabelInfo] = new ListBuffer[LabelInfo] // stack is used for recode label information for "point" output
    val nodeRecorder:ListBuffer[NodeInfo] = new ListBuffer[NodeInfo] // nodeRecorder is used for recode node information for "box" output
    display(trie.root, 0, 0, labelRecoderStack, nodeRecorder)
    out.println("}")
  }

  def display(root:TrieNode, level:Int = 0, nodeNumber:Int = 0, labelRecoderStack:ListBuffer[LabelInfo] = null,
              nodeRecorder:ListBuffer[NodeInfo] = null):(Int,ListBuffer[LabelInfo]) = {
    var newNodeNumber = nodeNumber // newNodeNumber is used to generate the number(ID) of the node for the "point" graph
    var newLabelStack = labelRecoderStack
    if (root.isLeaf) {
      if (newLabelStack != null) {
        // output "point" graph
        for (i <- newLabelStack.indices){
          newNodeNumber = newNodeNumber + 1
          if (i == 0) out.println(i + "->" + newNodeNumber + newLabelStack(i).label)
          else if (newLabelStack(i).self){
            newNodeNumber = newNodeNumber -1
            out.println(newNodeNumber + "->" + newNodeNumber + newLabelStack(i).label)
          }
          else out.println(newNodeNumber -1 + "->" + newNodeNumber + newLabelStack(i).label)
        }
      }
      if (newLabelStack != null){
        newLabelStack.trimEnd(1)
      }
      return (newNodeNumber, newLabelStack)
    }
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      val modelName = node.modelInfo.modelName
      val modelID = node.modelInfo.modelID.toString
     // val transName = node.transitionInfo.transitionName//.stripSuffix(" (1)") //TODO: need to fix based on "(" - drop things after "("
      //Log.info("trans:" + transName.take(transName.indexOf("(")))
      val transID = node.transitionInfo.transitionID.toString
      val transOriginState = node.transitionInfo.transOrigin
      val transDestState = node.transitionInfo.transDest
      val transName = transOriginState.toString + " => " + transDestState.toString
      val transQuality = node.transitionInfo.transitionQuality
      val transExecutionCounter = node.transitionInfo.transCounter.toString
      val selfTransCounter = "(Transition Self-execution Times:"+ node.selfTransCounter +")"
      val edgeStyle:String = if (transQuality == TransitionQuality.backtrack) "style=dotted, color=red," else ""
      // the newlabel here is used for constructing a label for the output of the "point" graph
      val newLabel =  "[" + edgeStyle + "label = \"" + "Model Name:" + modelName  + "\\n" +
        "Model ID:" + modelID + "\\n" +
        "Transition Name:" + transName  + "\\n" +
        "Transition ID:" + transID  + "\\n" +
        "Transition Execution Counter:" + transExecutionCounter  + "\\n" +
        selfTransCounter + "\"];"
      shape.toLowerCase match {
        case "box" => //val nodeIDFrom = transName.split(" => ")(0)
                      //val nodeIDTo = transName.split(" => ")(1).stripSuffix(" (1)")
                      //out.println("  " + nodeIDFrom + "->" + nodeIDTo  + newlabel)
                      var sameTransition = false
                      if (nodeRecorder != null){
                        for (n <- nodeRecorder){
                          // check if the transition already in the nodeRecorder list
                          if (n.node.transitionInfo.transitionID == node.transitionInfo.transitionID){
                            sameTransition = true
                            n.transCounter = n.transCounter.concat(";" + node.transitionInfo.transCounter.toString)
                          }
                        }
                      }
                      if (!sameTransition){
                        val newNodeInfo = NodeInfo(node,transExecutionCounter)
                        nodeRecorder += newNodeInfo
                      }
                      display(node,level+1,0,null,nodeRecorder)
        case "point" =>   val newlabelInfo =
                            if (transOriginState  == transDestState)
                              LabelInfo(newLabel,true)
                            else LabelInfo(newLabel)
                          newLabelStack += newlabelInfo
                          // Log.info("new stack:" + newStack)
                          val result = display(node,level+1, newNodeNumber, newLabelStack)
                          newNodeNumber = result._1
                          newLabelStack = result._2
      }
    }
    if (newLabelStack != null){
      newLabelStack.trimEnd(1)
    }
    // output "box" graph
    if (level == 0 && nodeRecorder != null && nodeRecorder.nonEmpty) {
      //Log.info(nodeRecorder.toString())
      for (n <- nodeRecorder) {
        val transName = n.node.transitionInfo.transOrigin.toString + " => " + n.node.transitionInfo.transDest.toString
        val edgeStyle = if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack) "style=dotted, color=red," else ""
        if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
          out.println(n.node.transitionInfo.transDest + "[color=red];")
        out.println("  " + n.node.transitionInfo.transOrigin.toString
          + "->" + n.node.transitionInfo.transDest.toString
          + "[" + edgeStyle + "label = \"" + "Model Name:" + n.node.modelInfo.modelName  + "\\n" +
          "Model ID:" + n.node.modelInfo.modelID.toString + "\\n" +
          "Transition Name:" + transName  + "\\n" +
          "Transition ID:" + n.node.transitionInfo.transitionID.toString  + "\\n" +
          "Transition Execution Counter:" + n.transCounter  + "\\n" +
          "(Transition Self-execution Times:"+ n.node.selfTransCounter +")" + "\"];")
      }
    }
    (newNodeNumber, newLabelStack)
  }
}