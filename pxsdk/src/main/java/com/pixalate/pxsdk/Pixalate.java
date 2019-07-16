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




    private static final String baseImpressionURL = "https://adrta.com/i?";


    Pixalate() {}

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

    public static class ImpressionBuilder {

        private HashMap<String,String> parameters = new HashMap<>();

        /**
         * A utility class to help construct impressions.
         * @param clientId The Pixalate client id under which pings should be sent.
         */
        public ImpressionBuilder ( String clientId ) {
            parameters.put( Pixalate.CLIENT_ID, clientId );
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
         * Build the Pixalate.
         * @return The Pixalate instance.
         */
        public Pixalate.Impression build () {
            Pixalate.Impression imp = new Pixalate.Impression();
            imp.parameters.put( Pixalate.CACHE_BUSTER, String.valueOf( Math.floor( Math.random() * 999999 ) ) );
            imp.parameters.putAll( parameters );
            return imp;
        }
    }


    public static class Impression {
        HashMap<String,String> parameters = new HashMap<>();

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
            try {
                URL url = new URL( urls[ 0 ] );

                Log.d( "PX", urls[ 0 ] );

                return sendImpression( url, true );
            } catch( Exception e ) {
                e.printStackTrace();
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

                Log.d( "PX", String.valueOf(status));

                if( status != HttpsURLConnection.HTTP_OK ) {
                    if ( followRedirects && status == HttpsURLConnection.HTTP_MOVED_TEMP
                            || status == HttpsURLConnection.HTTP_MOVED_PERM
                            || status == HttpsURLConnection.HTTP_SEE_OTHER ) {

                        String redirectUrl = connection.getHeaderField( "Location" );

                        return sendImpression( new URL( redirectUrl ), false );
                    } else {
                        throw new IOException("HTTPS Error: " + status);
                    }
                }
            } finally {
                if( connection != null ) connection.disconnect();
            }

            return true;
        }
    }

}

