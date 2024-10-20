/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author CarlosDaniel
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {

    public static final String ENCODING_UTF_8 = "UTF-8";

    /**
     * Helper class to allow getting ServletOutputStream
     * 
     * @author aric.tatan
     *
     */
    public static class ByteArrayServletStream extends ServletOutputStream {
        ByteArrayOutputStream baos;

        ByteArrayServletStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int param) throws IOException {
            baos.write(param);
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setWriteListener(WriteListener wl) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

    /**
     * Helper class to allow options to pick between getWriter or getStream
     *  
     * @author aric.tatan
     *
     */
    private static class ByteArrayPrintWriter {
        
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final PrintWriter pw = new PrintWriter(baos);
        private final ServletOutputStream sos = new ByteArrayServletStream(baos);

        public PrintWriter getWriter() {
            return pw;
        }

        public ServletOutputStream getStream() {
            return sos;
        }

        public byte[] toByteArray() {
            return baos.toByteArray();
        }

        @Override
        public String toString() {
            String result = "";
            try {
                result = baos.toString(ENCODING_UTF_8);
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private final ByteArrayPrintWriter output;

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayPrintWriter();
    }

    public byte[] getByteArray() {
        return output.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return output.getStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return output.getWriter();
    }

    @Override
    public String toString() {
        return output.toString();
    }

}
