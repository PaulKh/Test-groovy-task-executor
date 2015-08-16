package webService;

import com.google.gson.Gson;
import model.DatabaseHandler;
import model.Task;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Paul on 13/08/15.
 */
public class CreateTaskServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        Task task = new Gson().fromJson(readPostData(request), Task.class);
        if (task.getScript() != null){
            DatabaseHandler.getInstance().addNewTask(task.getScript());
        }
        response.getWriter().println("{\"success\":\"OK\"}");
    }
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