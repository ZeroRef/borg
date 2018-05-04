package org.zeroref.borg.pipeline;

public interface HandleMessages<T> {
    void handle(T message);
}
