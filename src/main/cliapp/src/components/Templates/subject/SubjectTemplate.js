import { Tooltip } from "primereact/tooltip";
import { getSubjectText } from "../../../utils/utils";

export const SubjectTemplate = ({ subject }) => {
  if (!subject) return null;

  const targetClass = `a${global.crypto.randomUUID()}`;
  const subjectText = getSubjectText(subject);

  if(!subjectText) return <div className='overflow-hidden text-overflow-ellipsis' >{subject.curie}</div>;

  return (
    <>
      <div className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
        dangerouslySetInnerHTML={{
          __html: `${subjectText} (${subject.curie})`
        }}
      />
      <Tooltip target={`.${targetClass}`}>
        <div dangerouslySetInnerHTML={{
          __html: `${subjectText} (${subject.curie})`
        }}
        />
      </Tooltip>
    </>
  );
};

