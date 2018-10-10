package modbat.cov

import modbat.dsl.Transition
import modbat.log.Log
import scala.collection.mutable.{HashMap, ListBuffer}

class Trie {
  val root: TrieNode = TrieNode()

  def insert(pathInfo:ListBuffer[(String, Int, Transition)]) = {
    var currentNode: TrieNode = root
    for (p <- pathInfo) {
      var node:TrieNode = currentNode.children.getOrElse(p._3.toString(),null)
      if (currentNode.previousTransition != null && currentNode.previousTransition == p._3) {
        currentNode.sameTransCounter += 1
       Log.info("the transition stored in trie node:" + p._3.toString() + " counter:" + currentNode.sameTransCounter)
      }else {
        if (node == null) {
          node = TrieNode()
          node.previousTransition = p._3
          node.modelInfo = (p._1,p._2)
          node.transitionInfo = (p._3.toString(), p._3.idx)
          currentNode.children.put(p._3.toString(), node)
        }else{
          Log.info("transition already exist in trie:" + node.transitionInfo)
        }
        currentNode = node
      }
    }
    currentNode.isLeaf = true
  }


  def display(root:TrieNode, level:Int = 0):Unit = {
    if (root.isLeaf) println()

    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      if (level == 0) {
        Log.info(node.modelInfo.toString() + node.transitionInfo.toString() + " ("+ node.sameTransCounter +")")
      }
      else Log.info("-"*level + node.modelInfo.toString() + node.transitionInfo.toString() + " ("+ node.sameTransCounter +")")
      display(node,level+1)
    }
  }

  def numOfPaths(root:TrieNode):Int = {
    var result = 0

    if (root.isLeaf) result += 1
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t,sys.error(s"unexpected key: $t"))
      result += numOfPaths(node)
    }
    result
  }
}


case class TrieNode() {
  var children: HashMap[String,TrieNode] = HashMap.empty[String,TrieNode] // children stores the transition in string and the next node
  var isLeaf: Boolean = false
  var sameTransCounter = 1
  var previousTransition:Transition = null
  var modelInfo: (String, Int) = _ // modelInfo stores the name of the model and its id
  var transitionInfo: (String, Int) = _ // transitionInfo store the transition and its id
}