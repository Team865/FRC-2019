@file:Suppress("unused")

package ca.warp7.actionkt.test

import ca.warp7.actionkt.*

fun test(): Action {
    return action {
        onStart { }
        onUpdate { }
        onStop {
        }
        finishWhen { true }
    }
}

fun test2(): Action {
    return queue {

        +runOnce {
        }

        +periodic {

        }

        +action {
        }

        +wait(1)
    }
}