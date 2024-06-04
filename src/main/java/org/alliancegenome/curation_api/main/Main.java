package org.alliancegenome.curation_api.main;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
	private Main() {
		// Hidden from view, as it is a utility class
	}
	public static void main(String[] args) {
		System.out.println("Running main method of quarkus");
		Quarkus.run(args);
	}

}
