import { render, fireEvent } from '@testing-library/react';
import { TextDialogTemplate } from '../dialog/TextDialogTemplate';
import '../../../tools/jest/setupTests';

describe('TextDialogTemplate', () => {
	it('should render a Button component when entity, handleOpen, and text are truthy', () => {
		const entity = { displayText: 'text' };
		const handleOpen = jest.fn();

		const result = render(<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={entity?.displayText} />);

		const button = result.getByRole('button');
		expect(button).toBeInTheDocument();
	});
	it('should return null when entity is falsy', () => {
		const entity = null;
		const handleOpen = jest.fn();

		const { container } = render(
			<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={entity?.displayText} />
		);

		expect(container.firstChild).toBeNull();
	});

	it('should return null when handleOpen is falsy', () => {
		const entity = { displayText: 'text' };
		const handleOpen = null;

		const { container } = render(
			<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={entity?.displayText} />
		);

		expect(container.firstChild).toBeNull();
	});
	it('should return null when text is falsy', () => {
		const entity = { displayText: 'text' };
		const handleOpen = null;

		const { container } = render(<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={undefined} />);

		expect(container.firstChild).toBeNull();
	});

	it('should call handleOpen function with correct parameter when Button is clicked', () => {
		const entity = { displayText: 'text' };
		const handleOpen = jest.fn();

		const result = render(<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={entity?.displayText} />);
		fireEvent.click(result.getByRole('button'));

		expect(handleOpen).toHaveBeenCalledWith(entity);
	});
	it('should accept any entity type', () => {
		const entity1 = { displayText: 'text 1' };
		const entity2 = {
			subEntity: {
				name: 'text 2',
			},
		};
		const entity3 = {
			subEntity: {
				subSubEntity: {
					display: 'text 3',
				},
			},
		};
		const handleOpen = jest.fn();

		const result1 = render(<TextDialogTemplate entity={entity1} handleOpen={handleOpen} text={entity1.displayText} />);
		const result2 = render(
			<TextDialogTemplate entity={entity2} handleOpen={handleOpen} text={entity2?.subEntity?.name} />
		);
		const result3 = render(
			<TextDialogTemplate entity={entity3} handleOpen={handleOpen} text={entity3?.subEntity?.subSubEntity?.display} />
		);

		const element1 = result1.getByText('text 1');
		const element2 = result2.getByText('text 2');
		const element3 = result3.getByText('text 3');

		expect(element1).toBeInTheDocument();
		expect(element2).toBeInTheDocument();
		expect(element3).toBeInTheDocument();
	});

	it('should render <sup> tags in the HTML', async () => {
		const entity = {
			subEntity: {
				displayText: 'Text with <sup>superscript</sup>',
			},
		};
		const handleOpen = jest.fn();

		const { container } = render(
			<TextDialogTemplate entity={entity} handleOpen={handleOpen} text={entity?.subEntity?.displayText} />
		);

		let superScript = container.querySelector('sup');
		expect(superScript).toBeInTheDocument();
	});
});
