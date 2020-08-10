package modbat.config

import ConfigTestHarness.test

import org.scalatest._

class ConfigTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "NoInput" should "produce no output" in { td => test(Array(), td) }

  "showConfig" should "produce the same output as in the output template" in { td =>
    test(Array("-s"), td) }

  "showConfigLong" should "produce the same output as in the output template" in { td =>
    test(Array("--show"), td) }

  "IllegalArg" should "fail with an exception" in { td =>
    test(Array("-x"), td) }

  "IllegalArg2" should "fail with an exception" in { td =>
    test(Array("--x"), td) }

  "MultipleArguments" should "be parsed correctly" in { td =>
    test(Array("-s", "--mode=exec"), td) }

  "DuplicateShow" should "show configuration each time" in { td =>
    test(Array("-s", "--mode=exec", "-s"), td) }

  "Illegal option" should "fail and be recognized" in { td =>
    test(Array("-s", "--mode=quux", "-s"), td) }

  "No options" should "print remaining args to console" in { td =>
    test(Array("a", "b", "c"), td) }

  "Double hyphen" should "print remaining args to console" in { td =>
    test(Array("--", "a", "b", "c"), td) }

  "Double hyphen with options" should
    "print remaining option and args to console" in { td =>
    test(Array("--", "-h", "a", "b", "c"), td) }

  "Double hyphen with long options" should
    "print remaining option and args to console" in { td =>
    test(Array("--", "--help", "a", "b", "c"), td) }

  "Boolean flag syntax test 1" should "pass" in { td =>
    test(Array("--redirectOut", "-s"), td) }

  "Boolean flag syntax test 2" should "pass" in { td =>
    test(Array("--redirectOut=true", "-s"), td) }

  "Boolean flag syntax test 3" should "pass" in { td =>
    test(Array("--redirectOut=false", "-s"), td) }

  "Boolean flag syntax test 4" should "fail" in { td =>
    test(Array("--redirectOut=xx", "-s"), td) }

  "Boolean flag dependency test 1" should "pass" in { td =>
    test(Array("--redirect-out", "--no-some-flag"), td) }

  "Boolean flag dependency test 2" should "pass" in { td =>
    test(Array("--no-some-flag", "--redirect-out"), td) }

  "Boolean flag dependency test 3" should "pass" in { td =>
    test(Array("--redirect-out", "--some-flag"), td) }

  "Boolean flag dependency test 4" should "fail" in { td =>
    test(Array("--no-redirect-out", "--some-flag"), td) }

  "Dependency between boolean and numerical option" should "pass" in { td =>
    test(Array("--even-prime"), td) }

  "Dependency between boolean and numerical option 2" should "pass" in { td =>
    test(Array("--no-even-prime"), td) }

  "Dependency between boolean and numerical option 3" should "fail" in { td =>
    test(Array("--even-prime", "--small-prime=three"), td) }

  "Dependency between boolean and numerical option 4" should "fail" in { td =>
    test(Array("--odd-prime"), td) }

  "Dependency between boolean and numerical option 5" should "pass" in { td =>
    test(Array("--no-odd-prime"), td) }

  "Dependency between boolean and numerical option 6" should "pass" in { td =>
    test(Array("--odd-prime", "--small-prime=three"), td) }

  "Option syntax test 1" should "pass" in { td =>
    test(Array("--no-redirectOut", "-s"), td) }

  "Option syntax test 2" should "fail" in { td =>
    test(Array("--no-redirectOut=true", "-s"), td) }

  "Option syntax test 3" should "fail" in { td =>
    test(Array("--no-redirectOut=false", "-s"), td) }

  "Option syntax test 4" should "fail" in { td =>
    test(Array("--no-redirectOut=xx", "-s"), td) }

  "Option syntax test 5" should "fail" in { td =>
    test(Array("--no-mode"), td) }

  "Option syntax test 6" should "fail" in { td =>
    test(Array("--nRuns"), td) }

  "Option syntax test 7" should "fail" in { td =>
    test(Array("--nRuns="), td) }

  "Option syntax test 8" should "fail" in { td =>
    test(Array("--nRuns=a"), td) }

  "Option syntax test 9" should "pass" in { td =>
    test(Array("--nRuns=1", "-s"), td) }

  "Option syntax test 10" should "pass" in { td =>
    test(Array("--nRuns=999999", "-s"), td) }

  "Option syntax test 11" should "fail" in { td =>
    test(Array("-n-runs=2"), td) }

  "Option syntax test 12" should "pass" in { td =>
    test(Array("--n-runs=2"), td) }

  "Option range test 1" should "fail" in { td =>
    test(Array("--nRuns=0"), td) }

  "Option range test 2" should "fail" in { td =>
    test(Array("--nRuns=999999999999"), td) }

  "Option range test 3" should "pass" in { td =>
    test(Array("-s", "--small-prime=three", "-s"), td) }

  "Option range test 4" should "fail" in { td =>
    test(Array("-s", "--small-prime=one"), td) }

  "Option range test 5" should "pass" in { td =>
    test(Array("--abortProbability=0.5", "-s"), td) }

  "Option range test 6" should "fail" in { td =>
    test(Array("--abortProbability=-0.5", "-s"), td) }

  "Option range test 7" should "fail" in { td =>
    test(Array("--abortProbability=1.5", "-s"), td) }
  //  TODO: Test for max range on int, min/max on long

  "Option parameter test 1" should "fail" in { td =>
    test(Array("-f=x", "-s"), td) }

  "Option parameter test 2" should "fail" in { td =>
    test(Array("-g=x", "-s"), td) }

  "Option parameter test 3" should "fail" in { td =>
    test(Array("-f="), td) }

  "Option parameter test 4" should "fail" in { td =>
    test(Array("-f"), td) }

  "Option parameter test 5" should "fail" in { td =>
    test(Array("--modelClass="), td) }

  "Option parameter test 6" should "fail" in { td =>
    test(Array("--modelClass"), td) }

  "Option parameter test 7" should "fail" in { td =>
    test(Array("-n=ffffffff", "-s"), td) } // n is not in hex

  "Option parameter test 8" should "pass" in { td =>
    test(Array("-s=10c1be9b302682f3", "-s"), td) }

  "Option parameter test 9" should "fail" in { td =>
    test(Array("-s=10c1be9b302682f30"), td) } // out of range

  "Option parameter test 10" should "pass" in { td =>
    test(Array("-s=ffffffffffffffff", "-s"), td) }

  "Bogus parameter test 1" should "fail" in { td =>
    test(Array("--Quux"), td) }

  "Bogus parameter test 2" should "fail" in { td =>
    test(Array("--baz-Quux"), td) }
}
