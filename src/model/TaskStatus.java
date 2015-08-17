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
        switch (status) {
            case 0:
                return EXECUTING;
            case 1:
                return SUCCEED;
            case 2:
                return FAILED;
            default:
            case 3:
                return NOTFOUND;
        }
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
