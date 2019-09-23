package com.pixalate.sampleappandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.pixalate.pxsdk.BlockingParameters;
import com.pixalate.pxsdk.BlockingStatusListener;
import com.pixalate.pxsdk.Impression;
import com.pixalate.pxsdk.Pixalate;
import com.pixalate.pxsdk.PixalateConfig;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PXSampleApp";

    private String deviceId;
    private MoPubView moPubView;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );

        MoPubLog.setLogLevel( MoPubLog.LogLevel.DEBUG );
        Pixalate.setLogLevel( Pixalate.LogLevel.DEBUG );

        getDeviceId();

        MoPub.initializeSdk(this, new SdkConfiguration.Builder(getString(R.string.mopub_test_banner_320x250)).build(), new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                Pixalate.requestBlockStatus( new BlockingParameters.Builder()
                        .setDeviceId( deviceId )
                        .setUserAgent( "Bot Googlebot/2.1 (iPod; N; RISC OS 2.4.35; IBM360; rv1.3.1) Alligator/20080524 Jungledog/3.0" )
                        .build(), new BlockStatusListener() );
            }
        });

        Pixalate.initialize( new PixalateConfig.Builder( "<your-fraud-api-username>", "<your-fraud-api-password>" )
                .setThreshold( 0.75 )
                .build());

        moPubView = findViewById( R.id.adview );
        moPubView.setAdUnitId( getString( R.string.mopub_test_banner_320x250 ) );
    }

    @SuppressLint( "HardwareIds" )
    private void getDeviceId() {
        deviceId = Settings.Secure.getString( getContentResolver(),
                Settings.Secure.ANDROID_ID );

        // Alternatively, deviceId can be the Google Play Services advertising ID.
        // AsyncTask.execute( new Runnable() {
        //     @Override
        //     public void run () {
        //         try {
        //             deviceId = AdvertisingIdClient.getAdvertisingIdInfo( getApplicationContext() ).getId();
        //         } catch ( GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e ) {
        //             e.printStackTrace();
        //         }
        //     }
        // });
    }

    private void loadAd() {
        moPubView.loadAd();
        moPubView.setBannerAdListener( new BannerAdListener() );
    }

    private class BlockStatusListener implements BlockingStatusListener {
        @Override
        public void onBlock () {
            moPubView.destroy();
            Log.d( TAG, "Device is over the IVT threshold, blocking the ad load." );
        }

        @Override
        public void onAllow () {
            loadAd();
            Log.d( TAG, "No IVT detected, allowing the ad load." );
        }

        @Override
        public void onError ( int errorCode, String message ) {
            loadAd();
            Log.e( TAG, "There was an error getting the results, default to showing the ad:" );
            Log.e( TAG, errorCode + " " + message );
        }
    }

    private class BannerAdListener implements MoPubView.BannerAdListener {

        @Override
        public void onBannerLoaded ( MoPubView banner ) {
            Pixalate.sendImpression( new Impression.Builder( "<your client id>" )
                    .setParameter( Pixalate.APP_NAME, "com.pixalate.sampleappandroid" )
                    .setParameter( Pixalate.DEVICE_ID, deviceId )
                    .setParameter( Pixalate.CREATIVE_SIZE, "320x250" )
                    .setIsVideo( false )
                    .build() );
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {}
        @Override
        public void onBannerClicked(MoPubView banner) {}
        @Override
        public void onBannerExpanded(MoPubView banner) {}
        @Override
        public void onBannerCollapsed(MoPubView banner) {}
    }
}
