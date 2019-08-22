/** Mock environment object to test online vs offline behavior.
  * Simulates case where non-deterministic system call does not
  * behave identically when called a second time (offline). */

package modbat.test

import modbat.mbt.MBT

object MockEnv {
  def nonDetCall() = {
    val result = !MBT.isOffline
    System.out.println("Result of call is " + result + ".")
    result
  }
}
