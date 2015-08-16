package model;

/**
 * Created by Paul on 16/08/15.
 */
public enum TaskStatus {
    EXECUTING,
    FAILED,
    SUCCEED;
    public static TaskStatus getStatusByInt(int status){
        switch (status){
            case 0:
                return EXECUTING;
            case 1:
                return FAILED;
            default: return SUCCEED;
        }
    }
    @Override
    public String toString(){
        switch (this){
            case EXECUTING:
                return "Executing";
            case FAILED:
                return "Failed";
            default: return "Succeed";
        }
    }
}
