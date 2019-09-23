package com.pixalate.pxsdk;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class BlockingConfigBuilderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void defaultParameters () {
        PixalateConfig result = new PixalateConfig.Builder( "foo", "bar" )
            .build();

        assertEquals( result.getUsername(), "foo" );
        assertEquals( result.getPassword(), "bar" );
        assertEquals( result.getThreshold(), 0.75, 0 );
    }

    @Test
    public void modifiedThreshold () {
        PixalateConfig result = new PixalateConfig.Builder( "foo", "bar" )
            .setThreshold( 0.9 )
            .build();

        assertEquals( result.getUsername(), "foo" );
        assertEquals( result.getPassword(), "bar" );
        assertEquals( result.getThreshold(), 0.9, 0 );
    }

    @Test
    public void invalidLowThresholdShouldThrow () {
        exception.expect( IllegalArgumentException.class );
        PixalateConfig result = new PixalateConfig.Builder( "foo", "bar" )
            .setThreshold( 0 )
            .build();
    }

    @Test
    public void invalidHighThresholdShouldThrow () {
        exception.expect( IllegalArgumentException.class );
        PixalateConfig result = new PixalateConfig.Builder( "foo", "bar" )
            .setThreshold( 1.1 )
            .build();
    }
}
