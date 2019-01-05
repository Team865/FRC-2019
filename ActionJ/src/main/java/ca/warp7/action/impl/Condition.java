package ca.warp7.action.impl;

import ca.warp7.action.IAction;

class Condition extends Singleton {
    private Predicate mPredicate;
    private IAction mIf;
    private IAction mElse;
    private IAction mSelected;

    Condition(Predicate predicate, IAction ifAction, IAction elseAction) {
        mPredicate = predicate;
        mIf = ifAction;
        mElse = elseAction;
    }

    @Override
    public void start_() {
        mSelected = mPredicate.test(this) ? mIf : mElse;
        if (mSelected != null) mSelected.start();
    }

    @Override
    public boolean shouldFinish_() {
        return mSelected == null || mSelected.shouldFinish();
    }

    @Override
    public void update() {
        if (mSelected != null) mSelected.update();
    }

    @Override
    public void stop() {
        if (mSelected != null) mSelected.stop();
    }
}
