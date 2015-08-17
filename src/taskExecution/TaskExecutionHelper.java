package taskExecution;

import model.DatabaseHandler;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Paul on 15/08/15.
 */
public class TaskExecutionHelper implements ExecutionTaskCallback {
    private static TaskExecutionHelper helper;
    private ThreadPoolExecutor executorPool;
    private Map<Integer, GroovyTaskThread> tasks = new HashMap<>();
    private Map<Integer, Future> futures = new HashMap<>();

    public static TaskExecutionHelper getInstance() {
        TaskExecutionHelper local = helper;
        if (local == null) {
            synchronized (TaskExecutionHelper.class) {
                local = helper;
                if (local == null) {
                    helper = new TaskExecutionHelper();
                    local = helper;
                }
            }
        }
        return local;
    }

    public TaskExecutionHelper() {
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), threadFactory, rejectionHandler);
    }

    public void addTask(Task task) {
        GroovyTaskThread taskThread = new GroovyTaskThread(task);
        tasks.put(task.getIdentifier(), taskThread);
        executorPool.submit(taskThread);
    }

    public void shutdown() {
        executorPool.shutdownNow();
    }

    public Task getTaskStatusAndResult(int identifier) {
        Task returnTask = new Task();
        Task taskFromDB = DatabaseHandler.getInstance().getTaskById(identifier);
        if (taskFromDB == null) {
            returnTask.setTaskStatus(TaskStatus.NOTFOUND);
        } else {
            switch (taskFromDB.getTaskStatus()) {
                case EXECUTING: {
                    GroovyTaskThread executingThread = tasks.get(identifier);
                    if (executingThread == null) {
                        returnTask.setTaskStatus(TaskStatus.FAILED);
                    } else returnTask.setTaskStatus(TaskStatus.EXECUTING);
                }
                break;
                default:
                case FAILED:
                    returnTask.setTaskStatus(taskFromDB.getTaskStatus());
                    break;
                case SUCCEED:
                    returnTask.setTaskStatus(taskFromDB.getTaskStatus());
                    returnTask.setResult(taskFromDB.getResult());
            }
        }
        return returnTask;
    }

    public void stopTask(int identifier) {
        Future future = futures.get(identifier);
        if (future != null)
            future.cancel(false);
    }

    @Override
    public synchronized void taskExecuted(Task task) {
        futures.remove(task.getIdentifier());
        tasks.remove(task.getIdentifier());
    }

    // In the case application failed before finishing the task we need to update the status of requests created by
    public void updateStatusOfTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getTaskStatus() == TaskStatus.EXECUTING) {
                GroovyTaskThread thread = this.tasks.get(task.getIdentifier());
                if (thread == null) {
                    task.setTaskStatus(TaskStatus.FAILED);
                    DatabaseHandler.getInstance().updateTask(task);
                }
            }
        }
    }
}
