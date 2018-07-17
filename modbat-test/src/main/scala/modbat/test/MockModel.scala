package modbat.test

import modbat.dsl._

class MockModel extends Model {
  "s1" -> "s2" := skip
  "s1" -> "s3" := skip
  "s2" -> "s3" := skip
}
