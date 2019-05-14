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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JdepPDialoguer implements Closeable {
	private static ExecutorService dialogueService = Executors.newFixedThreadPool(100);
	private static ExecutorService standardOutputConsumeService = Executors.newSingleThreadExecutor();
	private static BlockingQueue<Process> queue = new ArrayBlockingQueue<Process>(1);

	public JdepPDialoguer() throws IOException {
		startJdepP();
	}

	@Override
	public void close() throws IOException {
		Process p = JdepPDialoguer.queue.poll();
		p.destroyForcibly();
		dialogueService.shutdown();
		standardOutputConsumeService.shutdown();
	}

	public void startJdepP() throws IOException {
		if (queue.isEmpty()) {
			ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "mecab | jdepp");
			Process process = builder.start();
			queue.add(process);
		}
	}

	public void restartJdepP(Process p) throws IOException, InterruptedException {
		log.info("JdepPを再起動");
		p.destroyForcibly();
		log.info("JdepPを停止完了");
		startJdepP();
		log.info("JdepPを再実行完了");
	}

	public Future<List<String>> exec(String sentence) throws Exception {
		Future<List<String>> future = dialogueService.<List<String>> submit(() -> {
			List<String> list = new ArrayList<>();
			Process p = JdepPDialoguer.queue.take();
			Future<List<String>> f = null;
			try {
				f = recieveResult(p);
				sendSentence(p, sentence);
			} catch (Exception e) {
				restartJdepP(p);
				throw e;
			}
			queue.add(p);
			try {
				list = f.get(1, TimeUnit.SECONDS);
				return list;
			} catch (Exception e) {
				Process restartProcess = queue.poll();
				if (p == restartProcess) {
					restartJdepP(restartProcess);
				} else {
					log.info("別プロセスが再実行を行っているので、restartをスルーします。");
					if (restartProcess != null) {
						queue.add(restartProcess);
					}
				}
				throw e;
			}
		});
		return future;
	}

	public Future<List<String>> recieveResult(Process process) throws IOException, InterruptedException {
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader reader = new BufferedReader(isr);
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

	public void sendSentence(Process process, String sentence) throws IOException {
		OutputStream os = process.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
		log.info("標準入力に{}を書き込みます。", sentence);
		writer.write(sentence);
		writer.newLine();
		writer.flush();
	}

}
