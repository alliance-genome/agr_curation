package org.alliancegenome.curation_api.interfaces;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface AGRCurationSchemaVersion {

	String value();

}
