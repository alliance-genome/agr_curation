import React from "react";

export const TaxonAdditionalFieldData = ({ curie }) => {
  if(curie){
    return <div className="p-info">{curie}</div>;
  } else {
    return null;
  } 
}