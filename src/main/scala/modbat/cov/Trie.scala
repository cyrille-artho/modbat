package modbat.cov

import modbat.dsl.Transition
import modbat.log.Log
import modbat.mbt.PathInfo
import modbat.mbt.TransitionQuality.Quality

import scala.collection.mutable.{HashMap, ListBuffer}

class Trie {
  val root: TrieNode = TrieNode()
  // The insert method inserts the transition of each test into a trie data structure
  def insert(pathInfo:ListBuffer[PathInfo]) = {
    var currentNode: TrieNode = root
    for (p <- pathInfo) {
      var node:TrieNode = currentNode.children.getOrElse(p.transition.toString(),null)
      if (currentNode.currentTransition != null && currentNode.currentTransition == p.transition) {
        currentNode.selfTransCounter += 1
       Log.info("the transition stored in trie node:" + p.transition.toString() + " counter:" + currentNode.selfTransCounter)
      }else {
        if (node == null) {
          node = TrieNode()
          node.currentTransition = p.transition
          node.modelInfo = ModelInfo(p.modelName,p.modelID)
          node.transitionInfo = TransitionInfo(p.transition.toString(), p.transition.idx,1,p.transitionQuality)
          currentNode.children.put(node.transitionInfo.transitionName, node)
        }else{
          Log.info("transition already exist in trie:" + node.transitionInfo)
          if (node.transitionInfo.transitionID == p.transition.idx) {
            node.transitionInfo.transCounter = node.transitionInfo.transCounter + 1
           // node.transitionInfo = node.transitionInfo.copy(_3 = node.transitionInfo._3 + 1)
            Log.info ("got a same transition executed: " + node.transitionInfo.transCounter + " times")
          }
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
        Log.info(node.modelInfo.toString() + node.transitionInfo.toString() + " ("+ node.selfTransCounter +")")
      }
      else Log.info("-"*level + node.modelInfo.toString() + node.transitionInfo.toString() + " ("+ node.selfTransCounter +")")
      display(node,level+1)
    }
  }
  // The method numOfPaths computes the total number of paths
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
  var children: HashMap[String,TrieNode] = HashMap.empty[String,TrieNode] // children store the transitions in string and the next nodes
  var isLeaf: Boolean = false
  var selfTransCounter = 1 // this counter counts the number of times for a transition occurring to the same state itself during a test
  var currentTransition:Transition = null  // previousTransition stores the transition leading to the next state
  var modelInfo: ModelInfo  = _ // modelInfo stores the name of the model and its id
  var transitionInfo: TransitionInfo  = _ // transitionInfo store the transition as a string, its id, and the times that transition are executed
}

case class ModelInfo(modelName:String, modelID:Int)
case class TransitionInfo(transitionName:String, transitionID:Int, var transCounter:Int, transitionQuality:Quality)