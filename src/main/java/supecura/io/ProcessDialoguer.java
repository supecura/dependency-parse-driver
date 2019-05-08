package supecura.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public abstract class ProcessDialoguer {

	private Process process;

	public ProcessDialoguer(Process process) {
		this.process = process;
		Thread.currentThread().setName("JdepP");
	}

	public void sendStandardInput(String standardInput) throws IOException {
		OutputStream os = process.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		bw.write(standardInput);
		bw.newLine();
		bw.flush();
	}

	protected InputStream retrieveStandardOutput() throws IOException {
		return this.process.getInputStream();
	}
}
