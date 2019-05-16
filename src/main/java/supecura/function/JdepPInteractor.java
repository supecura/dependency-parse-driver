package supecura.function;

import java.io.Closeable;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JdepPInteractor extends ProcessInteractor implements Closeable {

	public JdepPInteractor() throws IOException {
		startProcess();
	}

	@Override
	void startProcess() throws IOException {
		startProcess("/bin/sh", "-c", "mecab | jdepp");
	}

}
