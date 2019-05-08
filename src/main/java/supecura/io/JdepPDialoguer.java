package supecura.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class JdepPDialoguer extends ProcessDialoguer implements Callable<List<String>> {

	private String standardInput;

	public JdepPDialoguer(Process process,String standardInput) {
		super(process);
		this.standardInput = standardInput;
	}

	public List<String> getDependencyResult() throws IOException {
		List<String> list = new ArrayList<>();
		InputStreamReader isr = new InputStreamReader(retrieveStandardOutput());
		BufferedReader br = new BufferedReader(isr);
		for (String str = br.readLine(); str != null; str = br.readLine()) {
			list.add(str);
			if ("EOS".equals(str)) {
				break;
			}
		}
		return list;
	}

	@Override
	public List<String> call() throws Exception {
		sendStandardInput(standardInput);
		return getDependencyResult();
	}
}
