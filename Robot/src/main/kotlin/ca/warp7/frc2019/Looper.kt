package ca.warp7.frc2019

import ca.warp7.frc.action.Action
import java.util.concurrent.ConcurrentHashMap

object Looper {

    private val loops: MutableSet<Action> = ConcurrentHashMap.newKeySet()

    fun add(loop: Action) {
        loop.firstCycle()
        loops.add(loop)
    }

    fun reset() {
        for (loop in loops) loop.lastCycle()
        loops.clear()
    }

    fun update() {
        val done = mutableListOf<Action>()
        for (loop in loops) {
            if (loop.shouldFinish()) {
                loop.lastCycle()
                done.add(loop)
            } else loop.update()
        }
        if (done.isNotEmpty()) loops.removeAll(done)
    }
}