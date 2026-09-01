import React from 'react';
import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { AlleleForm, ALLELE_TOGGLEABLE_FIELDS } from '../AlleleForm';
import { useAlleleReducer } from '../useAlleleReducer';

const alwaysVisible = () => true;

// AlleleForm reads reducer state, so drive it through the real reducer rather than a literal.
const AlleleFormHarness = ({ mode }) => {
	const { alleleState, alleleDispatch } = useAlleleReducer();
	return <AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={alwaysVisible} mode={mode} />;
};

const heading = (name) => screen.queryByRole('heading', { name });

describe('<AlleleForm />', () => {
	describe('detail mode', () => {
		it('Renders the fields the server assigns', async () => {
			await renderWithClient(<AlleleFormHarness mode="detail" />);

			expect(heading('Curie')).toBeInTheDocument();
			expect(heading('Updated By')).toBeInTheDocument();
			expect(heading('Date Updated')).toBeInTheDocument();
			expect(heading('Created By')).toBeInTheDocument();
			expect(heading('Date Created')).toBeInTheDocument();
		});

		it('Renders the identifiers read only', async () => {
			await renderWithClient(<AlleleFormHarness mode="detail" />);

			expect(heading('Primary External ID')).toBeInTheDocument();
			expect(screen.queryByLabelText('primaryExternalId')).not.toBeInTheDocument();
			expect(screen.queryByLabelText('modInternalId')).not.toBeInTheDocument();
		});

		it('Defaults to detail mode when no mode is given', async () => {
			await renderWithClient(<AlleleFormHarnessWithoutMode />);

			expect(heading('Curie')).toBeInTheDocument();
			expect(screen.queryByLabelText('primaryExternalId')).not.toBeInTheDocument();
		});
	});

	describe('create mode', () => {
		it('Drops the fields the server assigns', async () => {
			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(heading('Curie')).not.toBeInTheDocument();
			expect(heading('Updated By')).not.toBeInTheDocument();
			expect(heading('Date Updated')).not.toBeInTheDocument();
			expect(heading('Created By')).not.toBeInTheDocument();
			expect(heading('Date Created')).not.toBeInTheDocument();
		});

		it('Renders Primary External ID as an input', async () => {
			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(heading('Primary External ID')).toBeInTheDocument();
			expect(screen.getByLabelText('primaryExternalId')).toBeInTheDocument();
		});

		it('Drops MOD Internal ID', async () => {
			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(heading('MOD Internal ID')).not.toBeInTheDocument();
			expect(screen.queryByLabelText('modInternalId')).not.toBeInTheDocument();
		});

		it('Keeps the curatable fields, Internal and Obsolete', async () => {
			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(heading('Taxon')).toBeInTheDocument();
			expect(heading('Internal')).toBeInTheDocument();
			expect(heading('Obsolete')).toBeInTheDocument();
			expect(heading('Synonyms')).toBeInTheDocument();
			expect(heading('Allele Gene Associations')).toBeInTheDocument();
		});

		it("Shows the data provider the curator's affiliation will assign", async () => {
			localStorage.setItem(
				'cognito-token-storage',
				JSON.stringify({ accessToken: { payload: { 'cognito:groups': ['WBStaff'] } } })
			);

			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(heading('Data Provider')).toBeInTheDocument();
			expect(screen.getAllByText('WB').length).toBeGreaterThan(0);
		});

		it("Falls back to 'Alliance' when the curator has no MOD group", async () => {
			localStorage.removeItem('cognito-token-storage');

			await renderWithClient(<AlleleFormHarness mode="create" />);

			expect(screen.getAllByText('Alliance').length).toBeGreaterThan(0);
		});
	});

	it('Hides a section isVisible rejects, in either mode', async () => {
		const hideSynonyms = (field) => field !== 'Synonyms';
		const { unmount } = await renderWithClient(
			<AlleleFormHarnessWithVisibility mode="detail" isVisible={hideSynonyms} />
		);

		expect(heading('Synonyms')).not.toBeInTheDocument();
		expect(heading('Symbol')).toBeInTheDocument();
		unmount();

		await renderWithClient(<AlleleFormHarnessWithVisibility mode="create" isVisible={hideSynonyms} />);

		expect(heading('Synonyms')).not.toBeInTheDocument();
		expect(heading('Symbol')).toBeInTheDocument();
	});

	it('Lists every toggleable field it can render', () => {
		// Guards against a section being added to the form without being made hideable.
		expect(ALLELE_TOGGLEABLE_FIELDS).toContain('Allele Gene Associations');
		expect(ALLELE_TOGGLEABLE_FIELDS).not.toContain('Curie');
		expect(ALLELE_TOGGLEABLE_FIELDS).not.toContain('Taxon');
	});
});

const AlleleFormHarnessWithoutMode = () => {
	const { alleleState, alleleDispatch } = useAlleleReducer();
	return <AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={alwaysVisible} />;
};

const AlleleFormHarnessWithVisibility = ({ mode, isVisible }) => {
	const { alleleState, alleleDispatch } = useAlleleReducer();
	return <AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} mode={mode} />;
};
