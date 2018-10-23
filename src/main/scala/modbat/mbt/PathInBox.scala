package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import scala.collection.mutable.ListBuffer

case class NodeInfo(node: TrieNode, var transCounter: String) // NodeInfo is used for record  the node information used for "box" output

/** PathInBox extends PathVisualizer for showing path coverage in "Box" tree graph.
  *
  * @constructor Create a new pathInBox with a trie, and shape (Box),
  *
  * @param trie The trie that has path information stored
  * @param shape The shape should be "Box"
  */
class PathInBox(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Box", "the input of path visualizer must be Box")
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
    val nodeRecorder
      : ListBuffer[NodeInfo] = new ListBuffer[NodeInfo] // nodeRecorder is used for record node information for "box" output
    display(trie.root, 0, nodeRecorder)
    out.println("}")
  }

  def display(root: TrieNode,
              level: Int = 0,
              nodeRecorder: ListBuffer[NodeInfo] = null) {

    if (root.isLeaf) return

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      var sameTransition = false
      if (nodeRecorder != null) {
        for (n <- nodeRecorder) {
          // check if the transition already in the nodeRecorder list, and this transition is not backtracked
          if (n.node.transitionInfo.transitionID == node.transitionInfo.transitionID &&
              node.transitionInfo.transitionQuality != TransitionQuality.backtrack) {
            sameTransition = true
            n.transCounter = n.transCounter.concat(
              ";" + node.transitionInfo.transCounter.toString)
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
    if (level == 0 && nodeRecorder != null && nodeRecorder.nonEmpty) {
      for (n <- nodeRecorder) {
        val transName: String = n.node.transitionInfo.transOrigin.toString +
          " => " + n.node.transitionInfo.transDest.toString
        val edgeStyle: String =
          if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack)
            "style=dotted, color=red,"
          else ""
        if (n.node.transitionInfo.transitionQuality == TransitionQuality.backtrack) // backtrack
          out.println(n.node.transitionInfo.transDest + "[color=red];")
        out.println("  " + n.node.transitionInfo.transOrigin.toString
          + "->" + n.node.transitionInfo.transDest.toString
          + "[" + edgeStyle + "label = \"" + "M:" + n.node.modelInfo.modelName + "\\n" +
          "M-ID:" + n.node.modelInfo.modelID.toString + "\\n" +
          "T:" + transName + "\\n" +
          "T-ID:" + n.node.transitionInfo.transitionID.toString + "\\n" +
          "T-Counter:" + n.transCounter + "\\n" +
          "(T-Self:" + n.node.selfTransCounter + ")" + "\"];")
      }
    }
  }
}
