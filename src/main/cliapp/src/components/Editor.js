import React, { useState, useRef } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { trimWhitespace } from '../utils/utils';
import { DiseaseTooltip } from './../containers/diseaseAnnotationsPage/DiseaseTooltip';

export const Editor = (
  {
    rowProps,
    searchService,
    autocompleteFields,
    isObsolete,
    endpoint,
    filterName,
    fieldName,
  }
) => {
  const [filtered, setFiltered] = useState([]);
  const [fieldValue, setFieldValue] = useState(rowProps.rowData[fieldName].curie);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

  const search = (event) => {
    let filter = {};
    autocompleteFields.forEach(field => {
      filter[field] = {
        queryString: event.query
      }
    });

    let obsoleteFilter;

    if (isObsolete) {
      obsoleteFilter = {
        "obsolete": {
          queryString: false
        }
      };
    };


    searchService.search(endpoint, 15, 0, [], { filterName: filter, ...(isObsolete && { "obsoleteFilter": obsoleteFilter }) })
      .then((data) => {
        if (data.results && data.results.length > 0)
          setFiltered(data.results);
        else
          setFiltered([]);
      });
  };

  const onValueChange = (event) => {
    let updatedAnnotations = [...rowProps.props.value];


    if (event.target.value || event.target.value === '') {
      console.log(rowProps);
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
    let inputValue = trimWhitespace(rowProps.rowData[fieldName].curie.toLowerCase());
    if (autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length > 0) {
      for (let i in autocompleteSelectedItem.synonyms) {
        if (autocompleteSelectedItem.synonyms[i].toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.synonyms[i];
        }
      }
    }
    if (autocompleteSelectedItem.crossReferences && autocompleteSelectedItem.crossReferences.length > 0) {
      for (let i in autocompleteSelectedItem.crossReferences) {
        if (autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.crossReferences[i];
        }
      }
    }

    if (autocompleteSelectedItem.secondaryIdentifiers && autocompleteSelectedItem.secondaryIdentifiers.length > 0) {
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
        id={rowProps.rowData[fieldName].curie}
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
        field="curie"
        value={fieldValue}
        suggestions={filtered}
        itemTemplate={itemTemplate}
        completeMethod={search}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onValueChange(e)}
      />
      <DiseaseTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.object.curie.toLowerCase())}
      />
    </div>
  )
}
