package modbat.test

import modbat.dsl._

class PredActions extends Model {
  def odd(i: Int) = ((i % 2) != 0)

  def printOne { System.out.println("one") }
  def printTwo { System.out.println("two") }
  def printThree { System.out.println("three") }

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

