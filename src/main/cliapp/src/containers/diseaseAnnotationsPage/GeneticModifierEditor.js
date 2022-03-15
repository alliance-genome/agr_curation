import React, { useState, useRef } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { trimWhitespace } from '../../utils/utils';
import { SubjectTooltip } from './SubjectTooltip';


export const GeneicModifierEditor = ({ rowProps, searchService, autocompleteFields }) => {
  const [filteredGeneticModifiers, setFilteredGeneticModifiers] = useState([]);
  const [fieldValue, setFieldValue] = useState(rowProps.rowData.diseaseGeneticModifier.curie);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

  const searchGeneticModifier = (event) => {
    //console.log(event);
    let geneticModifierFilter = {};
    autocompleteFields.forEach(field => {
      geneticModifierFilter[field] = {
        queryString: event.query,
        tokenOperator: "AND"
      }
    });

    searchService.search("biologicalentity", 15, 0, null, { "geneticModifierFilter": geneticModifierFilter })
      .then((data) => {
        //console.log(data);
        if (data.results && data.results.length > 0)
          setFilteredGeneticModifiers(data.results);
        else
          setFilteredGeneticModifiers([]);
      });
  };

  const onGeneticModifierEditorValueChange = (event) => {
    let updatedAnnotations = [...rowProps.props.value];

    if (event.target.value || event.target.value === '') {
      updatedAnnotations[rowProps.rowIndex].diseaseGeneticModifier = {};
      if (typeof event.target.value === "object") {
        updatedAnnotations[rowProps.rowIndex].diseaseGeneticModifier.curie = event.target.value.curie;
      } else {
        updatedAnnotations[rowProps.rowIndex].diseaseGeneticModifier.curie = event.target.value;
      }
      setFieldValue(updatedAnnotations[rowProps.rowIndex].diseaseGeneticModifier.curie);
    }
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };

  const geneticModifierItemTemplate = (item) => {
    let inputValue = trimWhitespace(rowProps.rowData.diseaseGeneticModifier.curie.toLowerCase());
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
        id={rowProps.rowData.subject.curie}
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
        field="curie"
        value={fieldValue}
        suggestions={filteredGeneticModifiers}
        itemTemplate={geneticModifierItemTemplate}
        completeMethod={searchGeneticModifier}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onGeneticModifierEditorValueChange(e)}
      />
      <SubjectTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.diseaseGeneticModifier.curie.toLowerCase())}
      />
    </div>

  )
};
