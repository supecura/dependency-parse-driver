package supecura.interactor;

import java.io.Closeable;
import java.io.IOException;

public abstract class JdepPInteractor extends ProcessInteractor implements Closeable {

	public JdepPInteractor() throws IOException {
		startProcess();
	}

	@Override
	void startProcess() throws IOException {
		startProcess("/bin/sh", "-c", "mecab | jdepp");
	}

}
