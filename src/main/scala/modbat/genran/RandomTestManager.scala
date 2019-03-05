package modbat.genran

trait RandomTestManager {


  def init(paths: Seq[String])

  def run
  def validate

}