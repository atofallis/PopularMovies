package com.tofallis.popularmovies.network;

public interface AsyncTaskResult<T> {
    public void onTaskCompleted(T result);
}
