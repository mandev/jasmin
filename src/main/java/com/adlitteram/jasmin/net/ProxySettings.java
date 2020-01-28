package com.adlitteram.jasmin.net;

import com.adlitteram.jasmin.XProp;
import com.adlitteram.jasmin.utils.NumUtils;
import com.adlitteram.jasmin.utils.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySettings {

    private static final Logger logger = LoggerFactory.getLogger(ProxySettings.class);

    public static final int HTTP = 0;
    public static final int FTP = 1;

    private static final String AUTO_DETECT = "AutoDetect=";
    private static final String AUTO_CONFIG_URL = "AutoConfigURL=";
    private static final String PROXY_URL = "ProxyURL=";
    private static final String PROXY_BYPASS = "ProxyBypass=";

    private String autoDetect;
    private String autoConfigURL;
    private String proxyURL;
    private String proxyBypass;

    private ProxySettings() {
        this("0", "", "", "");
    }

    private ProxySettings(String autoDetect, String autoConfigURL, String proxyURL, String proxyBypass) {
        this.autoDetect = autoDetect;
        this.autoConfigURL = autoConfigURL;
        this.proxyURL = proxyURL;
        this.proxyBypass = proxyBypass;
    }

    public static ProxySettings getProxySettings() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return getWindowsProxySettings();
        }
        return new ProxySettings();
    }

    private static ProxySettings getWindowsProxySettings() {
        String autoDetect = "0";
        String autoConfigURL = "";
        String proxyURL = "";
        String proxyBypass = "";

        try {
            String processName = XProp.get("CheckProxy.Exe");
            Process process = Runtime.getRuntime().exec(processName);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERR");
            errorGobbler.start();

            try (InputStreamReader isr = new InputStreamReader(process.getInputStream());
                    BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(AUTO_DETECT)) {
                        autoDetect = line.substring(AUTO_DETECT.length());
                    }
                    else if (line.startsWith(AUTO_CONFIG_URL)) {
                        autoConfigURL = line.substring(AUTO_CONFIG_URL.length());
                    }
                    else if (line.startsWith(PROXY_URL)) {
                        proxyURL = line.substring(PROXY_URL.length()).toLowerCase();
                    }
                    else if (line.startsWith(PROXY_BYPASS)) {
                        proxyBypass = line.substring(PROXY_BYPASS.length()).toLowerCase();
                    }
                }
            }
        }
        catch (IOException ex) {
            logger.warn("", ex);
        }
        return new ProxySettings(autoDetect, autoConfigURL, proxyURL, proxyBypass);
    }

    public XProxy getDefaultProxy(String protocol) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return getWindowsProxy(protocol);
        }
        return null;
    }

    //ftp=proxy.free.fr:80;http=proxy.free.fr:3128
    //=*.toto.fr;<local>
    private XProxy getWindowsProxy(String protocol) {
        if (proxyURL.length() > 0) {
            String prefix = protocol.toLowerCase() + "=";
            String[] str = proxyURL.split(";");
            for (String str1 : str) {
                if (str1.startsWith(prefix)) {
                    String[] s = str1.substring(prefix.length()).split(":");
                    return new XProxy(s[0], NumUtils.intValue(s[1], 80));
                }
                if (str[0].indexOf('=') == -1) {
                    String[] s = str1.split(":");
                    return new XProxy(s[0], NumUtils.intValue(s[1], 80));
                }
            }
        }
        return null;
    }

    public String getAutoDetect() {
        return autoDetect;
    }

    public String getAutoConfigURL() {
        return autoConfigURL;
    }

    public String getProxyURL() {
        return proxyURL;
    }

    public String getProxyBypass() {
        return proxyBypass;
    }
}
