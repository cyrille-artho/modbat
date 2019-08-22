package modbat.test

import modbat.dsl.Model
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.IOException

class FileTestModel extends Model {
  var isOpen = false

  // transitions
  "uninit" -> "open" := {
    require(!isOpen); System.out.println("*** open *** "); isOpen = true
  }
  "uninit" -> "open" := {
    require(!isOpen)
    System.out.println("*** open2 *** ")
    maybe (throw new FileNotFoundException)
    maybe (throw new IOException)
  } throws ("FileNotFoundException")
  "open" -> "open" := {
    maybe (throw new IOException)
    maybe (throw new EOFException)
  } catches ("IOException" -> "err", "EOFException" -> "err2")
  "open" -> "open" := {
    require(isOpen)
    System.out.println("*** r/w ***")
    maybe { throw new IOException }
  }
  "open" -> "open" := skip
  "open" -> "closed" := { require(isOpen); System.out.println("*** close *** ") }
  "err" -> "err2" := { System.out.println("*** cleanup -> err2 *** ") }
}
