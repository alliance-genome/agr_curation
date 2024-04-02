import { ListTableCell } from "../../ListTableCell";
import { DetailMessage } from "../../DetailMessage";
import { SingleReferenceTemplate } from "./SingleReferenceTemplate";

export const TruncatedReferencesTemplate = ({ references, identifier, detailPage }) => {
  if (!references || references.length === 0 || !detailPage) return null;
  let truncatedReferences = references;
  let displayDetailMessage = false;

  const targetClass = `a${global.crypto.randomUUID()}`;

  if (truncatedReferences.length > 5) {
    truncatedReferences = references.slice(0, 5);
    displayDetailMessage = true;
  }

  const listTemplate = (singleReference) => <SingleReferenceTemplate singleReference={singleReference} />;

  return (
    <>
      <div className={`-my-4 p-1 ${targetClass}`}>
        <ListTableCell template={listTemplate} listData={truncatedReferences} />
      </div>
      <div className="mt-1">
        <DetailMessage identifier={`${identifier}`} display={displayDetailMessage} text={`View all references on ${detailPage} Detail Page`} />
      </div>
    </>
  );

};