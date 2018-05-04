package org.zeroref.borg.pipeline;


import org.zeroref.borg.MessageEnvelope;

public interface DispatchMessagesToHandlers {
    void dispatch(MessageEnvelope message);
}
