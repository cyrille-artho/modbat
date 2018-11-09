package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State

import scala.collection.mutable.ListBuffer

// LabelInfo is used to record the node information used for "point" output
case class LabelInfo(label: String,
                     transID: String,
                     transHasChoices: Boolean,
                     choiceTree: ChoiceTree = null,
                     selfTrans: Boolean,
                     transQuality: TransitionQuality.Quality)

/** PathInPoint extends PathVisualizer for showing path coverage in "Point" tree graph.
  *
  * @constructor Create a new pathInPoint with a trie, and shape (Point),
  *
  * @param trie The trie that has path information stored
  * @param shape The shape should be "Point"
  */
class PathInPointGraph(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Point", "the input of path visualizer must be Point")

  private var choiceNodeCounter
    : Int = 0 // the choice node counter is used to construct the IDs of choice nodes

  override def dotify() {
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"2\", nodesep=\"0.2\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", shape=\"" + shape.toLowerCase +
        "\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", arrowsize=\".3\", arrowhead=\"vee\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")
    val nodeRecordStack
      : ListBuffer[LabelInfo] = new ListBuffer[LabelInfo] // stack is used for record label information for "point" output
    display(trie.root, 0, nodeRecordStack)
    out.println("}")
  }

  private def display(
      root: TrieNode,
      nodeNumber: Int,
      nodeRecordStack: ListBuffer[LabelInfo]): (Int, ListBuffer[LabelInfo]) = {
    // newNodeNumber is used to generate the number(ID) of the node for the "point" graph
    var newNodeNumber: Int = nodeNumber
    var newNodeStack: ListBuffer[LabelInfo] = nodeRecordStack

    if (root.isLeaf) { // print graph when the node in trie is a leaf

      if (newNodeStack != null) {
        // draw point graph
        newNodeNumber = drawPointGraph(newNodeStack, newNodeNumber)
        // update stack
        newNodeStack.trimEnd(1)
      }
      return (newNodeNumber, newNodeStack)
    }

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      val modelName: String = node.modelInfo.modelName
      val modelID = node.modelInfo.modelID.toString
      val transID = node.transitionInfo.transitionID.toString
      val transOriginState: State = node.transitionInfo.transOrigin
      val transDestState: State = node.transitionInfo.transDest
      val selfTrans: Boolean = transOriginState == transDestState
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

      val transHasChoices = node.transitionInfo.transitionChoicesMap != null && node.transitionInfo.transitionChoicesMap.nonEmpty

      // choiceTree records choices
      val choiceTree: ChoiceTree = new ChoiceTree()
      if (transHasChoices) {
        // transition with choices
        for ((choiceList, counter) <- node.transitionInfo.transitionChoicesMap) {
          // insert choices and choice counter into choiceTree
          choiceTree.insert(choiceList, counter)
        }
        choiceTree.display(choiceTree.root, 0)
      }
      // check if the transition has the same original and target states, and if backtracked
      val newLabelInfo = LabelInfo(newLabel,
                                   transID,
                                   transHasChoices,
                                   choiceTree,
                                   selfTrans,
                                   transQuality)

      newNodeStack += newLabelInfo // store label information for each transition
      val result = display(node, newNodeNumber, newNodeStack)
      newNodeNumber = result._1
      newNodeStack = result._2
    }
    if (newNodeStack != null) {
      newNodeStack.trimEnd(1)
    }
    (newNodeNumber, newNodeStack)
  }

  private def drawPointGraph(newNodeStack: ListBuffer[LabelInfo],
                             nodeNumber: Int): Int = {
    var newNodeNumber = nodeNumber
    // output "point" graph
    var rootPointHasCircleEdge = false // rootPointIsCircle marks if the root point of the current path is a circle or not
    for (idx <- newNodeStack.indices) {

      newNodeNumber = newNodeNumber + 1 // update new number for the number point

      // circle edge is for current self transition or backtracked transition
      val circleEdge
        : Boolean = newNodeStack(idx).selfTrans || newNodeStack(idx).transQuality == TransitionQuality.backtrack

      if (idx == 0) { // starting root point

        if (circleEdge) { // self transition or backtracked transition
          rootPointHasCircleEdge = true // set rootPointIsCircle to true showing the root point is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = idx
          val destNodeID: Int = idx
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else {
          val originNodeID: Int = idx
          val destNodeID: Int = newNodeNumber
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        }

      } else { // non root point

        if (circleEdge && rootPointHasCircleEdge) { // the new edge is a circle again
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = idx - 1
          val destNodeID: Int = idx - 1
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else if (circleEdge && !rootPointHasCircleEdge) {
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = newNodeNumber
          val destNodeID: Int = newNodeNumber
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else if (!circleEdge && rootPointHasCircleEdge) {
          val originNodeID: Int = idx - 1
          val destNodeID: Int = newNodeNumber
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)

          rootPointHasCircleEdge = false // next starting point is not the root point again
        } else {
          val originNodeID: Int = newNodeNumber - 1
          val destNodeID: Int = newNodeNumber
          // TODO: draw transitions with choices
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        }
      }
    }
    newNodeNumber
  }

  private def printOut(nodeStack: ListBuffer[LabelInfo],
                       idx: Int,
                       originNodeID: Int,
                       destNodeID: Int): Unit = {
    // draw transitions with choices
    if (nodeStack(idx).transHasChoices) {
      drawTransWithChoices(nodeStack(idx).choiceTree.root,
                           nodeStack(idx).transID,
                           nodeStack(idx).label,
                           originNodeID,
                           destNodeID)
    } else { // draw transition, no choices
      out.println(originNodeID + "->" + destNodeID + nodeStack(idx).label)
    }
  }

  private def drawTransWithChoices(root: ChoiceTree#ChoiceNode,
                                   transID: String,
                                   label: String,
                                   originNodeID: Int,
                                   destNodeID: Int,
                                   level: Int = 0,
                                   currentNodeID: String = ""): Unit = {

    if (root.isLeaf) out.println(currentNodeID + "->" + destNodeID + label)

    for (choiceKey <- root.children.keySet) {
      choiceNodeCounter = choiceNodeCounter + 1
      val choiceNode = root.children(choiceKey)

      val choiceNodeStyle: String =
        " , shape=diamond, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
      val choiceNodeValue = choiceNode.recordedChoice.toString
      val choiceNodeID
        : String = "\"" + transID + "-" + originNodeID.toString + "-" + destNodeID.toString + "-" +
        level.toString + "-" + choiceNodeCounter.toString + "-" + choiceNodeValue + "\""

      out.println(
        choiceNodeID + " [label=\"" + choiceNodeValue + "\"" + choiceNodeStyle)

      if (level == 0) {
        out.println(originNodeID + "->" + choiceNodeID + label)
      } else {
        out.println(currentNodeID + "->" + choiceNodeID + label)
      }

      drawTransWithChoices(choiceNode,
                           transID,
                           label,
                           originNodeID,
                           destNodeID,
                           level + 1,
                           choiceNodeID)
    }
  }
}
