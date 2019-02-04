package modbat.test

import modbat.dsl._

class PredActions extends Model {
  def odd(i: Int) = ((i % 2) != 0)

  def printOne { println("one") }
  def printTwo { println("two") }
  def printThree { println("three") }

  // transitions
  "same" -> "same" := {
    chooseIf({ () => odd(1) } -> printOne _,
    	 { () => odd(2) } -> printTwo _,
    	 { () => odd(3) } -> printThree _)
  }
  "same" -> "same" := {
    chooseIf({ () => odd(0) } -> printTwo _,
    	 { () => odd(2) } -> printTwo _,
    	 { () => odd(4) } -> printTwo _)
  }
}

