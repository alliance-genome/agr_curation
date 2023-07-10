import { NavLink } from 'react-router-dom/cjs/react-router-dom.min';
import { Tooltip } from 'primereact/tooltip';

export const EntityDetailsAction = ({ props, disabled }) => {
  if (props?.curie) {
    return (
      <>
        <NavLink to={`allele/${props.curie}`} target="_blank" isActive={() => !disabled} className={props.curie.replace(':', '')}>
          <i className="pi pi-info"></i>
        </NavLink>
        <Tooltip target={`.${props.curie.replace(':', '')}`} content= {"Open Details"} />
      </>
    );
  }
};