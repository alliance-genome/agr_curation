import { Link } from 'react-router-dom/cjs/react-router-dom.min';
import { Tooltip } from 'primereact/tooltip';

export const EntityDetailsAction = ({ identifier, disabled }) => {
	const disabledClasses = disabled ? 'pointer-events-none opacity-50' : '';

	if (!identifier) return null;

	return (
		<>
			<Link to={`allele/${identifier}`} target="_blank" className={`${identifier.replace(':', '')} ${disabledClasses}`}>
				<i className="pi pi-info-circle"></i>
			</Link>
			<Tooltip target={`.${identifier.replace(':', '')}`} content={'Open Details'} />
		</>
	);
};
