package taskExecution;

import model.DatabaseHandler;
import model.Task;
import model.TaskStatus;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 14/08/15.
 */
public class GroovyTaskThread implements Runnable {
    private ExecutionTaskCallback callback;
    private Task task;

    public GroovyTaskThread(ExecutionTaskCallback callback, Task task) {
        this.callback = callback;
        this.task = task;
    }

    //Run groovy script, save the result to database
    //Callback says to the manager that task is executed and saved, so we can clean unneeded information
    private void invokeScript(String name, String... directories)
            throws ScriptException {
        ClassLoader loader = new URLClassLoader(
                buildClassPath(directories));
        ClassLoader oldLoader = Thread.currentThread()
                .getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            ScriptEngineManager seManager = new ScriptEngineManager(
                    loader);
            ScriptEngine engine = seManager.getEngineByName(name);
            if (engine == null) {
                throw new IllegalStateException("No engine for "
                        + name);
            }
            Object result = engine.eval(task.getScript());

            if (result != null)
                task.setResult(result.toString());
            task.setTaskStatus(TaskStatus.SUCCEED);
            DatabaseHandler.getInstance().updateTask(task);
            if (callback != null) {
                callback.taskExecuted(task);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(
                    oldLoader);
        }
    }

    //support method for script running
    private URL[] buildClassPath(String... directories) {
        try {
            final List<URL> classPath = new ArrayList<URL>();
            for (String directory : directories) {
                for (File pathname : new File(directory)
                        .listFiles()) {
                    if (pathname.isFile()
                            && pathname.toString().toLowerCase()
                            .endsWith(".jar")) {
                        URL url = pathname.toURI().toURL();
                        classPath.add(url);
                    }
                }
            }
            return classPath.toArray(new URL[classPath.size()]);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void run() {
        try {
            invokeScript("groovy", System.getenv("GROOVY_HOME") + "/lib");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
