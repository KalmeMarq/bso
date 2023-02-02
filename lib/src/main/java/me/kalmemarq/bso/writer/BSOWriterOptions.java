package me.kalmemarq.bso.writer;

public class BSOWriterOptions {
    public static final BSOWriterOptions DEFAULT = new BSOWriterOptions().withVarLength().withVarNum();

    private boolean allowVarNum;
    private boolean allowVarLength;
    private boolean allowIndefiniteLengthKeys;
    private boolean allowIndefiniteLengthStrings;
    private boolean compress;

    public BSOWriterOptions() {}

    public BSOWriterOptions(BSOWriterOptions options) {
        this.allowIndefiniteLengthKeys = options.allowIndefiniteLengthKeys;
        this.allowIndefiniteLengthStrings = options.allowIndefiniteLengthStrings;
        this.allowVarNum = options.allowVarNum;
        this.allowVarLength = options.allowVarLength;
        this.compress = options.compress;
    }

    public BSOWriterOptions withIndefiniteLengthKeys() {
        this.allowIndefiniteLengthKeys = true;
        return this;
    }

    public BSOWriterOptions withIndefiniteLengthStrings() {
        this.allowIndefiniteLengthStrings = true;
        return this;
    }

    public BSOWriterOptions withVarNum() {
        this.allowVarNum = true;
        return this;
    }

    public BSOWriterOptions withVarLength() {
        this.allowVarLength = true;
        return this;
    }

    public BSOWriterOptions withCompression() {
        this.compress = true;
        return this;
    }

    public boolean allowIndefiniteLengthKeys() {
        return this.allowIndefiniteLengthKeys;
    }

    public boolean allowIndefiniteLengthStrings() {
        return this.allowIndefiniteLengthStrings;
    }

    public boolean allowVarNum() {
        return this.allowVarNum;
    }

    public boolean allowVarLength() {
        return this.allowVarLength;
    }

    public boolean gzipCompression() {
        return this.compress;
    }
}
