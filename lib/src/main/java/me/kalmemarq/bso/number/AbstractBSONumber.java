package me.kalmemarq.bso.number;

import me.kalmemarq.bso.BSOElement;

public abstract class AbstractBSONumber implements BSOElement {
    protected static final int VARNUM_BYTE = 0x30;
    protected static final int VARNUM_SHORT = 0x20;
    protected static final int VARNUM_INT = 0x10;
    
    abstract public Number numberValue();
}
