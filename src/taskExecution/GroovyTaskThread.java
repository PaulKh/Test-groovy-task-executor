package taskExecution;

import model.DatabaseHandler;

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
public class GroovyTaskThread implements Runnable{
    private String script;
    public GroovyTaskThread(String script) {
        this.script = script;
    }
    public void addTask(String script){
        try {
            invokeScript("groovy", script, System.getenv("GROOVY_HOME") + "/lib");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    private void invokeScript(String name,
                                     String script, String... directories)
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
            Object result = engine.eval(script);
            result.toString();
        } finally {
            Thread.currentThread().setContextClassLoader(
                    oldLoader);
        }
    }

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

    @Override
    public void run() {
        addTask(script);
    }
}
