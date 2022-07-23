package com.wa9nnn.multicasttool.wsjt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Arrays;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

class JavaParser {
    private int schemaVersion;
    private int messageType;
    byte [] rData;
    int byteCount;
    int nextByte = 0;
    final Logger log = LoggerFactory.getLogger(JavaParser.class);

    JavaParser(byte[] rData, int byteCount){
        this.rData = rData;
        this.byteCount = byteCount;
    }
    
    private byte[] nextFour(){
        byte[] res = new byte[4];
        for(int i=0; i < 4; i++){
            res[i] = rData[nextByte++];
        }
        return res;
    }

     double nextDouble(){
        byte[] res = new byte[8];
        for(int i=0; i < 8; i++){
            res[i] = rData[nextByte++];
        }
        return ByteBuffer.wrap(res).getDouble();
    }

     String nextString(){
        if(byteCount <= nextByte){
            log.debug("nextString when byteCount is {} and nextByte is {}", byteCount, nextByte);
            return "";
        }
        int strSize = nextInt();
        if(strSize == 0xffffffff){
            return ""; // Return an empty rather than null
        }
        byte [] bytes = new byte[strSize];
        System.arraycopy(rData, nextByte, bytes, 0, strSize);
        String str = new String(rData,nextByte,strSize);
        nextByte += strSize;
        return str;
    }

    private int getIntInOrder(ByteBuffer bb){
        return bb.order(ByteOrder.BIG_ENDIAN).getInt();
    }

     int nextInt(){
        byte[] theWord = nextFour();
        ByteBuffer bb = ByteBuffer.wrap(theWord);
        return getIntInOrder(bb);
    }

    private long nextLong(){
        byte[] res = new byte[8];
        for(int i=0; i < 8; i++){
            res[i] = rData[nextByte++];
        }
        ByteBuffer bb = ByteBuffer.wrap(res);
        return bb.order(ByteOrder.BIG_ENDIAN).getLong();
    }

     boolean nextBool(){
        byte b = rData[nextByte++];
        return (b == 0) ? false : true;
    }

    private int nextTimeInt(){
        return nextInt();
    }
    LocalTime nextTime() {
        long ns = MILLISECONDS.convert(nextTimeInt(), MILLISECONDS);
        return LocalTime.ofNanoOfDay(ns);
    }

    private QDecodeMessage parseDecode(){
        String id = nextString();
        boolean newDecode = nextBool();
        int time = nextTimeInt();
        int snr = nextInt();
        double deltaTime = nextDouble();
        int deltaFrequency = nextInt();
        String mode = nextString();
        String message = nextString();
        boolean lowConfidence = nextBool();
        return new QDecodeMessage(
            id, newDecode, time, snr, deltaTime, deltaFrequency,
            mode, message, lowConfidence);
    }

    private QStatusMessage parseStatus(){
        String id = nextString();
        long dialFrequency = nextLong();
        String mode = nextString();
        String dxCall = nextString();
        String report = nextString();
        String txMode = nextString();
        boolean txEnabled = nextBool();
        boolean transmitting = nextBool();
        boolean decoding = nextBool();
        int rxDF = nextInt();
        int txDF = nextInt();
        String deCall = nextString();
        String deGrid = nextString();
        String dxGrid = nextString();
        boolean txWatchdog = nextBool();
        String subMode = nextString();
        boolean fastMode = nextBool();
        return new QStatusMessage(
            id, dialFrequency, mode, dxCall, report, txMode, 
            txEnabled, transmitting, decoding, rxDF, 
            txDF, deCall, deGrid, dxGrid, txWatchdog, 
            subMode, fastMode
        );
    }

    private QCloseMessage parseClose(){
        String id = nextString();
        return new QCloseMessage(id);
    }

    QMessage decode() {
        QMessage msg = null;
        byte[] theMagic = {(byte)0xad, (byte)0xbc, (byte)0xcb, (byte)0xda};
        byte[] theWord = nextFour();
        if(!Arrays.equals(theWord, theMagic)){
            log.debug("Magic start of stream is wrong: {}", theWord);
        }
        schemaVersion = nextInt();
        if(schemaVersion != 3){
            log.debug("The schema version is wrong: {}", schemaVersion);
        }
        messageType = nextInt();
        switch(messageType){
            case 0: 
                log.debug("Got Heartbeat");
                break;
            case 1: 
                msg = parseStatus();
                break;
            case 2:
                msg = parseDecode();
                break;
            case 3:
                log.debug("Got Clear");
                break;
            case 4:
                log.debug("Got Reply");
                break;
            case 5:
                log.debug("Got QSO Logged");
                break;
            case 6:
                msg = parseClose();
                break;
            case 7:
                log.debug("Got Relay");
                break;
            case 8:
                log.debug("Got Halt Tx");
                break;
            case 9:
                log.debug("Got Free Text");
                break;
            case 10:
                log.debug("Got WSPRDecode");
                break;
            default:
                log.debug("Unknown messageType: {}", messageType);
        }
        return msg;
    }
}