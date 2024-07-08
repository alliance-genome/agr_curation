import { render } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom.min';
import { DetailMessage } from '../DetailMessage';
describe('DetailMessage', () => {
	it('should return null when display is false', () => {
		const { container } = render(<DetailMessage display={false} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when identifier is undefined', () => {
		const { container } = render(
			<BrowserRouter>
				<DetailMessage identifier={undefined} text="Test Message" display={true} />
			</BrowserRouter>
		);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when text is undefined', () => {
		const identifier = '123';
		const display = true;

		const { container } = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} display={display} />
			</BrowserRouter>
		);

		expect(container.firstChild).toBeNull();
	});

	it('should render a Message component when display is true', () => {
		const identifier = '123';
		const text = 'Example Text';
		const display = true;

		const result = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} text={text} display={display} />
			</BrowserRouter>
		);

		const messageComponent = result.getByText(text);
		expect(messageComponent).toBeInTheDocument();
	});
});
