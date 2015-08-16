package model;

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
                    " RESULT            TEXT, " +
                    "STATUS INTEGER NOT NULL)";
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
                Integer status = rs.getInt("STATUS");
                Task task = new Task(identifier, script);
                task.setResult(taskResult);
                task.setTaskStatus(TaskStatus.getStatusByInt(status));
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

    public int addNewTask(String script) {
        String sql = "INSERT INTO TASK (GROOVY_SCRIPT, STATUS) " +
                "VALUES (\"" + script + "\"," + 0 + ");";
        PreparedStatement pstmt;
        int key = -1;
        try {
            pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();

            if(keys.next())
                key = keys.getInt(1);
            keys.close();
            pstmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        return key;

    }
    public DatabaseRequestStatus deleteTask(int identifier){
        String sql = "DELETE FROM TASK WHERE ID = "+ identifier + ";";
        return executeStatement(sql);
    }
    public DatabaseRequestStatus updateTask(int identifier, String result){
        String sql = "UPDATE TASK SET RESULT = \"" +
                result + "\" WHERE ID = "+ identifier + ";";
        return executeStatement(sql);
    }
    public DatabaseRequestStatus updateTask(int identifier, TaskStatus status){
        String sql = "UPDATE TASK SET STATUS = " +
                status.ordinal() + " WHERE ID = "+ identifier + ";";
        return executeStatement(sql);
    }
    public DatabaseRequestStatus updateTask(int identifier, String result, TaskStatus status){
        String sql = "UPDATE TASK SET RESULT = \"" +
                result + "\", STATUS = " + status.ordinal() + " WHERE ID = "+ identifier + ";";
        return executeStatement(sql);
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
                Integer status = rs.getInt("STATUS");
                task = new Task(identifier, script);
                task.setResult(taskResult);
                task.setTaskStatus(TaskStatus.getStatusByInt(status));
            }
            rs.close();
            stmt.close();
            return task;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private DatabaseRequestStatus executeStatement(String sql){
        Connection connection = getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            return DatabaseRequestStatus.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return DatabaseRequestStatus.ERROR;
        }
    }

}
