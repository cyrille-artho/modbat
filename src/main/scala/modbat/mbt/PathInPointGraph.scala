package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State

import scala.collection.mutable.ListBuffer
import scala.math.log10
import modbat.log.Log

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
  private var backtrackedEdgeCounter: Int = 0
  private var failedEdgeCounter: Int = 0
  private var nonChoiceEdgeCounter: Int = 0
  private var choiceEdgeCounter: Int = 0
  private var cycleSelfTranCounter: Int = 0

  override def dotify(): (Int, Int, Int, Int, Int, Int, Int) = {
    out.println("digraph model {")
    out.println("  orientation = portrait;")
    out.println(
      "  graph [ rankdir = \"TB\", ranksep=\"0.1\", nodesep=\"0.05\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", shape=\"" + "point" +
        "\", margin=\"0.01\"," + " height=\"0.1\"," + " width=\"0.5\" ];")
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
    val (numNodeCount, _) =
      display(trie.root, graphRootNodeNumber, nodeRecordStack)
    out.println("}")

//    Log.info(
//      "the total number of nodes in path-based graph:" + (numNodeCount + 1))
//    Log.info(
//      "the total number of choice nodes in path-based graph: " + choiceNodeCounter)
//    Log.info(
//      "the total number of backtracked edges in path-based graph: " + backtrackedEdgeCounter)
//    Log.info(
//      "the total number of failed edges in path-based graph: " + failedEdgeCounter)
//    Log.info(
//      "the total number of non choice edges in path-based graph: " + nonChoiceEdgeCounter)
//    Log.info(
//      "the total number of choice edges in path-based graph: " + choiceEdgeCounter)
//    Log.info(
//      "the total number of cycles in path-based graph: " + cycleSelfTranCounter)

    (numNodeCount + 1,
     choiceNodeCounter,
     backtrackedEdgeCounter,
     failedEdgeCounter,
     nonChoiceEdgeCounter,
     choiceEdgeCounter,
     cycleSelfTranCounter)
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

    // draw transitions with choices
    if (nodeStack(idx).transHasChoices) {
      drawTransWithChoices(nodeStack(idx),
                           nodeStack(idx).choiceTree.root,
                           originNodeID,
                           destNodeID)
    } else { // draw transition, no choices

      val transQuality: TransitionQuality.Quality =
        nodeStack(idx).node.transitionInfo.transitionQuality

      val edgeStyle: String =
        if (transQuality == TransitionQuality.backtrack) {
          backtrackedEdgeCounter += 1
          "style=dotted, color=blue,"
        } else if (transQuality == TransitionQuality.fail) {
          failedEdgeCounter += 1
          "color=red,"
        } else {
          if (originNodeID == destNodeID) cycleSelfTranCounter += 1 //update cycle counter
          ""
        }
      nonChoiceEdgeCounter += 1
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
      if (root.isLeaf && backtracked) {
        backtrackedEdgeCounter += 1
        "style=dotted, color=blue,"
      } else if (root.isLeaf && ((failed && choiceOfMaybe) || failed)) {
        failedEdgeCounter += 1
        "color=red,"
      } else {
        ""
      }

    if (root.isLeaf) {
      choiceEdgeCounter += 1
      out.println(
        currentNodeID + "->" + destNodeID + createEdgeLabel(
          nodeInfo.node,
          edgeStyle,
          root.choiceCounter.toString))
    }

    for (choiceKey <- root.children.keySet) {
      choiceNodeCounter = choiceNodeCounter + 1
      if (!backtracked && !failed && originNodeID == destNodeID && level == 0)
        cycleSelfTranCounter += 1 //update cycle counter

      val choiceNode = root.children(choiceKey)

      var choiceNodeStyle
        : String = " , shape=diamond, width=0.05, height=0.05, fontsize=11, xlabel=\"" + (if (Main.config.pathLabelDetail)
                                                                                            choiceNode.choiceCounter
                                                                                          else
                                                                                            "") + " \"];"
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
        choiceEdgeCounter += 1
        out.println(
          originNodeID + "->" + choiceNodeID + createEdgeLabel(
            nodeInfo.node,
            edgeStyle,
            choiceNode.choiceCounter.toString))
      } else {
        choiceEdgeCounter += 1
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
    val edgeWidth = "penwidth=\"" + log10(
      count
        .split(";")
        .map(_.toDouble)
        .sum * 100.0d / Main.config.nRuns.toDouble + 1.0d).toString + "\","

    val label: String =
      "[" + edgeStyle +
        edgeWidth +
        "label = \" " +
        labelOutputOptional("M:", modelName) +
        //"M:" + modelName + "\\n" +
        labelOutputOptional("MID:", modelID) +
        //"MID:" + modelID + "\\n" +
        labelOutputOptional("T:", transName + nextStateOfBacktrack) +
        labelOutputOptional("TID:", transID) +
        labelOutputOptional("T-Path-Counter:", transCounter) +
        labelOutputOptional("next state:", nextState) +
        labelOutputOptional("", transExecutedRecords) +
        //transExecutedRecords +
        " \"];"

    label
  }
}
