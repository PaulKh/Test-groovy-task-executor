package webService;

import com.google.gson.Gson;
import model.DatabaseHandler;
import model.DatabaseRequestStatus;
import model.Task;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Paul on 16/08/15.
 */
public class GetResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        response.setContentType("text/html");
        if (id == null) {
            response.setStatus(400);
            response.getWriter().println("{\"error\":\"Specify id as param\"}");
            return;
        }
        int idInt = stringToInt(id);
        if (idInt == -1)
            return;
        Task task = DatabaseHandler.getInstance().getTaskById(idInt);

        if (task == null){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"no tasks with such id\"}");
        }
        else{
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
    private int stringToInt(String param) {
        try {
            return Integer.valueOf(param);
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}