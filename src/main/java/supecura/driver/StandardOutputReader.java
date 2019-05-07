package supecura.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class StandardOutputReader implements Runnable {

	private Process process;

	public StandardOutputReader(Process process) {
		this.process = process;
	}

	@Override
	public void run() {
		try {
			OutputStream os = process.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			TimeUnit.SECONDS.sleep(1);
			bw.write(
					"This is a pen");
			bw.newLine();
			bw.flush();
			TimeUnit.SECONDS.sleep(1);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			br.lines().forEach(System.out::println);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
