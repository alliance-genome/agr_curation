package org.alliancegenome.curation_api.main;

import org.alliancegenome.exceptional.client.ExceptionCatcher;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
	public static void main(String[] args) {
		System.out.println("Running main method of quarkus");
		ExceptionCatcher.initialize();
		Quarkus.run(args);
	}
}
