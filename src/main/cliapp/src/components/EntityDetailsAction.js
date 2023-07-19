import { Link } from 'react-router-dom/cjs/react-router-dom.min';
import { Tooltip } from 'primereact/tooltip';

export const EntityDetailsAction = ({ entity, disabled }) =>{
  const disabledClasses = disabled ? "pointer-events-none opacity-50" : "";
  if (entity?.curie) {
    return (
      <>
        <Link to={`allele/${entity.curie}`} target="_blank" className={`${entity.curie.replace(':', '')} ${disabledClasses}`}>
          <i className="pi pi-info"></i>
        </Link>
        <Tooltip target={`.${entity.curie.replace(':', '')}`} content= {"Open Details"} />
      </>
    );
  }
};