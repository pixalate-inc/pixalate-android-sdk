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

    boolean usingIp () {
        return ip != null && !ip.equals( "" );
    }

    boolean usingDeviceId () {
        return deviceId != null && !deviceId.equals( "" );
    }

    boolean usingUserAgent () {
        return userAgent != null && !userAgent.equals( "" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockingParameters parameters = (BlockingParameters) o;
        return equals(ip,parameters.ip) &&
                equals(deviceId,parameters.deviceId) &&
                equals(userAgent,parameters.userAgent);
    }

    boolean equals( Object a, Object b ) {
        return ( a == b ) || ( a != null && a.equals( b ) );
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (ip == null ? 0 : ip.hashCode());
        result = 31 * result + (deviceId == null ? 0 : deviceId.hashCode());
        result = 31 * result + (userAgent == null ? 0 : userAgent.hashCode());

        return result;
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


            if( !params.usingDeviceId() && !params.usingUserAgent() && !params.usingIp() ) {
                throw new IllegalArgumentException( "You must include at least one of the three parameters before building the BlockingParameters object!   " );
            }

            return params;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockingParameters parameters = (BlockingParameters) o;
        return equals(ip,parameters.ip) &&
                equals(deviceId,parameters.deviceId) &&
                equals(userAgent,parameters.userAgent);
    }

    boolean equals( Object a, Object b ) {
        return ( a == b ) || ( a != null && a.equals( b ) );
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (ip == null ? 0 : ip.hashCode());
        result = 31 * result + (deviceId == null ? 0 : deviceId.hashCode());
        result = 31 * result + (userAgent == null ? 0 : userAgent.hashCode());

        return result;
    }
}
