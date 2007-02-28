package org.jamocha.parser;

import java.io.InputStream;
import java.io.Reader;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.cool.COOLParser;

public class ParserFactory {

	public static Parser getParser(String parserName, Reader reader) throws ParserNotFoundException {
		if(parserName.equalsIgnoreCase("cool")) {
			return new COOLParser(reader);
		}
		else if(parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(reader);
		}
		else {
			throw new ParserNotFoundException("The Parser with the name \""+parserName+"\" could not be found.");
		}
	}
	
	public static Parser getParser(String parserName, InputStream stream) throws ParserNotFoundException {
		if(parserName.equalsIgnoreCase("cool")) {
			return new COOLParser(stream);
		}
		else if(parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(stream);
		}
		else {
			throw new ParserNotFoundException("The Parser with the name \""+parserName+"\" could not be found.");
		}
	}
	
}
