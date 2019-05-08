package supecura.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import supecura.io.JdepPDialoguer;

public abstract class JdepPExecutor implements Closeable {
	private static ExecutorService executor = Executors.newFixedThreadPool(100);
	private Process process;

	public JdepPExecutor() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
		this.process = builder.start();
	}

	public abstract List<String> parse(String sentence) throws Exception;

	@Override
	public void close() throws IOException {
		this.process.destroy();
		executor.shutdown();
	}

	public Future<List<String>> exec(String sentence) throws Exception {
		return executor.submit(new JdepPDialoguer(this.process,sentence));
	}
}
