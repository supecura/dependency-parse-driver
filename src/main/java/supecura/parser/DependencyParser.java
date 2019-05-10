package supecura.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import supecura.function.JdepPDialoguer;

public class DependencyParser extends JdepPDialoguer {

	public DependencyParser() throws IOException {
		super();
	}

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setName("main");
		List<Future<List<String>>> list = new ArrayList<>();
		try (DependencyParser parser = new DependencyParser();
				BufferedReader br = Files.newBufferedReader(Paths.get("./testMessage"));) {
			for (String str = br.readLine(); str != null; str = br.readLine()) {
				list.add(parser.exec(str));
			}
			for (Future<List<String>> l : list) {
				try {
					l.get();
					System.out.println("OK");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Restarting");
					TimeUnit.SECONDS.sleep(10);
				}
			}
		}
	}
}
