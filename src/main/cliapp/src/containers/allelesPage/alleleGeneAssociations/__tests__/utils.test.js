import {
	buildDuplicateGeneAssociations,
	haveSameGeneAssociations,
	markRowsMissingGene,
	prepareGeneAssociations,
	stripGeneAssociation,
} from '../utils';
import { processOptionalField } from '../../../../utils/utils';

const gene = { type: 'Gene', id: 9, primaryExternalId: 'WB:WBGene1' };
const otherGene = { type: 'Gene', id: 10, primaryExternalId: 'WB:WBGene2' };
const reference = { type: 'Reference', id: 5, curie: 'AGRKB:101' };
const otherReference = { type: 'Reference', id: 6, curie: 'AGRKB:102' };

const storedAssociation = () => ({
	id: 16,
	dateCreated: '2022-03-09T22:10:12Z',
	relation: { name: 'is_allele_of' },
	alleleGeneAssociationObject: gene,
	evidenceCode: { curie: 'ECO:0000315' },
	evidence: [reference],
	relatedNote: { id: 19, freeText: 'a note', noteType: { name: 'comment' }, internal: false, references: [reference] },
});

describe('prepareGeneAssociations', () => {
	it('Gives each row its own key and the evidence curies its filter matches on', () => {
		const [row] = prepareGeneAssociations([{ ...storedAssociation(), evidence: [reference, otherReference] }]);

		expect(row.dataKey).toEqual(expect.any(String));
		expect(row.evidenceCurieSearchFilter).toEqual('AGRKB:101,AGRKB:102');
	});

	it('Leaves the rows it was given unchanged', () => {
		const associations = [storedAssociation()];

		prepareGeneAssociations(associations);

		expect(associations[0]).not.toHaveProperty('dataKey');
	});

	it('Reads a missing list as empty', () => {
		expect(prepareGeneAssociations(undefined)).toEqual([]);
	});
});

describe('stripGeneAssociation', () => {
	it('Drops the fields the table keeps for itself and the subject, keeping the id', () => {
		const [row] = prepareGeneAssociations([{ ...storedAssociation(), alleleAssociationSubject: { id: 1 } }]);

		const stripped = stripGeneAssociation(row);

		expect(stripped).not.toHaveProperty('dataKey');
		expect(stripped).not.toHaveProperty('evidenceCurieSearchFilter');
		expect(stripped).not.toHaveProperty('alleleAssociationSubject');
		expect(stripped.id).toEqual(16);
		expect(stripped.alleleGeneAssociationObject).toEqual(gene);
	});
});

describe('haveSameGeneAssociations', () => {
	const saved = () => prepareGeneAssociations([storedAssociation()]);

	it('Matches rows with the same values, whatever their table keys', () => {
		expect(haveSameGeneAssociations(saved(), saved())).toBe(true);
	});

	it.each([
		['relation', { relation: { name: 'duplication' } }],
		['gene', { alleleGeneAssociationObject: otherGene }],
		['gene typed but not picked', { alleleGeneAssociationObject: { primaryExternalId: 'WB:WBGene1' } }],
		['evidence code', { evidenceCode: { curie: 'ECO:0000316' } }],
		['evidence', { evidence: [reference, otherReference] }],
		['note text', { relatedNote: { ...storedAssociation().relatedNote, freeText: 'changed' } }],
		['note removed', { relatedNote: null }],
	])('Tells an edited %s apart', (_, changes) => {
		const [row] = saved();

		expect(haveSameGeneAssociations([{ ...row, ...changes }], saved())).toBe(false);
	});

	it('Matches a note the related note dialog returns with its own table key', () => {
		const [row] = saved();

		expect(haveSameGeneAssociations([{ ...row, relatedNote: { ...row.relatedNote, dataKey: 0 } }], saved())).toBe(true);
	});

	it('Matches an evidence code typed as the stored curie', () => {
		const [row] = saved();

		expect(haveSameGeneAssociations([{ ...row, evidenceCode: processOptionalField('ECO:0000315') }], saved())).toBe(
			true
		);
	});

	it('Matches evidence in any order', () => {
		const [row] = prepareGeneAssociations([{ ...storedAssociation(), evidence: [reference, otherReference] }]);

		expect(haveSameGeneAssociations([{ ...row, evidence: [otherReference, reference] }], [row])).toBe(true);
	});

	it('Tells an added row apart, and matches again once it is removed', () => {
		const rows = saved();
		const added = [{ dataKey: 'new' }, ...rows];

		expect(haveSameGeneAssociations(added, rows)).toBe(false);
		expect(haveSameGeneAssociations(added.slice(1), rows)).toBe(true);
	});
});

describe('buildDuplicateGeneAssociations', () => {
	it('Copies each association and its note without ids, audit fields or the stored allele as subject', () => {
		const [copy] = buildDuplicateGeneAssociations([{ ...storedAssociation(), alleleAssociationSubject: { id: 1 } }]);

		expect(copy).not.toHaveProperty('id');
		expect(copy).not.toHaveProperty('dateCreated');
		expect(copy).not.toHaveProperty('alleleAssociationSubject');
		expect(copy.relatedNote).not.toHaveProperty('id');
		expect(copy.relatedNote.freeText).toEqual('a note');
		expect(copy.alleleGeneAssociationObject).toEqual(gene);
		expect(copy.evidence).toEqual([reference]);
		expect(copy.dataKey).toEqual(expect.any(String));
	});

	it('Reads a missing list as empty', () => {
		expect(buildDuplicateGeneAssociations(undefined)).toEqual([]);
	});
});

describe('markRowsMissingGene', () => {
	it('Marks rows whose gene was typed rather than picked, or not given, keeping their other errors', () => {
		const rows = [
			{ dataKey: 'picked', alleleGeneAssociationObject: gene },
			{ dataKey: 'typed', alleleGeneAssociationObject: { primaryExternalId: 'WB:WBGene1' } },
			{ dataKey: 'blank' },
		];
		const existing = { blank: { relation: { severity: 'error', message: 'Required' } } };

		const marked = markRowsMissingGene(rows, existing);

		expect(marked).not.toHaveProperty('picked');
		expect(marked.typed.alleleGeneAssociationObject.severity).toEqual('error');
		expect(marked.blank.alleleGeneAssociationObject.severity).toEqual('error');
		expect(marked.blank.relation.message).toEqual('Required');
		expect(existing.blank).not.toHaveProperty('alleleGeneAssociationObject');
	});

	it('Marks nothing when every row has a gene', () => {
		expect(markRowsMissingGene([{ dataKey: 'picked', alleleGeneAssociationObject: gene }], {})).toBeNull();
	});
});
