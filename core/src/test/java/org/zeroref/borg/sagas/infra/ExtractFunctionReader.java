package org.zeroref.borg.sagas.infra;

import javax.annotation.Nullable;

final class ExtractFunctionReader<MESSAGE, KEY> implements ContextKeyExtractFunction<MESSAGE, KEY> {
    private final KeyExtractFunction<MESSAGE, KEY> extractFunction;

    /**
     * Generates a new instance of ExtractFunctionReader.
     */
    private ExtractFunctionReader(final KeyExtractFunction<MESSAGE, KEY> extractFunction) {
        this.extractFunction = extractFunction;
    }

    @Nullable
    @Override
    public KEY key(final MESSAGE message) {
        return extractFunction.key(message);
    }

    public static <MESSAGE, KEY> ContextKeyExtractFunction<MESSAGE, KEY> encapsulate(final KeyExtractFunction<MESSAGE, KEY> extractFunction) {
        return new ExtractFunctionReader<>(extractFunction);
    }
}
