import { AutocompleteEditor } from "../Autocomplete/AutocompleteEditor";
import { buildAutocompleteFilter, autocompleteSearch } from "../../utils/utils";
import { SearchService } from "../../service/SearchService";
import { EvidenceAutocompleteTemplate } from "../Autocomplete/EvidenceAutocompleteTemplate";
import { ErrorMessageComponent } from "../Error/ErrorMessageComponent";

const evidenceSearch = (event, setFiltered, setInputValue) => {
  const searchService = new SearchService();
  const autocompleteFields = ["curie", "name", "abbreviation"];
  const endpoint = "ecoterm";
  const filterName = "evidenceFilter";
  const filter = buildAutocompleteFilter(event, autocompleteFields);
  const otherFilters = {
    obsoleteFilter: {
      "obsolete": {
        queryString: false
      }
    },
    subsetFilter: {
      "subsets": {
        queryString: "agr_eco_terms"
      }
    }
  };

  setInputValue(event.query);
  autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
};

export const EvidenceCodeEditor = ({ props, errorMessages, onChangeHandler }) => {
  return (
    <>
      <AutocompleteEditor
        search={evidenceSearch}
        initialValue={props.rowData.evidenceCode}
        rowProps={props}
        fieldName='evidenceCode'
        valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
          <EvidenceAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />}
        onValueChangeHandler={onChangeHandler}
      />
      <ErrorMessageComponent
        errorMessages={errorMessages[props.rowIndex]}
        errorField="evidenceCode"
      />
    </>
  );
};
