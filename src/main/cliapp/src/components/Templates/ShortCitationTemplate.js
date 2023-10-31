export const ShortCitationTemplate = ({ rowData }) => {
  if (!rowData) return null;

  const shortCitation = differentiateShortCitation(rowData);

  return (
    <div>
      {shortCitation}
    </div>
  );
};

const differentiateShortCitation = (reference) => {
  let shortCitation;
  if(!reference.short_citation && !reference.shortCitation) return

  if (reference.short_citation) {
    shortCitation = global.structuredClone(reference.short_citation);
  } else if (reference.shortCitation) {
    shortCitation = global.structuredClone(reference.shortCitation);
  } 
  
  return shortCitation;
};