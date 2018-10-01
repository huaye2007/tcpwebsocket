package com.next.network.websocket;

import io.netty.util.internal.AppendableCharSequence;

public class LineParser extends HeaderParser {
    public LineParser(AppendableCharSequence seq, int maxLength) {
        super(seq, maxLength);
    }
}
