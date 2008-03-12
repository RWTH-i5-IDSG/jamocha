package org.jamocha.sampleimplementations;

import java.util.Iterator;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.util.ExportHandler;
import org.jamocha.rete.wme.Deffact;

public class SampleExportHandler implements ExportHandler {

	public long export(Iterator<Deffact> iterator, Map<String, String> config) {
		int deleted = 0;
		while (iterator.hasNext()) {
			Deffact a = iterator.next();
			
			try {
				if (a.getSlotValue( config.get("removeSlot") ) != null) {
					iterator.remove();
					deleted++;
				} else {
					System.out.println("fact :" + a.toPPString().toUpperCase());
				}
			} catch (EvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return deleted;
	}

}
