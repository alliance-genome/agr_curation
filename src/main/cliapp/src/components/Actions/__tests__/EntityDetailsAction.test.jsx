import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { EntityDetailsAction } from '../EntityDetailsAction';

const renderAction = (props) =>
	render(
		<BrowserRouter>
			<EntityDetailsAction endpoint="variant" {...props} />
		</BrowserRouter>
	);

describe('<EntityDetailsAction />', () => {
	it('Renders nothing without an identifier', () => {
		const { container } = renderAction({ identifier: null });

		expect(container).toBeEmptyDOMElement();
	});

	it('Links to the entity detail page', () => {
		const { container } = renderAction({ identifier: 'WB:WBVar00000001' });

		expect(container.querySelector('a')).toHaveAttribute('href', '/variant/WB:WBVar00000001');
	});

	it('Builds a tooltip class a browser can select on', () => {
		// A variant's identifier is often an MD5 MOD internal id. A class cannot start with a
		// digit, and querySelectorAll throws on the invalid selector, which took down the whole
		// table.
		const { container } = renderAction({ identifier: '6fd7a5002a942bd128ae7a9b8917bb1d' });

		const linkClass = container.querySelector('a').className.trim().split(/\s+/)[0];

		expect(linkClass).toEqual('entity-details-6fd7a5002a942bd128ae7a9b8917bb1d');
		expect(() => document.querySelectorAll(`.${linkClass}`)).not.toThrow();
	});

	it('Builds a selectable tooltip class from an identifier carrying a colon', () => {
		const { container } = renderAction({ identifier: 'AGRKB:101000000284070' });

		const linkClass = container.querySelector('a').className.trim().split(/\s+/)[0];

		expect(linkClass).toEqual('entity-details-AGRKB-101000000284070');
		expect(() => document.querySelectorAll(`.${linkClass}`)).not.toThrow();
	});

	it('Disables the link while the row is being edited', () => {
		const { container } = renderAction({ identifier: 'WB:WBVar00000001', disabled: true });

		expect(container.querySelector('a').className).toContain('pointer-events-none');
	});
});
