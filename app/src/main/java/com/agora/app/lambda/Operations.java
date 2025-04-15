package com.agora.app.lambda;

public enum Operations {
    GET(true, false),
    PUT(true, true),
    DELETE(true, false),
    BATCH_GET(false, false),
    BATCH_PUT(false, true),
    BATCH_DELETE(false, false);

    public final boolean isSingleOp;
    public final boolean isDataCarryingOp;

    private Operations (boolean isSingleOp, boolean isDataCarryingOp) {
        this.isSingleOp = isSingleOp;
        this.isDataCarryingOp = isDataCarryingOp;
    }
}
