package org.alliancegenome.curation_api.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AGRCurationSchemaVersion {

	String min();

	String max();

	Class<?>[] dependencies() default {};

	boolean submitted() default false;

	boolean partial() default false;
}
