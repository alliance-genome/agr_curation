import { EllipsisTableCell } from '../../components/EllipsisTableCell';

export const CrossReferencesTemplate = ({ rowData }) => {
  if (!rowData) return null;

  const { crossReferences, curieField } = differentiateCrossReferences(rowData);

  const sortedCrossReferences = crossReferences?.sort((a, b) => (a[curieField] > b[curieField]) ? 1 : -1);

  return (
    <div>
      <ul type={{ listType: 'none' }}>
        {sortedCrossReferences?.map((reference, index) =>
          <li key={index}>
            <EllipsisTableCell>
              {reference[curieField]}
            </EllipsisTableCell>
          </li>
        )}
      </ul>
    </div>
  );
};

const differentiateCrossReferences = (reference) => {
  let crossReferences;
  let curieField;

  if (reference.cross_references) {
    crossReferences = global.structuredClone(reference.cross_references);
    curieField = "curie";
  } else if (reference.crossReferences) {
    crossReferences = global.structuredClone(reference.crossReferences);
    curieField = "referencedCurie";
  } else {
    return {};
  }
  
  return {crossReferences, curieField};
};