/**
  * Copyright (C) 1999-2002 Emmanuel Deviller
  *
  * @version 1.0
  * @author Emmanuel Deviller  */
	
package com.adlitteram.jasmin.gui.widget;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;


// Utiliser cette classe en liaison avec ObjectString pour associer une objet 
// dont le toString() ne doit pas etre la valeur d'affichage
// ex. pour color => on peut souhaiter afficher "Rouge" et non Color.RED.toString()

public class KComboBox extends JComboBox {

	public KComboBox() {
		super() ;
	}

	public KComboBox(ComboBoxModel aModel) {
		super(aModel) ;
	}

	public KComboBox(Object[] items) {
		super(items) ;
	}

	public KComboBox(Vector items) {
		super(items) ;
	} 

	// Select the obj item 
	public void setSelectedObject(Object obj) {

		if ( obj == null ) return ; 
		//if ( obj instanceof ObjectString ) 	super.setSelectedItem(obj) ;

		for (int i=0; i<getItemCount(); i++ ) {
			ObjectString os = (ObjectString) getItemAt(i) ;
			if ( obj.equals(os.getObject()) ) {
				super.setSelectedItem(os) ;
				return ;
			}
		}

		// En desespoir de cause
		if ( isEditable ) 
			super.setSelectedItem(new ObjectString(obj, obj.toString())) ;
	}		

	// To bad ! cannot subclass getSelectedItem because it's used internally by JCombobox
	public Object getSelectedObject() {
		ObjectString os  = (ObjectString) super.getSelectedItem() ;
		return ( os == null ) ? null : os.getObject() ;	
	}

} 
