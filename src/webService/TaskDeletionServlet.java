package webService;

import model.DatabaseHandler;
import model.DatabaseRequestStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Paul on 13/08/15.
 */
public class TaskDeletionServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        DatabaseRequestStatus status = DatabaseHandler.getInstance().deleteTask(idInt);

        if (status == DatabaseRequestStatus.ERROR){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"database error\"}");
        }
        else{
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"success\":\"OK\"}");
        }
    }
    private int stringToInt(String param) {
        try {
            return Integer.valueOf(param);
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}
