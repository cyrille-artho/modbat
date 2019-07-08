package modbat.cov

import modbat.dsl.{State, Transition}
import modbat.log.Log
import modbat.mbt.PathInfo
import modbat.mbt.TransitionQuality.Quality
import modbat.trace.RecordedChoice

import scala.collection.mutable.{HashMap, ListBuffer, Map}

/** Trie stores sequences of transitions (execution paths) for the path coverage. */
class Trie {
  val root: TrieNode = TrieNode()

  /** Insert stores each executed path into the trie, and
    * each trie node stores a transition
    *
    *  @param pathInfo The recorded path (a sequence of transitions) in a listBuffer
    */
  def insert(pathInfo: ListBuffer[PathInfo]): Unit = {

    // If the the pathInfo buffer is empty, then return
    if (pathInfo.isEmpty) return

    var currentNode: TrieNode = root

    for (p <- pathInfo) {

      // Create a nodeKey and check if the node exist.
      // This key combines the transition ID and transition's quality (which is the action outcome in the paper)
      val nodeKey
        : String = p.transition.idx.toString + p.transitionQuality.toString

      var childNode: TrieNode =
        currentNode.children.getOrElse(nodeKey, null)

      // Check if the new transition is a self loop and repeated in the path (self-transition).
      if (currentNode.currentTransition != null && currentNode.currentTransition.idx == p.transition.idx
          && currentNode.transitionInfo.transitionQuality == p.transitionQuality) {

        // Only update the counter for this repeatedly executed transition of current test case
        currentNode.transExecutedCounter += 1

      } else if (childNode == null) { // new childNode situation

        // Update same repetition times happening for this transition during the whole test.
        // It means to update transition repetition counter (trc) and
        // and transition path counter (tpc) mentioned in the paper
        updateTransSameRepetitionTimes(currentNode)

        // Record choices into map
        var choicesMap: Map[List[RecordedChoice], Int] =
          Map[List[RecordedChoice], Int]()
        if (p.transition.recordedChoices != null && p.transition.recordedChoices.nonEmpty)
          choicesMap += (p.transition.recordedChoices -> 1)

        // Creat a new node
        childNode = TrieNode()

        // Store executed transition from path information and its choices into the new child node
        storePathInforIntoChildNode(childNode, p, choicesMap)

        // Create a nodeKey to link the child node
        val nodeKey
          : String = p.transition.idx.toString + p.transitionQuality.toString

        // Put childNode into the trie
        currentNode.children.put(nodeKey, childNode)

        currentNode = childNode // next node
      } else if (childNode != null && childNode.transitionInfo.transitionID == p.transition.idx) {
        // existing childNode situation
        // Update same repetition times happening for this transition during the whole test.
        // It means to update transition repetition counter (trc) and
        // and transition path counter (tpc) mentioned in the paper
        updateTransSameRepetitionTimes(currentNode)

        // Update the counter for this executed transition of current test case
        childNode.transExecutedCounter += 1
        // Update the transition counter stored in transition info
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
    // It means to update transition repetition counter (trc) and
    // and transition path counter (tpc) mentioned in the paper
    updateTransSameRepetitionTimes(currentNode)
    //if (currentNode.children.nonEmpty) Log.debug("not a leaf")
    // Set this node to leaf
    if (currentNode.children.isEmpty) currentNode.isLeaf = true
  }

  // Store an executed transition into a child node
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

  // Update same repetition times happening for this transition during the whole test
  // It means to update transition repetition counter (trc) and
  // and transition path counter (tpc) mentioned in the paper.
  // trc is the key of the map and tpc is the value of the map
  private def updateTransSameRepetitionTimes(currentNode: TrieNode): Unit = {
    if (currentNode.transExecutedCounter != 0) {
      if (currentNode.transExecutedRecords.contains(
            currentNode.transExecutedCounter))
        currentNode.transExecutedRecords(currentNode.transExecutedCounter) += 1
      else
        currentNode.transExecutedRecords += (currentNode.transExecutedCounter -> 1)
      // Reset the counter for the next case
      currentNode.currentNodeCounterRecorder += currentNode.transExecutedCounter
      currentNode.transExecutedCounter = 0
    }
  }

  /** Display tire
    *
    * @param root The TrieNode starting from root node
    * @param level The level number of the trie tree structure
    */
  def display(root: TrieNode, level: Int = 0): Unit = {
    if (root.isLeaf || root == null) {
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

  // User-defined search function
  def bfSearchT(root: TrieNode,
                key: String,
                targetLevel: Int,
                parentNodeTranID: Int,
                level: Int = 0): TrieNode = {

    if (root.isLeaf) return null //resultNode

    val v = root.children.getOrElse(key, null)
    //Log.debug(key ++ ":" ++ root.children.map(i => i._1).toString())

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
    * @return Returns the number of linearly independent paths
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
  //  Compute the longest path based on the number of transitions
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
  //  Compute the shortest path based on the number of transitions
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

/** TrieNode stores each current transition
  * and next node in the trie.
  */
case class TrieNode() {

  // children store the transitions in string and the next nodes
  var children: HashMap[String, TrieNode] = HashMap.empty[String, TrieNode]
  var isLeaf: Boolean = false

  // The key of the map represents how many times a transition can be repeatedly
  // executed continuously for the current test case, and key = 1 means no repetition happens.
  // The value of the map represents how many times the same key (same repetition times)
  // happens for this transition during the whole test, and if value is 1, then it means
  // there is no same repetition times happening for this transition during the whole test
  // In the paper, this means that the key is transition repetition counter (trc) and
  // and value is transition path counter (tpc).
  var transExecutedRecords: Map[Int, Int] = Map[Int, Int]()
  // This counter counts the number of times for this current transition to occur during the current test case
  var transExecutedCounter: Int = 0
  val currentNodeCounterRecorder
    : ListBuffer[Int] = new ListBuffer[Int] // to record transExecutedCounter for current node before reset transExecutedCounter

  var currentTransition
    : Transition = null // currentTransition stores the transition in this trie node
  var modelInfo
    : ModelInfo = _ // modelInfo stores the name of the model and its id
  var transitionInfo
    : TransitionInfo = _ // transitionInfo store the transition as a string
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
  * @param transitionQuality The quality of the transition, which could be OK, Backtrack, or Fail (action outcomes)
  * @param nextStateNextIf The information for the next state and the boolean result of nextif
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
