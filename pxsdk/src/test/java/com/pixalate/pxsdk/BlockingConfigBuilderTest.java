package com.pixalate.pxsdk;

import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class BlockingConfigBuilderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void defaultParameters () {
        BlockingConfig result = new BlockingConfig.Builder( "jason", "test" )
            .build();

        assertEquals( result.getUsername(), "jason" );
        assertEquals( result.getPassword(), "test" );
        assertEquals( result.getThreshold(), 0.75, 0 );
    }

    @Test
    public void modifiedThreshold () {
        BlockingConfig result = new BlockingConfig.Builder( "jason", "test" )
            .setThreshold( 0.9 )
            .build();

        assertEquals( result.getUsername(), "jason" );
        assertEquals( result.getPassword(), "test" );
        assertEquals( result.getThreshold(), 0.9, 0 );
    }

    @Test
    public void invalidLowThresholdShouldThrow () {
        exception.expect( IllegalArgumentException.class );
        BlockingConfig result = new BlockingConfig.Builder( "jason", "test" )
            .setThreshold( 0 )
            .build();
    }

    @Test
    public void invalidHighThresholdShouldThrow () {
        exception.expect( IllegalArgumentException.class );
        BlockingConfig result = new BlockingConfig.Builder( "jason", "test" )
            .setThreshold( 1.1 )
            .build();
    }
}
