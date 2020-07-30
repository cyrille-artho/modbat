/** Mock environment object to test online vs offline behavior.
  * Simulates case where non-deterministic system call does not
  * behave identically when called a second time (offline). */

package modbat.test

import modbat.mbt.MBT

object MockExcEnv {
  def nonDetCall(): Unit = {
    if (MBT.isOffline) {
      Console.out.println("Offline mode: throwing exception.")
      throw new java.io.IOException("Test")
    }
    Console.out.println("Online mode: not throwing exception.")
  }
}
