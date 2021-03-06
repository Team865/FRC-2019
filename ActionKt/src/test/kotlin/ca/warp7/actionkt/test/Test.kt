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
        !"runOnce"
        +runOnce {
        }

        !"Periodic"
        +periodic {
        }

        +action {
            printTaskGraph()
            onStart { }
            finishWhen { true }
            onUpdate { }
            onStop { }
        }

        +wait(1)
        +waitUntil { true }

        +async {
            +runOnce {

            }
        }
    }
}

fun test3(): Action {
    return async {
    }
}