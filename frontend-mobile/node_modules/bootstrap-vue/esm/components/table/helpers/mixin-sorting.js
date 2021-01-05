function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { NAME_TABLE } from '../../../constants/components';
import stableSort from '../../../utils/stable-sort';
import { arrayIncludes } from '../../../utils/array';
import { makePropsConfigurable } from '../../../utils/config';
import { isFunction, isUndefinedOrNull } from '../../../utils/inspect';
import { trim } from '../../../utils/string';
import defaultSortCompare from './default-sort-compare';
var SORT_DIRECTIONS = ['asc', 'desc', 'last'];
export default {
  props: makePropsConfigurable({
    sortBy: {
      type: String,
      default: ''
    },
    sortDesc: {
      // TODO: Make this tri-state: true, false, null
      type: Boolean,
      default: false
    },
    sortDirection: {
      // This prop is named incorrectly
      // It should be `initialSortDirection` as it is a bit misleading
      // (not to mention it screws up the ARIA label on the headers)
      type: String,
      default: 'asc',
      validator: function validator(value) {
        return arrayIncludes(SORT_DIRECTIONS, value);
      }
    },
    sortCompare: {
      type: Function // default: null

    },
    sortCompareOptions: {
      // Supported localCompare options, see `options` section of:
      // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/localeCompare
      type: Object,
      default: function _default() {
        return {
          numeric: true
        };
      }
    },
    sortCompareLocale: {
      // String: locale code
      // Array: array of Locale strings
      type: [String, Array] // default: undefined

    },
    sortNullLast: {
      // Sort null and undefined to appear last
      type: Boolean,
      default: false
    },
    noSortReset: {
      // Another prop that should have had a better name.
      // It should be noSortClear (on non-sortable headers).
      // We will need to make sure the documentation is clear on what
      // this prop does (as well as in the code for future reference)
      type: Boolean,
      default: false
    },
    labelSortAsc: {
      type: String,
      default: 'Click to sort Ascending'
    },
    labelSortDesc: {
      type: String,
      default: 'Click to sort Descending'
    },
    labelSortClear: {
      type: String,
      default: 'Click to clear sorting'
    },
    noLocalSorting: {
      type: Boolean,
      default: false
    },
    noFooterSorting: {
      type: Boolean,
      default: false
    },
    sortIconLeft: {
      // Place the sorting icon on the left of the header cells
      type: Boolean,
      default: false
    }
  }, NAME_TABLE),
  data: function data() {
    return {
      localSortBy: this.sortBy || '',
      localSortDesc: this.sortDesc || false
    };
  },
  computed: {
    localSorting: function localSorting() {
      return this.hasProvider ? !!this.noProviderSorting : !this.noLocalSorting;
    },
    isSortable: function isSortable() {
      return this.computedFields.some(function (f) {
        return f.sortable;
      });
    },
    // Sorts the filtered items and returns a new array of the sorted items
    // When not sorted, the original items array will be returned
    sortedItems: function sortedItems() {
      var sortBy = this.localSortBy,
          sortDesc = this.localSortDesc,
          locale = this.sortCompareLocale,
          nullLast = this.sortNullLast,
          sortCompare = this.sortCompare,
          localSorting = this.localSorting;
      var items = (this.filteredItems || this.localItems || []).slice();

      var localeOptions = _objectSpread(_objectSpread({}, this.sortCompareOptions), {}, {
        usage: 'sort'
      });

      if (sortBy && localSorting) {
        var field = this.computedFieldsObj[sortBy] || {};
        var sortByFormatted = field.sortByFormatted;
        var formatter = isFunction(sortByFormatted) ?
        /* istanbul ignore next */
        sortByFormatted : sortByFormatted ? this.getFieldFormatter(sortBy) : undefined; // `stableSort` returns a new array, and leaves the original array intact

        return stableSort(items, function (a, b) {
          var result = null; // Call user provided `sortCompare` routine first

          if (isFunction(sortCompare)) {
            // TODO:
            //   Change the `sortCompare` signature to the one of `defaultSortCompare`
            //   with the next major version bump
            result = sortCompare(a, b, sortBy, sortDesc, formatter, localeOptions, locale);
          } // Fallback to built-in `defaultSortCompare` if `sortCompare`
          // is not defined or returns `null`/`false`


          if (isUndefinedOrNull(result) || result === false) {
            result = defaultSortCompare(a, b, {
              sortBy: sortBy,
              formatter: formatter,
              locale: locale,
              localeOptions: localeOptions,
              nullLast: nullLast
            });
          } // Negate result if sorting in descending order


          return (result || 0) * (sortDesc ? -1 : 1);
        });
      }

      return items;
    }
  },
  watch: {
    /* istanbul ignore next: pain in the butt to test */
    isSortable: function isSortable(newVal) {
      if (newVal) {
        if (this.isSortable) {
          this.$on('head-clicked', this.handleSort);
        }
      } else {
        this.$off('head-clicked', this.handleSort);
      }
    },
    sortDesc: function sortDesc(newVal) {
      if (newVal === this.localSortDesc) {
        /* istanbul ignore next */
        return;
      }

      this.localSortDesc = newVal || false;
    },
    sortBy: function sortBy(newVal) {
      if (newVal === this.localSortBy) {
        /* istanbul ignore next */
        return;
      }

      this.localSortBy = newVal || '';
    },
    // Update .sync props
    localSortDesc: function localSortDesc(newVal, oldVal) {
      // Emit update to sort-desc.sync
      if (newVal !== oldVal) {
        this.$emit('update:sortDesc', newVal);
      }
    },
    localSortBy: function localSortBy(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit('update:sortBy', newVal);
      }
    }
  },
  created: function created() {
    if (this.isSortable) {
      this.$on('head-clicked', this.handleSort);
    }
  },
  methods: {
    // Handlers
    // Need to move from thead-mixin
    handleSort: function handleSort(key, field, evt, isFoot) {
      var _this = this;

      if (!this.isSortable) {
        /* istanbul ignore next */
        return;
      }

      if (isFoot && this.noFooterSorting) {
        return;
      } // TODO: make this tri-state sorting
      // cycle desc => asc => none => desc => ...


      var sortChanged = false;

      var toggleLocalSortDesc = function toggleLocalSortDesc() {
        var sortDirection = field.sortDirection || _this.sortDirection;

        if (sortDirection === 'asc') {
          _this.localSortDesc = false;
        } else if (sortDirection === 'desc') {
          _this.localSortDesc = true;
        } else {// sortDirection === 'last'
          // Leave at last sort direction from previous column
        }
      };

      if (field.sortable) {
        var sortKey = !this.localSorting && field.sortKey ? field.sortKey : key;

        if (this.localSortBy === sortKey) {
          // Change sorting direction on current column
          this.localSortDesc = !this.localSortDesc;
        } else {
          // Start sorting this column ascending
          this.localSortBy = sortKey; // this.localSortDesc = false

          toggleLocalSortDesc();
        }

        sortChanged = true;
      } else if (this.localSortBy && !this.noSortReset) {
        this.localSortBy = '';
        toggleLocalSortDesc();
        sortChanged = true;
      }

      if (sortChanged) {
        // Sorting parameters changed
        this.$emit('sort-changed', this.context);
      }
    },
    // methods to compute classes and attrs for thead>th cells
    sortTheadThClasses: function sortTheadThClasses(key, field, isFoot) {
      return {
        // If sortable and sortIconLeft are true, then place sort icon on the left
        'b-table-sort-icon-left': field.sortable && this.sortIconLeft && !(isFoot && this.noFooterSorting)
      };
    },
    sortTheadThAttrs: function sortTheadThAttrs(key, field, isFoot) {
      if (!this.isSortable || isFoot && this.noFooterSorting) {
        // No attributes if not a sortable table
        return {};
      }

      var sortable = field.sortable; // Assemble the aria-sort attribute value

      var ariaSort = sortable && this.localSortBy === key ? this.localSortDesc ? 'descending' : 'ascending' : sortable ? 'none' : null; // Return the attribute

      return {
        'aria-sort': ariaSort
      };
    },
    sortTheadThLabel: function sortTheadThLabel(key, field, isFoot) {
      // A label to be placed in an `.sr-only` element in the header cell
      if (!this.isSortable || isFoot && this.noFooterSorting) {
        // No label if not a sortable table
        return null;
      }

      var sortable = field.sortable; // The correctness of these labels is very important for screen-reader users.

      var labelSorting = '';

      if (sortable) {
        if (this.localSortBy === key) {
          // currently sorted sortable column.
          labelSorting = this.localSortDesc ? this.labelSortAsc : this.labelSortDesc;
        } else {
          // Not currently sorted sortable column.
          // Not using nested ternary's here for clarity/readability
          // Default for ariaLabel
          labelSorting = this.localSortDesc ? this.labelSortDesc : this.labelSortAsc; // Handle sortDirection setting

          var sortDirection = this.sortDirection || field.sortDirection;

          if (sortDirection === 'asc') {
            labelSorting = this.labelSortAsc;
          } else if (sortDirection === 'desc') {
            labelSorting = this.labelSortDesc;
          }
        }
      } else if (!this.noSortReset) {
        // Non sortable column
        labelSorting = this.localSortBy ? this.labelSortClear : '';
      } // Return the sr-only sort label or null if no label


      return trim(labelSorting) || null;
    }
  }
};