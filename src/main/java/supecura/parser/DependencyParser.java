package supecura.parser;

import java.io.IOException;
import java.util.List;

import supecura.function.JdepPExecutor;

public class DependencyParser extends JdepPExecutor {

	public DependencyParser() throws IOException {
		super();
	}

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setName("main");
		try (DependencyParser parser = new DependencyParser()) {
			parser.parse("this is a pen").forEach(System.out::println);
		}
	}

	@Override
	public List<String> parse(String sentence) throws Exception {
		return exec(sentence).get();
	}

}
