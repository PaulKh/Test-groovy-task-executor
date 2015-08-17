package taskExecution;

import java.util.concurrent.FutureTask;

/**
 * Created by Paul on 17/08/15.
 */
public class GroovyFutureTask<V> extends FutureTask<V> {

    private GroovyTaskThread myTask;

    public GroovyFutureTask(GroovyTaskThread runnable, V result) {
        super(runnable, result);
        this.myTask = runnable;
    }

    public GroovyTaskThread getGroovyThread() {
        return myTask;
    }
}