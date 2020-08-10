package modbat.config

import ConfigTestHarness.test

import org.scalatest._

class ConfigTest extends FlatSpec with Matchers {
  "NoInput" should "produce no output" in test(Array())

  "showConfig" should "produce the same output as in the output template" in
    test(Array("-s"))

  "showConfigLong" should "produce the same output as in the output template" in
    test(Array("--show"))

  "IllegalArg" should "produce an exception" in
    test(Array("-x"), 1)

  "IllegalArg2" should "produce an exception" in
    test(Array("--x"), 1)

  "MultipleArguments" should "be parsed correctly" in
    test(Array("-s", "--mode=exec"))

  "DuplicateShow" should "show configuration each time" in
    test(Array("-s", "--mode=exec", "-s"))

  "Illegal option" should "show be recognized" in
    test(Array("-s", "--mode=quux", "-s"), 1)

  "No options" should "print remaining args to console" in
    test(Array("a", "b", "c"))

  "Double hyphen" should "print remaining args to console" in
    test(Array("--", "a", "b", "c"))

  "Double hyphen with options" should
    "print remaining option and args to console" in
    test(Array("--", "-h", "a", "b", "c"))

  "Double hyphen with long options" should
    "print remaining option and args to console" in
    test(Array("--", "--help", "a", "b", "c"))

  "Boolean flag syntax test 1" should "pass" in
    test(Array("--redirectOut", "-s"))

  "Boolean flag syntax test 2" should "pass" in
    test(Array("--redirectOut=true", "-s"))

  "Boolean flag syntax test 3" should "pass" in
    test(Array("--redirectOut=false", "-s"))

  "Boolean flag syntax test 4" should "fail" in
    test(Array("--redirectOut=xx", "-s"), 1)

  "Boolean flag dependency test 1" should "pass" in
    test(Array("--redirect-out", "--no-some-flag"))

  "Boolean flag dependency test 2" should "pass" in
    test(Array("--no-some-flag", "--redirect-out"))

  "Boolean flag dependency test 3" should "pass" in
    test(Array("--redirect-out", "--some-flag"))

  "Boolean flag dependency test 4" should "fail" in
    test(Array("--no-redirect-out", "--some-flag"), 1)

  "Dependency between boolean and numerical option" should "pass" in
    test(Array("--even-prime"))

  "Dependency between boolean and numerical option 2" should "pass" in
    test(Array("--no-even-prime"))

  "Dependency between boolean and numerical option 3" should "fail" in
    test(Array("--even-prime", "--small-prime=three"), 1)

  "Dependency between boolean and numerical option 4" should "fail" in
    test(Array("--odd-prime"), 1)

  "Dependency between boolean and numerical option 5" should "pass" in
    test(Array("--no-odd-prime"))

  "Dependency between boolean and numerical option 6" should "pass" in
    test(Array("--odd-prime", "--small-prime=three"))

  "Option syntax test 1" should "pass" in
    test(Array("--no-redirectOut", "-s"))

  "Option syntax test 2" should "fail" in
    test(Array("--no-redirectOut=true", "-s"), 1)

  "Option syntax test 3" should "fail" in
    test(Array("--no-redirectOut=false", "-s"), 1)

  "Option syntax test 4" should "fail" in
    test(Array("--no-redirectOut=xx", "-s"), 1)

  "Option syntax test 5" should "fail" in
    test(Array("--no-mode"), 1)

  "Option syntax test 6" should "fail" in
    test(Array("--nRuns"), 1)

  "Option syntax test 7" should "fail" in
    test(Array("--nRuns="), 1)

  "Option syntax test 8" should "fail" in
    test(Array("--nRuns=a"), 1)

  "Option syntax test 9" should "pass" in
    test(Array("--nRuns=1", "-s"))

  "Option syntax test 10" should "pass" in
    test(Array("--nRuns=999999", "-s"))

  "Option syntax test 11" should "fail" in
    test(Array("-n-runs=2"), 1)

  "Option syntax test 12" should "pass" in
    test(Array("--n-runs=2"))

  "Option range test 1" should "fail" in
    test(Array("--nRuns=0"), 1)

  "Option range test 2" should "fail" in
    test(Array("--nRuns=999999999999"), 1)

  "Option range test 3" should "pass" in
    test(Array("-s", "--small-prime=three", "-s"))

  "Option range test 4" should "fail" in
    test(Array("-s", "--small-prime=one"), 1)

  "Option range test 5" should "pass" in
    test(Array("--abortProbability=0.5", "-s"))

  "Option range test 6" should "fail" in
    test(Array("--abortProbability=-0.5", "-s"), 1)

  "Option range test 7" should "fail" in
    test(Array("--abortProbability=1.5", "-s"), 1)
  //  TODO: Test for max range on int, min/max on long

  "Option parameter test 1" should "fail" in
    test(Array("-f=x", "-s"), 1)

  "Option parameter test 2" should "fail" in
    test(Array("-g=x", "-s"), 1)

  "Option parameter test 3" should "fail" in
    test(Array("-f="), 1)

  "Option parameter test 4" should "fail" in
    test(Array("-f"), 1)

  "Option parameter test 5" should "fail" in
    test(Array("--modelClass="), 1)

  "Option parameter test 6" should "fail" in
    test(Array("--modelClass"), 1)

  "Option parameter test 7" should "fail" in
    test(Array("-n=ffffffff", "-s"), 1) // n is not in hex

  "Option parameter test 8" should "pass" in
    test(Array("-s=10c1be9b302682f3", "-s"))

  "Option parameter test 9" should "fail" in
    test(Array("-s=10c1be9b302682f30"), 1) // out of range

  "Option parameter test 10" should "pass" in
    test(Array("-s=ffffffffffffffff", "-s"))

  "Bogus parameter test 1" should "fail" in
    test(Array("--Quux"), 1)

  "Bogus parameter test 2" should "fail" in
    test(Array("--baz-Quux"), 1)
}
