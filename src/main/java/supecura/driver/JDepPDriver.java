package supecura.driver;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JDepPDriver implements Closeable {

	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private Process process;

	public void JDepDriver() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
		this.process = builder.start();
	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		Thread.currentThread().setName("main");
		try (JDepPDriver driver = new JDepPDriver()) {
			driver.run();
		}
	}

	private void run() throws InterruptedException, ExecutionException {
		Future<List<String>> f = executor.submit(new StandardOutputReader(this.process, "This is a pen"));
		Future<List<String>> f2 = executor.submit(new StandardOutputReader(process, "ponpon pain"));
		f.get().forEach(System.out::println);
		f2.get().forEach(System.out::println);
	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
	}
}
