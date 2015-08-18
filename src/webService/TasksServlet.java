package webService;

import com.google.gson.Gson;
import model.DatabaseHandler;
import model.DatabaseRequestStatus;
import model.Task;
import taskExecution.TaskExecutionHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by Paul on 18/08/15.
 */
public class TasksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        List<Task> tasks = DatabaseHandler.getInstance().getAll();
        TaskExecutionHelper.getInstance().updateStatusOfTasks(tasks);
        response.getWriter().println(new Gson().toJson(tasks));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        response.setContentType("text/html");
        if (id == null) {
            response.setStatus(400);
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
        DatabaseRequestStatus status = DatabaseHandler.getInstance().deleteTask(idInt);
        TaskExecutionHelper.getInstance().stopTask(idInt);
        if (status == DatabaseRequestStatus.ERROR) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"database error\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"success\":\"OK\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        Task task = new Gson().fromJson(readPostData(request), Task.class);
        if (task.getScript() != null) {
            int id = DatabaseHandler.getInstance().addNewTask(task.getScript());
            task.setIdentifier(id);
            TaskExecutionHelper.getInstance().addTask(task);
            response.getWriter().println("{\"success\":\"OK\"," +
                    "\"identifier\":\"" + id + "\"}");
        } else {
            response.getWriter().println("{\"error\":\"Wrong format\"}");
        }
    }

    //Get json data from the request
    private String readPostData(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String data = buffer.toString();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}