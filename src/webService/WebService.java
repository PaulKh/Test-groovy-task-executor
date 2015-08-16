package webService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Created by Paul on 16/08/15.
 */
public class WebService {
    public void startServer() throws Exception {
        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(GetTasksServlet.class, "/tasks");//Set the servlet to run.
        handler.addServletWithMapping(webService.TaskDeletionServlet.class, "/tasks/delete");
        handler.addServletWithMapping(CreateTaskServlet.class, "/tasks/add");
        handler.addServletWithMapping(GetResultServlet.class, "/tasks/result");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
