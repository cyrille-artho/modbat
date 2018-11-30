package modbat.cov

import modbat.dsl.{State, Transition}
import modbat.log.Log
import modbat.mbt.{PathInfo, TransitionQuality}
import modbat.mbt.TransitionQuality.Quality
import modbat.trace.RecordedChoice

import scala.collection.mutable.{HashMap, ListBuffer, Map}

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

      // Create a nodeKey and check if the node exist.
      val nodeKey
        : String = p.transition.idx.toString + p.transitionQuality.toString
      var childNode: TrieNode =
        currentNode.children.getOrElse(nodeKey, null)

      // Check if the new transition is a self loop in the path.
      if (currentNode.currentTransition != null && currentNode.currentTransition.idx == p.transition.idx) {

        // Update the counter for this current executed transition of current test case
        currentNode.transExecutedCounter += 1
        Log.debug(
          "****** print debug ****** currentNode update selfTransExecuteCounter:" + currentNode.transExecutedCounter)

      } else if (childNode == null) { // new node situation

        // Update same repetition times happening for this transition during the whole test
        updateTransSameRepetitionTimes(currentNode)

        // Record choices into map
        var choicesMap: Map[List[RecordedChoice], Int] =
          Map[List[RecordedChoice], Int]()
        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty)
          choicesMap += (p.transition.recordedChoices -> 1)

        // Creat a new node
        childNode = TrieNode()

        // Update the counter for this executed transition of current test case
        childNode.transExecutedCounter += 1

        Log.debug(
          "****** print debug ****** currentNode selfTransRepeatMap when node=null:" + currentNode.transExecutedRecords
            .mkString(", "))
        Log.debug(
          "****** print debug ****** child Node selfTransExecuteCounter when node=null:" + childNode.transExecutedCounter)

        childNode.currentTransition = p.transition
        childNode.modelInfo = ModelInfo(p.modelName, p.modelID)
        childNode.transitionInfo = TransitionInfo(p.transition.origin,
                                                  p.transition.dest,
                                                  p.transition.idx,
                                                  1,
                                                  p.transitionQuality,
                                                  p.transition.nextStateNextIf,
                                                  choicesMap)
        // Create a nodeKey and new child node
        val nodeKey
          : String = p.transition.idx.toString + p.transitionQuality.toString

        currentNode.children.put(nodeKey, childNode)

        currentNode = childNode // next node
      } else if (childNode != null && childNode.transitionInfo.transitionID == p.transition.idx) { // existing node situation

        // Update same repetition times happening for this transition during the whole test
        updateTransSameRepetitionTimes(currentNode)

        // Update the counter for this executed transition of current test case
        childNode.transExecutedCounter += 1

        Log.debug(
          "****** print debug ****** currentNode selfTransRepeatMap when node!=null:" + currentNode.transExecutedRecords
            .mkString(", "))
        Log.debug(
          "****** print debug ****** child Node selfTransExecuteCounter when node!=null:" + childNode.transExecutedCounter)

        childNode.transitionInfo.transCounter = childNode.transitionInfo.transCounter + 1

        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty) {
          if (childNode.transitionInfo.transitionChoicesMap.contains(
                p.transition.recordedChoices))
            childNode.transitionInfo
              .transitionChoicesMap(p.transition.recordedChoices) =
                childNode.transitionInfo.transitionChoicesMap(
                  p.transition.recordedChoices) + 1
          else
            childNode.transitionInfo.transitionChoicesMap += (p.transition.recordedChoices -> 1)
        }

        currentNode = childNode // next node
      }
    }
    // Update same repetition times happening for this transition during the whole test
    updateTransSameRepetitionTimes(currentNode)
    // Set this node to leaf
    currentNode.isLeaf = true
  }

  private def updateTransSameRepetitionTimes(currentNode: TrieNode): Unit = {
    // Update same repetition times happening for this transition during the whole test
    if (currentNode.transExecutedCounter != 0) {
      if (currentNode.transExecutedRecords.contains(
            currentNode.transExecutedCounter))
        currentNode.transExecutedRecords(currentNode.transExecutedCounter) += 1
      else
        currentNode.transExecutedRecords += (currentNode.transExecutedCounter -> 1)
      // Reset the counter for the next text case
      currentNode.transExecutedCounter = 0
    }
  }

  /** Display tire
    *
    * @param root The TrieNode starting from root node
    * @param level The level number of the trie tree structure
    */
  def display(root: TrieNode, level: Int = 0): Unit = {
    if (root.isLeaf) return
    for (t <- root.children.keySet) {

      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      if (level == 0) {
        Log.debug(
          "[" + node.modelInfo.toString + node.transitionInfo.toString + "]")
      } else
        Log.debug(
          "-" * level + "[" + node.modelInfo.toString + node.transitionInfo.toString + "]")
      display(node, level + 1)
    }
  }

  /** Compute the number of paths
    *
    * @param root The TrieNode starting from root node
    * @return Returns the number of paths
    */
  def numOfPaths(root: TrieNode): Int = {
    var leave = 0

    if (root == null) return 0
    if (root.isLeaf) return 1
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      leave += numOfPaths(node)
    }
    leave
  }
}

/** TrieNode stores each current transition's information
  * and next node in the trie.
  */
case class TrieNode() {

  // children store the transitions in string and the next nodes
  var children: HashMap[String, TrieNode] = HashMap.empty[String, TrieNode]
  var isLeaf: Boolean = false

  // The key of the map represents how many times a transition can be repeatedly
  // executed continuously for the current test case, and key = 1 means no repetition happens.
  // The value of the map represents how many times the same key (same repetition times)
  // is happened for this transition during the whole test, and if value is 1, then it means
  //  there is no same repetition times happening for this transition during the whole test
  var transExecutedRecords: Map[Int, Int] = Map[Int, Int]()
  // This counter counter how many times for this current transition occurs during the current test case
  var transExecutedCounter: Int = 0

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
case class ModelInfo(modelName: String, modelID: Int) {
  override def toString: String =
    s" model name: $modelName, model ID: $modelID."
}

/** TransitionInfo stores a transition's information.
  *
  * @constructor Create a new transitionInfo with a transOrigin, transDest,
  *              transitionID, transCounter, and transitionQuality.
  * @param transOrigin The origin state of the transition
  * @param transDest The target state of the transition
  * @param transitionID The transition's ID
  * @param transCounter The number of time that transition is executed in a path
  * @param transitionQuality The quality of the transition, which could be OK, Backtrack, or Fail
  * @param transitionChoicesMap The choices of each transition for each tests stored, and a counter to counter same choices
  */
case class TransitionInfo(
    transOrigin: State,
    transDest: State,
    transitionID: Int,
    var transCounter: Int,
    transitionQuality: Quality,
    nextStateNextIf: Transition#NextStateNextIf,
    transitionChoicesMap: Map[List[RecordedChoice], Int]) {
  override def toString: String =
    s" trans: $transOrigin=>$transDest, " +
      s"trans Origin: $transOrigin, trans Dest: $transDest, transitionID: $transitionID, " +
      s"trans counter: $transCounter, transition quality: $transitionQuality, " +
      s"nextIf: $nextStateNextIf, " +
      s"trans choices map: $transitionChoicesMap."

}
