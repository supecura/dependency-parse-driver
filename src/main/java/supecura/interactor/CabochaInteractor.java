package supecura.interactor;

import java.io.Closeable;
import java.io.IOException;

public abstract class CabochaInteractor extends ProcessInteractor implements Closeable {

	public CabochaInteractor() throws IOException {
		startProcess();
	}

	@Override
	void startProcess() throws IOException {
		startProcess("/bin/sh", "-c", "cabocha");
	}

}
