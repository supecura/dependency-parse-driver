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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class JdepPDialoguer implements Closeable {
	private static ExecutorService dialogueService = Executors.newFixedThreadPool(100);
	private static ExecutorService standardOutputConsumeService = Executors.newSingleThreadExecutor();
	private Process process;
	private BufferedWriter writer;
	private BufferedReader reader;

	public JdepPDialoguer() throws IOException {
		startJdepP();
	}

	@Override
	public void close() throws IOException {
		stopJdepP();
		dialogueService.shutdown();
		standardOutputConsumeService.shutdown();
	}

	public synchronized void startJdepP() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
		this.process = builder.start();
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		this.reader = new BufferedReader(isr);
		OutputStream os = process.getOutputStream();
		this.writer = new BufferedWriter(new OutputStreamWriter(os));
	}

	public synchronized void stopJdepP() throws IOException {
		this.writer.close();
		this.reader.close();
		this.process.destroyForcibly();
	}

	public synchronized void restartJdepP() throws IOException {
		System.out.println("restart");
		stopJdepP();
		startJdepP();
	}

	public Future<List<String>> exec(String sentence) throws Exception {
		AtomicBoolean restartFlag = new AtomicBoolean(true);
		return dialogueService.<List<String>> submit(() -> {
			List<String> list = new ArrayList<>();
			try {
				Future<List<String>> f;
				synchronized (JdepPDialoguer.this) {
					f = recieveResult();
					sendSentence(sentence);
					restartFlag.set(true);
				}
				list = f.get(1, TimeUnit.SECONDS);
				return list;
			} catch (Exception e) {
				synchronized (JdepPDialoguer.this) {
					if (restartFlag.get()) {
						restartFlag.set(false);
						restartJdepP();
					}
				}
			}
			return list;
		});

	}

	public Future<List<String>> recieveResult() throws IOException, InterruptedException {
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

	public void sendSentence(String sentence) throws IOException {
		System.out.println("標準入力に" + sentence + "を書き込みます。");
		writer.write(sentence);
		writer.newLine();
		writer.flush();
	}

}
