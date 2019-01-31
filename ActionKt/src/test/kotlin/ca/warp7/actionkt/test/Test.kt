package ca.warp7.actionkt.test

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.action

fun test(): Action {
    return action {
        onStart { }
        onUpdate { }
        onStop {
        }
        finishWhen { true }
    }
}