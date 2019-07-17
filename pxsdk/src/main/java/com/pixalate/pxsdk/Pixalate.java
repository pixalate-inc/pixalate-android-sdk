package com.pixalate.pxsdk;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public final class Pixalate {
    public enum LogLevel {
        NONE(0),
        INFO(1),
        DEBUG(2);

        private int severity;

        LogLevel ( int severity ) {
            this.severity = severity;
        }

        public boolean includes ( LogLevel other ) {
            return this.severity >= other.severity;
        }
    }

    static final String TAG = "pxsdk";

    public static final String CLIENT_ID = "clid";
    public static final String PLATFORM_ID = "paid";
    public static final String ADVERTISER_ID = "avid";
    public static final String CAMPAIGN_ID = "caid";
    public static final String CREATIVE_ID = "plid";

    public static final String PUBLISHER_ID = "publisherId";
    public static final String SITE_ID = "siteId";
    public static final String LINE_ITEM_ID = "lineItemId";
    public static final String BID_PRICE = "priceBid";
    public static final String CLEAR_PRICE = "pricePaid";

    public static final String CREATIVE_SIZE = "kv1";
    public static final String PAGE_URL = "kv2";
    public static final String USER_ID = "kv3";
    public static final String USER_IP = "kv4";
    public static final String SELLER_ID = "kv7";
    public static final String VIDEO_LENGTH = "kv9";

    public static final String ISP = "kv10";
    public static final String IMPRESSION_ID = "kv11";
    public static final String PLACEMENT_ID = "kv12";
    public static final String CONTENT_ID = "kv13";
    public static final String MRAID_VERSION = "kv14";
    public static final String GEOGRAPHIC_REGION = "kv15";
    public static final String LATITUDE = "kv16";
    public static final String LONGITUDE = "kv17";
    public static final String APP_ID = "kv18";
    public static final String DEVICE_ID = "kv19";

    public static final String CARRIER_ID = "kv23";
    public static final String SUPPLY_TYPE = "kv24";
    public static final String APP_NAME = "kv25";
    public static final String DEVICE_OS = "kv26";
    public static final String USER_AGENT = "kv27";
    public static final String DEVICE_MODEL = "kv28";

    public static final String VIDEO_PLAY_STATUS = "kv44";

    public static final String CACHE_BUSTER = "cb";

    static final String S8_FLAG = "dvid";

    private static final String baseImpressionURL = "https://adrta.com/i?";

    static LogLevel logLevel = LogLevel.INFO;

    static int backoffCount = 5;


    Pixalate() {}

    /**
     * Sets the level to which debug statements should be logged to the console.
     * @param level The LogLevel to use.
     */
    public static void setLogLevel ( LogLevel level ) {
        logLevel = level;
    }

    /**
     * Sends the given impression to Pixalate as a url. You can create impressions with ImpressionBuilder.
     * @param impression The built impression to send.
     */
    public static void sendImpression ( Impression impression ) {
        SendRequestTask sendRequestTask = new SendRequestTask();

        sendRequestTask.execute( buildImpressionUrl( impression ) );
    }

    private static String buildImpressionUrl ( Impression impression ) {
        Uri.Builder uri = Uri.parse( baseImpressionURL )
                .buildUpon();

        for( Map.Entry<String,String> entry : impression.parameters.entrySet() ) {
            uri.appendQueryParameter( entry.getKey(), entry.getValue() );
        }

        return uri.build().toString();
    }

    /**
     * A utility class to help construct impressions.
     */
    public static class ImpressionBuilder {
        private HashMap<String,String> parameters = new HashMap<>();

        /**
         * @param clientId The Pixalate client id under which pings should be sent.
         */
        public ImpressionBuilder ( String clientId ) {
            applyDefaults( clientId );
        }

        void applyDefaults ( String clientId ) {
            setParameter( Pixalate.CLIENT_ID, clientId );
            setParameter( Pixalate.DEVICE_OS, "Android" );
            setParameter( Pixalate.SUPPLY_TYPE, "InApp" );
        }

        /**
         * Sets a parameter on the impression.
         * @param name The name of the parameter.
         * @param value The string value of the parameter.
         * @return This builder instance for chaining purposes.
         */
        public ImpressionBuilder setParameter ( String name, String value ) {
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

        ImpressionBuilder clear () {
            parameters.clear();

            return this;
        }

        /**
         * Remove a parameter that's been set on the impression.
         * @param name The name of the parameter to remove.
         * @return This builder instance for chaining purposes.
         */
        public ImpressionBuilder removeParameter ( String name ) {
            parameters.remove( name );

            return this;
        }

        /**
         * Clears all parameters from this impression builder, barring a few constants.
         * @return This builder instance for chaining purposes.
         */
        public ImpressionBuilder reset () {
            String clientId = getParameter( CLIENT_ID );

            clear();

            applyDefaults( clientId );

            return this;
        }

        /**
         * Marks this impression as being for a video ad. This acts as a shorthand to set several boilerplate parameters at once.
         * Impressions are marked as being non-video by default.
         * @param isVideo Whether this is a video ad or not.
         * @return This builder instance for chaining purposes.
         */
        public ImpressionBuilder setIsVideo ( boolean isVideo ) {
            if( isVideo ) {
                setParameter( Pixalate.SUPPLY_TYPE, "InApp_Video" );
                setParameter( Pixalate.S8_FLAG, "v" );
            } else {
                setParameter( Pixalate.SUPPLY_TYPE, "InApp" );
                removeParameter( Pixalate.S8_FLAG );
            }

            return this;
        }

        /**
         * Builds the Pixalate.Impression
         * @return The Pixalate instance.
         */
        public Pixalate.Impression build () {
            Pixalate.Impression imp = new Pixalate.Impression();
            imp.parameters.put( Pixalate.CACHE_BUSTER, String.valueOf( Math.floor( Math.random() * 999999 ) ) );
            imp.parameters.putAll( parameters );
            return imp;
        }
    }


    static void Log ( LogLevel level, String message ) {
        if( logLevel.includes( level ) ) {
            Log.d( TAG, message );
        }
    }

    static void LogError ( LogLevel level, String message ) {
        if( logLevel.includes( level ) ) {
            Log.e( TAG, message );
        }
    }

    static void LogWarning ( LogLevel level, String message ) {
        if( logLevel.includes( level ) ) {
            Log.w( TAG, message );
        }
    }

    /**
     * An immutable impression that is used to send data to Pixalate.
     */
    public static class Impression {
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
    }

    static class SendRequestTask extends AsyncTask<String,Integer,Boolean> {

        @Override
        protected Boolean doInBackground ( String... urls ) {
            for( int i = 0; i < backoffCount; i++ ) {
                try {
                    URL url = new URL( urls[ 0 ] );

                    Log( LogLevel.DEBUG, urls[ 0 ] );

                    return sendImpression( url, true );
                } catch( Exception e ) {
                    if( i == backoffCount - 1 ) {
                        Log( LogLevel.INFO, "An error occurred when attempting to send the ping." );
                        LogError( LogLevel.DEBUG, Log.getStackTraceString( e ));

                        return false;
                    } else {
                        try {
                            Thread.sleep( ( (int)Math.round( Math.pow( 2, i + 1 ) ) * 1000 ) );
                        } catch( InterruptedException ignored ) {}
                    }
                }
            }

            return false;
        }

        private boolean sendImpression ( URL url, boolean followRedirects ) throws IOException {
            HttpsURLConnection connection = null;

            try {
                connection = (HttpsURLConnection) url.openConnection();

                connection.setInstanceFollowRedirects( followRedirects );
                connection.setRequestMethod( "GET" );

                int status = connection.getResponseCode();

                Log( LogLevel.DEBUG,  String.format( "Impression response: %s", status ) );

                if( status != HttpsURLConnection.HTTP_OK ) {
                    if ( followRedirects && status == HttpsURLConnection.HTTP_MOVED_TEMP
                            || status == HttpsURLConnection.HTTP_MOVED_PERM
                            || status == HttpsURLConnection.HTTP_SEE_OTHER ) {

                        String redirectUrl = connection.getHeaderField( "Location" );

                        return sendImpression( new URL( redirectUrl ), false );
                    } else {
                        throw new IOException( "HTTPS Error: " + status );
                    }
                }
            } finally {
                if( connection != null ) connection.disconnect();
            }

            return true;
        }
    }

}

