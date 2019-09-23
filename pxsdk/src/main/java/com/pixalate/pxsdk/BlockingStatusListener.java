package com.pixalate.pxsdk;

/**
 * Interface for responses from requestBlockStatus.
 */
public interface BlockingStatusListener {
    /**
     * Method that is called if it is determined that the traffic is invalid.
     */
    void onBlock ();

    /**
     * Method that is called if it is determined that the traffic is valid.
     */
    void onAllow ();

    /**
     * Method that is called if something goes wrong with the request, for eg. incorrect login details, or a timeout.
     * @param errorCode The code for a particular error.
     * @param message The message for a particular error.
     */
    void onError ( int errorCode, String message );
}
