package org.zeroref.borg.sagas.infra;

public interface KeyReader<MESSAGE, KEY> {

    KEY readKey(MESSAGE message);
    Class<MESSAGE> getMessageClass();
}
