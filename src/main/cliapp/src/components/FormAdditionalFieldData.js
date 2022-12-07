import React from 'react';


export function FormAdditionalFieldData({ field, fieldData }){
	if(fieldData){
		if(fieldData["curie"]!== '' && fieldData["symbol"])
			return <div className="p-info">{fieldData["symbol"]}</div>;
		else if(fieldData["curie"]!== '' && fieldData["name"])
			return <div className="p-info">{fieldData["name"]}</div>;
		else
			return null;
	} else {
		return null;
	}
}
