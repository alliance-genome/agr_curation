package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class PersonDTO extends AgentDTO{
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("first_name")
	private String firstName;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("middle_name")
	private String middleName;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("last_name")
	private String lastName;
	
	@JsonView({View.FieldsOnly.class})
	private String orcid;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("mod_entity_id")
	private String modEntityId;
	
	@JsonView({View.FieldsAndLists.class})
	private List<String> emails;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("old_emails")
	private List<String> oldEmails;
}
