package modbat.mbt

import modbat.cov.TrieNode
import modbat.log.Log
import math.log10
import scala.collection.mutable.ListBuffer
import util.control.Breaks._

/** PathInStateGraph extends PathVisualizer for showing path coverage in "State" tree graph.
  *
  * @param typeName The type of the graph is state graph
  * @param graphInitNode The name of the initial node in the graph (only used for the generated file name)
  */
class PathInStateGraph(val root: TrieNode,
                       val typeName: String,
                       val graphInitNode: String)
    extends PathVisualizer {
  require(typeName == "State", "the input of path visualizer must be Ellipse")

  // class StateNodeInfo is used for record the node information used for "State" output graph
  class StateNodeInfo(val node: TrieNode,
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
      "  graph [ rankdir = \"TB\", ranksep=\"0.5\", nodesep=\"0.18\", size=\"8!\" ];") //ranksep="0.08", nodesep="0.18" for small graphs; ranksep="0.5", nodesep="0.1" for big graphs
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"11.0\", shape=\"" + "ellipse" +
        "\", margin=\"0.01\"," + " height=\"0.1\"," + " width=\"0.5\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", arrowsize=\".3\", arrowhead=\"normal\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")

    val nodeRecorder
      : ListBuffer[StateNodeInfo] = new ListBuffer[StateNodeInfo] // nodeRecorder is used for record node information for "State" output graph

    // display SG/FSG
    display(root, 0, nodeRecorder)

    out.println("}")
    // return counters
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
      // use abstractions to merge transitions' counters and choices's counters
      if (Main.config.pathCoverageGraphMode.equals("abstracted"))
        if (nodeRecorder != null) {
          breakable { // break this loop if found the same transition
            for (n <- nodeRecorder) {
              // the transition already in the nodeRecorder, and the transition quality is also the same
              if (n.node.transitionInfo.transitionID == node.transitionInfo.transitionID &&
                  n.node.transitionInfo.transitionQuality == node.transitionInfo.transitionQuality &&
                  n.node.transitionInfo.nextStateNextIf.nextState.toString == node.transitionInfo.nextStateNextIf.nextState.toString) {
                // set sameTransition flag to true
                sameTransition = true

                // merge the value of the transition counter
                n.transCounter = n.transCounter.concat(
                  ";" + node.transitionInfo.transCounter.toString)

                // get executed transitions' number records with a string format: trc:tpc, trc:tpc, ...
                val transExecutedRecords: String =
                  node.transExecutedRecords.toList
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

                break
              }
              /*else {
              // debug code:
              Log.debug(
                "--- print debug --- NOT same transition:" + node.transitionInfo.transOrigin +
                  " =>" + node.transitionInfo.transDest +
                  ", " + node.transitionInfo.transitionID + ", " +
                  node.transitionInfo.transitionQuality + ", " + node.transitionInfo.nextStateNextIf.nextState)

              //sameTransition = false
            }*/
            }
          }
        }

      if (!sameTransition) {
        // get executed transitions' number records with a string format: trc:tpc, trc:tpc, ...
        val transExecutedRecords: String = node.transExecutedRecords.toList
          .map { case (int1, int2) => s"$int1:$int2" }
          .mkString(",")

        val newNodeInfo =
          new StateNodeInfo(node,
                            node.transitionInfo.transCounter.toString,
                            transExecutedRecords)
        nodeRecorder += newNodeInfo
      }

      display(node, level + 1, nodeRecorder)
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

    for (n <- nodeRecorder) {

      val backtracked
        : Boolean = n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack
      val failed
        : Boolean = n.node.transitionInfo.transitionQuality == TransitionQuality.fail

      // S_origin
      val transOrigin: String = n.node.transitionInfo.transOrigin.toString
      // S_dest
      val transDest: String =
        if (backtracked)
          n.node.transitionInfo.nextStateNextIf.nextState.toString
        else n.node.transitionInfo.transDest.toString

      def updateCounters = {
        // update counters
        if (backtracked)
          backtrackedEdgeCounter += 1 // update backtracked edges counter
        else if (failed)
          failedEdgeCounter += 1 // update failed edges counter
        else if (transOrigin == transDest) cycleSelfTranCounter += 1 //update cycle counter
        nonChoiceEdgeCounter += 1 // update back normal edges counter
      }

      // choiceTree is a trie to store choices
      val choiceTree: ChoiceTree = new ChoiceTree()

      if (n.node.transitionInfo.transitionChoicesMap != null && n.node.transitionInfo.transitionChoicesMap.nonEmpty) {
        // transition with choices situation
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
        // transitions without choices situation

        // edge style for edges only connecting state nodes
        val edgeStyle: String =
          if (backtracked) "style=dotted, color=blue,"
          else if (failed) "color=red,"
          else ""
        // edge label
        val edgeLabel: String = createEdgeLabel(n, edgeStyle, n.transCounter)

        Main.config.pathCoverageGraphMode match {
          case "full" =>
            // output all the edges according to trc and tpc counters
            for (trc_tpc <- n.transExecutedRecords.split(",")) {
              for (i <- 0 until trc_tpc.split(":")(1).toInt) {
                for (j <- 0 until trc_tpc.split(":")(0).toInt) {
                  // update counters
                  updateCounters
                  // draw
                  drawOneEdge(transOrigin, transDest, edgeLabel)
                }
              }
            }
          case "abstracted" =>
            // update counters
            updateCounters
            // draw
            drawOneEdge(transOrigin, transDest, edgeLabel)
        }
      }

      // jumped edge when nextIf is true
      if (n.node.transitionInfo.nextStateNextIf != null && n.node.transitionInfo.nextStateNextIf.nextIf) {
        val jumpedNodeOriginNextIf = transDest
        val jumpedNodeDestNextIf =
          n.node.transitionInfo.nextStateNextIf.nextState.toString

        jumpedEdgeCounter += 1 // update counters for jumped edges
        // draw
        drawOneEdge(jumpedNodeOriginNextIf,
                    jumpedNodeDestNextIf,
                    "[style=dotted];")
      }
    }
  }

  private def drawTransWithChoices(nodeInfo: StateNodeInfo,
                                   root: ChoiceTree#ChoiceNode,
                                   level: Int = 0,
                                   currentNodeID: String,
                                   choiceOfMaybe: Boolean = false): Unit = {

    val backtracked
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack
    val failed
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.fail

    // S_origin
    val transOrigin: String = nodeInfo.node.transitionInfo.transOrigin.toString
    // S_dest
    val transDest: String =
      if (backtracked)
        nodeInfo.node.transitionInfo.nextStateNextIf.nextState.toString
      else nodeInfo.node.transitionInfo.transDest.toString
    // transition ID
    val transID: String = nodeInfo.node.transitionInfo.transitionID.toString

    def updateCounters = {
      // update counters
      if (root.isLeaf && backtracked)
        backtrackedEdgeCounter += 1 // update backtracked edge counter
      else if (root.isLeaf && ((failed && choiceOfMaybe) || failed))
        failedEdgeCounter += 1 // update failed edge counter

      if (!root.isLeaf) choiceNodeCounter = choiceNodeCounter + 1 // update the choice node counter
      if (!root.isLeaf && level == 0 && !backtracked && !failed && transOrigin == transDest)
        cycleSelfTranCounter += 1 //update cycle counter

      choiceEdgeCounter += 1 // update choice edge counter
    }

    val edgeStyle: String =
      if (root.isLeaf && backtracked) "style=dotted, color=blue,"
      else if (root.isLeaf && ((failed && choiceOfMaybe) || failed))
        "color=red,"
      else ""

    if (root.isLeaf) {
      // update counters
      updateCounters
      val lastStepEdgelabel: String =
        createEdgeLabel(nodeInfo, edgeStyle, root.choiceCounter.toString)
      // draw
      drawOneEdge(currentNodeID, transDest, lastStepEdgelabel)
    }

    for (choiceKey <- root.children.keySet) {
      // get choice node
      val choiceNode = root.children(choiceKey)

      // choice node style
      val choiceNodeStyle
        : String = " , shape=diamond, width=0.2, height=0.3, fontsize=11, xlabel=\"" + (if (Main.config.pathLabelDetail)
                                                                                          choiceNode.choiceCounter
                                                                                        else
                                                                                          "") + "\"];"
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

      val stepEdgeLabel: String =
        createEdgeLabel(nodeInfo, edgeStyle, choiceNode.choiceCounter.toString)

      Main.config.pathCoverageGraphMode match {
        case "full" =>
          for (cc <- 0 until choiceNode.choiceCounter) {
            // update counters
            updateCounters
            val destNodeValue = choiceNode.recordedChoice.toString
            val destNodeID = "\"" + transID + "-" + level.toString + "-" + destNodeValue + "-" + nodeInfo.node.transitionInfo.transitionQuality + "-" + choiceNodeCounter + "-" + cc + "\""
            // draw choice node
            out.println(
              destNodeID + " [label=\"" + destNodeValue + "\"" + choiceNodeStyle)

            if (level == 0) {
              // draw
              drawOneEdge(transOrigin, destNodeID, stepEdgeLabel)
            } else {
              // draw
              drawOneEdge(currentNodeID, destNodeID, stepEdgeLabel)
            }

            drawTransWithChoices(nodeInfo,
                                 choiceNode,
                                 level + 1,
                                 destNodeID,
                                 choiceOfMaybe)

          }
        case "abstracted" =>
          // update counters
          updateCounters
          val destNodeValue = choiceNode.recordedChoice.toString
          val destNodeID = "\"" + transID + "-" + level.toString + "-" + destNodeValue + "-" + nodeInfo.node.transitionInfo.transitionQuality + "-" + choiceNodeCounter + "\""
          // draw choice node
          out.println(
            destNodeID + " [label=\"" + destNodeValue + "\"" + choiceNodeStyle)

          if (level == 0) {
            // draw
            drawOneEdge(transOrigin, destNodeID, stepEdgeLabel)
          } else {
            // draw
            drawOneEdge(currentNodeID, destNodeID, stepEdgeLabel)
          }
          drawTransWithChoices(nodeInfo,
                               choiceNode,
                               level + 1,
                               destNodeID,
                               choiceOfMaybe)
      }
    }
  }

  private def drawOneEdge(origin: String, dest: String, label: String) {
    out.print(origin + "->" + dest + label)
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
    var edgeWidth = ""
    if (Main.config.pathCoverageGraphMode.equals("abstracted"))
      edgeWidth = "penwidth=\"" + log10(
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
        "\"];"
    label
  }

}
