package com.pixalate.pixalate_android_sdk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pixalate.pxsdk.PixalateConfig;
import com.pixalate.pxsdk.BlockingParameters;
import com.pixalate.pxsdk.BlockingStatusListener;
import com.pixalate.pxsdk.Pixalate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        Pixalate.setLogLevel( Pixalate.LogLevel.DEBUG );

        Button allowedBtn = findViewById( R.id.allowedBtn );
        Button blockedBtn = findViewById( R.id.blockedBtn );

        final TextView txt = findViewById( R.id.result );

        allowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                BlockingParameters parameters = new BlockingParameters.Builder()
                        .setUserAgent( "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36" )
                        .build();

                Pixalate.requestBlockStatus( parameters,
                        new BlockingStatusListener() {
                            @Override
                            public void onBlock () {
                                Log.d( "PX", "BLOCKED" );
                                txt.setText( "BLOCKED" );
                            }

                            @Override
                            public void onAllow () {
                                Log.d( "PX", "ALLOWED" );
                                txt.setText( "ALLOWED" );
                            }

                            @Override
                            public void onError ( int errorCode, String message ) {
                                txt.setText( "ERROR: " + errorCode + "\n" + message );
                                Log.d( "PX", errorCode + " " + message );
                            }
                        });
            }
        });

        blockedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                BlockingParameters parameters = new BlockingParameters.Builder()
                        .setUserAgent( "Bot Googlebot/2.1 (iPod; N; RISC OS 2.4.35; IBM360; rv1.3.1) Alligator/20080524 Jungledog/3.0" )
                        .build();

                Pixalate.requestBlockStatus( parameters,
                        new BlockingStatusListener() {
                            @Override
                            public void onBlock () {
                                Log.d( "PX", "BLOCKED" );
                                txt.setText( "BLOCKED" );
                            }

                            @Override
                            public void onAllow () {
                                Log.d( "PX", "ALLOWED" );
                                txt.setText( "ALLOWED" );
                            }

                            @Override
                            public void onError ( int errorCode, String message ) {
                                txt.setText( "ERROR: " + errorCode + "\n" + message );
                                Log.d( "PX", errorCode + " " + message );
                            }
                        });
            }
        });

        Pixalate.initialize( new PixalateConfig.Builder( "jason", "test" )
            .setThreshold( 0.75 )
            .build() );


    }

}
