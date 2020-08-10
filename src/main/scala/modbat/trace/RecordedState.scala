package modbat.trace

import modbat.dsl.State
import modbat.mbt.ModelInstance

case class RecordedState (val model: ModelInstance, val State: State)
