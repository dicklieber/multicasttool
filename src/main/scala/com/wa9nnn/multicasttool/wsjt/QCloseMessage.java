package com.wa9nnn.multicasttool.wsjt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QCloseMessage extends QMessage {
    final Logger log = LoggerFactory.getLogger(QCloseMessage.class); 

    QCloseMessage(String id){
        this.id = id;
        log.debug(toString());
    }

    MessageType getMessageType() {
        return QMessage.MessageType.CLOSE;
    }

    public String toString(){
        return String.format(
            "Id: %s", id    
        );
    }

}