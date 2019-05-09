package supecura.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import supecura.function.JdepPDialoguer;


public class DependencyParser extends JdepPDialoguer {

	public DependencyParser() throws IOException {
		super();
	}

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setName("main");
		List<Future<List<String>>> list = new ArrayList<>();
		try (DependencyParser parser = new DependencyParser()) {
			for (String line : Files.newBufferedReader(Paths.get("./testMessage")).lines()
					.collect(Collectors.toList())) {
				list.add(parser.exec(line));
			}
			for (Future<List<String>> l : list) {
				l.get().forEach(System.out::println);
				System.out.println("---------------------------------------------------------------------------------------------------------");
			}
		}
	}
}
