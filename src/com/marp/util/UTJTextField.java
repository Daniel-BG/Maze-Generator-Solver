package com.marp.util;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Update text JTextField for easier usage of text fields
 * @author Dani
 *
 */
public abstract class UTJTextField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2495240060647925L;

	final JTextField field = this;
	{
		this.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				check();
			}
			public void removeUpdate(DocumentEvent e) {
				check();
			}
			public void insertUpdate(DocumentEvent e) {
				check();
			}
			public void check() {
				onTextUpdate(field.getText());
			}
		});
	}
	
	protected abstract void onTextUpdate(String text);
}
