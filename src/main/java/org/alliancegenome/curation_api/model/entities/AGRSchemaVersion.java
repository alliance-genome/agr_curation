package org.alliancegenome.curation_api.model.entities;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface AGRSchemaVersion {

	String value();

}
