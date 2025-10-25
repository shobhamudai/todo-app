package com.example.model;

public class Todo {
    private String id;
    private String task;
    private boolean completed;

    public Todo(String id, String task, boolean completed) {
        this.id = id;
        this.task = task;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}