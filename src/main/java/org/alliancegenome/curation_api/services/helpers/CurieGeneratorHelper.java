package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class CurieGeneratorHelper extends ArrayList<String> {

	public String getCurie() {
		return StringUtils.join(this, "|");
	}

	public String getSummary() {
		return StringUtils.join(this, ":");
	}

	@Override
	public boolean add(String s) {
		if (StringUtils.isNotBlank(s)) {
			return super.add(s);
		}
		return false;
	}

}
