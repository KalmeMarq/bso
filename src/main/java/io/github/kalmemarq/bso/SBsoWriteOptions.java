package io.github.kalmemarq.bso;

public record SBsoWriteOptions(int indent, boolean smartNewLines, boolean smartMultilineText) {
    public static final SBsoWriteOptions MINIFIED = new SBsoWriteOptions();
    public static final SBsoWriteOptions PRETTY = new SBsoWriteOptions(2, false, false);

    public SBsoWriteOptions() {
        this(0, false, false);
    }
}
