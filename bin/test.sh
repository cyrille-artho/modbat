#!/bin/sh
TEST_LOG_PATH=log

. bin/testtool.sh

######################## Test declarations ########################

APP="scala -cp build/modbat.jar modbat.config.ConfigTest"

run 0 $APP -h
run 0 $APP -s


APP="scala build/config.jar"

export LOG_PATH="$TEST_LOG_PATH"
unset CLASSPATH
run 0 $APP
run 0 $APP -s

export CLASSPATH=foo
run 0 $APP -s cp_1
unset CLASSPATH

export CLASSPATH=""
run 1 $APP -s cp_2
unset CLASSPATH

export CLASSPATH=foo
run 0 $APP --classpath=bar -s
unset CLASSPATH

export CLASSPATH=foo
run 1 $APP -classp=baz -s
unset CLASSPATH

run 0 $APP -h
run 0 $APP -v
run 0 $APP --version
run 0 $APP --show
run 0 $APP --help
run 1 $APP -x
run 1 $APP --x
run 0 $APP -s --mode=exec
run 0 $APP -s --mode=exec -s
run 1 $APP -s --mode=quux -s
run 0 $APP a b c
run 0 $APP -- a b c
run 0 $APP -- -h a b c
run 0 $APP -- --help a b c
run 0 $APP --redirectOut -s
run 0 $APP --redirectOut=true -s
run 0 $APP --redirectOut=false -s
run 1 $APP --redirectOut=xx -s

run 0 $APP --no-redirect-out --no-some-flag
run 0 $APP --no-some-flag --no-redirect-out
# is OK if flag is false
run 1 $APP --no-redirect-out --some-flag
# not OK

run 1 $APP --odd-prime
run 0 $APP --no-odd-prime
run 0 $APP --odd-prime --small-prime=three

run 0 $APP --even-prime
run 0 $APP --no-even-prime
run 1 $APP --even-prime --small-prime=three

run 0 $APP --no-redirectOut -s
run 1 $APP --no-redirectOut=true -s
run 1 $APP --no-redirectOut=false -s
run 1 $APP --no-redirectOut=xx -s
run 1 $APP --no-mode
run 1 $APP --nRuns
run 1 $APP --nRuns=
run 1 $APP --nRuns=0
run 1 $APP --nRuns=a
run 0 $APP --nRuns=1 -s
run 0 $APP --nRuns=999999 -s
run 1 $APP --nRuns=999999999999
run 0 $APP -s --small-prime=three -s
run 1 $APP -s --small-prime=one
run 0 $APP --abortProbability=0.5 -s
run 1 $APP --abortProbability=-0.5 -s
run 1 $APP --abortProbability=1.5 -s
run 1 $APP -f=x -s
run 1 $APP -g=x -s
run 1 $APP -f=
run 1 $APP -f
run 1 $APP --modelClass=
run 1 $APP --modelClass
run 1 $APP -n-runs=2
run 0 $APP --n-runs=2 -s
run 0 $APP -s=10c1be9b302682f3 -s
run 1 $APP -s=10c1be9b302682f30
run 0 $APP -s=ffffffffffffffff -s
# n is not in hex
run 1 $APP -n=ffffffff -s
# TODO: Test for max range on int, min/max on long
run 1 $APP --Quux
run 1 $APP --baz-Quux


APP="scala build/modbat.jar"
run 1 $APP --model-class=x

#run 1 $APP
# FIXME: Disabled because dir/log file name not extracted correctly
run 1 $APP a b c
run 1 $APP -n=1 x b c
run 1 $APP x y -n=1
run 1 $APP -n=2 -- -n=1
run 1 $APP a -n=2 -- -n=1
run 1 $APP -s=0

run 1 $APP --log-level=fine
# lower-case option arg


export CLASSPATH=build/modbat-examples.jar
run 1 $APP -s=1 --no-redirect-out modbat.examples.SimpleCounter

run 0 $APP -n=5 -s=1 --no-redirect-out --log-level=fine \
	--no-init --no-shutdown modbat.examples.NioSocket1
run 0 $APP -n=10 -s=1 --no-redirect-out --log-level=fine \
	--no-init --no-shutdown modbat.examples.NioSocket1
run 0 $APP -n=20 -s=1 --no-redirect-out --log-level=fine \
	--no-init --no-shutdown modbat.examples.NioSocket1
run 0 $APP -n=50 -s=1 --no-redirect-out --log-level=fine \
	--no-init --no-shutdown modbat.examples.NioSocket1

run 0 $APP -n=10 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=20 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=50 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=100 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=200 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=500 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1
run 0 $APP -n=1000 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.NioSocket1

run 0 $APP --mode=dot --auto-labels modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1.dot niosocket1.dot
checkfile niosocket1.dot

rm -rf dot_test
mkdir dot_test
run 0 $APP --mode=dot --auto-labels --dot-dir=dot_test \
	modbat.examples.NioSocket1
savemv dot_test/modbat.examples.NioSocket1.dot niosocket1.dot
checkfile niosocket1.dot
rm -rf dot_test

run 1 $APP --mode=dot --auto-labels --dot-dir=dir_does_not_exit \
	modbat.examples.NioSocket1

run 0 $APP -n=10 -s=1 --dotify-coverage \
	--auto-labels \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1.dot cov_10.dot
checkfile cov_10.dot

run 0 $APP -n=100 -s=1 --dotify-coverage \
	--auto-labels \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1.dot cov_100.dot
checkfile cov_100.dot

run 0 $APP -n=500 -s=1 --dotify-coverage \
	--auto-labels \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1.dot cov_500.dot
checkfile cov_500.dot

run 0 $APP -n=500 -s=1 --maybe-probability=0 \
	--dotify-coverage --auto-labels \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1.dot cov_500-p0.dot
checkfile cov_500-p0.dot

run 0 $APP -n=1 -s=61a342c60d18ff4d --no-redirect-out \
	modbat.examples.NioSocket1

run 0 $APP -n=1000 -s=1 --no-redirect-out --stop-on-failure \
	modbat.examples.NioSocket1


run 0 $APP -n=5 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=10 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=20 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=50 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=100 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=200 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=500 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket
run 0 $APP -n=1000 -s=1 --no-redirect-out --log-level=fine \
	modbat.examples.JavaNioServerSocket

run 0 $APP --mode=dot --auto-labels \
	modbat.examples.JavaNioServerSocket
savemv modbat.examples.JavaNioServerSocket.dot nioserv-auto.dot
checkfile nioserv-auto.dot
## path coverage 20 test cases
run 0 $APP -n=20 -s=1 --no-redirect-out --dotify-path-coverage --path-coverage-graph-mode=abstracted \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1-root-StateGraph.dot nio-path-state-20.dot
checkfile nio-path-state-20.dot
savemv modbat.examples.NioSocket1-root-PointGraph.dot nio-path-point-20.dot
checkfile nio-path-point-20.dot
## path coverage 100 test cases
run 0 $APP -n=100 -s=1 --no-redirect-out --dotify-path-coverage --path-coverage-graph-mode=abstracted \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1-root-StateGraph.dot nio-path-state-100.dot
checkfile nio-path-state-100.dot
savemv modbat.examples.NioSocket1-root-PointGraph.dot nio-path-point-100.dot
checkfile nio-path-point-100.dot
## path coverage 500 test cases
run 0 $APP -n=500 -s=1 --no-redirect-out --dotify-path-coverage --path-coverage-graph-mode=abstracted \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1-root-StateGraph.dot nio-path-state-500.dot
checkfile nio-path-state-500.dot
savemv modbat.examples.NioSocket1-root-PointGraph.dot nio-path-point-500.dot
checkfile nio-path-point-500.dot
# path coverage 1000 test cases
run 0 $APP -n=1000 -s=1 --no-redirect-out --dotify-path-coverage --path-coverage-graph-mode=abstracted \
	modbat.examples.NioSocket1
savemv modbat.examples.NioSocket1-root-StateGraph.dot nio-path-state-1000.dot
checkfile nio-path-state-1000.dot
savemv modbat.examples.NioSocket1-root-PointGraph.dot nio-path-point-1000.dot
checkfile nio-path-point-1000.dot

run 0 $APP --mode=dot --log-level=debug \
	--auto-labels \
	modbat.examples.JavaNioServerSocket
savemv modbat.examples.JavaNioServerSocket.dot nioserv-auto.dot
checkfile nioserv-auto.dot

run 0 $APP --mode=dot \
	modbat.examples.JavaNioServerSocket3
savemv modbat.examples.JavaNioServerSocket3.dot nioserv3.dot
checkfile nioserv3.dot

run 0 $APP --mode=dot --show-choices \
	modbat.examples.JavaNioServerSocket3
savemv modbat.examples.JavaNioServerSocket3.dot nioserv3c.dot
checkfile nioserv3c.dot

run 0 $APP -n=10 -s=1 --dotify-coverage \
	--auto-labels \
	modbat.examples.JavaNioServerSocket
savemv modbat.examples.JavaNioServerSocket.dot scov_10.dot
checkfile scov_10.dot

run 0 $APP -n=100 -s=1 --dotify-coverage \
	--auto-labels \
	modbat.examples.JavaNioServerSocket
savemv modbat.examples.JavaNioServerSocket.dot scov_100.dot
checkfile scov_100.dot

run 0 $APP -n=1 -s=321e7808 --no-redirect-out \
	modbat.examples.JavaNioServerSocket

# modified ServerSocket so assertions are accurate
run 0 $APP -n=1000 -s=1 --no-redirect-out \
	modbat.examples.JavaNioServerSocket2

# modified ServerSocket2 so exception does not change state
run 0 $APP -n=10 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioServerSocket3

run 0 $APP -n=100 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioServerSocket3

run 0 $APP -n=200 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioServerSocket3

# modified JavaSocket so exception does not change state
# used count 3 for consistency with server model
run 0 $APP -n=10 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioSocket3

run 0 $APP -n=100 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioSocket3

run 0 $APP -n=1000 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioSocket3


# state "connected" is first filtered but then still reached
# via nextIf predicate, setting its count to 6. Hence,
# "==" needs to be changed to ">=" to cover this case.
run 0 $APP -n=1 -s=c2c2567 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioServerSocket3

run 0 $APP -n=1000 -s=1 --no-redirect-out --loop-limit=5 \
	modbat.examples.JavaNioServerSocket3


run 0 $APP -s=1 -n=1000 modbat.examples.JavaNioSocket
run 0 $APP -s=1 -n=10000 modbat.examples.JavaNioSocket


run 0 $APP -s=1 -n=30 --no-redirect-out --log-level=fine \
	modbat.examples.SimpleModel
run 0 $APP -s=49a846e52b813972 -n=1 --no-redirect-out \
	modbat.examples.SimpleModel
run 0 $APP -s=88af43883571af0c -n=1 --no-redirect-out \
	modbat.examples.SimpleModel
run 0 $APP -s=88af43883571af0c -n=1 --no-redirect-out \
	modbat.examples.SimpleModel --print-stack-trace

run 0 $APP -s=1 -n=30 --no-redirect-out modbat.examples.CounterModel
run 0 $APP --mode=dot --auto-labels modbat.examples.CounterModel
savemv modbat.examples.CounterModel.dot counter.dot
checkfile counter.dot

run 0 $APP -s=1 -n=30 --no-redirect-out modbat.examples.CounterModel2
run 0 $APP --mode=dot --auto-labels modbat.examples.CounterModel2
savemv modbat.examples.CounterModel2.dot counter2.dot
checkfile counter2.dot

export CLASSPATH=build/modbat-test.jar
run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.StateSetToTrans

run 0 $APP -s=1 -n=10 --no-redirect-out --log-level=fine \
	modbat.test.Redundancy
run 0 $APP -s=2e78dcb10b272e5 -n=1 --no-redirect-out \
	modbat.test.Redundancy

run 1 $APP -s=1 --no-redirect-out modbat.test.EmptyModel

run 1 $APP -s=1 --no-redirect-out modbat.test.AbstractCls

run 0 $APP -s=1 -n=3 --no-redirect-out --log-level=fine \
	modbat.test.Shortcut
run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.Shortcut


run 0 $APP -s=1 -n=5 --no-redirect-out modbat.test.SimpleFileModel

run 0 $APP -s=1 -n=5 --no-redirect-out modbat.test.PrecondFalse


run 0 $APP -s=1 -n=500 modbat.test.FileModel --stop-on-failure


run 0 $APP -s=1 -n=1 modbat.test.SingletonTest --no-redirect-out


run 0 $APP -s=1 -n=1 modbat.test.MaybeTest --maybe-probability=0.0 \
	--no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.MaybeTest --maybe-probability=1.0 \
	--no-redirect-out


run 0 $APP -s=1 -n=1 modbat.test.MaybeElseTest --maybe-probability=0.0 \
	--no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.MaybeElseTest --maybe-probability=1.0 \
	--no-redirect-out


run 0 $APP -s=1 -n=1 modbat.test.MaybeElseTest2 --maybe-probability=0.0 \
	--no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.MaybeElseTest2 --maybe-probability=1.0 \
	--no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.RequireMaybeTest --no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.RequireMaybeTest2 --no-redirect-out

run 0 $APP -s=1 -n=1 modbat.test.IllArgTest --no-redirect-out --log-level=fine

run 0 $APP -s=1 -n=3 modbat.test.ChooseInBefore --no-redirect-out

run 0 $APP -s=1 -n=20 modbat.test.CarModel --no-redirect-out

run 0 $APP -s=1 -n=5 --log-level=debug --no-redirect-out \
	modbat.test.ComplexLaunch

run 0 $APP -s=1 -n=50 --log-level=fine --print-stack-trace --no-redirect-out \
	modbat.test.ComplexLaunch

run 0 $APP --mode=dot --show-choices modbat.test.ComplexLaunch
savemv modbat.test.ComplexLaunch.dot claunch.dot
checkfile claunch.dot

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	modbat.test.ComplexLaunch2

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	modbat.test.ComplexLaunch3

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	modbat.test.ComplexLaunch4

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	modbat.test.ComplexLaunch5

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	modbat.test.ComplexLaunch6

run 0 $APP -s=1 -n=10 --no-redirect-out \
	modbat.test.RSeedTestSuccess

run 1 $APP --mode=dot modbat.test.Child

run 0 $APP --mode=dot modbat.test.InvalidMethod
savemv modbat.test.InvalidMethod.dot invmethod.dot
checkfile invmethod.dot

run 0 $APP -s=1 -n=15 --log-level=fine --no-redirect-out \
	 modbat.test.InvalidMethod

run 0 $APP -s=1 -n=20 --no-redirect-out modbat.test.InvariantCheck

run 0 $APP -s=1 -n=20 --no-redirect-out modbat.test.InvCheck2

run 0 $APP -s=1 -n=10 --loop-limit=2 --no-redirect-out modbat.test.InvCheck3

run 0 $APP -s=1 -n=20 --loop-limit=2 --no-redirect-out modbat.test.InvCheck3_1

run 0 $APP -s=1 -n=20 --loop-limit=2 --no-redirect-out modbat.test.InvCheck3_2

run 0 $APP -s=1 -n=20 --loop-limit=2 --no-redirect-out modbat.test.InvCheck3_3

run 0 $APP -s=1 -n=10 --loop-limit=2 --no-redirect-out \
	--log-level=fine modbat.test.InvCheck4

run 0 $APP --mode=dot --auto-labels modbat.test.InvalidMethod
savemv modbat.test.InvalidMethod.dot invmethod-auto.dot
checkfile invmethod-auto.dot

run 0 $APP -s=1 -n=20 --auto-labels --no-redirect-out modbat.test.InvCheck2

run 0 $APP -s=1 -n=20 --auto-labels --no-redirect-out modbat.test.InvCheck5

run 0 $APP -s=1 -n=20 --no-redirect-out modbat.test.InvCheck5

run 0 $APP -s=1 -n=10 --auto-labels --loop-limit=2 --no-redirect-out \
	modbat.test.InvCheck3

run 0 $APP -s=1 -n=10 --auto-labels --loop-limit=2 --no-redirect-out \
	--log-level=fine modbat.test.InvCheck4

# getCurrentState
run 0 $APP -s=1 -n=10 --log-level=fine modbat.test.GetState --no-redirect-out

run 0 $APP -s=1 -n=10 --log-level=fine \
	modbat.test.GetState2 --no-redirect-out

run 0 $APP -s=1 -n=100 modbat.test.ChooseTest

run 0 $APP --mode=dot --show-choices modbat.test.ChooseTest
savemv modbat.test.ChooseTest.dot chooseTest.dot
checkfile chooseTest.dot

# delete log files on success
run 0 $APP -s=1 -n=2 --remove-log-on-success modbat.test.Hello

#savemv 402b73cd0066eaea.log nohello.out
#checkfile nohello.out log/modbat.test.Hello
# currently can't check absence of files
# currently does not work:
#savemv log/3ba471c1785a0175.log log/hello.out
#checkfile hello.out log/modbat.test.Hello
#savemv log/3ba471c1785a0175.err log/hello.eout
#checkfile hello.eout log/modbat.test.Hello

# annotated methods of super class are (currently) not executed
run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=debug \
	modbat.test.SubModel

run 0 $APP -s=1 -n=10 --no-redirect-out modbat.test.SubModel2

run 0 $APP -s=1 -n=10 --no-redirect-out modbat.test.SubModel3

run 0 $APP -s=1 -n=100 modbat.test.MaybeTest --maybe-probability=0.01 \
	--no-redirect-out

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.PrecondOffline

run 0 $APP -s=1 -n=1 modbat.test.NextIfTest --no-redirect-out

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.NextIfTest2

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.NextIfTest3

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.NextIfTest4

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.NextIfTest5

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out \
	--maybe-probability=0.3 modbat.test.MaybeNextIfTest

run 0 $APP -s=1 -n=3 --log-level=fine --no-redirect-out \
	--maybe-probability=0.3 modbat.test.MaybeNextIfTest2

run 0 $APP -s=1 -n=3 --log-level=fine --no-redirect-out \
	--maybe-probability=0.9 modbat.test.MaybeNextIfTest3

run 0 $APP -s=1 -n=3 --log-level=fine --no-redirect-out \
	--maybe-probability=0.48 modbat.test.MaybeNextIfTest4

run 0 $APP -s=1 -n=3 --log-level=fine --no-redirect-out \
	--maybe-probability=0.48 modbat.test.MaybeNextIfTest5

run 0 $APP -s=9999 -n=1 --no-redirect-out --abort-probability=0.2 \
	modbat.test.ExcTest

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTest2

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTest3

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTest4

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTest5

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTest6

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTestA3

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTestA4

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTestA5

run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.ExcTestA5 --log-level=fine

run 0 $APP -s=1 -n=1 --no-redirect-out NoPackage

run 0 $APP -s=1 -n=20 --no-redirect-out modbat.test.AnyFuncTest

run 0 $APP -s=1 -n=1 --loop-limit=5 modbat.test.ThrowTest

run 0 $APP -s=1 -n=100 --loop-limit=3 --no-redirect-out --log-level=fine modbat.test.Immediate

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine modbat.test.NoSelfJoin

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine modbat.test.JoinNull

run 0 $APP -s=1 -n=30 --no-redirect-out --log-level=fine modbat.test.ForkJoin

run 0 $APP -s=1 -n=3 --no-redirect-out --log-level=fine modbat.test.ForkJoin2


export CLASSPATH=build/modbat-test.jar
# test against loops
run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=5 \
modbat.test.LoopTest

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=5 \
modbat.test.LoopTest2

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=0 \
modbat.test.LoopTest

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=0 \
modbat.test.LoopTest2

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=1 \
modbat.test.LoopTest

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine --loop-limit=1 \
modbat.test.LoopTest2

run 0 $APP -s=1 -n=2 --log-level=fine --no-redirect-out --loop-limit=3 \
modbat.test.LoopTestWithLaunch

run 0 $APP -s=1 -n=20 --no-redirect-out --loop-limit=4 \
modbat.test.LoopTestWithLaunch

run 0 $APP -s=1 -n=200 --no-redirect-out --stop-on-failure \
modbat.test.CrashSoon

run 0 $APP -s=1 -n=5 --no-redirect-out modbat.test.ChooseBool

run 0 $APP -s=1 -n=1 --no-redirect-out --loop-limit=50 \
	modbat.test.PredActions

run 0 $APP -s=1 -n=5 --no-redirect-out modbat.test.PreCond

run 0 $APP -s=1 -n=1000 --no-redirect-out --abort-probability=0.1 \
	--stop-on-failure modbat.test.WeightedTest

run 0 $APP -s=1 -n=5 --no-redirect-out --log-level=debug \
	--stop-on-failure modbat.test.WeightedTest2

run 0 $APP -s=1 -n=1000 --no-redirect-out --abort-probability=0.1 \
	--stop-on-failure modbat.test.WeightedTestWFrac

# user-friendly output when nullary constructor fails

run 1 $APP --mode=dot modbat.test.NullaryCons

run 1 $APP --mode=dot modbat.test.NullaryCons --print-stack-trace

# test auto-generated labels in trace output
run 0 $APP -s=1 -n=5 --no-redirect-out modbat.test.GenLabel

run 0 $APP -s=1 -n=5 --auto-labels --no-redirect-out modbat.test.GenLabel

run 0 $APP -s=1 -n=5 --auto-labels --no-redirect-out modbat.test.GenLabel2

run 0 $APP -s=1 -n=5 --auto-labels --no-redirect-out modbat.test.GenLabel3

run 0 $APP --mode=dot --auto-labels modbat.test.GenLabel3
savemv modbat.test.GenLabel3.dot genlabel3.dot
checkfile genlabel3.dot

# test inheritance and class loading
run 0 $APP -s=1 -n=1 --no-redirect-out modbat.test.SubClassModel


# test observers
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness

# multiple observers
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness2

# observers: warning about non-default weight
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness3

# observer: main model must not be observer
run 1 $APP -s=1 --no-redirect-out modbat.test.TestObserver

# observers: observer has to advance multiple steps to be up-to-date
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness4

# observers: precondition fails in offline mode
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness5

# observers: assertion fails in offline mode
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness6

# observers: assertion fails in separate thread
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.ObserverHarness7


# observers: tick/tock example (loop in observer)
run 0 $APP -s=1 -n=2 --no-redirect-out --log-level=fine \
	modbat.test.TickTockTest

# show choices
run 0 $APP -s=1 -n=2 --no-redirect-out --show-choices \
	modbat.test.TickTockTest

run 0 $APP -s=1 -n=4 --no-redirect-out --show-choices modbat.test.AnyFuncTest

run 0 $APP --mode=dot --show-choices modbat.test.AnyFuncTest
savemv modbat.test.AnyFuncTest.dot anyfunc.dot
checkfile anyfunc.dot

run 0 $APP -s=1 -n=5 --no-redirect-out --show-choices modbat.test.Choices

run 0 $APP --mode=dot --show-choices modbat.test.Choices
savemv modbat.test.Choices.dot choices.dot
checkfile choices.dot


# precondAsFailure
run 0 $APP -s=1 -n=2 --precond-as-failure --no-redirect-out --log-level=fine \
	modbat.test.SUTReq

run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine \
	modbat.test.SUTReq


# concurrency in test model
run 0 $APP -s=1 -n=1 --no-redirect-out --log-level=fine \
	modbat.test.ConcurrentModel

export CLASSPATH=build/modbat-examples.jar

run 0 $APP --mode=dot --auto-labels modbat.examples.CounterPosNeg
savemv modbat.examples.CounterPosNeg.dot counterpn.dot
checkfile counterpn.dot

export CLASSPATH=build/modbat-test.jar:build/modbat-examples.jar
run 0 $APP --mode=dot --auto-labels modbat.test.CounterPosNeg1
savemv modbat.test.CounterPosNeg1.dot counterpn1.dot
checkfile counterpn1.dot


######################## Test summary ########################

echo "# count=$count ok=$ok failed=$failed skipped=$skipped"
exit $failed
