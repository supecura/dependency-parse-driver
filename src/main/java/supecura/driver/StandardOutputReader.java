package supecura.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StandardOutputReader implements Callable<List<String>> {

	private Process process;
	private String sentence;

	public StandardOutputReader(Process process, String sentence) {
		this.sentence = sentence;
		this.process = process;
		Thread.currentThread().setName("JdepP");
	}

	@Override
	public List<String> call() throws Exception {
		List<String> list = new ArrayList<>();
		OutputStream os = process.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		bw.write(sentence);
		bw.newLine();
		bw.flush();
		for (String str = br.readLine(); str != null; str = br.readLine()) {
			list.add(str);
			if ("EOS".equals(str)) {
				break;
			}
		}
		return list;
	}
}
