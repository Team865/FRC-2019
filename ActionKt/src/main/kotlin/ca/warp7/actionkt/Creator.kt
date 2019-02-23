package ca.warp7.actionkt

interface Creator<T> {
    operator fun T.unaryPlus(): T
}