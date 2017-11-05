package com.tofallis.popularmovies.utils;

public interface AsyncTaskResult<T> {
    public void onTaskCompleted(T result);
}
