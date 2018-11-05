package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.log.Log

import scala.collection.mutable.ListBuffer

case class NodeInfo(node: TrieNode, var transCounter: String) // NodeInfo is used for record  the node information used for "box" output

/** PathInBox extends PathVisualizer for showing path coverage in "Box" tree graph.
  *
  * @constructor Create a new pathInBox with a trie, and shape (Box),
  *
  * @param trie The trie that has path information stored
  * @param shape The shape should be "Box"
  */
class PathInBoxGraph(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Box", "the input of path visualizer must be Box")

  override def dotify() {
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"2\", nodesep=\"0.2\" ];")
    out.println(
      "  node [ fontname = \"Helvetica\", fontsize=\"6.0\", style=rounded, shape=\"" + shape.toLowerCase +
        "\", margin=\"0.07\"," + " height=\"0.1\" ];")
    out.println(
      "  edge [ fontname = \"Helvetica\", fontsize=\"6.0\"," + " margin=\"0.05\" ];")

    val nodeRecorder
      : ListBuffer[NodeInfo] = new ListBuffer[NodeInfo] // nodeRecorder is used for record node information for "box" output
    display(trie.root, 0, nodeRecorder)
    out.println("}")
  }

  private def display(root: TrieNode,
                      level: Int = 0,
                      nodeRecorder: ListBuffer[NodeInfo] = null): Unit = {

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
          NodeInfo(node, node.transitionInfo.transCounter.toString)
        nodeRecorder += newNodeInfo
      }

      display(node, level + 1, nodeRecorder)
    }

    // output "box" graph
    drawBoxGraph(level, nodeRecorder)

  }

  private def drawBoxGraph(level: Int,
                           nodeRecorder: ListBuffer[NodeInfo]): Unit = {
    // output "box" graph
    if (level == 0 && nodeRecorder != null && nodeRecorder.nonEmpty) {
      for (n <- nodeRecorder) {
        val transOrigin: String = n.node.transitionInfo.transOrigin.toString
        val transDest: String = n.node.transitionInfo.transDest.toString
        val transName: String = transOrigin + " => " + transDest
        val edgeStyle: String =
          if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
            "style=dotted, color=red,"
          else ""
        val modelName: String = n.node.modelInfo.modelName
        val modelID: String = n.node.modelInfo.modelID.toString
        val transitionID: String = n.node.transitionInfo.transitionID.toString

        if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
          out.println(" " + transDest + " [color=red];")
        // have choices
        if (n.node.transitionInfo.transitionChoicesMap != null && n.node.transitionInfo.transitionChoicesMap.nonEmpty) {
          Log.info("map info:" + n.node.transitionInfo.transitionChoicesMap)
          for ((choiceList, counter) <- n.node.transitionInfo.transitionChoicesMap) {

            val label: String = "[" + edgeStyle + "label = \"" +
              "M:" + modelName + "\\n" +
              "M-ID:" + modelID + "\\n" +
              "T:" + transName + "\\n" +
              "T-ID:" + transitionID + "\\n" +
              "T-Counter:" + n.transCounter + "\\n" +
              "Choice-Counter:" + counter + "\\n" +
              "(T-Self:" + n.node.selfTransCounter + ")" + "\"];"
            val choiceNodeStyle: String =
              " [shape=diamond, width=0.1, height=0.1];"
            var currentNode: String = ""

            for (i <- 0 to choiceList.length) {
              var destNode: String = ""
              if (i < choiceList.length) {
                destNode = choiceList.apply(i).recordedChoice.toString
                out.println(" " + destNode + choiceNodeStyle)
              }
              if (i == 0) {
                out.println(" " + transOrigin + "->" + destNode + label)
              } else if (i == choiceList.length) {
                out.println(" " + currentNode + choiceNodeStyle)
                out.println(" " + currentNode + "->" + transDest + label)
              } else {
                out.println(" " + currentNode + choiceNodeStyle)
                out.println(" " + currentNode + "->" + destNode + label)
              }
              currentNode = destNode
            }
          }
        } else { // no choices
          out.println(
            "  " + transOrigin + "->" + transDest +
              "[" + edgeStyle + "label = \"" + "M:" + modelName + "\\n" +
              "M-ID:" + modelID + "\\n" +
              "T:" + transName + "\\n" +
              "T-ID:" + transitionID + "\\n" +
              "T-Counter:" + n.transCounter + "\\n" +
              // "T-Choices:" + choices + "\\n" +
              "(T-Self:" + n.node.selfTransCounter + ")" + "\"];")
        }
      }
    }
  }
}
