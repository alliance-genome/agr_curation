package org.alliancegenome.curation_api.base;

import java.util.HashMap;

import javax.ws.rs.*;

import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

public interface BaseSearchInterface<E extends BaseEntity> {


}
