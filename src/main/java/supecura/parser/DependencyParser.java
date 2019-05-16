package supecura.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;
import supecura.interactor.JdepPInteractor;

@Slf4j
public class DependencyParser extends JdepPInteractor {

	public DependencyParser() throws IOException {
		super();
	}

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setName("main");
		List<Future<List<String>>> list = new ArrayList<>();
		int ok = 0;
		int ng = 0;
		if (args.length == 0) {
			log.warn("解析対象のファイルパスを引数に入れてください。");
			System.exit(-1);
		}

		try (DependencyParser parser = new DependencyParser();
				BufferedReader br = Files.newBufferedReader(Paths.get(args[0]));) {
			for (String str = br.readLine(); str != null; str = br.readLine()) {
				list.add(parser.exec(str));
			}
			for (Future<List<String>> l : list) {
				try {
					List<String> result = l.get();
					ok++;
					log.info("解析完了:{}", result);
				} catch (Exception e) {
					ng++;
					log.info("要再実行", e);
				}
			}
		} catch (Exception e) {
			log.error("異常終了", e);
		}
		log.info("OK:" + ok);
		log.info("NG:" + ng);
	}
}
