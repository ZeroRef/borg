package org.zeroref.borg.pipeline;


import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.timeouts.Timeout;

public interface DispatchMessagesToHandlers {
    void dispatch(MessageEnvelope message);
    void dispatch(Timeout timeout);
}
