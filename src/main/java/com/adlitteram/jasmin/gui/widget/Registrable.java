/*
 * ProgressListener.java
 *
 * Created on 30 mai 2005, 12:46
 *
 */
package com.adlitteram.jasmin.gui.widget;

import javax.swing.Action;

public interface Registrable {

    public Action getAction();

    public void update(Object object);

}
