package com.pixalate.pxsdk;


import java.util.HashMap;

/**
 * An immutable impression that is used to send data to Pixalate.
 */
public final class Impression {
    HashMap<String,String> parameters = new HashMap<>();

    Impression () {}

    /**
     * Retrieves a parameter that has been set on this impression.
     *
     * @param parameterName The parameter to retrieve the value of.
     * @return The string value of the given parameter, or null if that parameter has not been set.
     */
    public String getParameter ( String parameterName ) {
        return parameters.get( parameterName );
    }

    /**
     * A utility class to help construct impressions.
     */
    public static final class Builder {
        private HashMap<String,String> parameters = new HashMap<>();

        // don't let the clientId be removed
        private String clientId;

        private Boolean isVideo;

        /**
         * @param clientId The Pixalate client id under which pings should be sent.
         */
        public Builder ( String clientId ) {
            this.clientId = clientId;
            isVideo = false;
        }

        /**
         * Sets a parameter on the impression.
         * @param name The name of the parameter.
         * @param value The string value of the parameter.
         * @return This builder instance for chaining purposes.
         */
        public Builder setParameter ( String name, String value ) {
            parameters.put( name, value );

            return this;
        }

        /**
         * Gets a parameter that's been set on the impression.
         * @param name The name of the parameter to get.
         * @return The value of the parameter, if it exists.
         */
        public String getParameter ( String name ) {
            return parameters.get( name );
        }

        Builder clear () {
            parameters.clear();

            return this;
        }

        /**
         * Remove a parameter that's been set on the impression.
         * @param name The name of the parameter to remove.
         * @return This builder instance for chaining purposes.
         */
        public Builder removeParameter ( String name ) {
            parameters.remove( name );

            return this;
        }

        /**
         * Clears all parameters from this impression builder, barring a few internal constants.
         * @return This builder instance for chaining purposes.
         */
        public Builder reset () {
            clear();

            isVideo = false;

            return this;
        }

        /**
         * Marks this impression as being for a video ad. This acts as a shorthand to set several boilerplate parameters at once.
         * Impressions are marked as being non-video by default.
         * @param isVideo Whether this is a video ad or not.
         * @return This builder instance for chaining purposes.
         */
        public Builder setIsVideo ( boolean isVideo ) {
            this.isVideo = isVideo;

            return this;
        }

        /**
         * Builds the Pixalate.Impression
         * @return The Pixalate instance.
         */
        public Impression build () {
            Impression imp = new Impression();
            imp.parameters.put( Pixalate.CLIENT_ID, clientId );
            imp.parameters.put( Pixalate.DEVICE_OS, "Android" );

            if( isVideo ) {
                imp.parameters.put( Pixalate.SUPPLY_TYPE, "InApp_Video" );
                imp.parameters.put( Pixalate.S8_FLAG, "v" );
            } else {
                imp.parameters.put( Pixalate.SUPPLY_TYPE, "InApp" );
            }

            imp.parameters.put( Pixalate.CACHE_BUSTER, String.valueOf( Math.floor( Math.random() * 999999 ) ) );
            imp.parameters.putAll( parameters );
            return imp;
        }
    }

}