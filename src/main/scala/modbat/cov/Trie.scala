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
      // check and get node in trie

      // todo: create a nodeKey and check if the node exist
      val nodeKey
        : String = p.transition.idx.toString + p.transitionQuality.toString
      var node: TrieNode =
        currentNode.children.getOrElse(nodeKey, null)
      /*      var node: TrieNode =
        currentNode.children.getOrElse(p.transition.idx, null)*/

      Log.debug("---*print debug*---node:" + node)

      // check if the new transition is a self loop in the path
      //TODO: currentTransition could be null
      if (currentNode.currentTransition != null && currentNode.currentTransition.idx == p.transition.idx) {
        currentNode.selfTransCounter += 1
      } else if (node == null) { // new node situation

        // record choices into map
        var choicesMap: Map[List[RecordedChoice], Int] =
          Map[List[RecordedChoice], Int]()
        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty)
          choicesMap += (p.transition.recordedChoices -> 1)

        // new node
        node = TrieNode()
        Log.debug("---*print debug*---quality:" + p.transitionQuality)
        node.qualityBuffer += p.transitionQuality // todo: add transition quality to buffer
        node.currentTransition = p.transition
        node.modelInfo = ModelInfo(p.modelName, p.modelID)
        node.transitionInfo = TransitionInfo(p.transition.origin,
                                             p.transition.dest,
                                             p.transition.idx,
                                             1,
                                             p.transitionQuality,
                                             p.transition.nextStateNextIf,
                                             choicesMap)
        // todo: create a nodeKey and check if the node exist
        val nodeKey
          : String = p.transition.idx.toString + p.transitionQuality.toString
        currentNode.children.put(nodeKey, node)
        //currentNode.children.put(node.transitionInfo.transitionID, node)
        currentNode = node // next node
      } else if (node != null && node.transitionInfo.transitionID == p.transition.idx) { // existing node situation
        // update transition counter
        Log.debug(
          "---*print debug*---node.transitionInfo:" + node.transitionInfo.toString)
        Log.debug("---*print debug*---p.transition.idx:" + p.transition.idx)
        Log.debug(
          "---*print debug*---recorded transition Q:" + node.transitionInfo.transitionQuality)
        Log.debug("---*print debug*---new transition Q:" + p.transitionQuality)
        node.qualityBuffer += p.transitionQuality // todo: add transition quality to buffer
        Log.debug(
          "---*print debug*---quality buffer:" + node.qualityBuffer.mkString(
            ", "))
        Log.debug(
          "--*print debug*---all same quality:" + node.qualityBuffer.toList
            .forall(_ == node.qualityBuffer.toList.head))
        Log.debug(
          "--------------------------------------------------------------")

        node.transitionInfo.transCounter = node.transitionInfo.transCounter + 1

        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty) {
          if (node.transitionInfo.transitionChoicesMap.contains(
                p.transition.recordedChoices))
            node.transitionInfo
              .transitionChoicesMap(p.transition.recordedChoices) =
                node.transitionInfo.transitionChoicesMap(
                  p.transition.recordedChoices) + 1
          else
            node.transitionInfo.transitionChoicesMap += (p.transition.recordedChoices -> 1)
        }

        currentNode = node // next node
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
    if (root.isLeaf) return
    for (t <- root.children.keySet) {

      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      if (level == 0) {
        Log.debug("[" +
          node.modelInfo.toString + node.transitionInfo.toString + " (" + "self:" + node.selfTransCounter + ") ]")
      } else
        Log.debug(
          "-" * level + "[" + node.modelInfo.toString + node.transitionInfo.toString +
            " (" + "self:" + node.selfTransCounter + ") ]")
      display(node, level + 1)
    }
  }

  /** Compute the number of paths
    *
    * @param root The TrieNode starting from root node
    * @return Returns the number of paths
    */
  /*  def numOfPaths(root: TrieNode): Int = {
    var result = 0

    if (root.isLeaf) result += 1
    for (t <- root.children.keySet) {
      val node = root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      result += numOfPaths(node)
    }
    result
  }*/

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
  var qualityBuffer: ListBuffer[TransitionQuality.Quality] =
    new ListBuffer[TransitionQuality.Quality] //todo: transition quality buffer, need to clean/remove it later
  var children
    : HashMap[String, TrieNode] = HashMap.empty[String, TrieNode] // children store the transitions in string and the next nodes
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
