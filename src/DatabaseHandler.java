import model.Task;

import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 13/08/15.
 */
public class DatabaseHandler {
    private static volatile DatabaseHandler databaseHandler;
    private static final boolean defaultIsExecuted = false;
    private static final String defaultResult = "";

    public static DatabaseHandler getInstance(){
        DatabaseHandler local = databaseHandler;
        if (local == null){
            synchronized (DatabaseHandler.class){
                local = databaseHandler;
                if (local == null){
                    databaseHandler = new DatabaseHandler();
                    local = databaseHandler;
                }
            }
        }
        return local;
    }
    public Connection getConnection() {
        Connection connection = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ping_database.db");
            System.out.println("Opened database successfully");
            stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS TASK " +
                    " (ID INTEGER PRIMARY KEY     NOT NULL," +
                    " GROOVY_SCRIPT           TEXT    NOT NULL," +
                    " RESULT            TEXT     NOT NULL) ";
            stmt.executeUpdate(sql);
            stmt.close();
            return connection;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return null;
    }
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<Task>();
        Connection connection = getConnection();
        if (connection == null)
            return null;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TASK;");
            while (rs.next()) {
                Integer identifier = rs.getInt("ID");
                String taskResult = rs.getString("RESULT");
                String script = rs.getString("GROOVY_SCRIPT");
                Task task = new Task(identifier, script);
                task.setResult(taskResult);
//                task.setIsExecuted(isExecuted == 0 ? false : true);
                tasks.add(task);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void addNewTask(String script) {
        String sql = "INSERT INTO TASK (GROOVY_SCRIPT,RESULT) " +
                "VALUES (\"" + script + "\", \"" + defaultResult + "\");";
        executeStatement(sql);
    }

    public void updateTable(int identifier, String result){
        String sql = "UPDATE TASK SET RESULT = \"" +
                result + "\" WHERE ID = "+ identifier + ";";
        executeStatement(sql);
    }
    public Task getTaskById(int identifier){
        Connection connection = getConnection();
        if (connection == null)
            return null;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TASK WHERE ID = " + identifier + ";");
            Task task = null;
            if (rs.next()) {
                String taskResult = rs.getString("RESULT");
                String script = rs.getString("GROOVY_SCRIPT");
                task = new Task(identifier, script);
                task.setResult(taskResult);
            }
            rs.close();
            stmt.close();
            return task;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void executeStatement(String sql){
        Connection connection = getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
