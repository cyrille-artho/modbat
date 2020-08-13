package modbat.mbt

import java.nio.channels.AsynchronousCloseException
import java.nio.channels.ClosedChannelException
import scala.util.matching.Regex
import org.scalatest._

import modbat.log.Log

class MBTTest extends FlatSpec with Matchers {
  val mbt = new MBT(new Configuration(), new Log(Console.out, Console.err))

  "ClosedChannelException" should "match" in {
    mbt.expected(List(new Regex("ClosedCh.*Exc")),
                 new ClosedChannelException()) shouldBe true
  }

  "SomeWeirdException" should "not match ClosedChannelException" in {
    mbt.expected(List(new Regex("SomeWeird.*Exc")),
                 new ClosedChannelException()) shouldBe false
  }

  "AsynchronousCloseException" should "match ClosedChannelException" in {
    mbt.expected(List(new Regex("ClosedCh.*Exc")),
                 new AsynchronousCloseException()) shouldBe true
  }
}
