package ca.warp7.action.test;

import ca.warp7.action.IAction;
import ca.warp7.action.impl.ActionMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;

public class PrintTests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private void startMode(double timeout, ActionMode mode) {
        IAction action = mode.getAction();
        IAction runner = ActionMode.createRunner(IAction.DefaultTimer, 0.02, timeout, action, false);
        runner.start();
        double old = System.nanoTime();
        try {
            while (!runner.shouldFinish() && System.nanoTime() - old < timeout * 1000000000) Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testPrintOnly() {
        startMode(0.05, new ActionMode() {
            @Override
            public IAction getAction() {
                return new Print("hello");
            }
        });
        assertEquals("hello", outContent.toString().trim());
    }

    @Test
    public void testQueue1() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return queue(new Print("hello"));
            }
        });
        assertEquals("hello", outContent.toString().trim());
    }

    @Test
    public void testQueue2() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return queue(
                        new Print("hello "),
                        new Print("world")
                );
            }
        });
        assertEquals("hello world", outContent.toString().trim());
    }

    @Test
    public void testQueue3() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return queue(
                        queue(
                                new Print("hello "),
                                new Print("world ")
                        ),
                        new Print("Action")
                );
            }
        });
        assertEquals("hello world Action", outContent.toString().trim());
    }

    @Test
    public void testAsync() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return async(
                        new Print("hello "),
                        new Print("world ")
                );
            }
        });
        assertEquals("hello world", outContent.toString().trim());
    }

    @Test
    public void testAsyncQueue() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return async(
                        new Print("hello "),
                        new Print("world ")
                ).queue(new Print("!!!"));
            }
        });
        assertEquals("hello world !!!", outContent.toString().trim());
    }

    @Test
    public void testAwait() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return await(d -> true).queue(new Print("hi"));
            }
        });
        assertEquals("hi", outContent.toString().trim());
    }

    @Test
    public void testWaitFor() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return waitFor(0.1).queue(new Print("hi"));
            }
        });
        assertEquals("hi", outContent.toString().trim());
    }

    @Test
    public void testAsyncWaitFor() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return async(
                        waitFor(0.1).queue(new Print("there")),
                        new Print("hi ")
                );
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }

    @Test
    public void testSimpleBroadcast() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return broadcast("INIT").when(triggeredOnce("INIT"), new Print("hi there"));
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }

    @Test
    public void testExec() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return queue(new Print("hi ")).exec(d -> System.out.print("there"));
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }

    @Test
    public void testBroadcastQueue() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return queue(
                        broadcast("INIT").when(triggeredOnce("INIT"), new Print("hi there"))
                );
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }

    @Test
    public void testBroadcastAsync() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return async(
                        broadcast("INIT"), when(triggeredOnce("INIT"), new Print("hi there"))
                );
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }
}
