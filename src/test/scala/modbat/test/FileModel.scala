package modbat.test

import java.io.File
import modbat.dsl._

object FileModel {
  var files: List[File] = (Nil)

  @Before def setup: Unit = {
    files = (Nil)
  }

  @After def cleanup: Unit = {
//    Console.out.println(files.size + " dirs/files created.")
    for (f <- files) {
      if (f.isDirectory()) {
//	Console.out.println("rmdir \"" + f + "\"")
  } else {
    assert (f.isFile(), { "\"" + f + "\" is neither file nor dir." })
//	Console.out.println("Deleting \"" + f + "\"")
  }
      assert(f.delete(), { "Cannot delete \"" + f + "\"" })
    }
  }
}

class FileModel (var parent: File) extends Model {
  import FileModel.files

  def this() = this(null)

  // transitions
  "init" -> "dirs" := skip
  "dirs" -> "dirs" := {
        require (files.size < 5)
    val dir = new File(parent, files.size.toString())
    assert(dir.mkdir(), { "Cannot mkdir \"" + dir.toString + "\"" })
    dir.deleteOnExit()
    files ::= dir
    maybe (launch(new FileModel(parent)))
  }
  "dirs" -> "cd" := {
    require (files.size > 0)
  }
  "cd" -> "dirs" := {
    val choice = files(choose(0, files.size))
    if (choice.isDirectory()) {
      parent = choice
      maybe (launch(new FileModel(parent)))
    }
  }
  "dirs" -> "files" := skip
  "files" -> "files" := {
    require (files.size < 7)
    val file = new File(parent, files.size.toString())
    try {
      assert(file.createNewFile())
    } catch {
      case t: Throwable => {
        Console.err.println("Cannot create file \"" + file.toString + "\"")
        throw t
  }
        }
    file.deleteOnExit()
    files ::= file
  }
  "files" -> "end" := skip
}
