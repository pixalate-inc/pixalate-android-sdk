package com.pixalate.pxsdk;

public final class BlockingParameters {
    String ip;
    String deviceId;
    String userAgent;

    public String getIp () {
        return ip;
    }

    public String getDeviceId () {
        return deviceId;
    }

    public String getUserAgent () {
        return userAgent;
    }

    public boolean usingIp () {
        return ip != null && !ip.equals( "" );
    }

    public boolean usingDeviceId () {
        return deviceId != null && !deviceId.equals( "" );
    }

    public boolean usingUserAgent () {
        return userAgent != null && !userAgent.equals( "" );
    }

    /**
     * Helper class for building BlockingParameter objects.
     */
    public static final class Builder {
        private String ip;
        private String deviceId;
        private String userAgent;

        public Builder () {}

        public Builder setIp ( String ip ) {
            this.ip = ip.trim();

            return this;
        }

        public Builder setDeviceId ( String deviceId ) {
            this.deviceId = deviceId.trim();

            return this;
        }

        public Builder setUserAgent ( String userAgent ) {
            this.userAgent = userAgent.trim();

            return this;
        }

        public BlockingParameters build () {
            BlockingParameters params = new BlockingParameters();
            params.ip = ip;
            params.deviceId = deviceId;
            params.userAgent = userAgent;

            return params;
        }
    }
}
