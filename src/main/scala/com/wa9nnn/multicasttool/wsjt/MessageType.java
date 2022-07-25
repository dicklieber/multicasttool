package com.wa9nnn.multicasttool.wsjt;

/**
 * See also: https://sourceforge.net/p/wsjt/wsjtx/ci/master/tree/Network/NetworkMessage.hpp
 */
public enum MessageType {
    HEARTBEAT, STATUS, DECODE, CLEAR, REPLY,
    QSO_LOGGED, CLOSE, REPLAY, HALT_TX, FREE_TEXT,
    WSPR_DECODE,
    LOCATION,
    ADIF,
    HIGHLIGHT_CALLSIGN,
    SWITCH_CONFIGURATION,
    CONFIGURE
};
