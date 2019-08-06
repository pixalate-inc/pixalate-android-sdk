package com.pixalate.pxsdk;

import com.pixalate.pxsdk.Pixalate;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ImpressionBuilderTest {
    // internal keys

    @Test
    public void defaultParameters () {
        Pixalate.Impression result = new Pixalate.ImpressionBuilder( "px" )
                .build();

        assertEquals( result.getParameter( Pixalate.CLIENT_ID ), "px" );
        assertEquals( result.getParameter( Pixalate.DEVICE_OS ), "Android" );
        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp" );
    }

    @Test
    public void setParameter () {
        Pixalate.Impression result = new Pixalate.ImpressionBuilder( "px" )
                .setParameter( Pixalate.APP_NAME, "com.pixalate.pxsdk" )
                .build();

        assertEquals( result.getParameter( Pixalate.APP_NAME ), "com.pixalate.pxsdk" );
    }

    @Test
    public void removeParameter () {
        Pixalate.Impression result = new Pixalate.ImpressionBuilder( "px" )
                .setParameter( Pixalate.APP_NAME, "com.pixalate.pxsdk" )
                .removeParameter( Pixalate.APP_NAME )
                .build();

        assertNull( result.getParameter( Pixalate.APP_NAME ) );
    }

    @Test
    public void setIsVideo () {
        Pixalate.Impression result = new Pixalate.ImpressionBuilder( "px" )
                .setIsVideo( true )
                .build();

        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp_Video" );
        assertEquals( result.getParameter( "dvid" ), "v" );

        result = new Pixalate.ImpressionBuilder( "px" )
                .setIsVideo( false )
                .build();

        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Pixalate.ImpressionBuilder( "px" )
                .setIsVideo( true )
                .setIsVideo( false )
                .build();

        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Pixalate.ImpressionBuilder( "px" )
                .setIsVideo( true )
                .reset()
                .build();

        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp" );
        assertNull( result.getParameter( "dvid" ) );

        result = new Pixalate.ImpressionBuilder( "px" )
                .setIsVideo( false )
                .setIsVideo( true )
                .build();

        assertEquals( result.getParameter( Pixalate.SUPPLY_TYPE ), "InApp_Video" );
        assertEquals( result.getParameter( "dvid" ), "v" );
    }

    @Test
    public void reset () {
        Pixalate.Impression result = new Pixalate.ImpressionBuilder( "px" )
                .setParameter( Pixalate.APP_NAME, "com.pixalate.pxsdk" )
                .reset()
                .build();

        assertNull( result.getParameter( Pixalate.APP_NAME ) );
    }
}