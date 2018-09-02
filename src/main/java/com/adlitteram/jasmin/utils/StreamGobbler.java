
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.utils;

import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StreamGobbler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(StreamGobbler.class);
    //
    private InputStream is;
    private OutputStream os;
    private String prompt;

    public StreamGobbler(InputStream is, String prompt) {
        this(is, prompt, null);
    }

    public StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.prompt = type;
        this.os = redirect;
    }

    public void close() {
        IOUtils.closeQuietly(os);
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            if (os == null) {
                while ((line = br.readLine()) != null) {
                    logger.info(prompt, line);
                }
            }
            else {
                PrintWriter pw = new PrintWriter(os);
                while ((line = br.readLine()) != null) {
                    logger.info(prompt, line);
                    pw.println(line);
                }
                pw.flush();
            }
        }
        catch (IOException ex) {
            logger.warn("", ex);
        }
        finally {
            close();
        }
    }
}
