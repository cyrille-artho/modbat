package modbat.cov

import modbat.dsl.{State, Transition}
import modbat.log.Log
import modbat.mbt.PathInfo
import modbat.mbt.TransitionQuality.Quality
import modbat.trace.RecordedChoice

import scala.collection.mutable.{HashMap, ListBuffer, Map}

/** Trie stores the path information for the path coverage. */
class Trie {
  val root: TrieNode = TrieNode()
  //var pathLengthRecorder: Map[Int, Int] = Map[Int, Int]()

  /** Insert recorded path information into trie
    *
    *  @param pathInfo The recorded path information in a listBuffer
    */
  def insert(pathInfo: ListBuffer[PathInfo]): Unit = {

    // if the the pathInfo buffer is empty, then return
    if (pathInfo.isEmpty) return

    var currentNode: TrieNode = root

    for (p <- pathInfo) {

      // Create a nodeKey and check if the node exist.
      // This key combines the transition ID and transition's quality
      val nodeKey
        : String = p.transition.idx.toString + p.transitionQuality.toString

      var childNode: TrieNode =
        currentNode.children.getOrElse(nodeKey, null)

      // Check if the new transition is a self loop and repeated in the path.
      if (currentNode.currentTransition != null && currentNode.currentTransition.idx == p.transition.idx
          && currentNode.transitionInfo.transitionQuality == p.transitionQuality) {

        // Only update the counter for this current repeatedly executed same transition of current test case
        currentNode.transExecutedCounter += 1

      } else if (childNode == null) { // new childNode situation

        // Update same repetition times happening for this transition during the whole test
        updateTransSameRepetitionTimes(currentNode)

        // Record choices into map
        var choicesMap: Map[List[RecordedChoice], Int] =
          Map[List[RecordedChoice], Int]()
        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty)
          choicesMap += (p.transition.recordedChoices -> 1)

        // Creat a new node
        childNode = TrieNode()

        // Store path information and choices into the new child node
        storePathInforIntoChildNode(childNode, p, choicesMap)

        // Create a nodeKey for linking to the child node
        val nodeKey
          : String = p.transition.idx.toString + p.transitionQuality.toString

        // Put childNode into the trie
        currentNode.children.put(nodeKey, childNode)

        currentNode = childNode // next node
      } else if (childNode != null && childNode.transitionInfo.transitionID == p.transition.idx) {
        // existing childNode situation
        // Update same repetition times happening for this transition during the whole test
        updateTransSameRepetitionTimes(currentNode)

        // Update the counter for this executed transition of current test case
        childNode.transExecutedCounter += 1
        // Update the transition counter in transition info stored
        childNode.transitionInfo.transCounter += 1
        // Update choices in map
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
      // debug code:
      /*else {
        Log.debug(
          "the missing transition:" + p.transition + ", its quality:" + p.transitionQuality + ", and nextIf:" + p.nextStateNextIf)
      }*/
    }
    // Update same repetition times happening for this transition during the whole test
    updateTransSameRepetitionTimes(currentNode)
    //if (currentNode.children.nonEmpty) Log.debug("not a leaf")
    // Set this node to leaf
    if (currentNode.children.isEmpty) currentNode.isLeaf = true
  }

  private def storePathInforIntoChildNode(
      childNode: TrieNode,
      p: PathInfo,
      choicesMap: Map[List[RecordedChoice], Int]): Unit = {

    // Update the counter for this executed transition of current test case
    childNode.transExecutedCounter += 1
    // Store current transition, model and transition information into child node
    childNode.currentTransition = p.transition
    childNode.modelInfo = ModelInfo(p.modelName, p.modelID)
    childNode.transitionInfo = TransitionInfo(
      p.transition.origin,
      p.transition.dest,
      p.transition.idx,
      1,
      p.transitionQuality,
      p.nextStateNextIf,
      choicesMap
    )
  }

  private def updateTransSameRepetitionTimes(currentNode: TrieNode): Unit = {
    // Update same repetition times happening for this transition during the whole test
    if (currentNode.transExecutedCounter != 0) {
      if (currentNode.transExecutedRecords.contains(
            currentNode.transExecutedCounter))
        currentNode.transExecutedRecords(currentNode.transExecutedCounter) += 1
      else
        currentNode.transExecutedRecords += (currentNode.transExecutedCounter -> 1)
      // Reset the counter for the next case
      currentNode.transExecutedCounter = 0
    }
  }

  /** Display tire
    *
    * @param root The TrieNode starting from root node
    * @param level The level number of the trie tree structure
    */
  def display(root: TrieNode, level: Int = 0): Unit = {
    if (root.isLeaf) {
      // debug code:
      // Log.debug("reached leaf and path ended, and the total depth (without counting loops):" + level)
      return
    }
    for (t <- root.children.keySet) {

      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      // debug code:
      if (level == 0) {
        Log.debug(
          "[" + node.modelInfo.toString + node.transitionInfo.toString +
            " counters of execution:" + node.transExecutedRecords +
            ", current depth:" + (level + 1) + checkLeaf(node) + "]")
      } else
        Log.debug(
          "-" * level + "[" + node.modelInfo.toString + node.transitionInfo.toString +
            " counters of execution:" + node.transExecutedRecords +
            ", current depth:" + (level + 1) + checkLeaf(node) + "]")
      display(node, level + 1)
    }

    def checkLeaf(node: TrieNode): String =
      if (node.isLeaf) ", leaf:end" else ""
  }

  def bfSearchT(root: TrieNode,
                key: String,
                targetLevel: Int,
                parentNodeTranID: Int,
                level: Int = 0): TrieNode = {

    if (root.isLeaf) return null //resultNode

    val v = root.children.getOrElse(key, null)
    Log.debug(key ++ ":" ++ root.children.map(i => i._1).toString())

    if (v != null && targetLevel == level) {
      if (root.currentTransition != null) {
        if (root.currentTransition.idx == parentNodeTranID) {
          return v
        } else return null //resultNode
      } else return v
    } else {
      val root2 = TrieNode()
      for (i <- root.children) {
        for (j <- i._2.children) {
          root2.children.put(j._1, j._2)
        }
      }
      if (root2.children.isEmpty)
        root2.isLeaf = true // xxx should probably be in TrieNode!
      return bfSearchT(root2, key, targetLevel, parentNodeTranID, level + 1)
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
  //  compute the longest path based on the number of transitions
  def longestPath(root: TrieNode, level: Int = 0): Int = {
    var longest = level
    if (root.isLeaf) return level

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      var currentPathlong = longestPath(node, level + 1)
      longest = if (currentPathlong > longest) currentPathlong else longest
    }

    longest
  }

  def shortestPath(root: TrieNode, level: Int = 0, pathLength: Int = 0): Int = {
    var shortest = pathLength
    if (root.isLeaf) {
      if (pathLength == 0) return level
      else if (level < pathLength) return level
      else return pathLength
    }

    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))

      shortest = shortestPath(node, level + 1, shortest)
    }
    shortest
  }

  // pathLengthRecorder returns the pathLengthResults,
  // pathLengthResults is a map that records the all length of paths (keys),
  // and the number of the same length (values), based on the number of transitions
  def pathLengthRecorder(
      root: TrieNode,
      level: Int = 0,
      pathLengthResults: Map[Int, Int] = Map[Int, Int]()): Map[Int, Int] = {
    if (root.isLeaf) {
      if (pathLengthResults.contains(level)) pathLengthResults(level) += 1
      else pathLengthResults += (level -> 1)
      return pathLengthResults
    }
    for (t <- root.children.keySet) {
      val node: TrieNode =
        root.children.getOrElse(t, sys.error(s"unexpected key: $t"))
      pathLengthRecorder(node, level + 1, pathLengthResults)
    }
    pathLengthResults
  }
}

/** TrieNode stores each current transition's information
  * and next node in the trie.
  */
case class TrieNode() {

  // children store the transitions in string and the next nodes
  var children: HashMap[String, TrieNode] = HashMap.empty[String, TrieNode]
  var isLeaf: Boolean = if (children.isEmpty) true else false

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
      s"trans choices map: $transitionChoicesMap,"

}
