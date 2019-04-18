package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.log.Log
import math.log10
import scala.collection.mutable.ListBuffer

/** PathInStateGraph extends PathVisualizer for showing path coverage in "State" tree graph.
  *
  * @param trie The trie that has path information stored
  * @param typeName The type of the graph is state graph
  */
class PathInStateGraph(trie: Trie, val typeName: String)
    extends PathVisualizer {
  require(typeName == "State", "the input of path visualizer must be Ellipse")

  // case class StateNodeInfo is used for record the node information used for "State" output graph
  case class StateNodeInfo(node: TrieNode,
                           var transCounter: String,
                           var transExecutedRecords: String)

  private var choiceNodeCounter: Int = 0
  private var backtrackedEdgeCounter: Int = 0
  private var failedEdgeCounter: Int = 0
  private var nonChoiceEdgeCounter: Int = 0
  private var choiceEdgeCounter: Int = 0
  private var cycleSelfTranCounter: Int = 0
  private var jumpedEdgeCounter: Int = 0

  override def dotify(): (Int, Int, Int, Int, Int, Int, Int) = {
    out.println("digraph model {")
    out.println("  orientation = portrait;")
    out.println(
      "  graph [ rankdir = \"TB\", ranksep=\"0.5\", nodesep=\"0.1\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", shape=\"" + "ellipse" +
        "\", margin=\"0.01\"," + " height=\"0.1\"," + " width=\"0.5\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", arrowsize=\".3\", arrowhead=\"normal\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")

    val nodeRecorder
      : ListBuffer[StateNodeInfo] = new ListBuffer[StateNodeInfo] // nodeRecorder is used for record node information for "State" output graph

    //display
    display(trie.root, 0, nodeRecorder)

    out.println("}")

//    Log.info(
//      "the total number of choice nodes in state-based graph: " + choiceNodeCounter)
//    Log.info(
//      "the total number of backtracked edges in state-based graph: " + backtrackedEdgeCounter)
//    Log.info(
//      "the total number of failed edges in state-based graph: " + failedEdgeCounter)
//    Log.info(
//      "the total number of non choice edges in state-based graph: " + nonChoiceEdgeCounter)
//    Log.info(
//      "the total number of choice edges in state-based graph: " + choiceEdgeCounter)
//    Log.info(
//      "the total number of cycles in path-based graph: " + cycleSelfTranCounter)

    (jumpedEdgeCounter,
     choiceNodeCounter,
     backtrackedEdgeCounter,
     failedEdgeCounter,
     nonChoiceEdgeCounter,
     choiceEdgeCounter,
     cycleSelfTranCounter)
  }

  private def display(root: TrieNode,
                      level: Int = 0,
                      nodeRecorder: ListBuffer[StateNodeInfo] = null): Unit = {

    if (root.isLeaf) return

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      var sameTransition = false
      if (nodeRecorder != null) {
        for (n <- nodeRecorder) {
          // the transition already in the nodeRecorder, and the transition quality is also the same
          if (n.node.transitionInfo.transitionID == node.transitionInfo.transitionID &&
              n.node.transitionInfo.transitionQuality == node.transitionInfo.transitionQuality &&
              n.node.transitionInfo.nextStateNextIf.nextState == node.transitionInfo.nextStateNextIf.nextState) { // fixed nextif problem -Rui
            sameTransition = true
            // merge the value of the transition counter
            n.transCounter = n.transCounter.concat(
              ";" + node.transitionInfo.transCounter.toString)

            // get executed transitions' number records
            val transExecutedRecords: String = node.transExecutedRecords.toList
              .map { case (int1, int2) => s"$int1:$int2" }
              .mkString(",")
            // merge the executed transitions's number records:
            n.transExecutedRecords =
              n.transExecutedRecords.concat(";" + transExecutedRecords)

            // merge the counter in map of choices
            for (key <- node.transitionInfo.transitionChoicesMap.keySet) {
              if (n.node.transitionInfo.transitionChoicesMap.contains(key)) {
                val mergedChoiceCoutner = n.node.transitionInfo
                  .transitionChoicesMap(key) + node.transitionInfo
                  .transitionChoicesMap(key)
                n.node.transitionInfo.transitionChoicesMap(key) =
                  mergedChoiceCoutner
              } else {
                n.node.transitionInfo.transitionChoicesMap += (key -> node.transitionInfo
                  .transitionChoicesMap(key))
              }
            }
          }
        }
      }

      if (!sameTransition) {
        // get executed transitions' number records
        val transExecutedRecords: String = node.transExecutedRecords.toList
          .map { case (int1, int2) => s"$int1:$int2" }
          .mkString(",")

        val newNodeInfo =
          StateNodeInfo(node,
                        node.transitionInfo.transCounter.toString,
                        transExecutedRecords)
        nodeRecorder += newNodeInfo
      }

      display(node, level + 1, nodeRecorder)
      // I think there's no need to repeat the same prefix node in the graph - Rui
    }

    // output "State" graph
    if (level == 0 && nodeRecorder != null && nodeRecorder.nonEmpty) {
      drawStateGraph(nodeRecorder)
    }
  }

  private def drawStateGraph(nodeRecorder: ListBuffer[StateNodeInfo]): Unit = {

    // initial node is "none"
    val graphNoneNode: String = "None"
    out.println(
      graphNoneNode + " [shape=none, style=invis, width=0.1, height=0.1];")
    val graphRootNode: String =
      nodeRecorder.head.node.transitionInfo.transOrigin.toString
    out.println(graphNoneNode + "->" + graphRootNode + ";")

    // source level node on the top
    out.println("{rank = source; " + graphNoneNode + "}")

    var jumpedNodeOriginNextIf: String = ""

    for (n <- nodeRecorder) {

      val transOrigin: String = n.node.transitionInfo.transOrigin.toString
      val transDest: String = n.node.transitionInfo.transDest.toString
      val backtracked
        : Boolean = n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack
      val failed
        : Boolean = n.node.transitionInfo.transitionQuality == TransitionQuality.fail

      val nextStateOfBacktrack: String =
        if (backtracked)
          n.node.transitionInfo.nextStateNextIf.nextState.toString
        else ""

      // debug code:
      //Log.debug("before drew the transition:" + transOrigin + " => " + transDest + ", its nextif:" + n.node.transitionInfo.nextStateNextIf)

      // choiceTree can record choices
      val choiceTree: ChoiceTree = new ChoiceTree()

      if (n.node.transitionInfo.transitionChoicesMap != null && n.node.transitionInfo.transitionChoicesMap.nonEmpty) {
        // transition with choices
        for ((choiceList, counter) <- n.node.transitionInfo.transitionChoicesMap) {
          // insert choices and choice counter into choiceTree
          choiceTree.insert(choiceList, counter)
        }
        // draw Choices with transitions
        drawTransWithChoices(n, choiceTree.root, 0, "")
        // display choice tree for debug
        if (Main.config.logLevel == Log.Debug)
          choiceTree.displayChoices(choiceTree.root, 0)
      } else {
        // transitions without choices
        // edge style
        val edgeStyle: String =
          if (backtracked) {
            backtrackedEdgeCounter += 1 // update backtracked edges counter
            "style=dotted, color=blue,"
          } else if (failed) {
            failedEdgeCounter += 1 // update failed edges counter
            "color=red,"
          } else {
            if (transOrigin == transDest) cycleSelfTranCounter += 1 //update cycle counter
            ""
          }
        nonChoiceEdgeCounter += 1 // update back normal edges counter
        out.println(
          transOrigin + "->" + (if (backtracked) nextStateOfBacktrack
                                else
                                  transDest) + createEdgeLabel(n,
                                                               edgeStyle,
                                                               n.transCounter)
        )
      }
      // debug code:
      //Log.debug(" after drew the transition:" + n.node.transitionInfo.transOrigin + " => " + n.node.transitionInfo.transDest + ", its nextif:" + n.node.transitionInfo.nextStateNextIf)

      // jumped edge when nextIf is true
      if (n.node.transitionInfo.nextStateNextIf != null && n.node.transitionInfo.nextStateNextIf.nextIf) {
        jumpedNodeOriginNextIf =
          if (backtracked) nextStateOfBacktrack else transDest
        val jumpedNodeDestNextIf =
          n.node.transitionInfo.nextStateNextIf.nextState

        /*        // debug code:
        Log.debug(
          "--- print debug --- transition that needs to jump:" + n.node.transitionInfo.transDest +
            " => " + n.node.transitionInfo.transDest + ", nextif:" + n.node.transitionInfo.nextStateNextIf)
        Log.debug(
          "--- print debug --- jumpedNodeOriginNextIf:" + jumpedNodeOriginNextIf)
        Log.debug(
          "--- print debug --- jumpedNodeDestNextIf:" + jumpedNodeDestNextIf)*/
        jumpedEdgeCounter += 1
        out.println(
          jumpedNodeOriginNextIf + "->" + jumpedNodeDestNextIf + "[style=dotted];")
      }
    }
  }

  private def drawTransWithChoices(nodeInfo: StateNodeInfo,
                                   root: ChoiceTree#ChoiceNode,
                                   level: Int = 0,
                                   currentNodeID: String,
                                   choiceOfMaybe: Boolean = false): Unit = {

    val transOrigin: String = nodeInfo.node.transitionInfo.transOrigin.toString
    val transDest: String = nodeInfo.node.transitionInfo.transDest.toString
    val transID: String = nodeInfo.node.transitionInfo.transitionID.toString
    val backtracked
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack
    val failed
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.fail
    val nextStateOfBacktrack: String =
      if (backtracked)
        nodeInfo.node.transitionInfo.nextStateNextIf.nextState.toString
      else ""

    val edgeStyle: String =
      if (root.isLeaf && backtracked) {
        backtrackedEdgeCounter += 1 // update backtracked edge counter
        "style=dotted, color=blue,"
      } else if (root.isLeaf && ((failed && choiceOfMaybe) || failed)) {
        failedEdgeCounter += 1 // update failed edge counter
        "color=red,"
      } else {
        ""
      }

    if (root.isLeaf) {
      choiceEdgeCounter += 1 // update choice edge counter
      out.println(
        currentNodeID + "->" + (if (backtracked) nextStateOfBacktrack
                                else transDest) + createEdgeLabel(
          nodeInfo,
          edgeStyle,
          root.choiceCounter.toString))
    }

    for (choiceKey <- root.children.keySet) {
      choiceNodeCounter = choiceNodeCounter + 1 // update the choice node counter
      if (!backtracked && !failed && transOrigin == transDest && level == 0)
        cycleSelfTranCounter += 1 //update cycle counter

      val choiceNode = root.children(choiceKey)
      /*      var choiceNodeStyle: String =
        if (nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
          " , shape=diamond, color=red, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
        else
          " , shape=diamond, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"*/
      var choiceNodeStyle
        : String = " , shape=diamond, width=0.2, height=0.3, xlabel=\"" + choiceNode.choiceCounter + "\"];"

      val destNodeValue = choiceNode.recordedChoice.toString
      val destNodeID = "\"" + transID + "-" + level.toString + "-" + destNodeValue + "-" + nodeInfo.node.transitionInfo.transitionQuality + "-" + choiceNodeCounter + "\""

      var choiceOfMaybe: Boolean = false
      // check special case for failure when the recorded choice "maybe" is true
      choiceNode.recordedChoice match {
        case _: Boolean =>
          if (nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.fail && choiceNode.recordedChoice
                .equals(true))
            choiceOfMaybe = true
        //choiceNodeStyle = " , shape=diamond, color=blue, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
        case _ =>
      }

      out.println(
        destNodeID + " [label=\"" + destNodeValue + "\"" + choiceNodeStyle)

      if (level == 0) {
        choiceEdgeCounter += 1 // update choice edge counter
        out.println(
          transOrigin + "->" + destNodeID + createEdgeLabel(
            nodeInfo,
            edgeStyle,
            choiceNode.choiceCounter.toString))
      } else {
        choiceEdgeCounter += 1 // update choice edge counter
        out.println(
          currentNodeID + "->" + destNodeID + createEdgeLabel(
            nodeInfo,
            edgeStyle,
            choiceNode.choiceCounter.toString))
      }

      drawTransWithChoices(nodeInfo,
                           choiceNode,
                           level + 1,
                           destNodeID,
                           choiceOfMaybe)
    }
  }

  private def createEdgeLabel(nodeInfo: StateNodeInfo,
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

    val modelName: String = nodeInfo.node.modelInfo.modelName.toString
    val modelID: String = nodeInfo.node.modelInfo.modelID.toString

    val transOrigin: String = nodeInfo.node.transitionInfo.transOrigin.toString
    val transDest: String = nodeInfo.node.transitionInfo.transDest.toString
    val transName: String = transOrigin + transitionArrow(
      nodeInfo.node.transitionInfo.transitionQuality) + transDest
    val transID: String = nodeInfo.node.transitionInfo.transitionID.toString
    val transCounter: String = nodeInfo.transCounter

    val transExecutedRecords: String = nodeInfo.transExecutedRecords

    // next state
    val nextState: String =
      if (nodeInfo.node.transitionInfo.nextStateNextIf != null)
        nodeInfo.node.transitionInfo.nextStateNextIf.nextState.toString
      else "null"

    val backtracked
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack

    val nextStateOfBacktrack: String =
      if (backtracked)
        "(" + nodeInfo.node.transitionInfo.nextStateNextIf.nextState.toString + ")"
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
        //labelOutputOptional("", transExecutedRecords) +
        transExecutedRecords +
        "\"];"
    label
  }

}
