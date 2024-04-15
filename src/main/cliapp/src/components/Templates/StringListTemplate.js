import { EllipsisTableCell } from "../EllipsisTableCell";
import { ListTableCell } from "../ListTableCell";
import { Tooltip } from "primereact/tooltip";

export const StringListTemplate = ({ list }) => {
  if (!list || list.length === 0) return null;

  const targetClass = `a${global.crypto.randomUUID()}`;

  const sortedList = list.sort();

  const listTemplate = (item) => {
    return (
      <EllipsisTableCell>
        {item}
      </EllipsisTableCell>
    );
  };

  return (
    <>
      <div className={`-my-4 p-1 ${targetClass}`}>
        <ListTableCell template={listTemplate} listData={sortedList} />
      </div>
      <Tooltip target={`.${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
        <ListTableCell template={listTemplate} listData={sortedList} />
      </Tooltip>
    </>
  );
};