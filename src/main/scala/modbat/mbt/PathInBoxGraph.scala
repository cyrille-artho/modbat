package modbat.mbt

import modbat.cov.{Trie, TrieNode}

import scala.collection.mutable.ListBuffer

/** PathInBox extends PathVisualizer for showing path coverage in "Box" tree graph.
  *
  * @constructor Create a new pathInBox with a trie, and shape (Box),
  *
  * @param trie The trie that has path information stored
  * @param shape The shape should be "Box"
  */
class PathInBoxGraph(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Box", "the input of path visualizer must be Box")

  // case class NodeInfo is used for record the node information used for "box" output
  case class boxNodeInfo(node: TrieNode, var transCounter: String)

  override def dotify() {
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"2\", nodesep=\"0.2\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", style=rounded, shape=\"" + "ellipse" +
        "\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", arrowsize=\".3\", arrowhead=\"vee\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")

    val nodeRecorder
      : ListBuffer[boxNodeInfo] = new ListBuffer[boxNodeInfo] // nodeRecorder is used for record node information for "box" output
    display(trie.root, 0, nodeRecorder)
    out.println("}")
  }

  private def display(root: TrieNode,
                      level: Int = 0,
                      nodeRecorder: ListBuffer[boxNodeInfo] = null): Unit = {

    if (root.isLeaf) return

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      var sameTransition = false
      if (nodeRecorder != null) {
        for (n <- nodeRecorder) {
          // the transition already in the nodeRecorder, and the transition quality is also the same
          if (n.node.transitionInfo.transitionID == node.transitionInfo.transitionID &&
              n.node.transitionInfo.transitionQuality == node.transitionInfo.transitionQuality) {
            sameTransition = true
            // merge the value of the transition counter
            n.transCounter = n.transCounter.concat(
              ";" + node.transitionInfo.transCounter.toString)

            // merge the counter in map of choices
            for (key <- node.transitionInfo.transitionChoicesMap.keySet) {
              if (n.node.transitionInfo.transitionChoicesMap.contains(key)) {
                val mergedChoiceCoutner = n.node.transitionInfo
                  .transitionChoicesMap(key) +
                  node.transitionInfo.transitionChoicesMap(key)
                n.node.transitionInfo.transitionChoicesMap(key) =
                  mergedChoiceCoutner
              }
            }
          }
        }
      }

      if (!sameTransition) {
        val newNodeInfo =
          boxNodeInfo(node, node.transitionInfo.transCounter.toString)
        nodeRecorder += newNodeInfo
      }

      display(node, level + 1, nodeRecorder)
      // TODO: I think there's no need to repeat the same father node in the graph - Rui
    }

    // output "box" graph
    if (level == 0 && nodeRecorder != null && nodeRecorder.nonEmpty) {
      drawBoxGraph(nodeRecorder)
    }
  }

  private def drawBoxGraph(nodeRecorder: ListBuffer[boxNodeInfo]): Unit = {

    // initial node is "none"
    val graphNoneNode: String = "None"
    out.println(graphNoneNode + " [shape=none, width=0.1, height=0.1];")
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
        if (backtracked) n.node.transitionInfo.nextState.toString
        else ""

      // edge style
      val edgeStyle: String =
        if (backtracked) "style=dotted, color=red,"
        else if (failed) "color=blue,"
        else ""

      // node color style
      /*      if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
        out.println(" " + transDest + " [color=red];")
      else if (n.node.transitionInfo.transitionQuality == TransitionQuality.fail)
        out.println(" " + transDest + " [color=blue];")*/

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
        //choiceTree.displayChoices(choiceTree.root, 0)
      } else {
        // transitions without choices
        out.println(
          transOrigin + "->" + (if (backtracked) nextStateOfBacktrack
                                else transDest) + createEdgeLabel(n, edgeStyle)
        )
      }
      // jumped edge when nextIf is true
      if (n.node.transitionInfo.nextStateNextIf != null && n.node.transitionInfo.nextStateNextIf.nextIf) {
        jumpedNodeOriginNextIf =
          if (backtracked) nextStateOfBacktrack else transDest
        val jumpedNodeDestNextIf =
          n.node.transitionInfo.nextStateNextIf.nextState
        out.println(
          jumpedNodeOriginNextIf + "->" + jumpedNodeDestNextIf + "[style=dotted];")
      }
    }
  }

  private def drawTransWithChoices(nodeInfo: boxNodeInfo,
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
      if (backtracked) nodeInfo.node.transitionInfo.nextState.toString
      else ""

    val edgeStyle: String =
      if (root.isLeaf && backtracked)
        "style=dotted, color=red,"
      else if (root.isLeaf && failed && choiceOfMaybe)
        "color=blue,"
      else ""

    if (root.isLeaf)
      out.println(
        currentNodeID + "->" + (if (backtracked) nextStateOfBacktrack
                                else transDest) + createEdgeLabel(nodeInfo,
                                                                  edgeStyle))

    for (choiceKey <- root.children.keySet) {
      val choiceNode = root.children(choiceKey)

      /*      var choiceNodeStyle: String =
        if (nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
          " , shape=diamond, color=red, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
        else
          " , shape=diamond, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"*/
      var choiceNodeStyle
        : String = " , shape=diamond, width=0.1, height=0.1, xlabel=\"Choice-Counter:" + choiceNode.choiceCounter + "\"];"
      val destNodeValue = choiceNode.recordedChoice.toString
      val destNodeID = "\"" + transID + "-" + level.toString + "-" + destNodeValue + "\""

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
        out.println(
          transOrigin + "->" + destNodeID + createEdgeLabel(nodeInfo,
                                                            edgeStyle))
      } else {
        out.println(
          currentNodeID + "->" + destNodeID + createEdgeLabel(nodeInfo,
                                                              edgeStyle))
      }

      drawTransWithChoices(nodeInfo,
                           choiceNode,
                           level + 1,
                           destNodeID,
                           choiceOfMaybe)
    }
  }

  private def createEdgeLabel(nodeInfo: boxNodeInfo,
                              edgeStyle: String): String = {

    val modelName: String = nodeInfo.node.modelInfo.modelName.toString
    val modelID: String = nodeInfo.node.modelInfo.modelID.toString

    val transOrigin: String = nodeInfo.node.transitionInfo.transOrigin.toString
    val transDest: String = nodeInfo.node.transitionInfo.transDest.toString
    val transName: String = transOrigin + " => " + transDest
    val transID: String = nodeInfo.node.transitionInfo.transitionID.toString
    val transCounter: String = nodeInfo.transCounter
    val selfTransCounter: String = nodeInfo.node.selfTransCounter.toString

    val backtracked
      : Boolean = nodeInfo.node.transitionInfo.transitionQuality == TransitionQuality.backtrack

    // next state
    val nextState: String =
      if (nodeInfo.node.transitionInfo.nextState != null)
        nodeInfo.node.transitionInfo.nextState.toString
      else "null"

    val nextStateOfBacktrack: String =
      if (backtracked)
        "(backtracked to " + nodeInfo.node.transitionInfo.nextState.toString + ")\\n"
      else ""

    val label: String =
      "[" + edgeStyle + "label = \"" +
        "M:" + modelName + "\\n" +
        "M-ID:" + modelID + "\\n" +
        "T:" + transName + "\\n" + nextStateOfBacktrack +
        "T-ID:" + transID + "\\n" +
        "T-Counter:" + transCounter + "\\n" +
        "next state:" + nextState + "\\n" +
        "(T-Self:" + selfTransCounter + ")" + "\"];"

    label
  }
}
