package org.alliancegenome.curation_api.services;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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
