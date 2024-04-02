import { ListTableCell } from '../../ListTableCell';
import { getCrossReferences } from '../../../containers/allelesPage/utils';

export const CrossReferenceTemplate = ({ reference }) => {
  if (!reference) return null;

  const { crossReferences, curieField } = getCrossReferences(reference);

  const sortedCrossReferences = crossReferences?.sort((a, b) => (a[curieField] > b[curieField]) ? 1 : -1);

  const listTemplate = (item) => item[curieField];

  return (
    <div>
      <ListTableCell template={listTemplate} listData={sortedCrossReferences} />
    </div>
  );
};
