package com.wa9nnn.multicasttool.wsjt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QDecodeMessage extends QMessage{
    boolean newDecode;
    int time;
    int snr; 
    double deltaTime;
    int deltaFrequency;
    String mode;
    String message;
    boolean lowConfidence;
    final Logger log = LoggerFactory.getLogger(QDecodeMessage.class);
    
    QDecodeMessage(String id, boolean newDecode, int time, int snr, 
                   double deltaTime, int deltaFrequency, String mode,
                   String message, boolean lowConfidence){
        this.id = id;
        this.newDecode = newDecode;
        this.time = time;
        this.snr = snr;
        this.deltaTime = deltaTime;
        this.deltaFrequency = deltaFrequency;
        this.mode = mode;
        this.message = message;
        this.lowConfidence = lowConfidence;            
        log.debug(toString());
    }

    MessageType getMessageType() {
        return QMessage.MessageType.DECODE;
    }

    public String toString(){
        return String.format(
            "Id: %s, New: %b, Time: %s, SNR: %d, dT: %s, dF: %d, mode: %s, message: %s, lowConfidence: %b",
            id, newDecode, Formatter.formatTime(time), snr,
            Formatter.formatDeltaTime(deltaTime), deltaFrequency, mode,
            message, lowConfidence
        );
    }
}