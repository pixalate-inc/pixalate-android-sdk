package com.pixalate.pixalate_android_sdk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.pixalate.pxsdk.BlockingConfig;
import com.pixalate.pxsdk.BlockingParameters;
import com.pixalate.pxsdk.BlockingStatusListener;
import com.pixalate.pxsdk.Pixalate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View view ) {
                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                    .setAction( "Action", null ).show();
            }
        } );


        Pixalate.setLogLevel( Pixalate.LogLevel.DEBUG );

        Pixalate.setBlockingConfig( new BlockingConfig.Builder( "jason", "test" )
            .setThreshold( 0.75 )
            .build() );

        Pixalate.requestBlockStatus( new BlockingParameters.Builder()
            .setUserAgent( "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0" )
            .build(), new BlockingStatusListener() {
            @Override
            public void onBlock () {
                Log.d( "PX", "BLOCKED" );
            }

            @Override
            public void onAllow () {
                Log.d( "PX", "ALLOWED" );
            }

            @Override
            public void onError ( int errorCode, String message ) {

            }
        } );
    }

}
