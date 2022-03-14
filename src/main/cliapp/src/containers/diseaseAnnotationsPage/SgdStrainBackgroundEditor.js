import React, { useState, useRef } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { trimWhitespace } from '../../utils/utils';
import { SgdStrainBackgroundTooltip } from './SgdStrainBackgroundTooltip';


export const SgdStrainBackgroundEditor = ({ rowProps, searchService, autocompleteFields }) => {
  const [filteredStrains, setFilteredStrains] = useState([]);
  const [fieldValue, setFieldValue] = useState(rowProps.rowData.sgdStrainBackground.curie);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

  const searchSgdStrainBackground = (event) => {
    //console.log(event);
    let strainFilter = {};
    autocompleteFields.forEach(field => {
      strainFilter[field] = {
        queryString: event.query,
        tokenOperator: "AND"
      }
    });

    searchService.search("agm", 15, 0, null, { "strainFilter": strainFilter })
      .then((data) => {
        //console.log(data);
        if (data.results && data.results.length > 0)
          setFilteredStrains(data.results);
        else
          setFilteredStrains([]);
      });
  };

  const onSgdStrainBackgroundEditorValueChange = (event) => {
    let updatedAnnotations = [...rowProps.props.value];

    if (event.target.value || event.target.value === '') {
      updatedAnnotations[rowProps.rowIndex].sgdStrainBackground = {};
      if (typeof event.target.value === "object") {
        updatedAnnotations[rowProps.rowIndex].sgdStrainBackground.curie = event.target.value.curie;
      } else {
        updatedAnnotations[rowProps.rowIndex].sgdStrainBackground.curie = event.target.value;
      }
      setFieldValue(updatedAnnotations[rowProps.rowIndex].sgdStrainBackground.curie);
    }
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };

  const sgdStrainBackgroundItemTemplate = (item) => {
    let inputValue = trimWhitespace(rowProps.rowData.sgdStrainBackground.curie.toLowerCase());
    if (autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length > 0) {
      for (let i in autocompleteSelectedItem.synonyms) {
        if (autocompleteSelectedItem.synonyms[i].name.toString().toLowerCase().indexOf(inputValue) < 0) {
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

    if (item.name) {
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
        id={rowProps.rowData.sgdStrainBackground.curie}
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
        field="curie"
        value={fieldValue}
        suggestions={filteredStrains}
        itemTemplate={sgdStrainBackgroundItemTemplate}
        completeMethod={searchSgdStrainBackground}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onSgdStrainBackgroundEditorValueChange(e)}
      />
      <SgdStrainBackgroundTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.sgdStrainBackground.curie.toLowerCase())}
      />
    </div>

  )
};
