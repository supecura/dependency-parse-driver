package supecura.driver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JDepPDriver {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		Thread.currentThread().setName("main");
		ExecutorService executor = Executors.newFixedThreadPool(1);
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
		Process process = builder.start();
		Future<List<String>> f = executor.submit(new StandardOutputReader(process, "This is a pen"));
		Future<List<String>> f2 = executor.submit(new StandardOutputReader(process, "ponpon pain"));

		f.get().forEach(System.out::println);
		f2.get().forEach(System.out::println);
		executor.shutdown();
		//		if (f.isDone()) {
		//			f.cancel(true);
		//		}
	}
}
