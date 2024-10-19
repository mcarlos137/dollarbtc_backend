/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 *
 * @author CarlosDaniel
 */
public class CharRequestWrapper extends HttpServletRequestWrapper {

    public static final String ENCODING_UTF_8 = "UTF-8";

    /**
     * Helper class to allow getting ServletInputStream
     * 
     * @author aric.tatan
     *
     */
    public static class ByteArrayServletStream extends ServletInputStream {
        ByteArrayInputStream bais;

        ByteArrayServletStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int read() throws IOException {
            return bais.read();
        }

        @Override
        public boolean isFinished() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setReadListener(ReadListener rl) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

    private ByteArrayServletStream input;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public CharRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream inputStream = request.getInputStream();

        byte[] byteChunk = new byte[1024];
        int length = -1;

        while ((length = inputStream.read(byteChunk)) != -1) {
            baos.write(byteChunk, 0, length);
        }
        baos.flush();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        this.input = new ByteArrayServletStream(bais);

    }

    public void resetInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        this.input = new ByteArrayServletStream(bais);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.input;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        Reader reader = new InputStreamReader(this.input);
        return new BufferedReader(reader);
    }

}