package org.alliancegenome.curation_api.main;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
	
	private Main() {
		// Hidden from view, as it is a utility class
	}
	
	public static void main(String[] args) {
		Log.info("Running main method of quarkus");
		Quarkus.run(args);
	}

}
