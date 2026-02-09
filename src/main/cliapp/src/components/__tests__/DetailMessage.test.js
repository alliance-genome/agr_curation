import { render } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { BrowserRouter } from 'react-router-dom';
import { DetailMessage } from '../DetailMessage';
describe('DetailMessage', () => {
	it('should return null when display is false', () => {
		const { container } = render(<DetailMessage display={false} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when identifier is undefined', () => {
		const identifier = undefined;
		const text = 'Test Message';
		const display = true;
		const detailPage = 'Construct';

		const { container } = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} text={text} display={display} detailPage={detailPage} />
			</BrowserRouter>
		);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when text is undefined', () => {
		const identifier = '123';
		const display = true;
		const detailPage = 'Construct';

		const { container } = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} display={display} detailPage={detailPage} />
			</BrowserRouter>
		);

		expect(container.firstChild).toBeNull();
	});

	it('should return null when detailPage is undefined', () => {
		const identifier = '123';
		const text = 'Example Text';
		const display = true;
		const detailPage = undefined;

		const { container } = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} display={display} detailPage={detailPage} />
			</BrowserRouter>
		);

		expect(container.firstChild).toBeNull();
	});

	it('should render a Message component when display is true', () => {
		const identifier = '123';
		const text = 'Example Text';
		const display = true;
		const detailPage = 'Construct';

		const result = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} text={text} display={display} detailPage={detailPage} />
			</BrowserRouter>
		);

		const messageComponent = result.getByText(text);
		expect(messageComponent).toBeInTheDocument();
	});

	it('detailPage should be in the component link', () => {
		const identifier = '123';
		const text = 'Example Text';
		const display = true;
		const detailPage = 'Construct';
		const expectedHref = `/${detailPage.toLowerCase()}/${identifier}`;

		const result = render(
			<BrowserRouter>
				<DetailMessage identifier={identifier} text={text} display={display} detailPage={detailPage} />
			</BrowserRouter>
		);

		const linkElement = result.getByRole('link', { name: text });
		expect(linkElement).toHaveAttribute('href', expectedHref);
	});
});
