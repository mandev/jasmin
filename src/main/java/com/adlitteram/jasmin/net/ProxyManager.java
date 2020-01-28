package com.adlitteram.jasmin.net;

import com.adlitteram.jasmin.XProp;

public class ProxyManager {

    public static final int DIRECT = 0;
    public static final int SYSTEM = 1;
    public static final int MANUAL = 2;
    private static int httpDefault = DIRECT;

    static {
        if (XProp.get("Proxy.Http.Host") == null) {
            if (ProxySettings.getProxySettings().getDefaultProxy("http") != null) {
                httpDefault = SYSTEM;
            }
        }
    }

    public static void setDirectConnectionMode() {
        XProp.put("Proxy.Http.Connection", DIRECT);
    }

    public static void setSystemConnectionMode() {
        XProp.put("Proxy.Http.Connection", SYSTEM);
    }

    public static void setManualConnectionMode() {
        XProp.put("Proxy.Http.Connection", MANUAL);
    }

    public static boolean isConnectionDirect() {
        return (getConnectionMode() == DIRECT);
    }

    public static boolean isConnectionSystem() {
        return (getConnectionMode() == SYSTEM);
    }

    public static boolean isConnectionManual() {
        return (getConnectionMode() == MANUAL);
    }

    public static void setConnectionMode(int mode) {
        XProp.put("Proxy.Http.Connection", mode);
    }

    public static int getConnectionMode() {
        return XProp.getInt("Proxy.Http.Connection", httpDefault);
    }

    public static void init() {
        getHttpProxy();
    }

    public static XProxy getHttpProxy() {
        XProxy proxy = null;

        switch (getConnectionMode()) {
            case MANUAL:
                proxy = new XProxy(XProp.get("Proxy.Http.Host", ""), XProp.getInt("Proxy.Http.Port", 80));
                break;

            case SYSTEM:
                proxy = ProxySettings.getProxySettings().getDefaultProxy("http");
                break;
        }

        if (proxy == null) {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        }
        else {
            System.setProperty("http.proxyHost", proxy.getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
        }
        return proxy;
    }
}
