import React, { useState, useRef, useEffect } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { trimWhitespace } from '../utils/utils';
import { Tooltip } from "primereact/tooltip";

export const InputEditor = (
  {
    rowProps,
    searchService,
    autocompleteFields,
    otherFilters = [],
    endpoint,
    filterName,
    fieldName,
    isSubject = false,
    isWith = false,
    isMultiple = false
  }
) => {
  const [filtered, setFiltered] = useState([]);
  const [query, setQuery] = useState();
  const [fieldValue, setFieldValue] = useState(() => {
    return isMultiple ?
      rowProps.rowData[fieldName] :
      rowProps.rowData[fieldName].curie
  }
  );

  useEffect(() => {
    console.log(fieldValue);
  }, [fieldValue]);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

  const search = (event) => {
    setQuery(event.query);
    let filter = {};
    autocompleteFields.forEach(field => {
      filter[field] = {
        queryString: event.query,
        ...((isSubject || isWith) && { tokenOperator: "AND" })
      }
    });

    searchService.search(endpoint, 15, 0, [], { [filterName]: filter, ...otherFilters })
      .then((data) => {
        if (data.results?.length > 0) {
          if (isWith) {
            setFiltered(data.results.filter((gene) => Boolean(gene.curie.startsWith("HGNC:"))));
          } else {
            setFiltered(data.results);
          };
        } else {
          setFiltered([]);
        }
      });
  };

  const onValueChange = (event) => {
    let updatedAnnotations = [...rowProps.props.value];

    if (isMultiple && event.target.value) {
      updatedAnnotations[rowProps.rowIndex][fieldName] = event.target.value;
      setFieldValue(updatedAnnotations[rowProps.rowIndex][fieldName]);
      return;
    }

    if (event.target.value || event.target.value === '') {
      updatedAnnotations[rowProps.rowIndex][fieldName] = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
      if (typeof event.target.value === "object") {
        updatedAnnotations[rowProps.rowIndex][fieldName].curie = event.target.value.curie;
      } else {
        updatedAnnotations[rowProps.rowIndex][fieldName].curie = event.target.value;
      }
      setFieldValue(updatedAnnotations[rowProps.rowIndex][fieldName].curie);
    }
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };

  const itemTemplate = (item) => {
    console.log(item);
    let inputValue = trimWhitespace(query.toLowerCase());
    if (autocompleteSelectedItem.synonyms?.length > 0) {
      for (let i in autocompleteSelectedItem.synonyms) {
        if (autocompleteSelectedItem.synonyms[i].toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.synonyms[i];
        }
      }
    }
    if (autocompleteSelectedItem.crossReferences?.length > 0) {
      for (let i in autocompleteSelectedItem.crossReferences) {
        if (autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.crossReferences[i];
        }
      }
    }

    if (autocompleteSelectedItem.secondaryIdentifiers?.length > 0) {
      for (let i in autocompleteSelectedItem.secondaryIdentifiers) {
        if (autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.secondaryIdentifiers[i];
        }
      }
    }

    if (item.symbol) {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{ __html: item.symbol + ' (' + item.curie + ') ' }} />
        </div>
      );
    } else if (item.name) {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{ __html: item.name + ' (' + item.curie + ') ' }} />
        </div>
      );
    } else {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)}>{item.curie}</div>
        </div>
      );
    }
  };

  return (
    <div>
      <AutoComplete
        multiple={isMultiple}
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
        field="curie"
        value={fieldValue}
        suggestions={filtered}
        itemTemplate={itemTemplate}
        completeMethod={search}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onValueChange(e)}
      />
      <EditorTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} />
    </div>
  )
}

const EditorTooltip = ({ op, autocompleteSelectedItem }) => {
  return (
    <>
      <Tooltip ref={op} style={{ width: '450px', maxWidth: '450px' }} position={'right'} mouseTrack mouseTrackLeft={30}>
        Curie: {autocompleteSelectedItem.curie}<br />
        {autocompleteSelectedItem.name &&
          <div key={`name${autocompleteSelectedItem.name}`} dangerouslySetInnerHTML={{ __html: 'Name: ' + autocompleteSelectedItem.name }} />
        }
        {autocompleteSelectedItem.symbol &&
          <div key={`symbol${autocompleteSelectedItem.symbol}`} dangerouslySetInnerHTML={{ __html: 'Symbol: ' + autocompleteSelectedItem.symbol }} />
        }
        {autocompleteSelectedItem.synonyms &&
          autocompleteSelectedItem.synonyms.map((syn) => <div key={`synonyms${syn}`}>Synonym: {syn}</div>)
        }
        {autocompleteSelectedItem.crossReferences &&
          autocompleteSelectedItem.crossReferences.map((cr) => <div key={`crossReferences${cr.curie}`}>Cross Reference: {cr.curie}</div>)
        }
        {autocompleteSelectedItem.secondaryIdentifiers &&
          autocompleteSelectedItem.secondaryIdentifiers.map((si) => <div key={`secondaryIdentifiers${si}`}>Secondary Identifiers: {si}</div>)
        }
      </Tooltip>
    </>
  )
};



