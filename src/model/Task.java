package model;

import java.io.Serializable;

/**
 * Created by Paul on 13/08/15.
 */
public class Task implements Serializable{
    private int identifier;
    private String script;
    private boolean isExecuted;
    private String result;

    public Task(int identifier, String script) {
        this.identifier = identifier;
        this.script = script;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setIsExecuted(boolean isExecuted) {
        this.isExecuted = isExecuted;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
