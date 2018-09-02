/*
 * XProxy.java
 *
 * Created on 15 avril 2007, 21:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.adlitteram.jasmin.net;

/**
 *
 * @author manu
 */
public class XProxy {

    private String host;
    private int port;

    public XProxy(String host) {
        this(host, 80);
    }

    public XProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "host: " + host + " , port: " + port;
    }
}
