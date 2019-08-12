package com.pixalate.pxsdk;

import org.junit.Test;

import static org.junit.Assert.*;
import static com.pixalate.pxsdk.Pixalate.*;

public class ImpressionBuilderTest {
    // internal keys

    @Test
    public void defaultParameters () {
        Impression result = new Impression.Builder( "px" )
                .build();

        assertEquals( result.getParameter( CLIENT_ID ), "px" );
        assertEquals( result.getParameter( DEVICE_OS ), "Android" );
        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp" );
    }

    @Test
    public void setParameter () {
        Impression result = new Impression.Builder( "px" )
                .setParameter( APP_NAME, "com.pixalate.pxsdk" )
                .build();

        assertEquals( result.getParameter( APP_NAME ), "com.pixalate.pxsdk" );
    }

    @Test
    public void removeParameter () {
        Impression result = new Impression.Builder( "px" )
                .setParameter( APP_NAME, "com.pixalate.pxsdk" )
                .removeParameter( APP_NAME )
                .build();

        assertNull( result.getParameter( APP_NAME ) );
    }

    @Test
    public void setIsVideo () {
        Impression result = new Impression.Builder( "px" )
                .setIsVideo( true )
                .build();

        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp_Video" );
        assertEquals( result.getParameter( "dvid" ), "v" );

        result = new Impression.Builder( "px" )
                .setIsVideo( false )
                .build();

        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Impression.Builder( "px" )
                .setIsVideo( true )
                .setIsVideo( false )
                .build();

        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Impression.Builder( "px" )
                .setIsVideo( true )
                .reset()
                .build();

        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Impression.Builder( "px" )
                .setIsVideo( false )
                .setIsVideo( true )
                .build();

        assertEquals( result.getParameter( SUPPLY_TYPE ), "InApp_Video" );
        assertEquals( result.getParameter( "dvid" ), "v" );
    }

    @Test
    public void reset () {
        Impression result = new Impression.Builder( "px" )
                .setParameter( APP_NAME, "com.pixalate.pxsdk" )
                .reset()
                .build();

        assertNull( result.getParameter( APP_NAME ) );
    }
}