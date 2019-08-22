package modbat.test

import java.io.File
import modbat.dsl._

object SimpleFileModel {
  var files: List[File] = (Nil)

  @Before def setup {
    files = (Nil)
  }

  @After def cleanup {
    System.out.println(files.size + " dirs/files created.")
    for (f <- files) {
      if (f.isDirectory()) {
    System.out.println("rmdir \"" + f + "\"")
  } else {
    assert (f.isFile(), { "\"" + f + "\" is neither file nor dir." })
    System.out.println("Deleting \"" + f + "\"")
  }
      assert(f.delete(), { "Cannot delete \"" + f + "\"" })
    }
  }
}

class SimpleFileModel (var parent: File) extends Model {
  import SimpleFileModel.files

  def this() = this(null)

  // transitions
  "init" -> "dirs" := skip
  "dirs" -> "dirs" := {
        require (files.size < 5)
    val dir = new File(parent, files.size.toString())
    assert(dir.mkdir(), { "Cannot mkdir \"" + dir.toString + "\"" })
    dir.deleteOnExit()
    files ::= dir
    maybe (launch(new SimpleFileModel(parent)))
  }
  "dirs" -> "cd" := {
    require (files.size > 0)
  }
  "cd" -> "dirs" := {
    parent = files(choose(0, files.size))
    maybe (launch(new SimpleFileModel(parent)))
  }
  "dirs" -> "files" := skip
  "files" -> "files" := {
    require (files.size < 7)
    val file = new File(parent, files.size.toString())
    try {
      assert(file.createNewFile())
    } catch {
      case t: Throwable => {
        System.err.println("Cannot create file \"" + file.toString + "\"")
        throw t
  }
        }
    file.deleteOnExit()
    files ::= file
  }
  "files" -> "end" := skip
}
