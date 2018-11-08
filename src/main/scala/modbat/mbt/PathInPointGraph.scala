package modbat.mbt

import modbat.cov.{Trie, TrieNode}
import modbat.dsl.State
import modbat.log.Log

import scala.collection.mutable.ListBuffer

case class LabelInfo(
    label: String,
    transID: String,
    transHasChoices: Boolean, //TODO: a boolean parameter showing the transition has choices
    choiceTree: ChoiceTree = null,
    selfTrans: Boolean = false,
    transQuality: TransitionQuality.Quality = TransitionQuality.OK) // LabelInfo is used to record the node information used for "point" output

/** PathInPoint extends PathVisualizer for showing path coverage in "Point" tree graph.
  *
  * @constructor Create a new pathInPoint with a trie, and shape (Point),
  *
  * @param trie The trie that has path information stored
  * @param shape The shape should be "Point"
  */
class PathInPointGraph(trie: Trie, val shape: String) extends PathVisualizer {
  require(shape == "Point", "the input of path visualizer must be Point")
  private var transHasChoices: Boolean = false
  private var choiceNodeCounter: Int = 0

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

  private def display(root: TrieNode,
                      nodeNumber: Int = 0,
                      nodeRecordStack: ListBuffer[LabelInfo] = null)
    : (Int, ListBuffer[LabelInfo]) = {
    // newNodeNumber is used to generate the number(ID) of the node for the "point" graph
    var newNodeNumber: Int = nodeNumber
    var newLabelStack: ListBuffer[LabelInfo] = nodeRecordStack

    if (root.isLeaf) { // print graph when the node in trie is a leaf

      if (newLabelStack != null) {
        // draw point graph
        newNodeNumber = drawPointGraph(newLabelStack, newNodeNumber)
      }
      if (newLabelStack != null) {
        newLabelStack.trimEnd(1)
      }
      return (newNodeNumber, newLabelStack)
    }

    for (t <- root.children.keySet) {
      transHasChoices = false
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
      // get choices into a string for the output
      /*      val choices: String =
        if (node.transitionInfo.transitionChoices != null) {
          if (node.transitionInfo.transitionChoices.nonEmpty)
            node.transitionInfo.transitionChoices.toList
              .map(_.mkString(", "))
              .mkString("||\\n")
          else "Empty"
        } else "Null"*/
      // TODO: record choices into a tree
      // choiceTree can record choices
      val choiceTree: ChoiceTree = new ChoiceTree()

      if (node.transitionInfo.transitionChoicesMap != null && node.transitionInfo.transitionChoicesMap.nonEmpty) {
        // transition with choices
        transHasChoices = true //TODO: mark the transition that has choices
        Log.info("******* print the choice list")
        for ((choiceList, counter) <- node.transitionInfo.transitionChoicesMap) {

          Log.info(
            "******* the choice list in point graph:" + choiceList + ", counter:" + counter)
          // insert choices and choice counter into choiceTree
          choiceTree.insert(choiceList, counter)
        }
        choiceTree.display(choiceTree.root, 0)
      }

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
        // "T-Choices:" + choices + "\\n" +
        selfTransCounter + "\"];"
      // check if the transition has the same original and target states, and if backtracked
      val newlabelInfo =
        if (transOriginState == transDestState && transQuality == TransitionQuality.backtrack)
          LabelInfo(newLabel,
                    transID,
                    transHasChoices,
                    choiceTree,
                    true,
                    transQuality)
        else if (transOriginState == transDestState)
          LabelInfo(newLabel, transID, transHasChoices, choiceTree, true)
        else if (transQuality == TransitionQuality.backtrack)
          LabelInfo(newLabel,
                    transID,
                    transHasChoices,
                    choiceTree,
                    false,
                    transQuality)
        else LabelInfo(newLabel, transID, transHasChoices, choiceTree)
      newLabelStack += newlabelInfo // store label information for each transition
      val result = display(node, newNodeNumber, newLabelStack)
      newNodeNumber = result._1
      newLabelStack = result._2
    }
    if (newLabelStack != null) {
      newLabelStack.trimEnd(1)
    }
    (newNodeNumber, newLabelStack)
  }

  private def drawPointGraph(newLabelStack: ListBuffer[LabelInfo],
                             nodeNumber: Int): Int = {
    var newNodeNumber = nodeNumber
    // output "point" graph
    var rootPointIsCircle = false // rootPointIsCircle marks if the root point of the current path is a circle or not
    for (i <- newLabelStack.indices) {

      //TODO: see transition
      Log.info("******* see transition ******")
      Log.info("index of label stack:" + i)
      Log.info("transition ID:" + newLabelStack(i).transID)
      Log.info("transition has choices:" + newLabelStack(i).transHasChoices)
      Log.info(
        "transition has choice tree:" + newLabelStack(i).choiceTree.toString)
      Log.info("******* labal info:" + newLabelStack(i).label)
      //TODO: can use i as part of the id for the choice node

      // if (newLabelStack(i).transHasChoices) {} else {

      newNodeNumber = newNodeNumber + 1 // update new number for the number point
      // TODO: draw transitions with choices
      /*      if (newLabelStack(i).transHasChoices) {
        drawTransWithChoices(newLabelStack(i).choiceTree.root,
                             newLabelStack(i).transID,
                             originNodeID,
                             destNodeID)
      }*/

      if (i == 0) { // starting point
        // check if the root point of the current path is a circle or not
        if (newLabelStack(i).selfTrans || newLabelStack(i).transQuality == TransitionQuality.backtrack) {
          rootPointIsCircle = true // set rootPointIsCircle to true showing the root point is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println("# when i = 0, self and backtrack, and has choices")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 i,
                                 i)
          } else {
            out.println("# when i = 0, self and backtrack, and no choices")
            out.println(i + "->" + i + newLabelStack(i).label) // the root point is a circle
          }
        } else {
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println(
              "# when i = 0, no self and backtrack, but has choices, ")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 i,
                                 newNodeNumber)
          } else {
            out.println("# when i = 0, no self and backtrack, and no choices")
            out.println(i + "->" + newNodeNumber + newLabelStack(i).label)
          }
        }
      } else if (newLabelStack(i).selfTrans || newLabelStack(i).transQuality == TransitionQuality.backtrack) {
        if (rootPointIsCircle) { // the root point of the current path is a circle
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is Circle, and has choices")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 0,
                                 0)
          } else {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is Circle, and no choices, ")
            out.println(0 + "->" + 0 + newLabelStack(i).label) // same point if self or backtracked transition and
          }
        } else {
          newNodeNumber = newNodeNumber - 1 // node number should the same as previous one
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is NOT Circle, and has choices")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 newNodeNumber,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, self and backtrack, root Point Is NOT Circle, and no choices")
            out.println(
              newNodeNumber + "->" + newNodeNumber + newLabelStack(i).label) // same point if self or backtracked transition

          }
        }
      } else {
        if (rootPointIsCircle) { // the root point of the current path is a circle
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is Circle, and has choices")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 0,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is Circle, and no choices")
            out.println(0 + "->" + newNodeNumber + newLabelStack(i).label)
          }

          rootPointIsCircle = false // next starting point is not the root point again
        } else {
          // TODO: draw transitions with choices
          if (newLabelStack(i).transHasChoices) {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is NOT Circle, and has choices")
            drawTransWithChoices(newLabelStack(i).choiceTree.root,
                                 newLabelStack(i).transID,
                                 newLabelStack(i).label,
                                 newNodeNumber - 1,
                                 newNodeNumber)
          } else {
            out.println(
              "# when i is not 0, NOT self and backtrack, root Point Is NOT Circle, and no choices")
            out.println(
              newNodeNumber - 1 + "->" + newNodeNumber + newLabelStack(i).label)
          }

        }
      }
      //}
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
