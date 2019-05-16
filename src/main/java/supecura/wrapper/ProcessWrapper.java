package supecura.wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import lombok.Getter;

public class ProcessWrapper implements Closeable {

	private Process process;
	@Getter
	private BufferedWriter writer;
	@Getter
	private BufferedReader reader;

	public ProcessWrapper(Process process) {
		this.process = process;
		OutputStream os = process.getOutputStream();
		this.writer = new BufferedWriter(new OutputStreamWriter(os));
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		this.reader = new BufferedReader(isr);
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
		this.reader.close();
		this.process.destroyForcibly();
	}

}
