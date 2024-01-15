import { getRefString } from "../../utils/utils";
import { Tooltip } from "primereact/tooltip";

export const SingleReferenceBodyTemplate = ({ rowData }) => {
  if (rowData?.singleReference) {
    let refString = getRefString(rowData.singleReference);
    return (
      <>
        <div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}
          dangerouslySetInnerHTML={{
            __html: refString
          }}
        />
        <Tooltip target={`.a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}>
          <div dangerouslySetInnerHTML={{
            __html: refString
          }}
          />
        </Tooltip>
      </>
    );

  } else {
    return null;
  }
};