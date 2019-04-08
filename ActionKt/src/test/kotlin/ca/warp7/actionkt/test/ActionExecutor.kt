package ca.warp7.actionkt.test

import ca.warp7.actionkt.Action

fun executeUnrestricted(action: Action) {
    action.start()
    while (!action.shouldFinish) {
        action.update()
        Thread.sleep(20)
    }
    action.stop()
}