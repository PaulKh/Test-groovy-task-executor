package taskExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Paul on 15/08/15.
 */
public class TaskExecutionHelper {
    private ThreadPoolExecutor executorPool;
    private Map<Integer, GroovyTaskThread> tasks = new HashMap<>();
    public TaskExecutionHelper(){
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);
    }
    public void addTask(String script, Integer identifier){
        GroovyTaskThread taskThread = new GroovyTaskThread(script);
        tasks.put(identifier, taskThread);
        executorPool.execute(taskThread);
    }
    public void shutdown(){
        executorPool.shutdown();
    }
}
