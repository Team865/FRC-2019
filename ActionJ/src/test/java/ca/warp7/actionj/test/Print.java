package ca.warp7.actionj.test;

import ca.warp7.actionj.IAction;

public class Print implements IAction {

    private String mToPrint;

    Print(String toPrint) {
        mToPrint = toPrint;
    }

    @Override
    public void start() {
        System.out.print(mToPrint);
    }

    @Override
    public boolean shouldFinish() {
        return true;
    }
}
