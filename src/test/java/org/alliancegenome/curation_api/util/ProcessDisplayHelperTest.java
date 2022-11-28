package org.alliancegenome.curation_api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessDisplayHelperTest {

	ProcessDisplayHelper ph;
	long total = 1000;

	@BeforeEach
	void setUp() {
		ph = new ProcessDisplayHelper(100);
	}

	@Test
	void initialTestNoMessage() {
		ph.startProcess("initialTestNoMessage", total);
		for(int i = 0; i < total; i++) {
			try { Thread.sleep(15); } catch (Exception e) { }
			ph.progressProcess();
		}
		ph.finishProcess();
	}

	@Test
	void initialTestMessage() {
		ph.startProcess("initialTestMessage", total);
		for(int i = 0; i < total; i++) {
			try { Thread.sleep(15); } catch (Exception e) { }
			ph.progressProcess("I: " + i);
		}
		ph.finishProcess("End Message");
	}
}