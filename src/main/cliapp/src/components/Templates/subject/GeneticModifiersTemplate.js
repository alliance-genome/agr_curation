import { EllipsisTableCell } from "../../EllipsisTableCell";
import { ListTableCell } from "../../ListTableCell";
import { getSubjectText } from "../../../utils/utils";
import { Tooltip } from "primereact/tooltip";

export const GeneticModifiersTemplate = ({ diseaseGeneticModifiers }) => {
  if (!diseaseGeneticModifiers || diseaseGeneticModifiers.length !== 0) return null;
  const targetClass = `a${global.crypto.randomUUID()}`;
  const diseaseGeneticModifierStrings = diseaseGeneticModifiers.map((dgm) => {
    const text = getSubjectText(dgm);
    if(!text) return dgm.curie; 
    return `${text} (${dgm.curie})`;
  });

  const sortedDiseaseGeneticModifierStrings = diseaseGeneticModifierStrings.sort();

  const listTemplate = (dgmString) => {
    return (
      <EllipsisTableCell>
        <div dangerouslySetInnerHTML={{ __html: dgmString }} />
      </EllipsisTableCell>
    );
  };

  return (
    <>
      <div className={`-my-4 p-1 a${targetClass}`}>
        <ListTableCell template={listTemplate} listData={sortedDiseaseGeneticModifierStrings} />
      </div>
      <Tooltip target={`.a${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
        <ListTableCell template={listTemplate} listData={sortedDiseaseGeneticModifierStrings} />
      </Tooltip>
    </>
  );
};