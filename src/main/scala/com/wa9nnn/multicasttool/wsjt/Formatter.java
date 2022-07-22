package com.wa9nnn.multicasttool.wsjt;

class Formatter{
    public static String formatDeltaTime(double deltaTime){
        return String.format("%4.1f", deltaTime);
    }

    public static String formatTime(int msec){
        int t = msec;
        int hours = msec/(60 * 60000);
        t -= 60 * 60000 * hours;
        int mins = t/(60000);
        t -= 60000 * mins;
        int secs = t/1000;
        t -= 1000 * secs;
        int msecs = t;
        return String.format("%02d:%02d:%02d.%03d", hours, mins, secs, msecs); 
    }
}