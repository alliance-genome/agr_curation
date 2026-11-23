# Editors

The editors are built in three layers. A **widget** is the control itself. A **field** binds a widget to the place it
appears: a detail page, a dialog or a table cell. A **per-surface editor** puts the two together for one column or
one field, so tables and pages stay short.

```
widgets/          the controls: value in, value out
fields/           FormField (pages and dialogs), TableField (table cells), errors, layouts
text/, dropdown/,
autocomplete/,    ready-made editors for a column or field, built from a widget and a field
dialog/
```

## Widgets (`widgets/`)

Plain controls that know nothing about tables, forms or validation. Each one that edits a value takes the same core
props:

| Prop              | Meaning                                     |
| ----------------- | ------------------------------------------- |
| `value`           | the current value                           |
| `onChange(value)` | called with the new **value**, not an event |
| `name`            | accessible name                             |
| `invalid`         | applies the invalid styling                 |
| `disabled`, `id`  | as usual                                    |

| Widget                                      | Edits                                                 |
| ------------------------------------------- | ----------------------------------------------------- |
| `TextInput`, `TextArea`                     | a string                                              |
| `StringListInput`                           | a list of strings                                     |
| `BooleanSelect`                             | `true` / `false` (and `null` with `showClear`)        |
| `VocabularySelect`, `VocabularyMultiSelect` | one or several terms from an `options` list           |
| `SingleAutocomplete`, `MultiAutocomplete`   | one or several entities found by a `search` function  |
| `DialogTriggerButton`                       | nothing itself; opens an edit dialog through `onOpen` |

`SingleAutocomplete` reports typed text that was not picked from the suggestions as `{ [subField]: text }`
(`subField` defaults to `curie`), so a form can tell a picked entity from typed text.

## Fields (`fields/`)

### `FormField`: detail pages and dialogs

```jsx
<FormField label="Symbol" name="alleleSymbol" value={value} onChange={onChange} errors={errorMessages} required>
	{(binding) => <TextInput {...binding} />}
</FormField>
```

- Lays the field out with `DetailPageFieldWrapper`: the label (a heading), the widget, the error under it, and an
  optional details column (`additionalDataField`).
- The render function gets a **binding**, `{ value, onChange, error, invalid, name }`, to spread onto the widget.
- `errors` is an object keyed by field. The field's message is looked up under `name`, or under `errorField` when
  its errors live under another key.
- Layout props: `labelColumnSize`, `widgetColumnSize`, `fieldDetailsColumnSize`, `labelClassName`,
  `showAdditionalData`, plus `required` and `isPending` (the "Pending Edits!" mark).
- `dialogFieldLayout.js` exports `DIALOG_FIELD_LAYOUT`, the layout for narrow New Entity dialogs: the label above the
  widget, full width, no details column. Spread it onto each field.

### `TableField`: table cells

```jsx
<TableField editorOptions={editorOptions} field="evidenceCodes">
	{(binding) => <MultiAutocomplete {...binding} search={evidenceCodesSearch} />}
</TableField>
```

- `editorOptions` is what PrimeReact passes a column's `editor`.
- Where the value is read from and written to is decided by a **strategy**, supplied once per table by
  `TableEditorProvider` (`TableEditorContext.jsx`), so the cells don't need to know:

  | Strategy (`fields/strategies/`) | Use for                                                                               | Rows addressed by |
  | ------------------------------- | ------------------------------------------------------------------------------------- | ----------------- |
  | `usePrimeRowEditStrategy`       | PrimeReact row-edit tables (the main data tables; `GenericDataTable` sets it up)      | row index         |
  | `useLocalRowsStrategy`          | a table whose rows are cloned into local state and handed back on save (edit dialogs) | `dataKey`         |
  | `useDataKeyRowsStrategy`        | a table whose rows live in a reducer or other state the caller owns                   | `dataKey`         |

- New Entity forms usually get a `useDataKeyRowsStrategy` through a wrapper hook such as
  `hooks/useSubResourceFormStrategy.js`, `affectedGenomicModelPage/useAGMFormStrategy.js` or
  `transgenicToolsPage/detail/useTransgenicToolFormStrategy.js`.
- The binding adds `row` and `rowKey` to the FormField binding. `read(row)` and `write(value, row)` convert between
  the stored row value and the widget's value when they differ.
- `errorField` looks the errors up under another key than `field` (the binding's `name` is then `errorField`);
  `errorLayout` and `strategy` override the strategy's error layout or the strategy itself.

### Errors

- Server `errorMessages` and client-side messages are both objects keyed by field name. Merge them (client messages
  last) and pass the result as `errors`.
- A message can be a string or `{ severity, message }`. `normalizeError` turns either into `{ severity, message }`,
  and `FieldErrors` shows it, as an overlay in table cells or as text under a form widget.

## Per-surface editors

Ready-made editors for a column or a detail-page field. Prefer one of these when it exists.

- `text/`: `InputTextTableEditor`, `InputTextAreaTableEditor`, `StringListTableEditor`, `StringListTextAreaTableEditor`
- `dropdown/`: true/false (`BooleanTableEditor`, `BooleanDetailPageEditor`), NOT (`NotEditor`), condition handles
  (`ConditionHandleTableEditor`) and controlled vocabularies (`ControlledVocabularyTableEditor`,
  `ControlledVocabularyMultiSelectTableEditor`, `ControlledVocabularyDetailPageEditor`)
- `autocomplete/<entity>/`: a `…TableEditor` per column that searches that kind of entity, and some
  `…DetailPageEditor`s; the generic ones are `base/AutocompleteSingleTableEditor` and
  `base/AutocompleteMultiTableEditor`
- `dialog/DialogTriggerTableEditor`: a cell that opens an edit dialog and shows the errors for the field the dialog
  owns

## Searches (`autocomplete/<entity>/utils.jsx` or `utils.js`)

Each folder's `utils` file holds its **search configs** and, where the folder needs one, the searches built from
them:

```js
export const diseaseSearchConfig = {
	endpoint: Endpoints.Ontology.DO,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'diseaseFilter',
	otherFilters: { obsoleteFilter: { obsolete: { queryString: false } } },
};
export const diseaseSearch = buildSearchFn(diseaseSearchConfig);
```

- `buildSearchFn` (`autocomplete/base/buildSearchFn.js`) turns a config into a search for `SingleAutocomplete` /
  `MultiAutocomplete`: it passes the query to the input, sends `filterName` over `autocompleteFields`, adds
  `otherFilters`, and hands back the results.
- Every search goes through `autocompleteSearch` (`utils/utils.js`), which always excludes obsolete entries (except
  for literature references) and overrides any `obsoleteFilter` in `otherFilters`. The one in the example above is
  therefore redundant.
- `otherFilters` can be a function, worked out on each search, for filters that aren't ready until then, such as
  the curator's species (`() => buildCuratorSpeciesFilter()`).
- When the filter depends on something on screen, build the search where that value is known, spreading the config:
  `NewVocabularyTermSetForm` builds its term search with
  `otherFilters: () => buildMemberTermsOtherFilters(vocabulary?.name)`, so it offers only the chosen vocabulary's
  terms.
- The same config can be spread onto `AutocompleteSingleTableEditor` / `AutocompleteMultiTableEditor`, so a table
  column and a form field search the same way.
- A config may also carry `valueDisplay`, the suggestion template.

## Recipes

**Add a field to a detail page or dialog**

1. Pick the widget, and an existing `…DetailPageEditor` if one fits.
2. Wrap it in `FormField` with the page's layout props (or `DIALOG_FIELD_LAYOUT` in a New Entity dialog), passing
   the page's merged errors.
3. For an autocomplete, use the search from the entity's `utils` file, or add one (below).

**Add a table column editor**

1. Use an existing `…TableEditor`, or write one: a `TableField` around a widget.
2. In the table's column definition: `editor: (editorOptions) => <MyTableEditor editorOptions={editorOptions} />`.
   The column's `field` must name the property the editor writes: the row-edit strategy stores the value through
   PrimeReact's `editorCallback`, which files it under the column's `field`. Put a dotted display path on the
   column's `columnKey` instead.
3. If the table isn't a `GenericDataTable`, wrap it in `TableEditorProvider` with the strategy that matches where
   its rows live.

**Add a search**

1. Add a config to the entity's `autocomplete/<entity>/utils` file, or create the folder.
2. Export `buildSearchFn(config)`, and a `…TableEditor` that spreads the config if a table needs it.
3. Add the column to `autocomplete/__tests__/tableSearchPayloads.test.jsx`, which pins the request each table
   autocomplete column sends.

## Testing

| Helper                                      | For                                                                                                                                                                                                                      |
| ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `widgets/__tests__/widgetTestUtils.js`      | a widget alone: `pickOption`, `typeInto`, `removeFirstToken`, `searchReturning`                                                                                                                                          |
| `__tests__/editorTestUtils.jsx`             | a table editor under the main tables' row-edit strategy: `makeEditorOptions` builds the editor options; `renderInTable` renders an editor element under that strategy, with optional `errorMessages` / `uiErrorMessages` |
| `src/tools/jest/newEntityFormTestUtils.jsx` | a form by its labels: `getField`, `typeInField`, `pickSuggestionInField`, `selectInField`, `clickSave`, `resolvedCreate`, `rejectedCreate`                                                                               |
