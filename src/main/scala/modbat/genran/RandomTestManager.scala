package modbat.genran

abstract class RandomTestManager {


  def init(classes: Seq[String], objects: Seq[AnyRef], observers: Seq[String], methods: Seq[String])

  def run

  def validate

}