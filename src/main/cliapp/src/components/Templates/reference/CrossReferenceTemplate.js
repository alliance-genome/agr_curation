import { EllipsisTableCell } from '../../EllipsisTableCell';
import { differentiateCrossReferences } from '../../../containers/allelesPage/utils';

export const CrossReferencesTemplate = ({ rowData }) => {
  if (!rowData) return null;

  const { crossReferences, curieField } = differentiateCrossReferences(rowData);

  const sortedCrossReferences = crossReferences?.sort((a, b) => (a[curieField] > b[curieField]) ? 1 : -1);

  return (
    <div>
      <ul>
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
