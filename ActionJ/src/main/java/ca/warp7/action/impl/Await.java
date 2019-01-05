package ca.warp7.action.impl;

public class Await extends Singleton {
    private Predicate mPredicate;

    Await(Predicate predicate) {
        mPredicate = predicate;
    }

    @Override
    public void start_() {
    }

    @Override
    public boolean shouldFinish_() {
        return mPredicate.test(this);
    }
}
