package modbat.mbt

import modbat.log.Log
import modbat.trace.RecordedChoice

import scala.collection.mutable.HashMap

class ChoiceTree {
  class ChoiceNode() {
    // children store the transitions in string and the next nodes
    var children: HashMap[Any, ChoiceNode] = HashMap.empty[Any, ChoiceNode]
    var isLeaf: Boolean = false
    var choiceCounter: Int = 0
    var recordedChoice: Any = _
  }

  val root: ChoiceNode = new ChoiceNode()

  def insert(choiceList: List[RecordedChoice], counter: Int): Unit = {
    var currentNode: ChoiceNode = root

    for (choice <- choiceList) {

      val hasNode: Boolean =
        currentNode.children.contains(choice.recordedChoice)

      if (!hasNode) { // add new child
        // new node
        val node = new ChoiceNode()
        node.choiceCounter = counter
        node.recordedChoice = choice.recordedChoice
        currentNode.children.put(node.recordedChoice, node)
        currentNode = node // next node
      } else { // existing node
        val node = currentNode.children(choice.recordedChoice)
        if (node.recordedChoice == choice.recordedChoice) {
          node.choiceCounter = node.choiceCounter + counter
        }
        currentNode = node // next node
      }
    }
    currentNode.isLeaf = true
  }

  def displayChoices(root: ChoiceNode, level: Int = 0): Unit = {
    if (root.isLeaf) return
    for (choice <- root.children.keySet) {

      val node: ChoiceNode =
        root.children.getOrElse(choice, sys.error(s"unexpected key: $choice"))
      if (level == 0) {
        Log.debug(
          "recorded choice:" + node.recordedChoice + ", choice counter:" + node.choiceCounter)
      } else {
        Log.debug("*" * level +
          "recorded choice:" + node.recordedChoice + ", choice counter:" + node.choiceCounter)
      }
      displayChoices(node, level + 1)
    }
  }
}
