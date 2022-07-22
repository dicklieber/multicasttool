package com.wa9nnn.multicasttool.wsjt;

abstract class QMessage{
    protected String id;

    public enum MessageType {
        HEARTBEAT, STATUS, DECODE, CLEAR, REPLY,
        QSO_LOGGED, CLOSE, REPLAY, HALT_TX, FREE_TEXT,
        WSPR_DECODE  
    };

    String getId(){
        return id;
    }

    abstract MessageType getMessageType();

    abstract public  String toString();
}