package webService;

import model.Task;
import taskExecution.TaskExecutionHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Paul on 16/08/15.
 */
public class GetResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        response.setContentType("text/html");
        if (id == null) {
            response.setStatus(420);
            response.getWriter().println("{\"error\":\"Specify id as param\"}");
            return;
        }
        int idInt;
        try {
            idInt = Integer.valueOf(id);
        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().println("{\"error\":\"Wrong format of id\"}");
            return;
        }
        Task task = TaskExecutionHelper.getInstance().getTaskStatusAndResult(idInt);

        if (task == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"no tasks with such id\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            if (task.getResult() != null)
                response.getWriter().println("{\"result\":\"" + task.getResult() + "\"," +
                        "\"status\":\"" + task.getTaskStatus() + "\"}");
            else
                response.getWriter().println("{\"status\":\"" + task.getTaskStatus() + "\"}");
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println();
    }
}