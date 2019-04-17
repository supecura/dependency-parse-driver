package supecura.driver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JDepPDriver {

	public static void main(String[] args) throws IOException {
		ExecutorService executor = Executors.newFixedThreadPool(3);

		ProcessBuilder builder = new ProcessBuilder("", "");


		Process process = builder.start();
		Future<?> f = executor.submit(new StandardOutputReader(process));
		if (f.isDone()) {
			f.cancel(true);
		}
	}
}
