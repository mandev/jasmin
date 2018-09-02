/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adlitteram.jasmin;

import java.awt.Window;

/**
 *
 * @author Administrateur
 */
public interface Applicationable {

    public String getApplicationName();

    public String getApplicationRelease();

    public String getApplicationBuild();

    public Class getMainClass() ;

    public Window getMainFrame() ;
    
    public String getUserConfDir() ;

    public String getUserLogDir() ;

    public String getUserPropFile() ;

    public String getLangDir() ;

    public String getLogName() ;

}
