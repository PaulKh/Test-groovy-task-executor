package model;

/**
 * Created by Paul on 16/08/15.
 */
public enum TaskStatus {
    EXECUTING,
    SUCCEED,
    FAILED,
    NOTFOUND;

    public static TaskStatus getStatusByInt(int status) {
        return TaskStatus.values()[status];
    }

    @Override
    public String toString() {
        switch (this) {
            case EXECUTING:
                return "Executing";
            case SUCCEED:
                return "Succeed";
            case FAILED:
                return "Failed";
            default:
                return "Task not found";
        }
    }
}
