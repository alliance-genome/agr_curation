import { ListTableCell } from "../../ListTableCell";
import { getGenomicEntityText } from "../../../utils/utils";
import { Tooltip } from "primereact/tooltip";

export const GenomicEntityListTemplate = ({ genomicEntities }) => {

  if (!genomicEntities || genomicEntities.length === 0) return null;

  const targetClass = `a${global.crypto.randomUUID()}`;

  const genomicEntityStrings = genomicEntities.map((genomicEntity) => {
    const text = getGenomicEntityText(genomicEntity);
    if(!text) return genomicEntity.curie; 
    if(!genomicEntity.curie) return text;
    return `${text} (${genomicEntity.curie})`;
  });

  const sortedGenomicEntityStrings = genomicEntityStrings.sort();

  const listTemplate = (genomicEntityString) => {
    return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: genomicEntityString }} />;
  };

  return (
    <>
      <div className={`-my-4 p-1 a${targetClass}`}>
        <ListTableCell template={listTemplate} listData={sortedGenomicEntityStrings} />
      </div>
      <Tooltip target={`.a${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
        <ListTableCell template={listTemplate} listData={sortedGenomicEntityStrings} />
      </Tooltip>
    </>
  );
};