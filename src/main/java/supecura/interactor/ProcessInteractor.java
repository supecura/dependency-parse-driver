package supecura.interactor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import supecura.wrapper.ProcessWrapper;

@Slf4j
public abstract class ProcessInteractor implements Closeable {
	private static ExecutorService dialogueService = Executors.newFixedThreadPool(100);
	private static ExecutorService standardOutputConsumeService = Executors.newSingleThreadExecutor();
	private static BlockingQueue<ProcessWrapper> queue = new ArrayBlockingQueue<ProcessWrapper>(1);
	private final int processTimeoutSeconds = 1;

	public Future<List<String>> exec(String sentence) throws Exception {
		Future<List<String>> future = dialogueService.<List<String>> submit(() -> {
			ProcessWrapper p = queue.take();
			Future<List<String>> f = interact(p, sentence);
			queue.add(p);
			try {
				return f.get(processTimeoutSeconds, TimeUnit.SECONDS);
			} catch (Exception e) {
				log.error("異常発生:{}", sentence, e);
				ProcessWrapper restartProcess = queue.poll();
				if (p == restartProcess) {
					restartProcess(restartProcess);
				} else {
					log.info("別プロセスが再実行を行っているので、restartをスルーします。");
					if (restartProcess != null) {
						//Queueから取り出したProcessを返却
						queue.add(restartProcess);
					}
				}
				throw e;
			}
		});
		return future;
	}

	public Future<List<String>> interact(ProcessWrapper p, String sentence) throws IOException, InterruptedException {
		try {
			Future<List<String>> f = submitReciever(p);
			sendSentence(p, sentence);
			return f;
		} catch (Exception e) {
			restartProcess(p);
			throw e;
		}
	};

	public Future<List<String>> submitReciever(ProcessWrapper process) throws IOException, InterruptedException {
		BufferedReader reader = process.getReader();
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

	public void sendSentence(ProcessWrapper process, String sentence) throws IOException {
		BufferedWriter writer = process.getWriter();
		log.info("標準入力に{}を書き込みます。", sentence);
		writer.write(sentence);
		writer.newLine();
		writer.flush();
	}

	public void restartProcess(ProcessWrapper p) throws IOException, InterruptedException {
		log.info("Processをrestart");
		p.close();
		log.info("Processをstop");
		startProcess();
		log.info("Processをrestart-ed");
	}

	public void startProcess(String... command) throws IOException {
		if (queue.isEmpty()) {
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			queue.add(new ProcessWrapper(process));
		}
	}

	abstract void startProcess() throws IOException;

	@Override
	public void close() throws IOException {
		ProcessWrapper p = queue.poll();
		if (p != null) {
			p.close();
		}
		dialogueService.shutdown();
		standardOutputConsumeService.shutdown();
	}

}
