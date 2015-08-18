package taskExecution;

import model.DatabaseHandler;
import model.TaskStatus;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    //In the case when queue is full new task will be rejected and marked as failed
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        if (r instanceof GroovyFutureTask) {
            GroovyTaskThread taskThread = ((GroovyFutureTask) r).getGroovyThread();
            taskThread.getTask().setTaskStatus(TaskStatus.FAILED);
            DatabaseHandler.getInstance().updateTask(taskThread.getTask());
            System.out.println(r.toString() + " is rejected");
        }
    }
}
