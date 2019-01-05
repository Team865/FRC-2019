package ca.warp7.action.test;

import ca.warp7.action.IAction;
import ca.warp7.action.impl.ActionMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;

public class AsyncOpTests {
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
    public void testAsync() {
        startMode(0.1, new ActionMode() {
            @Override
            public IAction getAction() {
                return asyncOp(
                        AsyncStart.OnStart,
                        AsyncStop.OnEachFinished,
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
                return asyncOp(
                        AsyncStart.OnStart,
                        AsyncStop.OnEachFinished,
                        new Print("hello "),
                        new Print("world ")
                ).queue(new Print("!!!"));
            }
        });
        assertEquals("hello world !!!", outContent.toString().trim());
    }

    @Test
    public void testAsyncWaitFor() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return asyncOp(
                        AsyncStart.OnStart,
                        AsyncStop.OnEachFinished,
                        waitFor(0.1).queue(new Print("there")),
                        new Print("hi ")
                );
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }

    @Test
    public void testAsyncWaitForAny() {
        startMode(0.2, new ActionMode() {
            @Override
            public IAction getAction() {
                return asyncOp(
                        AsyncStart.OnStart,
                        AsyncStop.OnAnyFinished,
                        waitFor(0.1).queue(new Print("there")),
                        new Print("hi ")
                );
            }
        });
        assertEquals("hi", outContent.toString().trim());
    }

    @Test
    public void testAsyncWaitForInverse() {
        startMode(0.5, new ActionMode() {
            @Override
            public IAction getAction() {
                return asyncOp(
                        AsyncStart.OnStaticInverse,
                        AsyncStop.OnEachFinished,
                        waitFor(0.1).queue(new Print("there")),
                        new Print("hi ")
                );
            }
        });
        assertEquals("hi there", outContent.toString().trim());
    }
}
