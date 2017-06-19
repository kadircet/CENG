/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.Strings;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.ProgressMonitor;

public class GifEncoder {
    private short width_;
    private short height_;
    private int numColors_;
    private byte[] pixels_;
    private byte[] colors_;

    public GifEncoder(Image image, ProgressMonitor monitor) throws AWTException {
        this.width_ = (short)image.getWidth(null);
        this.height_ = (short)image.getHeight(null);
        int[] values = new int[this.width_ * this.height_];
        PixelGrabber grabber = monitor != null ? new MyGrabber(monitor, image, 0, 0, this.width_, this.height_, values, 0, this.width_) : new PixelGrabber(image, 0, 0, (int)this.width_, (int)this.height_, values, 0, (int)this.width_);
        try {
            if (!grabber.grabPixels()) {
                throw new AWTException(Strings.get("grabberError") + ": " + grabber.status());
            }
        }
        catch (InterruptedException e) {
            // empty catch block
        }
        byte[][] r = new byte[this.width_][this.height_];
        byte[][] g = new byte[this.width_][this.height_];
        byte[][] b = new byte[this.width_][this.height_];
        int index = 0;
        for (int y = 0; y < this.height_; ++y) {
            for (int x = 0; x < this.width_; ++x) {
                r[x][y] = (byte)(values[index] >> 16 & 255);
                g[x][y] = (byte)(values[index] >> 8 & 255);
                b[x][y] = (byte)(values[index] & 255);
                ++index;
            }
        }
        this.ToIndexedColor(r, g, b);
    }

    public GifEncoder(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
        this.width_ = (short)r.length;
        this.height_ = (short)r[0].length;
        this.ToIndexedColor(r, g, b);
    }

    public void write(OutputStream output) throws IOException {
        BitUtils.WriteString(output, "GIF87a");
        ScreenDescriptor sd = new ScreenDescriptor(this.width_, this.height_, this.numColors_);
        sd.Write(output);
        output.write(this.colors_, 0, this.colors_.length);
        ImageDescriptor id = new ImageDescriptor(this.width_, this.height_, ',');
        id.Write(output);
        byte codesize = BitUtils.BitsNeeded(this.numColors_);
        if (codesize == 1) {
            codesize = (byte)(codesize + 1);
        }
        output.write(codesize);
        LZWCompressor.LZWCompress(output, codesize, this.pixels_);
        output.write(0);
        id = new ImageDescriptor(0, 0, ';');
        id.Write(output);
        output.flush();
    }

    void ToIndexedColor(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
        this.pixels_ = new byte[this.width_ * this.height_];
        this.colors_ = new byte[768];
        int colornum = 0;
        for (int x = 0; x < this.width_; ++x) {
            for (int y = 0; y < this.height_; ++y) {
                int search;
                for (search = 0; search < colornum && (this.colors_[search * 3] != r[x][y] || this.colors_[search * 3 + 1] != g[x][y] || this.colors_[search * 3 + 2] != b[x][y]); ++search) {
                }
                if (search > 255) {
                    throw new AWTException(Strings.get("manyColorError"));
                }
                this.pixels_[y * this.width_ + x] = (byte)search;
                if (search != colornum) continue;
                this.colors_[search * 3] = r[x][y];
                this.colors_[search * 3 + 1] = g[x][y];
                this.colors_[search * 3 + 2] = b[x][y];
                ++colornum;
            }
        }
        this.numColors_ = 1 << BitUtils.BitsNeeded(colornum);
        byte[] copy = new byte[this.numColors_ * 3];
        System.arraycopy(this.colors_, 0, copy, 0, this.numColors_ * 3);
        this.colors_ = copy;
    }

    public static void toFile(Image img, String filename, ProgressMonitor monitor) throws IOException, AWTException {
        FileOutputStream out = new FileOutputStream(filename);
        new GifEncoder(img, monitor).write(out);
        out.close();
    }

    public static void toFile(Image img, File file, ProgressMonitor monitor) throws IOException, AWTException {
        FileOutputStream out = new FileOutputStream(file);
        new GifEncoder(img, monitor).write(out);
        out.close();
    }

    public static void toFile(Image img, String filename) throws IOException, AWTException {
        GifEncoder.toFile(img, filename, null);
    }

    public static void toFile(Image img, File file) throws IOException, AWTException {
        GifEncoder.toFile(img, file, null);
    }

    private static class MyGrabber
    extends PixelGrabber {
        ProgressMonitor monitor;
        int progress;
        int goal;

        MyGrabber(ProgressMonitor monitor, Image image, int x, int y, int width, int height, int[] values, int start, int scan) {
            super(image, x, y, width, height, values, start, scan);
            this.monitor = monitor;
            this.progress = 0;
            this.goal = width * height;
            monitor.setMinimum(0);
            monitor.setMaximum(this.goal * 21 / 20);
        }

        @Override
        public void setPixels(int srcX, int srcY, int srcW, int srcH, ColorModel model, int[] pixels, int srcOff, int srcScan) {
            this.progress += srcW * srcH;
            this.monitor.setProgress(this.progress);
            if (this.monitor.isCanceled()) {
                this.abortGrabbing();
            } else {
                super.setPixels(srcX, srcY, srcW, srcH, model, pixels, srcOff, srcScan);
            }
        }
    }

    private static class BitUtils {
        private BitUtils() {
        }

        static byte BitsNeeded(int n) {
            byte ret = 1;
            if (n-- == 0) {
                return 0;
            }
            while ((n >>= 1) != 0) {
                ret = (byte)(ret + 1);
            }
            return ret;
        }

        static void WriteWord(OutputStream output, short w) throws IOException {
            output.write(w & 255);
            output.write(w >> 8 & 255);
        }

        static void WriteString(OutputStream output, String string) throws IOException {
            for (int loop = 0; loop < string.length(); ++loop) {
                output.write((byte)string.charAt(loop));
            }
        }
    }

    private static class ImageDescriptor {
        byte separator_;
        short leftPosition_;
        short topPosition_;
        short width_;
        short height_;
        private byte byte_;

        ImageDescriptor(short width, short height, char separator) {
            this.separator_ = (byte)separator;
            this.leftPosition_ = 0;
            this.topPosition_ = 0;
            this.width_ = width;
            this.height_ = height;
            this.SetLocalColorTableSize(0);
            this.SetReserved(0);
            this.SetSortFlag(0);
            this.SetInterlaceFlag(0);
            this.SetLocalColorTableFlag(0);
        }

        void Write(OutputStream output) throws IOException {
            output.write(this.separator_);
            BitUtils.WriteWord(output, this.leftPosition_);
            BitUtils.WriteWord(output, this.topPosition_);
            BitUtils.WriteWord(output, this.width_);
            BitUtils.WriteWord(output, this.height_);
            output.write(this.byte_);
        }

        void SetLocalColorTableSize(byte num) {
            this.byte_ = (byte)(this.byte_ | num & 7);
        }

        void SetReserved(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 3) << 3);
        }

        void SetSortFlag(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 1) << 5);
        }

        void SetInterlaceFlag(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 1) << 6);
        }

        void SetLocalColorTableFlag(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 1) << 7);
        }
    }

    private static class ScreenDescriptor {
        short localScreenWidth_;
        short localScreenHeight_;
        private byte byte_;
        byte backgroundColorIndex_;
        byte pixelAspectRatio_;

        ScreenDescriptor(short width, short height, int numColors) {
            this.localScreenWidth_ = width;
            this.localScreenHeight_ = height;
            this.SetGlobalColorTableSize((byte)(BitUtils.BitsNeeded(numColors) - 1));
            this.SetGlobalColorTableFlag(1);
            this.SetSortFlag(0);
            this.SetColorResolution(7);
            this.backgroundColorIndex_ = 0;
            this.pixelAspectRatio_ = 0;
        }

        void Write(OutputStream output) throws IOException {
            BitUtils.WriteWord(output, this.localScreenWidth_);
            BitUtils.WriteWord(output, this.localScreenHeight_);
            output.write(this.byte_);
            output.write(this.backgroundColorIndex_);
            output.write(this.pixelAspectRatio_);
        }

        void SetGlobalColorTableSize(byte num) {
            this.byte_ = (byte)(this.byte_ | num & 7);
        }

        void SetSortFlag(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 1) << 3);
        }

        void SetColorResolution(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 7) << 4);
        }

        void SetGlobalColorTableFlag(byte num) {
            this.byte_ = (byte)(this.byte_ | (num & 1) << 7);
        }
    }

    private static class LZWCompressor {
        private LZWCompressor() {
        }

        static void LZWCompress(OutputStream output, int codesize, byte[] toCompress) throws IOException {
            short prefix = -1;
            BitFile bitFile = new BitFile(output);
            LZWStringTable strings = new LZWStringTable();
            int clearcode = 1 << codesize;
            int endofinfo = clearcode + 1;
            int numbits = codesize + 1;
            int limit = (1 << numbits) - 1;
            strings.ClearTable(codesize);
            bitFile.WriteBits(clearcode, numbits);
            for (int loop = 0; loop < toCompress.length; ++loop) {
                short c = toCompress[loop];
                short index = strings.FindCharString(prefix, (byte)c);
                if (index != -1) {
                    prefix = index;
                    continue;
                }
                bitFile.WriteBits(prefix, numbits);
                if (strings.AddCharString(prefix, (byte)c) > limit) {
                    if (++numbits > 12) {
                        bitFile.WriteBits(clearcode, numbits - 1);
                        strings.ClearTable(codesize);
                        numbits = codesize + 1;
                    }
                    limit = (1 << numbits) - 1;
                }
                prefix = (short)(c & 255);
            }
            if (prefix != -1) {
                bitFile.WriteBits(prefix, numbits);
            }
            bitFile.WriteBits(endofinfo, numbits);
            bitFile.Flush();
        }
    }

    private static class LZWStringTable {
        private static final int RES_CODES = 2;
        private static final short HASH_FREE = -1;
        private static final short NEXT_FIRST = -1;
        private static final int MAXBITS = 12;
        private static final int MAXSTR = 4096;
        private static final short HASHSIZE = 9973;
        private static final short HASHSTEP = 2039;
        byte[] strChr_ = new byte[4096];
        short[] strNxt_ = new short[4096];
        short[] strHsh_ = new short[9973];
        short numStrings_;

        LZWStringTable() {
        }

        int AddCharString(short index, byte b) {
            if (this.numStrings_ >= 4096) {
                return 65535;
            }
            int hshidx = LZWStringTable.Hash((short)index, b);
            while (this.strHsh_[hshidx] != -1) {
                hshidx = (hshidx + 2039) % 9973;
            }
            this.strHsh_[hshidx] = this.numStrings_;
            this.strChr_[this.numStrings_] = b;
            this.strNxt_[this.numStrings_] = index != -1 ? index : -1;
            short s = this.numStrings_;
            this.numStrings_ = (short)(s + 1);
            return s;
        }

        short FindCharString(short index, byte b) {
            short nxtidx;
            if (index == -1) {
                return b;
            }
            int hshidx = LZWStringTable.Hash(index, b);
            while ((nxtidx = this.strHsh_[hshidx]) != -1) {
                if (this.strNxt_[nxtidx] == index && this.strChr_[nxtidx] == b) {
                    return nxtidx;
                }
                hshidx = (hshidx + 2039) % 9973;
            }
            return -1;
        }

        void ClearTable(int codesize) {
            this.numStrings_ = 0;
            for (int q = 0; q < 9973; ++q) {
                this.strHsh_[q] = -1;
            }
            int w = (1 << codesize) + 2;
            for (int q2 = 0; q2 < w; ++q2) {
                this.AddCharString(-1, (byte)q2);
            }
        }

        static int Hash(short index, byte lastbyte) {
            return (((short)(lastbyte << 8) ^ index) & 65535) % 9973;
        }
    }

    private static class BitFile {
        OutputStream output_;
        byte[] buffer_;
        int index_;
        int bitsLeft_;

        BitFile(OutputStream output) {
            this.output_ = output;
            this.buffer_ = new byte[256];
            this.index_ = 0;
            this.bitsLeft_ = 8;
        }

        void Flush() throws IOException {
            int numBytes = this.index_ + (this.bitsLeft_ == 8 ? 0 : 1);
            if (numBytes > 0) {
                this.output_.write(numBytes);
                this.output_.write(this.buffer_, 0, numBytes);
                this.buffer_[0] = 0;
                this.index_ = 0;
                this.bitsLeft_ = 8;
            }
        }

        void WriteBits(int bits, int numbits) throws IOException {
            int bitsWritten = 0;
            int numBytes = 255;
            do {
                if (this.index_ == 254 && this.bitsLeft_ == 0 || this.index_ > 254) {
                    this.output_.write(numBytes);
                    this.output_.write(this.buffer_, 0, numBytes);
                    this.buffer_[0] = 0;
                    this.index_ = 0;
                    this.bitsLeft_ = 8;
                }
                if (numbits <= this.bitsLeft_) {
                    byte[] arrby = this.buffer_;
                    int n = this.index_;
                    arrby[n] = (byte)(arrby[n] | (bits & (1 << numbits) - 1) << 8 - this.bitsLeft_);
                    bitsWritten += numbits;
                    this.bitsLeft_ -= numbits;
                    numbits = 0;
                    continue;
                }
                byte[] arrby = this.buffer_;
                int n = this.index_++;
                arrby[n] = (byte)(arrby[n] | (bits & (1 << this.bitsLeft_) - 1) << 8 - this.bitsLeft_);
                bitsWritten += this.bitsLeft_;
                bits >>= this.bitsLeft_;
                numbits -= this.bitsLeft_;
                this.buffer_[this.index_] = 0;
                this.bitsLeft_ = 8;
            } while (numbits != 0);
        }
    }

}

