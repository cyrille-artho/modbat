package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State
import modbat.log.Log

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
      // TODO: record choices into a tree
      // choiceTree can record choices
      val choiceTree: ChoiceTree = new ChoiceTree()
      if (transHasChoices) {
        // transition with choices
        Log.info("******* print the choice list")
        for ((choiceList, counter) <- node.transitionInfo.transitionChoicesMap) {

          Log.info(
            "******* the choice list in point graph:" + choiceList + ", counter:" + counter)
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
    var rootPointIsCircle = false // rootPointIsCircle marks if the root point of the current path is a circle or not
    for (i <- newNodeStack.indices) {

      //TODO: see transition
      Log.info("******* see transition ******")
      Log.info("index of label stack:" + i)
      Log.info("transition ID:" + newNodeStack(i).transID)
      Log.info("transition has choices:" + newNodeStack(i).transHasChoices)
      Log.info(
        "transition has choice tree:" + newNodeStack(i).choiceTree.toString)
      Log.info("******* labal info:" + newNodeStack(i).label)

      newNodeNumber = newNodeNumber + 1 // update new number for the number point
      val circleEdge
        : Boolean = newNodeStack(i).selfTrans || newNodeStack(i).transQuality == TransitionQuality.backtrack

      if (i == 0) { // starting point
        // check if the root point of the current path is a circle or not
        if (circleEdge) {
          rootPointIsCircle = true // set rootPointIsCircle to true showing the root point is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println("# when i = 0, self and backtrack, and has choices")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 i,
                                 i)
          } else {
            out.println("# when i = 0, self and backtrack, and no choices")
            out.println(i + "->" + i + newNodeStack(i).label) // the root point is a circle
          }
        } else {
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println(
              "# when i = 0, no self and backtrack, but has choices, ")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 i,
                                 newNodeNumber)
          } else {
            out.println("# when i = 0, no self and backtrack, and no choices")
            out.println(i + "->" + newNodeNumber + newNodeStack(i).label)
          }
        }
      } else if (circleEdge) {
        if (rootPointIsCircle) { // the root point of the current path is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is Circle, and has choices")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 0,
                                 0)
          } else {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is Circle, and no choices, ")
            out.println(0 + "->" + 0 + newNodeStack(i).label) // same point if self or backtracked transition and
          }
        } else {
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is NOT Circle, and has choices")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 newNodeNumber,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is NOT Circle, and no choices")
            out.println(
              newNodeNumber + "->" + newNodeNumber + newNodeStack(i).label) // same point if self or backtracked transition

          }
        }
      } else {
        if (rootPointIsCircle) { // the root point of the current path is a circle
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is Circle, and has choices")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 0,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is Circle, and no choices")
            out.println(0 + "->" + newNodeNumber + newNodeStack(i).label)
          }

          rootPointIsCircle = false // next starting point is not the root point again
        } else {
          // TODO: draw transitions with choices
          if (newNodeStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is NOT Circle, and has choices")
            drawTransWithChoices(newNodeStack(i).choiceTree.root,
                                 newNodeStack(i).transID,
                                 newNodeStack(i).label,
                                 newNodeNumber - 1,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is NOT Circle, and no choices")
            out.println(
              newNodeNumber - 1 + "->" + newNodeNumber + newNodeStack(i).label)
          }

        }
      }
    }
    newNodeNumber
  }

  private def drawTransWithChoices(root: ChoiceTree#ChoiceNode,
                                   transID: String,
                                   label: String,
                                   originNodeID: Int,
                                   destNodeID: Int,
                                   level: Int = 0,
                                   currentNodeID: String = ""): Unit = {

    // val edgeStyle: String = "style=dotted, color=blue,"
    // val label: String = "[" + edgeStyle + "label = \"" + "\"];"

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

      Log.info("****** print choice node id ******")
      Log.info("the choiceNodeID:" + choiceNodeID)

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
