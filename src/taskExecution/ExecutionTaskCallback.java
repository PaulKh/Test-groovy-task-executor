package taskExecution;

import model.Task;

/**
 * Created by Paul on 17/08/15.
 */
public interface ExecutionTaskCallback {
    void taskExecuted(Task task);
}
