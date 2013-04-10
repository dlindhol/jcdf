package cdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class Record {

    private final RecordPlan plan_;
    private static final Logger logger_ =
        Logger.getLogger( Record.class.getName() );

    protected Record( RecordPlan plan ) {
        this( plan, null );
    }

    protected Record( RecordPlan plan, int fixedType ) {
        this( plan, new int[] { fixedType } );
    }

    protected Record( RecordPlan plan, int[] fixedTypes ) {
        plan_ = plan;
        if ( fixedTypes != null ) {
            int planType = plan.getRecordType();
            if ( ! containsValue( fixedTypes, planType ) ) {
                StringBuffer msgBuf = new StringBuffer()
                    .append( "Incorrect record type (" )
                    .append( planType );
                if ( fixedTypes.length == 1 ) {
                    msgBuf.append( " != " )
                          .append( fixedTypes[ 0 ] );
                }
                else {
                    msgBuf.append( " not in " )
                          .append( Arrays.toString( fixedTypes ) );
                }
                throw new IllegalArgumentException( msgBuf.toString() );
            }
        }
    }

    public static int checkIntValue( int actualValue, int fixedValue ) {
        if ( actualValue != fixedValue ) {
            String warning = "Unexpected fixed value " + actualValue + " != "
                           + fixedValue;
            assert false : warning;
            logger_.warning( warning );
        }
        return actualValue;
    }

    /**
     * Reads a moderately-sized integer array.
     * If it's bulk data, we should use a different method.
     */
    public static int[] readIntArray( Buf buf, Pointer ptr, int count ) {
        int[] array = new int[ count ];
        for ( int i = 0; i < count; i++ ) {
            array[ i ] = buf.readInt( ptr );
        }
        return array;
    }

    /**
     * Splits an ASCII string into 0x0A-terminated lines.
     * As per CdfDescriptorRecord copyright field.
     */
    public static String[] toLines( String text ) {
        List<String> lines = new ArrayList<String>();

        /* Line ends in regexes are so inscrutable that use of String.split()
         * seems too much trouble.  See Goldfarb's First Law Of Text
         * Processing. */
        int nc = text.length();
        StringBuilder sbuf = new StringBuilder( nc );
        for ( int i = 0; i < nc; i++ ) {
            char c = text.charAt( i );
            if ( c == 0x0a ) {
                lines.add( sbuf.toString() );
                sbuf.setLength( 0 );
            }
            else {
                sbuf.append( c );
            }
        }
        if ( sbuf.length() > 0 ) {
            lines.add( sbuf.toString() );
        }
        return lines.toArray( new String[ 0 ] );
    }

    private static boolean containsValue( int[] haystack, int needle ) {
        for ( int i = 0; i < haystack.length; i++ ) {
            if ( needle == haystack[ i ] ) {
                return true;
            }
        }
        return false;
    }
}
