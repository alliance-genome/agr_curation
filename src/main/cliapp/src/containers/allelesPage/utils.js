import { getRefString, getRefStrings } from "../../utils/utils";

export const generateCrossRefSearchFields = (references) => {
  let refString = getRefStrings(references);

  refString.forEach((string, index) => {
    //match and replace parenthesis 
    let regex = /\(|\)/g;
    const filteredString = string?.replace(regex, "");

    references[index].crossReferencesFilter = filteredString;
  });

};

export const generateCrossRefSearchField = (references) => {
  let refString = getRefString(references);

  //match and replace parenthesis 
  let regex = /\(|\)/g;
  const filteredString = refString?.replace(regex, "");

  return filteredString;


};