package supecura.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public abstract class JdepPDialoguer implements Closeable {
	private static ExecutorService dialogueService = Executors.newFixedThreadPool(100);
	private static ExecutorService standardOutputConsumeService = Executors.newSingleThreadExecutor();
	private Process process;
	private BufferedWriter writer;
	private BufferedReader reader;

	public JdepPDialoguer() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
		this.process = builder.start();
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		this.reader = new BufferedReader(isr);
		OutputStream os = process.getOutputStream();
		this.writer = new BufferedWriter(new OutputStreamWriter(os));
	}

	@Override
	public void close() throws IOException {
		writer.close();
		reader.close();
		this.process.destroy();
		dialogueService.shutdown();
		standardOutputConsumeService.shutdown();
	}

	public Future<List<String>> exec(String sentence) throws Exception {
		return dialogueService.<List<String>> submit(() -> {
			Future<List<String>> f = getDependencyResult();
			sendSentence(sentence);
			List<String> list = f.get();
			return list;
		});
	}

	public Future<List<String>> getDependencyResult() throws IOException, InterruptedException {
		Thread.currentThread().setName("DependencyResult");
		Future<List<String>> f = standardOutputConsumeService.<List<String>> submit(() -> {
			List<String> list = new ArrayList<>();
			for (String str = reader.readLine(); str != null; str = reader.readLine()) {
				list.add(str);
				if ("EOS".equals(str)) {
					break;
				}
			}
			return list;
		});
		return f;
	}

	public void sendSentence(String standardInput) throws IOException {
		System.out.println("標準入力に" + standardInput + "を書き込みます。");
		writer.write(standardInput);
		writer.newLine();
		writer.flush();
	}

}
