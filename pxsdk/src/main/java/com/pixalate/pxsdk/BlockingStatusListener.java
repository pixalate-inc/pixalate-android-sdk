package com.pixalate.pxsdk;

/**
 * Interface for responses from requestBlockStatus.
 */
public interface BlockingStatusListener {
    void onBlock ();
    void onAllow ();
    void onError ( int errorCode, String message );
}