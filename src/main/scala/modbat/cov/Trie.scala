package modbat.cov

import modbat.dsl.Transition
import modbat.log.Log
import modbat.trace.RecordedTransition
import scala.collection.mutable.{HashMap, ListBuffer}

class Trie {
  val root: TrieNode = TrieNode()

  def insert(executedTransitions:ListBuffer[RecordedTransition]) = {
    var currentNode: TrieNode = root
    for (t <- executedTransitions) {
      var node:TrieNode = currentNode.children.getOrElse(t.transition,null)
      if (currentNode.previousTransition != null && currentNode.previousTransition == t.transition) {
        currentNode.sameTransCounter += 1
       Log.info("the transition stored in trie node:" + t.transition.toString() + " counter:" + currentNode.sameTransCounter)
      }else {
        if (node == null) {
          //Log.info(" the current node got is null")
          node = TrieNode()
          node.previousTransition = t.transition
          currentNode.children.put(t.transition, node)
        }
        currentNode = node
      }
    }
    currentNode.isLeaf = true
  }


  def display(root:TrieNode, level:Int = 0):Unit = {
    if (root.isLeaf) println()

    for (t <- root.children.keySet) {
      if (level == 0) Log.info(t.toString() + " ("+ root.children.getOrElse(t,sys.error(s"unexpected key: $t")).sameTransCounter +")")
      else Log.info("-"*level + t.toString() + " ("+ root.children.getOrElse(t,sys.error(s"unexpected key: $t")).sameTransCounter +")")
      display(root.children.getOrElse(t, sys.error(s"unexpected key: $t")),level+1)
    }
  }
}


case class TrieNode() {
  var children: HashMap[Transition,TrieNode] = HashMap.empty[Transition,TrieNode]
  var isLeaf: Boolean = false
  var sameTransCounter = 1
  var previousTransition:Transition = null
}