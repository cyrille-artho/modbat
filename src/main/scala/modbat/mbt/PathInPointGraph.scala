package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State

import scala.collection.mutable.ListBuffer
import scala.math.log10

/** PathInPoint extends PathVisualizer for showing path coverage in "Point" tree graph.
  *
  * @constructor Create a new pathInPoint with a trie, and shape (Point),
  *
  * @param trie The trie that has path information stored
  * @param typeName The type of the graph is point graph
  */
class PathInPointGraph(trie: Trie, val typeName: String)
    extends PathVisualizer {
  require(typeName == "Point", "the input of path visualizer must be Point")

  // PointNodeInfo is used to record the node information used for "point" output graph
  case class PointNodeInfo(node: TrieNode,
                           transHasChoices: Boolean,
                           choiceTree: ChoiceTree = null,
                           isSelfTrans: Boolean)

  // The choice node counter is used to construct the IDs of choice nodes
  private var choiceNodeCounter: Int = 0

  override def dotify() {
    out.println("digraph model {")
    out.println("  orientation = portrait;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"2\", nodesep=\"0.2\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", style=rounded, shape=\"" + "point" +
        "\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", arrowsize=\".3\", arrowhead=\"normal\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")

    // The stack is used to record label information for "point" output
    val nodeRecordStack: ListBuffer[PointNodeInfo] =
      new ListBuffer[PointNodeInfo]

    // Initial node is "none"
    val graphNoneNode: String = "None"
    out.println(
      graphNoneNode + " [shape=none, style=invis, width=0.1, height=0.1]")
    val graphRootNodeNumber: Int = 0
    out.println(graphNoneNode + "->" + graphRootNodeNumber.toString)
    // source level node on the top
    out.println("{rank = source; " + graphNoneNode + "}")

    // display
    display(trie.root, graphRootNodeNumber, nodeRecordStack)
    out.println("}")
  }

  private def display(root: TrieNode,
                      nodeNumber: Int,
                      nodeRecordStack: ListBuffer[PointNodeInfo])
    : (Int, ListBuffer[PointNodeInfo]) = {

    // newNodeNumber is used to generate the number(ID) of the node for the "point" graph
    var newNodeNumber: Int = nodeNumber
    var newNodeStack: ListBuffer[PointNodeInfo] = nodeRecordStack

    if (root.isLeaf) { // Print graph when the node in trie reaches a leaf

      if (newNodeStack != null) {
        // Draw point graph
        newNodeNumber = drawPointGraph(newNodeStack, newNodeNumber)
        // Update stack
        newNodeStack.trimEnd(1)
      }
      return (newNodeNumber, newNodeStack)
    }

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      // Check if the transition is a self-transition
      val transOriginState: State = node.transitionInfo.transOrigin
      val transDestState: State = node.transitionInfo.transDest
      val isSelfTrans: Boolean = transOriginState == transDestState

      // check if transition has choices
      val transHasChoices =
        node.transitionInfo.transitionChoicesMap != null && node.transitionInfo.transitionChoicesMap.nonEmpty

      // choiceTree records choices
      val choiceTree: ChoiceTree = new ChoiceTree()
      if (transHasChoices) {
        // transition with choices
        for ((choiceList, counter) <- node.transitionInfo.transitionChoicesMap) {
          // insert choices and choice counter into choiceTree
          choiceTree.insert(choiceList, counter)
        }
        //choiceTree.displayChoices(choiceTree.root, 0)
      }
      // Record point node information
      val newNodeInfo =
        PointNodeInfo(node, transHasChoices, choiceTree, isSelfTrans)

      newNodeStack += newNodeInfo // store node information for each transition into stack
      val result = display(node, newNodeNumber, newNodeStack)
      newNodeNumber = result._1
      newNodeStack = result._2
    }
    if (newNodeStack != null) {
      newNodeStack.trimEnd(1)
    }

    (newNodeNumber, newNodeStack)
  }

  private def drawPointGraph(newNodeStack: ListBuffer[PointNodeInfo],
                             nodeNumber: Int): Int = {
    var newNodeNumber = nodeNumber
    // output "point" graph
    var rootPointHasCircleEdge = false // rootPointIsCircle marks if the root point of the current path is a circle or not
    var numOfCirclePath = 0

    for (idx <- newNodeStack.indices) {

      newNodeNumber = newNodeNumber + 1 // update new number for the number point

      // circle edge is for current self transition or backtracked transition
      val circleEdge: Boolean = newNodeStack(idx).isSelfTrans || newNodeStack(
        idx).node.transitionInfo.transitionQuality == TransitionQuality.backtrack

      if (idx == 0) { // starting root point

        if (circleEdge) { // self transition or backtracked transition
          rootPointHasCircleEdge = true // set rootPointIsCircle to true showing the root point is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = idx
          val destNodeID: Int = idx
          numOfCirclePath = numOfCirclePath + 1 // update the number of circle path
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else {
          val originNodeID: Int = idx
          val destNodeID: Int = newNodeNumber
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        }

      } else { // non root point

        if (circleEdge && rootPointHasCircleEdge) { // the new edge is a circle again
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = idx - numOfCirclePath
          val destNodeID: Int = originNodeID
          numOfCirclePath = numOfCirclePath + 1 // update the number of circle path
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else if (circleEdge && !rootPointHasCircleEdge) {
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          val originNodeID: Int = newNodeNumber
          val destNodeID: Int = newNodeNumber
          //numOfCirclePath = numOfCirclePath + 1
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        } else if (!circleEdge && rootPointHasCircleEdge) {
          val originNodeID: Int = idx - numOfCirclePath
          val destNodeID: Int = newNodeNumber
          numOfCirclePath = numOfCirclePath + 1 // update the number of circle path
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)

          rootPointHasCircleEdge = false // next starting point is not the root point again
        } else {
          val originNodeID: Int = newNodeNumber - 1
          val destNodeID: Int = newNodeNumber
          // draw
          printOut(newNodeStack, idx, originNodeID, destNodeID)
        }
      }
    }
    newNodeNumber
  }

  private def printOut(nodeStack: ListBuffer[PointNodeInfo],
                       idx: Int,
                       originNodeID: Int,
                       destNodeID: Int): Unit = {

    val transQuality: TransitionQuality.Quality =
      nodeStack(idx).node.transitionInfo.transitionQuality

    val edgeStyle: String =
      if (transQuality == TransitionQuality.backtrack)
        "style=dotted, color=red,"
      else if (transQuality == TransitionQuality.fail)
        "color=blue,"
      else ""

    // draw transitions with choices
    if (nodeStack(idx).transHasChoices) {
      drawTransWithChoices(nodeStack(idx),
                           nodeStack(idx).choiceTree.root,
                           originNodeID,
                           destNodeID)
    } else { // draw transition, no choices
      out.println(
        originNodeID + "->" + destNodeID + createEdgeLabel(
          nodeStack(idx).node,
          edgeStyle,
          nodeStack(idx).node.transitionInfo.transCounter.toString))
    }
  }

  private def drawTransWithChoices(nodeInfo: PointNodeInfo,
                                   root: ChoiceTree#ChoiceNode,
                                   originNodeID: Int,
                                   destNodeID: Int,
                                   level: Int = 0,
                                   currentNodeID: String = "",
                                   choiceOfMaybe: Boolean = false): Unit = {

    val transID: String = nodeInfo.node.transitionInfo.transitionID.toString
    val backtracked
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack
    val failed
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.fail

    val edgeStyle: String =
      if (root.isLeaf && backtracked)
        "style=dotted, color=red,"
      else if (root.isLeaf && ((failed && choiceOfMaybe) || failed))
        "color=blue,"
      else ""

    if (root.isLeaf)
      out.println(
        currentNodeID + "->" + destNodeID + createEdgeLabel(
          nodeInfo.node,
          edgeStyle,
          root.choiceCounter.toString))

    for (choiceKey <- root.children.keySet) {
      choiceNodeCounter = choiceNodeCounter + 1
      val choiceNode = root.children(choiceKey)

      var choiceNodeStyle
        : String = " , shape=diamond, width=0.1, height=0.1, xlabel=\" Count:" + choiceNode.choiceCounter + " \"];"
      /*      var choiceNodeStyle: String =
        if (nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
          " , shape=diamond, color=red, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
        else
          " , shape=diamond, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"*/

      val choiceNodeValue = choiceNode.recordedChoice.toString
      val choiceNodeID
        : String = "\"" + transID + "-" + originNodeID.toString + "-" + destNodeID.toString + "-" +
        level.toString + "-" + choiceNodeCounter.toString + "-" + choiceNodeValue + "\""

      // check special case for failure when the recorded choice "maybe" is true
      var choiceOfMaybe: Boolean = false
      choiceNode.recordedChoice match {
        case _: Boolean =>
          if (nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.fail && choiceNode.recordedChoice
                .equals(true))
            choiceOfMaybe = true
        //choiceNodeStyle = " , shape=diamond, color= blue, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
        case _ =>
      }

      out.println(
        choiceNodeID + " [label=\"" + choiceNodeValue + "\"" + choiceNodeStyle)

      if (level == 0) {
        out.println(
          originNodeID + "->" + choiceNodeID + createEdgeLabel(
            nodeInfo.node,
            edgeStyle,
            choiceNode.choiceCounter.toString))
      } else {
        out.println(
          currentNodeID + "->" + choiceNodeID + createEdgeLabel(
            nodeInfo.node,
            edgeStyle,
            choiceNode.choiceCounter.toString))
      }

      drawTransWithChoices(nodeInfo,
                           choiceNode,
                           originNodeID,
                           destNodeID,
                           level + 1,
                           choiceNodeID,
                           choiceOfMaybe)
    }
  }

  private def createEdgeLabel(node: TrieNode,
                              edgeStyle: String,
                              count: String): String = {

    // create transition arrow
    def transitionArrow(
        transitionQuality: TransitionQuality.Quality): String = {
      transitionQuality match {
        case TransitionQuality.backtrack => "-->" //backtracked transition
        case TransitionQuality.fail      => "--|" //failed transition
        case TransitionQuality.OK        => "=>" //ok transition
      }
    }
    // set output label optional
    def labelOutputOptional(labelName: String, labelValue: String): String =
      if (Main.config.pathLabelDetail) labelName + labelValue + "\\n"
      else ""

    val modelName: String = node.modelInfo.modelName
    val modelID: String = node.modelInfo.modelID.toString

    val transOrigin: String = node.transitionInfo.transOrigin.toString
    val transDest: String = node.transitionInfo.transDest.toString
    val transName: String = transOrigin + transitionArrow(
      node.transitionInfo.transitionQuality) + transDest
    val transID: String = node.transitionInfo.transitionID.toString
    val transCounter: String = node.transitionInfo.transCounter.toString

    // executed transitions' number records
    val transExecutedRecords: String = node.transExecutedRecords.toList
      .map { case (int1, int2) => s"$int1:$int2" }
      .mkString(",")

    val nextState: String =
      if (node.transitionInfo.nextStateNextIf != null)
        node.transitionInfo.nextStateNextIf.nextState.toString
      else "null"

    val backtracked
      : Boolean = node.transitionInfo.transitionQuality == TransitionQuality.backtrack

    val nextStateOfBacktrack: String =
      if (backtracked)
        "(" + node.transitionInfo.nextStateNextIf.nextState.toString + ")"
      else ""

    // calculate penwidth
    val edgeWidth = "penwidth=\"" + (log10(
      count
        .split(";")
        .map(_.toDouble)
        .sum * 100 / Main.config.nRuns) + 1).toString + "\","

    val label: String =
      "[" + edgeStyle +
        edgeWidth +
        "label = \" " +
        labelOutputOptional("M:", modelName) +
        //"M:" + modelName + "\\n" +
        "MID:" + modelID + "\\n" +
        labelOutputOptional("T:", transName + nextStateOfBacktrack) +
        labelOutputOptional("TID:", transID) +
        labelOutputOptional("T-Path-Counter:", transCounter) +
        labelOutputOptional("next state:", nextState) +
        labelOutputOptional("Count:", transExecutedRecords) +
        //"Count:" + transExecutedRecords +
        " \"];"

    label
  }
}
