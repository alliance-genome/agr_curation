import { getShortCitation } from "../../../containers/allelesPage/utils";
export const ShortCitationTemplate = ({ reference }) => {
  if (!reference) return null;

  const shortCitation = getShortCitation(reference);

  return (
    <div>
      {shortCitation}
    </div>
  );
};

