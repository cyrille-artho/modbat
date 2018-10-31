package modbat.cov

import modbat.dsl.{State, Transition}
import modbat.log.Log
import modbat.mbt.PathInfo
import modbat.mbt.TransitionQuality.Quality
import scala.collection.mutable.{HashMap, ListBuffer}

/** Trie stores the path information for the path coverage. */
class Trie {
  val root: TrieNode = TrieNode()

  /** Insert recorded path information into trie
    *
    *  @param pathInfo The recorded path information in a listBuffer
    */
  def insert(pathInfo: ListBuffer[PathInfo]): Unit = {
    var currentNode: TrieNode = root

    for (p <- pathInfo) {
      // check and get node in trie
      var node: TrieNode =
        currentNode.children.getOrElse(p.transition.idx, null)
      // check if the new transition is a self loop in the path
      if (currentNode.currentTransition != null && currentNode.currentTransition.idx == p.transition.idx) {
        currentNode.selfTransCounter += 1
      } else {
        if (node == null) {
          // new node
          node = TrieNode()
          node.currentTransition = p.transition
          node.modelInfo = ModelInfo(p.modelName, p.modelID)
          node.transitionInfo = TransitionInfo(p.transition.origin,
                                               p.transition.dest,
                                               p.transition.idx,
                                               1,
                                               p.transitionQuality)
          currentNode.children.put(node.transitionInfo.transitionID, node)
        } else {
          // existing node
          if (node.transitionInfo.transitionID == p.transition.idx) {
            node.transitionInfo.transCounter = node.transitionInfo.transCounter + 1
          }
        }
        currentNode = node
      }
    }
    currentNode.isLeaf = true
  }

  /** Display tire
    *
    * @param root The TrieNode starting from root node
    * @param level The level number of the trie tree structure
    */
  def display(root: TrieNode, level: Int = 0): Unit = {
    if (root.isLeaf) return //Log.debug("")
    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      if (level == 0) {
        Log.info(
          node.modelInfo.toString + node.transitionInfo.toString + " (" + node.selfTransCounter + ")")
      } else
        Log.info(
          "-" * level + node.modelInfo.toString + node.transitionInfo.toString +
            " (" + node.selfTransCounter + ")")
      display(node, level + 1)
    }
  }

  /** Compute the number of paths
    *
    * @param root The TrieNode starting from root node
    * @return Returns the number of paths
    */
  def numOfPaths(root: TrieNode): Int = {
    var result = 0

    if (root.isLeaf) result += 1
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      result += numOfPaths(node)
    }
    result
  }
}

/** TrieNode stores each current transition's information
  * and next node in the trie.
  */
case class TrieNode() {
  var children
    : HashMap[Int, TrieNode] = HashMap.empty[Int, TrieNode] // children store the transitions in string and the next nodes
  var isLeaf: Boolean = false
  var selfTransCounter = 1 // this counter counts the number of times for a transition occurring to the same state itself during a test
  var currentTransition
    : Transition = null // previousTransition stores the transition leading to the next state
  var modelInfo
    : ModelInfo = _ // modelInfo stores the name of the model and its id
  var transitionInfo
    : TransitionInfo = _ // transitionInfo store the transition as a string, its id, and the times that transition are executed
}

/** ModelInfo stores a model's information.
  *
  * @constructor Create a new modelInfo with a modelName and modelID.
  * @param modelName The model's name
  * @param modelID The model's ID
  */
case class ModelInfo(modelName: String, modelID: Int)

/** TransitionInfo stores a transition's information.
  *
  * @constructor Create a new transitionInfo with a transOrigin, transDest,
  *              transitionID, transCounter, and transitionQuality.
  * @param transOrigin The origin state of the transition
  * @param transDest The target state of the transition
  * @param transitionID The transition's ID
  * @param transCounter The number of time that transition is executed in a path
  * @param transitionQuality The quality of the transition, which could be OK, Backtrack, or Fail
  */
case class TransitionInfo(transOrigin: State,
                          transDest: State,
                          transitionID: Int,
                          var transCounter: Int,
                          transitionQuality: Quality)
