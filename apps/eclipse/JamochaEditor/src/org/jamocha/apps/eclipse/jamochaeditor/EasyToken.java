package org.jamocha.apps.eclipse.jamochaeditor;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.SWT;

public class EasyToken extends Token {
	
	public EasyToken(String rgb_color_string) {
	    super(new TextAttribute(new Color(Display.getCurrent(),
	    		StringConverter.asRGB(rgb_color_string))));
	}
	
	public EasyToken(String rgb_color_string,boolean bold,boolean italic) {
	    super(new TextAttribute(new Color(Display.getCurrent(),
	    		StringConverter.asRGB(rgb_color_string)),null,
	    			(bold? SWT.BOLD : SWT.NORMAL) | (italic? SWT.ITALIC : SWT.NORMAL)
	    		));

	}


}
