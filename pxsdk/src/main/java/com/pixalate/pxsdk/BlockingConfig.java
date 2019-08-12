package com.pixalate.pxsdk;

public final class BlockingConfig {
    String username;
    String password;

    double threshold;

    boolean useSystemDeviceId;

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

    public boolean getUseSystemDeviceId () {
        return useSystemDeviceId;
    }

    public int getRequestTimeout () {
        return requestTimeout;
    }

    /**
     * Helper class for building BlockingConfig objects.
     */
    public static final class Builder {
        private final String password;
        private final String username;

        private int requestTimeout;

        private double threshold;

        public Builder ( String username, String password ) {
            this.username = username;
            this.password = password;
            this.threshold = 0.75f;
            this.requestTimeout = 2000;
        }

        public Builder setThreshold ( double threshold ) {
            if( threshold < 0.1 || threshold > 1 ) {
                throw new IllegalArgumentException( "The blocking threshold must be between 0.1 and 1." );
            }

            this.threshold = threshold;

            return this;
        }

        public Builder setRequestTimeout ( int value ) {
            this.requestTimeout = value;

            return this;
        }

        public BlockingConfig build () {
            BlockingConfig config = new BlockingConfig();
            config.username = username;
            config.password = password;
            config.threshold = threshold;
            config.requestTimeout = requestTimeout;

            return config;
        }
    }
}
