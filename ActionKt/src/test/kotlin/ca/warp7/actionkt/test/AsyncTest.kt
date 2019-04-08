package ca.warp7.actionkt.test

import ca.warp7.actionkt.async
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class AsyncTest {

    private val maskedOut = ByteArrayOutputStream()
    private val systemOut = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(maskedOut))
    }

    @After
    fun restoreStreams() {
        System.setOut(systemOut)
    }

    @Test
    fun async12() {
        executeUnrestricted(async {
            +runOnce { print("1") }
            +runOnce { print("2") }
        })
        assertEquals("12", maskedOut.toString())
    }

    @Test
    fun async12wait() {
        executeUnrestricted(async {
            +queue {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
            }
            +runOnce { print("2") }
        })
        assertEquals("21", maskedOut.toString())
    }

    @Test
    fun async2queue() {
        executeUnrestricted(async {
            +queue {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
            }
            +queue {
                +wait(0.06)
                +runOnce {
                    print("2")
                }
            }
        })
        assertEquals("21", maskedOut.toString())
    }

    @Test
    fun asyncInQueue() {
        executeUnrestricted(queue {
            +async {
                +queue {
                    +wait(0.1)
                    +runOnce {
                        print("1")
                    }
                }
                +runOnce { print("2") }
            }
            +runOnce { print("3") }
        })
        assertEquals("213", maskedOut.toString())
    }
}