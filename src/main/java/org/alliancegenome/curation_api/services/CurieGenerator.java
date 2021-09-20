package org.alliancegenome.curation_api.services;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class CurieGenerator extends ArrayList<String> {

    public String getCurie() {
        return StringUtils.join(this, "|");
    }

    @Override
    public boolean add(String s) {
        if (StringUtils.isNotEmpty(s)) {
            return super.add(s);
        }
        return false;
    }

}
