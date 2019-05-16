package supecura.function;

import java.io.Closeable;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CabochaInteractor extends ProcessInteractor implements Closeable {

	public CabochaInteractor() throws IOException {
		startProcess();
	}

	@Override
	void startProcess() throws IOException {
		startProcess("/bin/sh", "-c", "cabocha");
	}

}
