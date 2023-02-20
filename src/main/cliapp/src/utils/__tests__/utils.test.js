import { describe, expect, it } from '@jest/globals';
import { getInvalidFilterFields, deleteInvalidFilters } from '../utils';
import { FilterAndSortFields } from '../../constants/FilterFields';
import 'core-js/features/structured-clone';


describe('getInvalidFilterFields', () => {
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
    const tableFieldsMap = FilterAndSortFields["DiseaseAnnotations"];
    const invalidFields = getInvalidFilterFields(localStorageFilters, tableFieldsMap);
    expect(invalidFields.length).toBe(0);
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
    const tableFieldsMap = FilterAndSortFields["DiseaseAnnotations"];
    const invalidFields = getInvalidFilterFields(localStorageFilters, tableFieldsMap);
    expect(invalidFields.length).toBe(1);
  });

  it('Empty filters', () => {
    const localStorageFilters = {};
    const tableFieldsMap = FilterAndSortFields["DiseaseAnnotations"];
    const invalidFields = getInvalidFilterFields(localStorageFilters, tableFieldsMap);
    expect(invalidFields.length).toBe(0);
  });
});

//tests for deleteInvalidFilters function
describe('deleteInvalidFilters', () => {
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
    const invalidFields = [];
    const newFilters = deleteInvalidFilters(invalidFields, localStorageFilters);
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
    const invalidFields = ["objectFilter"];
    const newFilters = deleteInvalidFilters(invalidFields, localStorageFilters);
    expect(newFilters).toEqual(
      {
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

  it('Empty filters', () => {
    const localStorageFilters = {};
    const invalidFields = [];
    const newFilters = deleteInvalidFilters(invalidFields, localStorageFilters);
    expect(newFilters).toEqual({});
  });
});




//   //jest mock for global.structuredClone function
//   beforeEach(() => {
//     global.structuredClone = jest.fn((obj) => {
//       return JSON.parse(JSON.stringify(obj));
//     });
//   });

//   it('All Valid fields', () => {
//     const invalidFields = [];
//     const localStorageFilters = {
//       "objectFilter": {
//         "object.name": {
//           "queryString": "j",
//           "tokenOperator": "AND"
//         },
//         "object.curie": {
//           "queryString": "j",
//           "tokenOperator": "AND"
//         }
//       },
//       "negatedFilter": {
//         "negated": {
//           "queryString": "true",
//           "tokenOperator": "OR"
//         }
//       },
//       "modentityidFilter": {
//         "modEntityId": {
//           "queryString": "s",
//           "tokenOperator": "AND"
//         }
//       }
//     };
//     const newFilters = deleteInvalidFilters(invalidFields, localStorageFilters);
//     expect(newFilters).toEqual(localStorageFilters);
//   });

//   it('One invalid field', () => {
//     const invalidFields = ["object.invalidName"];
//     const localStorageFilters = {
//       "objectFilter": {
//         "object.invalidName": {
//           "queryString": "j",
//           "tokenOperator": "AND"
//         },
//         "object.curie": {
//           "queryString": "j",
//           "tokenOperator": "AND"
//         }
//       },
//       "negatedFilter": {
//         "negated": {
//           "queryString": "true",
//           "tokenOperator": "OR"
//         }
//       },
//       "modentityidFilter": {
//         "modEntityId": {
//           "queryString": "s",
//           "tokenOperator": "AND"
//         }
//       }
//     };
//     const newFilters = deleteInvalidFilters(invalidFields, localStorageFilters);
//     expect(newFilters).toEqual({
//       "negatedFilter": {
//         "negated": {
//           "queryString": "true",
//           "tokenOperator": "OR"
//         }
//       },
//       "modentityidFilter": {
//         "modEntityId": {
//           "queryString": "s",
//           "tokenOperator": "AND"
//         }
//       }}
//     );
//   });
// });