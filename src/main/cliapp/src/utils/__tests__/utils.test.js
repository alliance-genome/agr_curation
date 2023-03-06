import { describe, expect, it } from '@jest/globals';
import { removeInvalidFilters, removeInvalidSorts } from '../utils';
import 'core-js/features/structured-clone';


describe('removeInvalidFilters', () => {
  it('All Valid fields', () => {
    const localStorageFilters = {
      "objectFilter": {
        "object.name": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "object.curie": {
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
        "object.invalidName": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "object.curie": {
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
        "object.curie": {
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
        "object.invalidName": {
          "queryString": "j",
          " tokenOperator": "AND"
        },
        "object.invalidCurie": {
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
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
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
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
        "order": 1
      }
    ];

    const newSorts = removeInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([
      {
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
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
        "field": "object.invalidName",
        "order": 1
      },
      {
        "field": "subject.invalidSymbol",
        "order": 1
      }
    ];

    const newSorts = removeInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([]);
  });
});


