*Check LICENSE for licensing conditions.*

# Modbat: Model-based Tester

Modbat is specialized to testing the application programming interface
(API) of software. The model used by Modbat is compatible with Java
bytecode. The user defines and compiles a model, which is then explored
by Modbat and executed against the system under test. Failed test runs
are reported as an error trace.

## Installation

Requirements: Scala 2.11 or higher (tested on 2.11.1).

The distribution comes with two JAR files: modbat.jar and
modbat-examples.jar. The first file is an executable JAR file that has to
be run using the Scala runtime environment. The second file is optional
and contains examples.

### Building from source

Requirements: [Gradle Build Tool](https://gradle.org/) version 4.

Clone the repository and `cd` into it.

    $ git clone https://github.com/cyrille-artho/modbat.git
    $ cd modbat

Assemble the project by running:

    $ gradle assemble

This will build the project and place the JAR files in "./build".

## Model syntax

A Modbat model inherits from modbat.dsl.Model. Inside the class body
(effectively, its default constructor), model transitions can be declared:

```scala
package modbat

import modbat.dsl._

class ModelTemplate extends Model {
  // transitions
  "reset" -> "somestate" := {
    // insert code here
  }
  "somestate" -> "end" := skip // empty transition function
  "reset" -> "end" := {
  }
}
```

Note that direct calls to functions are possible; curly braces are only
needed to group multiple statements inside a transition function.

## Basic usage

The examples below assume that Modbat is compiled into build/, so
the commands below have to be preceded with

	cd build

once.

	scala modbat.jar [OPTIONS] MODELCLASS...

For help, try

	scala modbat.jar -h

and to check the current configuration, use

	scala modbat.jar -s

The second file contains examples and a model template; unpack that
file using

	jar xf modbat-examples.jar

The examples are contained in modbat/examples, while the template
is in modbat/ModelTemplate.scala.

## Semantics of basic models

See the following publication:

C. Artho, A. Biere, M. Hagiya, M. Seidl, E. Platon, Y. Tanabe,
M. Yamamoto.
Modbat: A Model-based API Tester for Event-driven Systems.
9th Haifa Verification Conference (HVC 2013), November 2013,
Haifa, Israel.

## How to run the examples

Usage is as above. The examples can be run either from the JAR file
examples.jar, or using the class files from the unpacked JAR file.

In either case, the examples must reside in the classpath as set by
CLASSPATH or given by option --classpath (note: two dashes):

Example:

(1) With the examples provided as a JAR file:

	scala modbat.jar --classpath=modbat-examples.jar \
		modbat.examples.SimpleModel

(2) If the contents of modbat-examples.jar are unpacked:

	scala modbat.jar --classpath=. modbat.examples.SimpleModel

The model is explored non-deterministically. By default, 50 runs on
the model are executed, and all failing traces are written to a file
(one file for each trace).

The defaults are set such that you do usually not have to change
them; options that may be of interest are "-n", "-s", "--mode",
"stop-on-failure". Run

	scala modbat.jar -h

for more information.

Modbat will generate a given number of tests from the model and
execute them under the given options. At the end, it will print a
summary:

[INFO] 50 tests executed, 50 ok, 0 failed.
[INFO] 6 states covered (100 % out of 6),
[INFO] 17 transitions covered (85 % out of 20).
[INFO] Random seed for next test would be: ...

The summary shows the number of executed and successful/failed tests,
followed by a state and transition coverage. In case there are active
model instances of different types, coverage for each type of model is
shown. Finally, the random seed for the next test is displayed; this
makes it possible to generate more tests in the same "random chain".

As of version 3.1, if some tests fail, Modbat also shows a
classification of test failures based on the exact exception and
the transition in which they occurred.
This classification is imperfect because it may map two different
faults (which result in the same exception) to one issue; it may
also map one issue to two failures (in case multiple transitions
trigger the same fauilure). However, it gives a good overview if
a model exhibits multiple types of failures.

## How to read the error trace

If a model run causes an assertion violation or other unhandled exception,
an error trace is generated in a .err file. The name of the .err file
(without the extension .err) corresponds to the random seed used for
that failed test run. To re-run the failed test without logging the
output to a file, use

	scala modbat.jar -n=1 -s=<seed> --no-redirect-out <model>

Example:

	scala modbat.jar -n=1 -s=2455cfedeadbeef \
		--no-redirect-out \
		modbat.examples.SimpleModel

Note that the random seed must not be 0.

## Semantics of model

The model is given as an "extended finite state machine" in a
domain-specific language that is embedded in Scala. Basic transitions
between two states are given as

```scala
"pre" -> "post"
```

Most transitions will have a transition function attached to them, using
":="

```scala
"pre" -> "post" := { // Scala code }
```

Any Scala code is allowed, but preconditions have a special semantics:
Exploration of the model backtracks if a precondition is violated.
Therefore, all preconditions should be stated at the beginning of a
transition function, and be side-effect-free:

```scala
"pre" -> "post" := {
	require (x < 0)
}
```

A model will contain multiple transitions, separated by a comma, as
shown in src/scala/modbat/ModelTemplate.scala.

## Preconditions and assertions

Preconditions are defined by "require(predicate)". If a precondition
fails in the model, the transition is considered not to be eligible,
and is backtracked. In the SUT, a failed precondition (in Scala code)
has the same effect unless option --precond-as-failure is used.

This distinction is possible because Modbat defines its own "require"
implementation, resulting in two cases:

* Modbat require fails in model -> backtrack;
* Predef require fails in SUT   -> backtrack or abort (according to option).

Assertions behave in the same way in both the model and the SUT, but
standard assertions that fail in a separate thread (not the main thread)
are not directly visible to Modbat, and thus do not result in a test
failure. Model-level assertions use extra code to become visible every
time before a Modbat transition is scheduled. Note that the user is
responsible for avoiding race conditions between different threads in
the model, if multiple threads or asynchronous callbacks are used.

Assertions are therefore evaluated in two possible ways, which may result
in unintended effects if an assertion fails in a separate thread:

* Modbat assert fails in model -> stop (directly or before next transition);
* Predef assert fails -> stop if in main thread, no effect otherwise.

## Inheritance

Inheritance of methods works normally as in Scala. Transitions defined
normally ("a" -> "b" := transfunc) are also inherited, as are annotated
methods from the super class.

## Advanced features

Advanced choices give flexibility when modeling non-determinism:

* choose(min, max): Returns a random number between min (inclusive)
  and max (exclusive). This is a shorthand for
  min + choose(0, max - min);
  therefore, details in the error trace or offline code record the
  choice before "min" is added.

* choose(actions: AnyFunc*): Takes a list of actions, randomly
  chooses one action, and executes it.

* chooseIf((predicate, action)*): Takes a list of predicates and actions,
  randomly chooses one of the actions of which the predicate is true,
  and executes it. No action gets executed if all predicates are false.

Advanced exception handler constructs can be attached to a transition
function. These are:

* throws:

```scala
"pre" -> "post" := {
	codeThrowingException
} throws("IOException", "SecurityException")
```

"throws" specifies that an exception must always occur in that transition;
the absence of an exception is regarded as an error.  A list of exception
types, given as strings, is supported. Please note that the exception type
is matched by pattern, given in the order in which they are declared.
Pattern "Exception" will match almost anything.

* catches:

```scala
"pre" -> "post" := {
	codeWithPossibleException
} catches("Exception" -> "handlerState")
```

"catches" deals with non-deterministic exceptions that may occur, but
are not always expected. Input/output errors can be handled in the model
in this way. A "catches" statement supports a list of comma-separated
(exceptionType -> handlerState) pairs, given as strings.

Note that when multiple models are active, it may be necessary that the
system state does not change by transitions taken by other models,
until a transition from "handlerState" is taken. In this case,
"immediatelyCatches" achieves the desired effect by ensuring that the
next executed transition is one of the available transitions in state
"handlerState" from the same model instance.

* nextIf:

```scala
"pre" -> "post" := {
	n = choose(0, 40)
} nextIf({ () => n > 30 } -> "nextState")
```

Non-deterministic conditions other than exceptions can be handled using
"nextIf"; given a list of (condition, nextState) pairs, the transition
branches to "nextState" if the given condition is true.

Conditions are usually given as an anonymous function in Scala, using the
"() =>" notation, before the actual condition.

* maybe:

```scala
"pre" -> "post" := {
	maybe (doSomething)
}
```

An action is only executed with a certain probability (default: 0.5).
If a random number between 0 and 1 exceeds that probability, then the
function is not executed.
Note: The function in brackets must be a single function that affects
the state of the model or SUT via side effects.

* or_else

"maybe" can be used with an optional "or_else", which executes only
if "maybe" is not chosen, similar to an "if"/"else" statement:

```scala
"pre" -> "post" := {
	maybe (doSomething)
	or_else (doSomethingElse)
}
```

* maybeBool:

Same as "maybe" but returns a boolean. If the function is not executed
(due to the given probability not being fulfilled), false is returned.

* maybeNextIf:

```scala
"pre" -> "post" := {
	n = choose(0, 40)
} maybeNextIf({ () => n > 30 } -> "nextState")
```

Same as "nextIf", but the condition is only evaluated with a certain
probability (default: 0.5). Otherwise, the given condition is ignored,
and the default transition is chosen. This function is syntactic sugar for

```scala
nextIf({ () => maybeBool({ () => n > 30 }) } -> "nextState")
```

* label:

For the visualization of the model in dot format, the label is generated
by looking at the code of the transition function. The result is not
always the best possible description, but it is possible to override
the default label with a "label" declaration following the transition:

```scala
"pre" -> "post" := { code } label "initialization"
```

The label is not used in the error trace as the state information,
together with the code location, is already sufficient to identify
the function in question.

* weight:

A transition can be made to be chosen more often than others by assigning
a weight > 1.0 to it. Weights are integers representing how often a
transition is represented w.r.t. the default weight 1.0. A weight of 0
disables a transition:

```scala
"pre" -> "post" := { code } weight 2
```

## API functions

In addition to variants of choose (see above), Modbat also has other API
functions:

* getCurrentState: Returns the name of the current state as a string.

* getRandomSeed: The random seed of the current test case.

* launch(Model): Launches a new model instance that runs in parallel
  with existing models (also see below). Models are executed using
  stepwise interleaving.

* testFailed: Only available for "cleanup" functions annotated by @After
  (see below): Returns whether the just-finished test failed. Undefined
  otherwise.

## Observer models

If a model extends Observer (rather than Model), it is considered to
be a passive observer state machine. These machines have the following
key differences compared to normal Modbat models:

* They observe and check SUT properties, but should not modify the SUT.

* They cannot be the main model instance, but always have to be
  launched using "launch".

* They are not executed when a normal model transition is chosen.
  Instead, after each successful execution of a model transition,
  /all/ registered observer state machines are evaluated successively.
  Transitions are executed until a fix point is reached (either no
  precondition is true for transitions matching the given state,
  or a transition leads again to a previously visited state).

With registered observers, model execution therefore works in an
alternating fashion:

1. Execute an eligible transition from one of all available models.

2. Execute all eligible transitions from all available observers.

## Helper method annotations

Modbat supports several method annotations. These methods are invoked
at certain times. Annotated methods must take zero arguments, or they
will be ignored. Supported annotations include:

* `@States(Array("state1" [, "state2"[, ...]]))`:
  This annotation specifies that the given action may be executed
  at any time at a given state. It gets executed like any regular
  model transition, but it does not change the model state.
  This annotation results in self-loop transitions being added to
  the model before it is executed. For example

  ```scala
  @States(Array("state1", "state2")) def invariantCheck { ... }
  ```

  results in transitions

  ```scala
  "state1" -> "state1" := invariantCheck
  "state2" -> "state2" := invariantCheck
  ```

* `@Throws(Array("Exception1" [, ...]))`:
  If used together with "@States", specifies which exceptions this
  method is expected to throws. Counterpart to ``throws "Exception1"``
  for normal model transitions.

* `@Weight(double)`:
  If used together with "@States", specifies the weight for each resulting
  transition function. As one copy of the function is created for each
  self-loop, such functions would be executed very often if their weight
  was 1.0. To counter this, the default value of the function weight is
  1.0/N, where N equals the number of states in which the function appears.
  This low default weight is useful for functions that do not modify the
  SUT state (such as invariant checks). For functions modifying
  parts of the SUT, consider using @Weight(1.0).

* `@Init`: The given method is executed before the first test is run,
  but not before each test. This is useful, for example, to execute
  a server instance for testing client-side behavior, as is done in
  model modbat.test.JavaNioSocket.

* `@Shutdown`: This method is executed after the last test completes,
  or when Modbat is forcibly shut down by signal TERM.

* `@Before`: This action is execute before each test is executed, but
  not when a new instance model is launched. This annotation therefore
  allows a differentiation between the need to initialize data before
  each test (using the annotation) or before a model instance is
  executed (using an initialization transition preceding the actual
  functionality of the model). The action is executed on the default
  instance of the model.

* `@After`: This action is executed after each test execution run,
  on the default instance of the model.

`@Before` annotations are also executed on a newly launched model instance
(using launch), right when the model is launched (while the transition
of the parent model is executing). `@After` annotations are also executed
on all launched model instances at the end of a given test (regardless
of when a model instance completed its last transition).

Annotations are only recognized if they are directly present in the
declaring class. An inherited annotated method will NOT be recognized
and used! To use inheritance, declare another annotated method that
delegates the call, such as

```scala
@Before def setup { super.setup }
```

## Field annotations

Modbat currently supports one model field annotation, `@Trace`.
Model fields with this annotation are traced throughout test execution.
After each step, all annotated fields of the model whose transition
just executed, is checked for updates of these fields. Updates are
logged and shown as part of the error trace.

## Visualization

The tool supports a basic visualization (requiring graphviz), using

	scala modbat.jar --mode=dot <model>

The output file is `<modelname>.dot`. The destination directory can
be changed using --dot-dir=...; default is the current directory.

## How to compile your own model

It is recommended that you copy ModelTemplate.scala to create your own
model. Compilation of a model requires modbat.jar in the classpath.

For a model defined in "Model.scala", and modbat.jar in the current
directory, use:

	scalac -cp modbat.jar Model.scala

## Replaying traces

Error traces can be reproduced by supplying the same random seed to
Modbat. For example, a test on modbat.examples.NioSocket1 produces a
failure using random seed 61a342c60d18ff4d. The random seed is used as
the name of the file containing the error trace (61a342c60d18ff4d.err).
It can also be used to reproduce the same trace:

	scala modbat.jar -n=1 -s=61a342c60d18ff4d modbat.examples.NioSocket1

When replaying a trace, Modbat explores the model again, making the same
choices as originally made when the random number generator was in a
given state.

## Other test configuration options

Also see the output of "`scala modbat/modbat.jar -h`".

Note that a loop limit of 1 means no loops will be allowed, so
self-loops will never be executed.

## Troubleshooting

At this stage, many problems encountered are class path/class loader
issues. Please check the following:

* CLASSPATH setting.

* Use "`scala modbat.jar --classpath=...`" to override this setting.

* Note that any CLASSPATH setting or -cp/-classpath argument to
  scala itself is ignored when using an executable JAR file.
  If modbat.jar is not found, use the correct (full) file name,
  regardless of CLASSPATH, as that setting is ignored by the Scala
  runtime environment (which is the Java Virtual Machine).

* Modbat itself checks for the CLASSPATH setting, and uses it;
  however, the syntax of --classpath=PATH uses two dashes and an equals
  sign in Modbat, not one dash and a space like for Java and Scala.

* Scala compiler errors: If you get "error: reassignment to val" for
  each transition declaration (at ":="), then you have probably
  accidentally deleted the import statement:
  ```scala
  import modbat.dsl._
  ```
  This statement is necessary for internal type conversions.
