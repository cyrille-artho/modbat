class TraitImpl extends Configuration {
  var data = 0
}

object TraitImpl {
  def main(args: Array[String]) {
    val ti = new TraitImpl()
    Console.out.println(ti.data)
    val clone = ti.clone.asInstanceOf[TraitImpl]
    clone.data = 1
    Console.out.println(clone.data)
    Console.out.println(ti.data)
  }
}
