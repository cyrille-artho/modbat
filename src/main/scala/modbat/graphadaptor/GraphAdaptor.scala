package modbat.graphadaptor

import modbat.dsl.{State, Transition}
import modbat.graph.{Edge, Graph, Node}
import modbat.mbt.{Configuration, ModelInstance}

import java.io.{FileOutputStream, IOException, PrintStream}
import scala.collection.JavaConverters.{asScalaBufferConverter, asScalaSetConverter, bufferAsJavaListConverter, mapAsScalaMapConverter}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * This class represents a graph adaptor (of class [[Graph]]). It is mainly
  * used to store a (sub-)model as a graph data structure. It is used to
  * generate test requirements (edges and edge-pair) and to cover the
  * generated test requirements using an execution path.
  */
class GraphAdaptor(val config: Configuration, val model: ModelInstance) {
  // log that could be used for debugging purposes
  //  private val log = model.mbt.log
  private var errStream: PrintStream = _

  /*
     "graph" is a public field representing the model instance.
     - "graph" has root "model.initialState".
     - "graph" has an initial number of nodes of "model.states.size" which is allowed
       to grow by adding more states and edges.
   */
  val graph: Graph[StateData, EdgeData] = new Graph(new Node(new StateData(model.initialState)))

  // call createGraph (which is the starting point for creating a graph representation of the model)
  createGraph()

  private def toLabel(origLabel: State): String = origLabel.toString.replace('/', '_')

  /**
    * Get a string representing the dot representation of transition.
    */
  private def getTransitionDotString(transition: Transition, transitionLabel: String = "",
                                     style: String = "", color: String = ""): String = {
    // add padding to label
    val labelString: String = if (transitionLabel.isEmpty) {
      transitionLabel
    } else {
      " " + transitionLabel + " "
    }

    val outgoingTransitions = model.transitions.filter(_.origin == transition.origin)
    val totalCoverage = outgoingTransitions.map(_.coverage.count).sum

    // coverage
    val coverage: Double = if (totalCoverage == 0) {
      0.0
    } else {
      transition.coverage.count * 100.0 / totalCoverage
    }

    val coverageString: String = if (coverage > 0.0) {
      "\\[ " + "%2.1f".format(coverage) + " % \\]"
    } else {
      ""
    }

    // from-state and to-state
    val fromState: String = toLabel(transition.origin)
    val toState: String = toLabel(transition.dest)

    // create the string representation of edge in dot format
    val buf = new StringBuffer(
      "  " + fromState + "\t-> " + toState + " [ label = \"" + labelString + coverageString + "\""
    )

    // add extra space before and after label because graphviz often
    // puts label right next to edge (at least graphviz version 2.26.3)
    if (style != "") {
      buf.append(" style = \"" + style + "\"")
    }
    if (color != "") {
      buf.append(" color = \"" + color + "\"")
    }
    if (coverage > 0.0) {
      buf.append(" penwidth = \"3.0\"")
    }
    buf.append(" ];")

    buf.toString
  }


  private def visitEdge(out: PrintStream, edge: Edge[StateData, EdgeData]): Unit = {
    val transition: Transition = edge.getData.transition
    val transitionLabel: String = edge.getData.transitionLabel
    val transitionType: TransitionType = edge.getData.transitionType

    transitionType match {
      case NormalTransition(_) =>
        // if the label of the current transition starts with "(", this means that the
        // current transition OVERRIDEs another transition

        if (transitionLabel.startsWith("(")) {
          out.println(getTransitionDotString(transition, transitionLabel, style = "dashed"))
        } else {
          out.println(getTransitionDotString(transition, transitionLabel))
        }

      case ExpectedExceptionTransition(isDeterministic) if isDeterministic =>
        out.println(getTransitionDotString(transition, transitionLabel, color = "red"))

      case ExpectedExceptionTransition(isDeterministic) if !isDeterministic =>
        out.println(getTransitionDotString(transition, transitionLabel, style = "dotted", color = "red"))
    }
  }

  /**
    * Print the graph represented by this GraphAdapter to a file of the given name.
    */
  def printGraphTo(fileName: String): Unit = {
    val out: PrintStream = getPrintStream(fileName)
    out.println("digraph model {")
    //    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"0.4\", nodesep=\"0.2\" ];")
    out.println("  node [ fontname = \"Helvetica\", fontsize=\"12.0\"," +
      " margin=\"0.07\" ];")
    out.println("  edge [ fontname = \"Helvetica\", fontsize=\"12.0\"," +
      " margin=\"0.05\" ];")
    // initial state
    out.println("  \"\" [ shape = \"point\", height=\"0.1\" ];")

    val rootNode: Node[StateData] = graph.getRoot
    // print an edge to the root state
    out.println("  \"\" -> " + toLabel(rootNode.getData.state))

    // traverse the graph in a pre-order breadth first manner.

    // create a discovered set of nodes and add the root to it
    val discoveredNodes: mutable.Set[Node[StateData]] = mutable.Set(rootNode)

    // graph edges
    val edges: mutable.Buffer[Edge[StateData, EdgeData]] = graph.getAllEdges.asScala

    for (edge <- edges) {
      // visit current node (the visit action of the current node is
      // to visit every edge coming out from the current node and print it)
      visitEdge(out, edge)

      // add source node and destination node to discovered nodes
      discoveredNodes.add(edge.getSource)
      discoveredNodes.add(edge.getDestination)
    }

    // print all unreachable nodes
    val undiscoveredNodes = graph.getAllNodes.asScala.diff(discoveredNodes)
    for (undiscoveredNode <- undiscoveredNodes) {
      out.println("  " + toLabel(undiscoveredNode.getData.state))
    }

    out.println("}")
    out.flush()
    out.close()
  }

  private def getPrintStream(outputFileName: String): PrintStream = {
    val pathOfOutputFile: String = outputFileName
    try {
      new PrintStream(new FileOutputStream(pathOfOutputFile), false, "UTF-8")
    } catch {
      case ioe: IOException =>
        printToErr("Cannot open file " + pathOfOutputFile + ":")
        printToErr(ioe.getMessage)
        throw ioe
    }
  }

  /**
    * Get transition label of "transition" given the configuration and
    * the model instance of this graph adaptor.
    */
  private def getTransitionLabel(transition: Transition): String = {
    if (!config.autoLabels && transition.action.label.isEmpty) {
      ""
    } else if (transition.action.transfunc != null) {
      transition.ppTrans(config.autoLabels) // String
    } else {
      ""
    }
  }

  /**
    * Add parentheses around label "(label)".
    */
  private def addParens(label: String): String = {
    if (label.equals("")) {
      ""
    } else {
      "(" + label + ")"
    }
  }

  /**
    * Create (and return) edge data for "transition" whether it is a normal transition
    * or an expected exception transition.
    */
  def createEdgeData(transition: Transition): EdgeData = {
    val transitionLabel: String = getTransitionLabel(transition)
    if (transition.expectedExceptions.isEmpty) {
      new EdgeData(
        transitionLabel = transitionLabel,
        transitionType = NormalTransition(isDeterministic = true),
        transition = transition
      )
    } else {
      val transitionLabel = transition.expectedExceptions.mkString(", ")
      new EdgeData(
        transitionLabel = transitionLabel,
        transitionType = ExpectedExceptionTransition(isDeterministic = true),
        transition = transition
      )
    }
  }

  /**
    * This method is the entry point for this graph adapter to create
    * a graph given a model instance and a configuration.
    */
  private def createGraph(): Unit = {
    for (transition <- model.transitions) {
      if (!transition.isSynthetic) {
        // create edges data for deterministic "transition" whether it is a normal transition
        // or an expected exception transition
        val transitionEdgeData = createEdgeData(transition)
        // add edge data to graph (that goes from origin state to destination state)
        graph.addEdge(transitionEdgeData.originState, transitionEdgeData.destinationState, transitionEdgeData)

        // create edge data for all nextIf (deterministic) transitions and maybeNextif (non-deterministic) transitions
        for (nextStatePredicate <- transition.nextStatePredicates) {
          val overridingTransitionEdgeData = new EdgeData(
            transitionLabel = addParens(transitionEdgeData.transitionLabel),
            transitionType = NormalTransition(isDeterministic = !nextStatePredicate.nonDet),
            transition = nextStatePredicate.target // transition
          )

          // add edge data to graph (that goes from origin state to destination state)
          graph.addEdge(
            overridingTransitionEdgeData.originState,
            overridingTransitionEdgeData.destinationState,
            overridingTransitionEdgeData
          )
        }

        // create edge data for all non-deterministic exception transitions
        for (nonDeterministicException <- transition.nonDetExceptions) {
          val nonDeterministicExceptionTransition_EdgeData = new EdgeData(
            transitionLabel = nonDeterministicException.exception.toString(),
            transitionType = ExpectedExceptionTransition(isDeterministic = false),
            transition = nonDeterministicException.target // transition
          )

          // add edge data to graph (that goes from origin state to destination state)
          graph.addEdge(
            nonDeterministicExceptionTransition_EdgeData.originState,
            nonDeterministicExceptionTransition_EdgeData.destinationState,
            nonDeterministicExceptionTransition_EdgeData
          )
        }
      }
    }
  }

  def coverTestRequirements(path: ListBuffer[EdgeData]): Unit = {
    this.graph.coverTestRequirements(
      path.map(edgeData => new Edge(edgeData.originState, edgeData.destinationState, edgeData)).asJava
    )
  }

  def updateTestRequirements(): Unit = {
    this.graph.updateTestRequirements()
  }

  def getEdgesCovered: Int = {
    this.graph.getCoverageInfoKeyPairs.asScala.getOrElse(Graph.EDGES_COVERED,
      throw new IllegalStateException("there should be edges covered info"))
  }

  def getTotalEdges: Int = {
    this.graph.getCoverageInfoKeyPairs.asScala.getOrElse(Graph.TOTAL_EDGES,
      throw new IllegalStateException("there should be total edges info"))
  }

  def getEdgePairsCovered: Int = {
    this.graph.getCoverageInfoKeyPairs.asScala.getOrElse(Graph.EDGE_PAIRS_COVERED,
      throw new IllegalStateException("there should be edge-pairs covered info"))
  }

  def getTotalEdgePairs: Int = {
    this.graph.getCoverageInfoKeyPairs.asScala.getOrElse(Graph.TOTAL_EDGE_PAIRS,
      throw new IllegalStateException("there should be total edge-pairs info"))
  }

  private def printToErr(message: String): Unit = {
    if (this.errStream != null) {
      this.errStream.println(message)
    }
  }

  def setOutStream(out: PrintStream): Unit = {
    this.graph.setOutStream(out)
  }

  def setErrStream(err: PrintStream): Unit = {
    this.errStream = err
    this.graph.setErrStream(err)
  }
}
