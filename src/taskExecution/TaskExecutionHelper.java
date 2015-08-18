package taskExecution;

import model.DatabaseHandler;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Paul on 15/08/15.
 */
public class TaskExecutionHelper implements ExecutionTaskCallback {
    private static TaskExecutionHelper helper;
    private ThreadPoolExecutor executorPool;
    private Map<Integer, GroovyFutureTask> tasks = new ConcurrentHashMap<>();

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

    //Init thread pool
    public TaskExecutionHelper() {
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200), threadFactory, rejectionHandler);
    }

    //put task in the list and in the queue of the pool
    public void addTask(Task task) {
        GroovyTaskThread taskThread = new GroovyTaskThread(this, task);
        GroovyFutureTask futureTask = new GroovyFutureTask<>(taskThread, null);
        tasks.put(task.getIdentifier(), futureTask);
        executorPool.execute(futureTask);
    }

    public void shutdown() {
        executorPool.shutdownNow();
    }

    //This method checks if task is executing or already executed in the database or failed
    public Task getTaskStatusAndResult(int identifier) {
        Task returnTask = new Task();
        Task taskFromDB = DatabaseHandler.getInstance().getTaskById(identifier);
        if (taskFromDB == null) {
            returnTask.setTaskStatus(TaskStatus.NOTFOUND);
        } else {
            switch (taskFromDB.getTaskStatus()) {
                case EXECUTING: {
                    GroovyTaskThread executingThread = tasks.get(identifier).getGroovyThread();
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
        Future future = tasks.get(identifier);
        if (future != null)
            future.cancel(false);
    }

    //Remove task from the list after finishing the execution
    @Override
    public void taskExecuted(Task task) {
        tasks.remove(task.getIdentifier());
    }

    // In the case application failed before finishing the task we need to update the status of requests created by
    public void updateStatusOfTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getTaskStatus() == TaskStatus.EXECUTING) {
                GroovyFutureTask futureTask = this.tasks.get(task.getIdentifier());
                if (futureTask != null) {
                    GroovyTaskThread thread = futureTask.getGroovyThread();
                    if (thread == null) {
                        task.setTaskStatus(TaskStatus.FAILED);
                        DatabaseHandler.getInstance().updateTask(task);
                    }
                }
            }
        }
    }
}
