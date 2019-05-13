package modbat.mbt

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream

import modbat.dsl.State
import modbat.dsl.Transition
import modbat.log.Log

import modbat.util.SourceInfo.InternalAction
import modbat.util.SourceInfo.Choice
import modbat.util.SourceInfo.Launch

class Dotify(val model: MBT, outFile: String = "") {
  var out: PrintStream = null

  def init {
    assert (outFile != "")
    val fullOutFile = Main.config.dotDir + File.separatorChar + outFile
    try {
      out = new PrintStream(new FileOutputStream(fullOutFile), false, "UTF-8")
    } catch {
      case ioe: IOException => {
	Log.error("Cannot open file " + fullOutFile + ":")
	Log.error(ioe.getMessage)
	throw ioe
      }
    }
  }

  def pad(label: String) = {
    if (label.isEmpty) {
      label
    } else {
      " " + label + " "
    }
  }

  def toLabel(origLabel: State) = origLabel.toString.replace('/', '_')

  def printAction(a: InternalAction, prev: String, newLabel: String,
		  cov: Double, child: String = "") = {
    if (a == null) {
      printEdge(prev, newLabel, "", "", "", cov)
    } else {
      a match {
	case c: Choice => {
	  c.choices.foreach {
	    ch => printEdge(prev, newLabel, ch, "dashed", "red", cov)
	  }
	}
	case l: Launch => {
	  printEdge(prev, child, "", "", "blue", cov)
	  printEdge(prev, newLabel, "", "", "", cov)
	}
      }
    }
    newLabel
  }

  def covP(count: Int, total: Int) = {
    if (total == 0) {
      0.0
    } else {
      count * 100.0 / total
    }
  }

  def printTrans(tr: Transition, label: String = "", style: String = "",
		color: String = "") {
    val outgoing = model.transitions.filter(_.origin == tr.origin)
    val totalCov = outgoing.map(_.coverage.count).sum
    val internalActions = tr.launchesAndChoices
    var prev = toLabel(tr.origin)
    var prevAction: InternalAction = null
    var prevChild = ""
    val cov = covP(tr.coverage.count, totalCov)
    if (modbat.mbt.Main.config.showChoices) {
      var n = 0
      internalActions.foreach {
	a => {
	  var newLabel: String = null
	  a match {
	    case c: Choice => {
	      val newLabel = "__choice__" + n
	      out.println("  " + newLabel +
			  " [ label = \"?\", shape=\"box\" " +
			  "width=\"0.2\" height=\"0.2\" ]")
	      prev = printAction(prevAction, prev, newLabel, cov)
	      prevChild = ""
	    }
	    case l: Launch =>
	      val newLabel = "__launch__" + n
	      out.println("  " + newLabel +
			  " [ label = \"!\", shape=\"box\" " +
			  "width=\"0.2\" height=\"0.2\" ]")
	      prevChild = "__child__" + n
	      out.println("  " + prevChild +
			  " [ label = \"" + l.launchedModel +
			  "\", shape=\"component\" ]")
	      out.println("  { rank=\"same\"; " +
			  newLabel + " " + prevChild + " }")
	      prev =
		printAction(prevAction, prev, newLabel, cov, prevChild)
	  }
	  prevAction = a
	  n = n + 1
	}
      }
    }
    if (prevAction == null) {
      printEdge(prev, toLabel(tr.dest), pad(label), style, color, cov)
    } else {
      printAction(prevAction, prev, toLabel(tr.dest), cov, prevChild)
    }
  }

  def covStr(covP: Double) = {
    if (covP > 0.0) {
      "\\[ " + "%2.1f".format(covP) + " % \\]"
    } else {
      ""
    }
  }

  def printEdge(from: String, to: String, label: String, style: String,
		color: String, cov: Double) {
    val buf =
      new StringBuffer("  " + from + "\t-> " + to
		       + " [ label = \"" + label + covStr(cov) + "\"")
    // add extra space before and after label because graphviz often
    // puts label right next to edge (at least graphviz version 2.26.3)
    if (style != "") {
      buf.append(" style = \"" + style + "\"")
    }
    if (color != "") {
      buf.append(" color = \"" + color + "\"")
    }
    if (cov > 0.0) {
      buf.append(" penwidth = \"3.0\"")
    }
    buf.append(" ];")
    out.println(buf.toString())
  }

  def ppTrans(tr: Transition) = {
    if (!Main.config.autoLabels && (tr.action.label.isEmpty)) {
      ""
    } else if (tr.action.transfunc != null) {
      tr.ppTrans()
    } else {
      ""
    }
  }

  def addParens(label: String) = {
    if (label.equals("")) {
      ""
    } else {
      "(" + label + ")"
    }
  }

  def dotifyEdge(tr: Transition) {
    val label = ppTrans(tr)
    if (tr.expectedExceptions.isEmpty) {
      printTrans(tr, label)
    } else {
      val labels =
	tr.expectedExceptions.map (t =>
	  t.toString.replace("Exception", "Exc."))
      printTrans (tr, labels.mkString(", "), color="red")
    }
    for (nextSt <- tr.nextStatePredicates) {
      printTrans(nextSt.target, addParens(label), style = "dashed")
    }
  }

  def dotify(coverage: Boolean = false) {
    val ret = init
    out.println("digraph model {")
    out.println("  orientation = landscape;")
    out.println("  graph [ rankdir = \"TB\", ranksep=\"0.4\", nodesep=\"0.2\" ];")
    out.println("  node [ fontname = \"Helvetica\", fontsize=\"12.0\"," +
		" margin=\"0.07\" ];")
    out.println("  edge [ fontname = \"Helvetica\", fontsize=\"12.0\"," +
		" margin=\"0.05\" ];")
    // initial state
    out.println("  \"\" [ shape = \"point\", height=\"0.1\" ];")
    out.println("  \"\" -> " + toLabel(model.initialState))
    for (tr <- model.transitions) {

      if (!tr.isSynthetic) {
	dotifyEdge(tr)

	for (exc <- tr.nonDetExceptions) {
	  val label = exc.exception.toString.replace("Exception", "Exc.")
	  printTrans(exc.target, label, style = "dotted", color = "red")
	}
      }
    }
    out.println("}")
  }
}
