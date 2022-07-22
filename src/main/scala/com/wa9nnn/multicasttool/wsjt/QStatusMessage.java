package com.wa9nnn.multicasttool.wsjt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QStatusMessage extends QMessage{
    long dialFrequency;
    String mode;
    String dxCall;
    String report;
    String txMode;
    boolean txEnabled;
    boolean transmitting;
    boolean decoding;
    int rxDF;
    int txDF;
    String deCall;
    String deGrid;
    String dxGrid;
    boolean txWatchdog;
    String subMode;
    boolean fastMode;

    final Logger log = LoggerFactory.getLogger(QStatusMessage.class);
    
    QStatusMessage(
            String id, long dialFrequency, String mode, String dxCall,
            String report, String txMode, boolean txEnabled, boolean transmitting,
            boolean decoding, int rxDF, int txDF, String deCall, String deGrid,
            String dxGrid, boolean txWatchdog, String subMode, boolean fastMode
        ){
        this.id = id;
        this.dialFrequency = dialFrequency;
        this.mode = mode;
        this.dxCall = dxCall;
        this.report = report;
        this.txMode = txMode;
        this.txEnabled = txEnabled;
        this.transmitting = transmitting;
        this.decoding = decoding;
        this.rxDF = rxDF;
        this.txDF = txDF;
        this.deCall = deCall;
        this.deGrid = deGrid;
        this.dxGrid = dxGrid;
        this.txWatchdog = txWatchdog;
        this.subMode = subMode;
        this.fastMode = fastMode;
        log.debug(toString());
    }

    MessageType getMessageType() {
        return QMessage.MessageType.STATUS;
    }

    public String toString(){
        return String.format(
            "Id: %s, dialFreq: %d, mode: %s, dxCall: %s, report: %s, txMode: %s, " +
            "txEnabled: %b, transmitting: %b, decoding: %b, rxDF: %d, " +
            "txDF: %d, deCall: %s, deGrid: %s, dxGrid: %s, txWatchdog: %b, " +
            "subMode: %s, fastMode: %b",
            id, dialFrequency, mode, dxCall, report, txMode, 
            txEnabled, transmitting, decoding, rxDF, 
            txDF, deCall, deGrid, dxGrid, txWatchdog, 
            subMode, fastMode
        );
    }
}