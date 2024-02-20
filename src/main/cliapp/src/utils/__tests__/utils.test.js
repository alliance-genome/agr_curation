import { describe, expect, it } from '@jest/globals';
import { removeInvalidFilters, removeInvalidSorts } from '../utils';
import 'core-js/features/structured-clone';


describe('removeInvalidFilters', () => {
  it('All Valid fields', () => {
    const localStorageFilters = {
      "objectFilter": {
        "diseaseAnnotationObject.name": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "diseaseAnnotationObject.curie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };
    const newFilters = removeInvalidFilters(localStorageFilters);
    expect(newFilters).toEqual(localStorageFilters);
  });

  it('One invalid field', () => {
    const localStorageFilters = {
      "objectFilter": {
        "diseaseAnnotationObject.invalidName": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "diseaseAnnotationObject.curie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };
    
    const newFilters = removeInvalidFilters(localStorageFilters);

    expect(newFilters).toEqual({
      "objectFilter": {
        "diseaseAnnotationObject.curie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    });
  });

  it('All invalid fields', () => {
    const localStorageFilters = {
      "objectFilter": {
        "diseaseAnnotationObject.invalidName": {
          "queryString": "j",
          " tokenOperator": "AND"
        },
        "diseaseAnnotationObject.invalidCurie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "invalidNegated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "invalidModEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };

    const newFilters = removeInvalidFilters(localStorageFilters);
    expect(newFilters).toEqual({});
  });
});

describe('removeInvalidSorts', () => {
  it('All Valid fields', () => {
    const localMultiSortMeta = [
      {
        "field": "negated",
        "order": 1
      },
      {
        "field": "diseaseAnnotationObject.name",
        "order": 1
      },
      {
        "field": "diseaseAnnotationSubject.symbol",
        "order": 1
      }
    ];
    const newSorts = removeInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual(localMultiSortMeta);
  });

  it('One invalid field', () => {
    const localMultiSortMeta = [
      {
        "field": "invalidNegated",
        "order": 1
      },
      {
        "field": "diseaseAnnotationObject.name",
        "order": 1
      },
      {
        "field": "diseaseAnnotationSubject.symbol",
        "order": 1
      }
    ];

    const newSorts = removeInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([
      {
        "field": "diseaseAnnotationObject.name",
        "order": 1
      },
      {
        "field": "diseaseAnnotationSubject.symbol",
        "order": 1
      }
    ]);
  });

  it('All invalid fields', () => {
    const localMultiSortMeta = [
      {
        "field": "invalidNegated",
        "order": 1
      },
      {
        "field": "diseaseAnnotationObject.invalidName",
        "order": 1
      },
      {
        "field": "diseaseAnnotationSubject.invalidSymbol",
        "order": 1
      }
    ];

    const newSorts = removeInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([]);
  });
});


