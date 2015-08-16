package webService;

import com.google.gson.Gson;
import model.DatabaseHandler;
import model.Task;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Paul on 13/08/15.
 */
public class GetTasksServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        List<Task> tasks = DatabaseHandler.getInstance().getAll();
        response.getWriter().println(new Gson().toJson(tasks));
    }
}
