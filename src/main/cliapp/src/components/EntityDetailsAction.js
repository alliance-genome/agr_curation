import { Link } from 'react-router-dom/cjs/react-router-dom.min';
import { Tooltip } from 'primereact/tooltip';

export const EntityDetailsAction = ({ curie, disabled }) =>{
  const disabledClasses = disabled ? "pointer-events-none opacity-50" : "";

  if (!curie) return null;

  return (
    <>
      <Link to={`allele/${curie}`} target="_blank" className={`${curie.replace(':', '')} ${disabledClasses}`}>
        <i className="pi pi-info"></i>
      </Link>
      <Tooltip target={`.${curie.replace(':', '')}`} content= {"Open Details"} />
    </>
  );
};