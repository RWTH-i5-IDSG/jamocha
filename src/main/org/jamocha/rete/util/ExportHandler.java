package org.jamocha.rete.util;

import java.util.Iterator;

import org.jamocha.rete.wme.Deffact;

import java.util.Map;

public interface ExportHandler {
	public long export(Iterator<Deffact> iterator, Map<String,String> config);
}
