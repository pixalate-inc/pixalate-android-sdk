package com.pixalate.pxsdk;

public final class PixalateConfig {
    long cacheAge;
    String username;
    String password;

    double threshold;

//    boolean useSystemDeviceId;

    int requestTimeout;

    public String getUsername () {
        return username;
    }

    public String getPassword () {
        return password;
    }

    public double getThreshold () {
        return threshold;
    }

    public long getCacheAge() {
        return cacheAge;
    }

    //    public boolean getUseSystemDeviceId () {
//        return useSystemDeviceId;
//    }

    public int getRequestTimeout () {
        return requestTimeout;
    }

    /**
     * Helper class for building PixalateConfig objects.
     */
    public static final class Builder {
        private final String password;
        private final String username;
        private final long cacheAge;

        private int requestTimeout;

        private double threshold;

        public Builder ( String username, String password ) {
            this.username = username;
            this.password = password;
            this.threshold = 0.75f;
            this.requestTimeout = 2000;
            this.cacheAge = 1000 * 60 * 60 * 8;
        }

        /**
         * The comparison value to be used as a measure of whether to allow the traffic, or block it.
         * @param threshold The threshold, from 0.1 to 1, that sets the maximum allowable IVT probability.
         * @return This builder instance for chaining purposes.
         */
        public Builder setThreshold ( double threshold ) {
            if( threshold < 0.1 || threshold > 1 ) {
                throw new IllegalArgumentException( "The blocking threshold must be between 0.1 and 1." );
            }

            this.threshold = threshold;

            return this;
        }

        /**
         * The maximum time a request for blocking information can take -- anything beyond this will count as a failed attempt and call the error listener.
         * @param timeout The timeout value in milliseconds.
         * @return This builder instance for chaining purposes.
         */
        public Builder setRequestTimeout ( int timeout ) {
            this.requestTimeout = timeout;

            return this;
        }

        public PixalateConfig build () {
            PixalateConfig config = new PixalateConfig();
            config.username = username;
            config.password = password;
            config.threshold = threshold;
            config.requestTimeout = requestTimeout;
            config.cacheAge = cacheAge;

            return config;
        }
    }
}
