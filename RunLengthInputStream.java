package cdf;

import java.io.IOException;
import java.io.InputStream;

/**
 * Run length encoding decompression stream.
 * The compressed stream is just like the uncompressed
 * one, except that a byte with the special value V is followed by
 * a byte giving the number of additional bytes V to consider present
 * in the stream.
 * Thus the compressed stream:
 * <blockquote>
 *    1 2 3 0 0 4 5 6 0 2
 * </blockquote>
 * is decompressed as 
 * <blockquote>
 *    1 2 3 0 4 5 6 0 0 0 
 * </blockquote>
 * (assuming a special value V=0).
 * <p>This format was deduced from reading the cdfrle.c source file
 * from the CDF distribution.
 * 
 * @author   Mark Taylor
 * @since    17 May 2013
 */
class RunLengthInputStream extends InputStream {

    private final InputStream base_;
    private final int rleVal_;
    private int vCount_;

    public RunLengthInputStream( InputStream base, byte rleVal ) {
        base_ = base;
        rleVal_ = rleVal & 0xff;
    } 

    public int read() throws IOException {
        if ( vCount_ > 0 ) {
            vCount_--;
            return rleVal_;
        }
        else {
            int b = base_.read();
            if ( b == rleVal_ ) {
                int c = base_.read();
                if ( c >= 0 ) {
                    vCount_ = c;
                    return rleVal_;
                }
                else {
                    throw new CdfFormatException( "Bad RLE data" );
                }
            }
            else {
                return b;
            }
        }
    }

    public int available() throws IOException {
        return base_.available() + vCount_;
    }

    public void close() throws IOException {
        base_.close();
    }

    public boolean markSupported() {
        return false;
    }
}