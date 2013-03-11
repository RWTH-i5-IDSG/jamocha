package org.jamocha.engine.rating;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

	final FileWriter file;
	static final String linesep = System.getProperty("line.separator");
	static final String tab = "\t";

	public CSVWriter(final File file, final String... headers)
			throws IOException {
		this.file = new FileWriter(file);
		println(implode(headers, tab));
	}

	private String implode(final Object[] values, final String seperator) {
		if (null == values) {
			return null;
		}
		if (0 == values.length) {
			return "";
		}
		final StringBuffer sb = new StringBuffer();
		sb.append(values[0]);
		for (int i = 1; i < values.length; ++i) {
			sb.append(seperator);
			sb.append(values[i]);
		}
		return sb.toString();
	}

	public void addValues(final Object... values) throws IOException {
		println(implode(values, tab));
	}

	public void print(final String str) throws IOException {
		this.file.write(str);
	}

	public void println(final String str) throws IOException {
		this.file.write(str);
		this.file.write(linesep);
	}

	public void newline() throws IOException {
		this.file.write(linesep);
	}

	public void flush() throws IOException {
		this.file.flush();
	}

	public void close() throws IOException {
		this.file.close();
	}
}
