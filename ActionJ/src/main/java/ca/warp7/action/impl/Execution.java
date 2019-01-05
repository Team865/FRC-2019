package ca.warp7.action.impl;

class Execution extends Singleton {
    private Consumer mConsumer;

    Execution(Consumer consumer) {
        mConsumer = consumer;
    }

    @Override
    public void start_() {
        mConsumer.accept(this);
    }

    @Override
    public boolean shouldFinish_() {
        return true;
    }
}
