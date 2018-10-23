package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State

import scala.collection.mutable.ListBuffer

case class LabelInfo(label: String,
                     selfTrans: Boolean = false,
                     transQuality: TransitionQuality.Quality =
                       TransitionQuality.OK) // LabelInfo is used for record the label information used for "point" output

class PathInPoint(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Point", "the input of path visualizer must be Point")
  override def dotify() {
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println(
      "  graph [ rankdir = \"TB\", ranksep=\"0.3\", nodesep=\"0.2\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", shape=\"" + shape.toLowerCase +
        "\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")
    val labelRecoderStack
      : ListBuffer[LabelInfo] = new ListBuffer[LabelInfo] // stack is used for recode label information for "point" output
    display(trie.root, 0, labelRecoderStack)
    out.println("}")
  }

  def display(root: TrieNode,
              nodeNumber: Int = 0,
              labelRecoderStack: ListBuffer[LabelInfo] = null)
    : (Int, ListBuffer[LabelInfo]) = {

    var newNodeNumber
      : Int = nodeNumber // newNodeNumber is used to generate the number(ID) of the node for the "point" graph
    var newLabelStack: ListBuffer[LabelInfo] = labelRecoderStack

    if (root.isLeaf) {
      if (newLabelStack != null) {
        // output "point" graph
        for (i <- newLabelStack.indices) {
          newNodeNumber = newNodeNumber + 1
          if (i == 0)
            out.println(i + "->" + newNodeNumber + newLabelStack(i).label)
          else if (newLabelStack(i).selfTrans || newLabelStack(i).transQuality == TransitionQuality.backtrack) {
            newNodeNumber = newNodeNumber - 1
            out.println(
              newNodeNumber + "->" + newNodeNumber + newLabelStack(i).label)
          } else
            out.println(
              newNodeNumber - 1 + "->" + newNodeNumber + newLabelStack(i).label)
        }
      }
      if (newLabelStack != null) {
        newLabelStack.trimEnd(1)
      }
      return (newNodeNumber, newLabelStack)
    }
    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      val modelName: String = node.modelInfo.modelName
      val modelID = node.modelInfo.modelID.toString
      val transID = node.transitionInfo.transitionID.toString
      val transOriginState: State = node.transitionInfo.transOrigin
      val transDestState: State = node.transitionInfo.transDest
      val transName = transOriginState.toString + " => " + transDestState.toString
      val transQuality: TransitionQuality.Quality =
        node.transitionInfo.transitionQuality
      val transExecutionCounter = node.transitionInfo.transCounter.toString
      val selfTransCounter = "(T-Self:" + node.selfTransCounter + ")"
      val edgeStyle: String =
        if (transQuality == TransitionQuality.backtrack)
          "style=dotted, color=red,"
        else ""
      // the newlabel here is used for constructing a label for the output of the "point" graph
      val newLabel = "[" + edgeStyle + "label = \"" + "M:" + modelName + "\\n" +
        "M-ID:" + modelID + "\\n" +
        "T:" + transName + "\\n" +
        "T-ID:" + transID + "\\n" +
        "T-Counter:" + transExecutionCounter + "\\n" +
        selfTransCounter + "\"];"
      val newlabelInfo =
        if (transOriginState == transDestState && transQuality == TransitionQuality.backtrack)
          LabelInfo(newLabel, true, transQuality)
        else if (transOriginState == transDestState)
          LabelInfo(newLabel, true)
        else if (transQuality == TransitionQuality.backtrack)
          LabelInfo(newLabel, false, transQuality)
        else LabelInfo(newLabel)
      newLabelStack += newlabelInfo
      val result = display(node, newNodeNumber, newLabelStack)
      newNodeNumber = result._1
      newLabelStack = result._2
    }
    if (newLabelStack != null) {
      newLabelStack.trimEnd(1)
    }
    (newNodeNumber, newLabelStack)
  }

}
