package org.zeroref.borg.sagas.infra;

import javax.annotation.Nullable;

public final class FunctionKeyReader<MESSAGE, KEY> implements KeyReader<MESSAGE, KEY> {
    private final ContextKeyExtractFunction<MESSAGE, KEY> extractFunction;
    private final Class<MESSAGE> messageClass;

    /**
     * Generates a new instance of FunctionKeyReader.
     */
    private FunctionKeyReader(final Class<MESSAGE> messageClass, final ContextKeyExtractFunction<MESSAGE, KEY> extractFunction) {
        this.messageClass = messageClass;
        this.extractFunction = extractFunction;
    }

    /**
     * Read the saga instance key from the provided message.
     */
    @Override
    @Nullable
    public KEY readKey(final MESSAGE message) {
        return extractFunction.key(message);
    }

    /**
     * Gets the class associated with the reader.
     */
    @Override
    public Class<MESSAGE> getMessageClass() {
        return messageClass;
    }

    /**
     * Creates new extractor capable of extracting a saga instance key from a message.
     */
    public static <MESSAGE> FunctionKeyReader<MESSAGE, String> create(final Class<MESSAGE> messageClazz, final KeyReadFunction<MESSAGE> readFunction) {
        return new FunctionKeyReader<>(messageClazz, ExtractFunctionReader.encapsulate(readFunction));
    }

    /**
     * Creates new extractor capable of extracting a saga instance key from a message.
     */
    public static <MESSAGE, KEY> FunctionKeyReader<MESSAGE, KEY> create(
            final Class<MESSAGE> messageClazz,
            final ContextKeyExtractFunction<MESSAGE, KEY>  contextReadFunction) {
        return new FunctionKeyReader<>(messageClazz, contextReadFunction);
    }

    /**
     * Creates new extractor capable of extracting a saga instance key from a message.
     */
    public static <MESSAGE, KEY> FunctionKeyReader<MESSAGE, KEY> create(
            final Class<MESSAGE> messageClazz,
            final KeyExtractFunction<MESSAGE, KEY> readFunction) {
        return new FunctionKeyReader<>(messageClazz, ExtractFunctionReader.encapsulate(readFunction));
    }
}
