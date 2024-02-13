import { ListTableCell } from "../ListTableCell";

export const DiseaseQualifiersTemplate = ({ diseaseQualifiers }) => {
  if (!diseaseQualifiers) return null;

  const sortedDiseaseQualifiers = diseaseQualifiers.sort((a, b) => (a.name > b.name) ? 1 : -1);

  const listTemplate = (item) => item.name;

  return (
    <div className="-my-4 p-1">
      <ListTableCell template={listTemplate} listData={sortedDiseaseQualifiers} />
    </div>
  );
};