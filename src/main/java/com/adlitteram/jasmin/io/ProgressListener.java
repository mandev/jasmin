/*
 * ProgressListener.java
 *
 * Created on 30 mai 2005, 12:46
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.adlitteram.jasmin.io;

/**
 *
 * @author Administrateur
 */
public interface ProgressListener {

    public void bytesTransferred(long count);

    public void finished(Object object);

    public void init(Object object);
}
