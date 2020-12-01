# Modbat tutorial #

This repository contains introductory exercises for using Modbat, a model-based tester.
The tool is included in the repository.

## Requirements ##

* Scala 2.11.X (tested with 2.11.12)
* Java 11 (tested with 11.0.3).
* Modbat (provided).

## Overview of examples ##

1. modbat/tutorial/simple: A simple model of a LinkedList.
2. modbat/tutorial/iterator: A more complex model that includes the usage of iterators.
3. modbat/tutorial/listit: A model showing list iterators with multiple states, and a few other Modbat features.

## Slides ##

[https://github.com/cyrille-artho/modbat/blob/master/mbt.pdf](Slides in PDF)

## Getting started

This tutorial works by cloning the git repository and then trying the different steps locally:

```bash
git clone https://github.com/cyrille-artho/modbat.git
cd modbat
./gradlew assemble
cd src/test/scala/modbat/tutorial
```

## How to compile and run ##

All scripts are in `src/test/scala/modbat/tutorial`.

* Working directory: `cd src/test/scala/modbat/tutorial`
* Compilation: `sh compile.sh`
* Simple example: `sh runSimpleList.sh`
* Complex example: `sh runLinkedList.sh`
* Iterator example: `sh runListIt.sh`

All examples initially show failing test cases.
In this case, the Java collection classes are correct, so each model has a flaw.
The problems in the models can be fixed with one or a few lines of code.

## A simple model: Java collections ##

The first model tests four operations on a Java LinkedList: add, remove, clear, and size.
The file can be found in subdirectory `simple` (in `src/test/scala/modbat/tutorial`).

```scala
class SimpleListModel extends Model {
  val N = 10 // range of integers to choose from
  val collection = new LinkedList[Integer] // the "system under test"
  var n = 0 // Number of elements in the collection

  def add {
    val element = Integer.valueOf(choose(0, N))
    val ret = collection.add(element)
    n += 1
    assert(ret)
  }

  def clear {
    collection.clear
    n = 0
  }

  def remove {
    val obj = Integer.valueOf(choose(0, N))
    val res = collection.remove(obj)
    n = n - 1
  }

  def size {
    assert (collection.size == n,
	    "Predicted size: " + n +
	    ", actual size: " + collection.size)
  }

  "main" -> "main" := add
  "main" -> "main" := size
  "main" -> "main" := clear
  "main" -> "main" := remove
}
```

### Test using the simple model ###

	cd src/test/scala/modbat/tutorial
	sh compile.sh
	sh runSimpleList.sh
	[INFO] 5 tests executed, 0 ok, 5 failed.
	[INFO] 2 types of test failures:
	[INFO] 1) java.lang.AssertionError: assertion failed:
	          Predicted size: 0, actual size: 1 at size:
	[INFO]    ba471c1085a01750 2251f4042ff65867 89a677f51847fa26 …
	[INFO] 2) java.lang.AssertionError: assertion failed:
	          Predicted size: 1, actual size: 2 at size:
	[INFO]    fd53cd70667aea0
	[INFO] 1 states covered (100 % out of 1),
	[INFO] 4 transitions covered (100 % out of 4).
	[INFO] Random seed for next test would be: 4f9dd7062e2f7ae4

	real	0m0.493s
	user	0m0.505s
	sys 	0m0.078s
	
This output shows that all five test sequences fail. Two identical types
of assertion errors with slightly different messages are generated.
The failure also shows the random seeds of each failing test.

A test can be replayed as follows:

	scala -classpath . modbat.jar \
	  -s=89a677f51847fa26 -n=1 modbat.tutorial.simple.SimpleListModel

Each failed test also produces a trace file, e. g., 89a677f51847fa26.err.

### Analysis of the trace file ###

	[WARNING] java.lang.AssertionError:assertion failed:
	          Predicted size: 0, actual size: 1 occurred, aborting.
	[ERROR] java.lang.AssertionError:assertion failed:
	        Predicted size: 0, actual size: 1
	[ERROR] 	at scala.Predef$.assert(Predef.scala:170)
	[ERROR] 	at modbat.dsl.Model$class.assert(Model.scala:82)
	[ERROR] 	at modbat.tutorial.simple.SimpleListModel.assert(SimpleListModel.scala:6)
	[ERROR] 	at modbat.tutorial.simple.SimpleListModel.size(SimpleListModel.scala:30)
	        	...
	[WARNING] Error found, model trace:
	[WARNING] modbat/tutorial/simple/SimpleListModel.scala:35: add; choices = (1)
	[WARNING] modbat/tutorial/simple/SimpleListModel.scala:36: size
	[WARNING] modbat/tutorial/simple/SimpleListModel.scala:38: remove; choices = (4)
	[WARNING] modbat/tutorial/simple/SimpleListModel.scala:36: size

Sequence leading to failure: add(1), check size, remove(4), check size.

## Complex model: Java collections and iterators ##

* A _collection_ holds a number of data items.
* An _iterator_ can access these data items sequentially.
* An iterator is _valid_ as long as the underlying collection has not been modified.
* _hasNext_ queries if an iterator has more elements available.
* If an iterator goes beyond the last element, NoSuchElementException is thrown.
* If the collection has been modified, ConcurrentModificationException is thrown.

## Compile and run

You compile the model as before with `sh compile.sh`.
You run the model by using `sh runLinkedList.sh`, a different shell script that runs this model and generates 1000 tests.

### How to orchestrate multiple models ###

```scala
abstract class CollectionModel extends Model {
  val collection: Collection[Integer] // the "system under test"
  def iterator {
    val it = collection.iterator()
    val modelIt = new IteratorModel(this, it)
    launch(modelIt)	
  }
```

* _launch_ activates a new model instance.
* In this example, the instance is initialized with a reference to the current model and the iterator.

### Iterator model ###

```scala
class IteratorModel(val dataModel: CollectionModel,
                    val it: Iterator[Integer]) extends Model {

  var pos = 0
  val version = dataModel.version
  	
  def valid = (version == dataModel.version)

  def actualSize = dataModel.collection.size

  def hasNext {
    if (valid) {
      assert ((pos < actualSize) == it.hasNext)
    } else {
      it.hasNext
    }
  }

  def next {
    require (valid)
    require (pos < actualSize)
    it.next
    pos += 1
  }

  def failingNext { // throws NoSuchElementException
    require (valid)
    require (pos >= actualSize)
    it.next
  }

  def concNext { // throws ConcurrentModificationException
    require(!valid)
    it.next
  }

  "main" -> "main" := hasNext
  "main" -> "main" := next
  "main" -> "main" := failingNext throws "NoSuchElementException"
  "main" -> "main" := concNext throws "ConcurrentModificationException"
}
```

* Preconditions determine when a given transition function is enabled.

* In this case, the preconditions distinguish normal behavior from exceptions.

### Test case generation with the example model ###

	sh compile.sh
	sh runLinkedList.sh
	[INFO] 1000 tests executed, 997 ok, 3 failed.
	[INFO] 2 types of test failures:
	[INFO] 1) java.util.ConcurrentModificationException at failingNext:
	[INFO]    6e8ddf360994ae26 36ae40ee3f8301d6
	[INFO] 2) java.util.ConcurrentModificationException at next:
	[INFO]    6929277733240995

* Interpretation: ConcurrentModificationException is thrown by Java's iterator, but model does not expect it.
* Only 3 out of 1000 tests fail; only particular combinations of actions.
* Can you see a pattern and find the flaw in the model?
* Hint: You need to consider both the base model (CollectionModel) and the iterator model.

## A more complex model: ListIterator with modifications

The Java list iterator supports all concept that its universal cousin, iterator supports, and then some:

* Iteration can both be forward (with `next`) and backward (with `previous`).

* In addition to the possible to _remove_ the item at the iterator position, the list iterator also support _set_ to modify the current element, and _add_ to add a new element at the current position.

It is always possible to add an element at the current position, but removing or modifying an existing element requires that an element has been _selected_ previously, by having called "next" or "previous" prior.

In the model, we will handle this feature by creating a state "modifiable" that reflects that the iterator is in a state where the selected element can be modified. The iterator goes to that state whenever "next" or "previous" is called. These transitions also memorize which one of the two operations has been called last, in order to correctly adjust the current position after "remove". (The adjustment follows the direction in which the iterator was moved previously.)

We can then model "set" and "remove" as to be usable from either state "main" or "modifiable", but to throw an exception when called in state "main". An illustration of the model, alongside a complete description of the full model, can be found in the following paper:

C. Artho, M. Seidl, Q. Gros, E. Choi, T. Kitamura, A. Mori, R. Ramler, Y. Yamagata. [Model-based Testing of Stateful APIs with Modbat.](https://people.kth.se/~artho/papers/artho-ase2015-tool.pdf) Int. Conf. on Automated Software Engineering (ASE 2015), November 2015, Lincoln, USA.

### Tutorial version of model

The tutorial version is simplified, and it keeps track only of the _size_ of the collection but not its contents. It tracks the detailed iterator position, though, and tests if modifications of the underlying collection are allowed when expected. It even supports multiple iterators in parallel!

The part that is relevant for this exercise is implemented in `listit/ListIteratorModel.scala`.

The model can be executed by running

```
sh compile.sh
sh runListIt.sh
```

The output should be as follows:

```
[INFO] 5 tests executed, 4 ok, 1 failed.
[INFO] One type of test failure:
[INFO] 1) java.util.NoSuchElementException at previous:
[INFO]    33902dc134f03093
```

After that, some statistics follow; coverage is not 100 % because five tests are not sufficient for that. To look at the error trace, run

```
sh showTrace.sh 33902dc134f03093.err
```

You can see that the error is caused by a test that creates an iterator and then uses "previous" on it. Because the underlying list is empty, the iterator (correctly) fails with a `NoSuchElementException`. However, the model does not expect an exception and mis-diagnoses this case.

The reason for the mistake is that the precondition in the model is too weak. There are too many cases where "previous" is called with the expectation of success.

Task 1: Take a look at the implementations of "next" and "previous" and fill in the missing code in "previous"!

If you got the precondition correct but have not updated the state of the model, you will then see problems because the model state (in particular, `pos`) does not follow the semantics of the iterator. You can observe this issue by increasing the number of generated test cases in `runListIt.sh` from 5 to 500 by changing "-n=5" to "-n=500" in that file.

After this change, the model will pass all tests. The statistics also show 100 % state and transition coverage, which means that each part of the model has been executed at least once.

Two more tasks are left to be added:

1. You can add more variants of function calls that cause a `ConcurrentModificationException` when the iterator is used after the underlying collection has been modified (by means other than the current iterator). This condition is reflected by "valid" in function "concNext":

```scala
  @States(Array("main", "modifiable")) // specify all states
  @Throws(Array("ConcurrentModificationException")) // specify the exception(s)
  def concNext { // throws ConcurrentModificationException
    require(!valid)
    choose(
      { () => it.next() } // TODO: Add a variant testing "it.previous()"
      // note: to pass multiple options to "choose", use a comma to separate
      // all options
    )
  }
```

You can see that this function uses `choose` over a range of lambda expressions, written as

```
{ () => ... }
```

The code in these expressions may call any method that throws a `ConcurrentModificationException` in the current state, where `valid` is false.

Task 2: Add another variant, "it.previous()" to that block of code!

2. Finally, it would be good if the position returned by "it.nextIndex" or "it.previousIndex" corresponds to our expectation. You can use the model variable "pos" as the test oracle.

Task 3: Add two assertions in `checkIdx` to check "pos" against the result returned by "nextIndex" and "previousIndex", respectively!

After this, the tutorial is finished. Extensions of the model could involve checking the data in addition to the index, or checking if "previous" fails with "NoSuchElementException" when expected.
