package com.marp;

import javax.swing.UIManager;

import com.marp.display.Window;

public class Main {

	public static void main(String[] args) {
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception ex) {
            return;
        }
        new Window();
	}

}
