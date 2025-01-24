package com.adlitteram.jasmin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StreamGobbler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(StreamGobbler.class);

    private final InputStream is;
    private final OutputStream os;
    private final String prompt;

    public StreamGobbler(InputStream is, String prompt) {
        this(is, null, prompt);
    }

    public StreamGobbler(InputStream is, OutputStream redirect, String prompt) {
        this.is = is;
        this.prompt = prompt;
        this.os = redirect;
    }

    @Override
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {

            String line;

            if (os == null) {
                while ((line = br.readLine()) != null) {
                    logger.info(prompt, line);
                }
            } else {
                PrintWriter pw = new PrintWriter(os);
                while ((line = br.readLine()) != null) {
                    logger.info(prompt, line);
                    pw.println(line);
                }
                pw.flush();
            }
        } catch (IOException ex) {
            logger.warn("", ex);
        }
    }
}
