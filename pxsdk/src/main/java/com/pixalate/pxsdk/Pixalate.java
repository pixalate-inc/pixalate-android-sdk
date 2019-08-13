package com.pixalate.pxsdk;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public static final String APP_NAME = "kv25";
    public static final String USER_AGENT = "kv27";
    public static final String DEVICE_MODEL = "kv28";

    public static final String VIDEO_PLAY_STATUS = "kv44";

    // internal keys
    static final String DEVICE_OS = "kv26";
    static final String CLIENT_ID = "clid";
    static final String SUPPLY_TYPE = "kv24";
    static final String CACHE_BUSTER = "cb";
    static final String S8_FLAG = "dvid";

    private static final String baseImpressionURL = "https://adrta.com/i?";
    private static final String baseFraudURL = "https://api.adrta.com/services/2012/Suspect/get?";

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
     * Sends the given impression to Pixalate as a url. You can create impressions with Builder.
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
                connection.setRequestMethod( "HEAD" );

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



    static HashMap<BlockingParameters,BlockingResult> cachedResults = new HashMap<>();

    static PixalateConfig globalConfig;

    public static void initialize ( PixalateConfig config ) {
        globalConfig = config;
    }

    public static PixalateConfig getGlobalConfig () {
        return globalConfig;
    }

    /**
     * Requests a block status for the given parameters. If anything goes wrong with the request, eg. incorrect login details, it will
     * return an onError result in the listener. Otherwise, it will use the set threshold to compare probabilities and return a positive
     * or negative result in onAllow and onBlock respectively.
     * @param parameters The blocking parameters to check for.
     * @param listener The listener will be called with the results of the request.
     */
    public static void requestBlockStatus ( BlockingParameters parameters, BlockingStatusListener listener ) throws IllegalStateException {
        SendFraudRequestTask task = new SendFraudRequestTask( parameters, listener );

        if( globalConfig == null ) {
            throw new IllegalStateException( "You must set the global blocking config using `Pixalate.initialize` before requesting block status." );
        }

        if( globalConfig.cacheAge > 0 ) {
            BlockingResult result = cachedResults.get(parameters);

            if (result != null) {
                if (result.time > new Date().getTime()) {
                    Log(LogLevel.DEBUG, "Using cached results.");

                    if (result.probability > globalConfig.threshold) {
                        listener.onBlock();
                    } else {
                        listener.onAllow();
                    }
                    return;
                } else {
                    cachedResults.remove(parameters);
                }
            }
        }

        task.execute( buildBlockUrl( parameters ) );
    }

    private static String buildBlockUrl ( BlockingParameters parameters ) {
        Uri.Builder uri = Uri.parse( baseFraudURL )
            .buildUpon();

        uri.appendQueryParameter( "username", globalConfig.getUsername() );
        uri.appendQueryParameter( "password", globalConfig.getPassword() );

        if( parameters.usingIp() ) uri.appendQueryParameter( "ip", parameters.getIp() );
        if( parameters.usingDeviceId() ) uri.appendQueryParameter( "deviceId", parameters.getDeviceId() );
        if( parameters.usingUserAgent() ) uri.appendQueryParameter( "userAgent", parameters.getUserAgent() );

        return uri.build().toString();
    }


    static class BlockingResult {
        String message = null;
        int errorCode = -1;
        double probability = -1;

        long time;

        public boolean hasError () {
            return errorCode > -1;
        }
    }

    static class RequestErrorException extends Exception {
        public int status;

        public RequestErrorException ( int status, String message ) {
            super( message );
            this.status = status;
        }
    }

    static class SendFraudRequestTask extends AsyncTask<String,Integer,BlockingResult> {

        BlockingStatusListener listener;

        BlockingParameters parameters;

        public SendFraudRequestTask ( BlockingParameters parameters, BlockingStatusListener listener ) {
            this.listener = listener;
            this.parameters = parameters;
        }

        @Override
        protected BlockingResult doInBackground ( String... urls ) {
            for( int i = 0; i < backoffCount; i++ ) {
                try {
                    URL url = new URL( urls[ 0 ] );

                    Log( LogLevel.DEBUG, urls[ 0 ] );

                    return sendRequest( url );
                } catch ( Exception e ) {
                    BlockingResult result = new BlockingResult();

                    result.errorCode = 500;
                    result.message = "An unknown error occurred when attempting to send the request.";

                    if( e instanceof SocketTimeoutException ) {
                        result.errorCode = 408;
                        result.message = "The request timed out.";
                        return result;
                    }

                    if( e instanceof RequestErrorException ) {
                        result.errorCode = ((RequestErrorException) e).status;

                        if( result.errorCode == 401 || result.errorCode == 403 ) {
                            result.message = "Incorrect authentication details.";
                        }

                        // @todo: there are perhaps other statuses we need to cover for
                        return result;
                    }

                    if( i == backoffCount - 1 ) {
                        Log( LogLevel.INFO, result.message );
                        LogError( LogLevel.DEBUG, Log.getStackTraceString( e ));
                        return result;
                    } else {
                        try {
                            Thread.sleep( ( (int)Math.round( Math.pow( 2, i + 1 ) ) * 1000 ) );
                        } catch( InterruptedException ignored ) {}
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute ( BlockingResult result ) {
            if( result.hasError() ) {
                LogError( LogLevel.INFO, String.format( "Error getting data: %s %s", result.errorCode, result.message ) );
                listener.onError( result.errorCode, result.message );
            } else {
                result.time = new Date().getTime() + globalConfig.cacheAge;
                cachedResults.put( parameters, result );
                Log( LogLevel.DEBUG, "CACHING PARAMS" );
                BlockingResult res = cachedResults.get( parameters );
                Log( LogLevel.DEBUG, String.valueOf(res));

                if( result.probability > globalConfig.threshold ) {
                    listener.onBlock();
                } else {
                    listener.onAllow();
                }
            }
        }

        private BlockingResult sendRequest ( URL url ) throws IOException, RequestErrorException {
            HttpsURLConnection connection = null;

            long time = new Date().getTime();

            // @note: There's a possibility that the HTTPS connection will take seconds to connect --
            // As far as I can tell this is limited to being an emulator-only issue.

            try {
                connection = (HttpsURLConnection) url.openConnection();

                connection.setRequestMethod( "GET" );
                connection.setConnectTimeout( globalConfig.requestTimeout );

                int connStatus = connection.getResponseCode();

                // There shouldn't be any need for redirect management w/ this api.
                if( connStatus != 200 ) {
                    throw new RequestErrorException( connStatus, "HTTPS Error" );
                }

                InputStream in = connection.getInputStream();

                JsonReader reader = new JsonReader( new InputStreamReader( in, "utf8" ) );

                reader.beginObject();

                BlockingResult result = new BlockingResult();

                while( reader.hasNext() ) {
                    String name = reader.nextName();

                    switch( name ) {
                        case "status":
                            result.errorCode = reader.nextInt();
                            break;
                        case "message":
                            result.message = reader.nextString();
                            break;
                        case "probability":
                            result.probability = reader.nextDouble();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }

                reader.endObject();

                return result;
            } finally {
                if( connection != null ) connection.disconnect();
            }
        }
    }

}

