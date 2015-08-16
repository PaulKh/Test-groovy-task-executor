import taskExecution.GroovyTaskThread;
import taskExecution.TaskExecutionHelper;
import webService.WebService;

/**
 * Created by Paul on 13/08/15.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        TaskExecutionHelper executionHelper = new TaskExecutionHelper();
        WebService webService = new WebService();
        webService.startServer();

//
//        model.DatabaseHandler databaseHandler = new model.DatabaseHandler();
        String script = "class Person {\n" +
                "    String name\n" +
                "}\n" +
                "def p = new Person(name: 'Norman')\n" +
                "p.getName()";

//        new Thread(new GroovyTaskThread(script)).start();
    }


}
