function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { NAME_TABLE } from '../../../constants/components';
import identity from '../../../utils/identity';
import looseEqual from '../../../utils/loose-equal';
import range from '../../../utils/range';
import { arrayIncludes } from '../../../utils/array';
import { makePropsConfigurable } from '../../../utils/config';
import { isArray, isNumber } from '../../../utils/inspect';
import { mathMax, mathMin } from '../../../utils/math';
import sanitizeRow from './sanitize-row';
var SELECT_MODES = ['range', 'multi', 'single'];
export default {
  props: makePropsConfigurable({
    selectable: {
      type: Boolean,
      default: false
    },
    selectMode: {
      type: String,
      default: 'multi',
      validator: function validator(value) {
        return arrayIncludes(SELECT_MODES, value);
      }
    },
    selectedVariant: {
      type: String,
      default: 'active'
    },
    noSelectOnClick: {
      // Disable use of click handlers for row selection
      type: Boolean,
      default: false
    }
  }, NAME_TABLE),
  data: function data() {
    return {
      selectedRows: [],
      selectedLastRow: -1
    };
  },
  computed: {
    isSelectable: function isSelectable() {
      return this.selectable && this.selectMode;
    },
    hasSelectableRowClick: function hasSelectableRowClick() {
      return this.isSelectable && !this.noSelectOnClick;
    },
    supportsSelectableRows: function supportsSelectableRows() {
      return true;
    },
    selectableHasSelection: function selectableHasSelection() {
      return this.isSelectable && this.selectedRows && this.selectedRows.length > 0 && this.selectedRows.some(identity);
    },
    selectableIsMultiSelect: function selectableIsMultiSelect() {
      return this.isSelectable && arrayIncludes(['range', 'multi'], this.selectMode);
    },
    selectableTableClasses: function selectableTableClasses() {
      var _ref;

      return _ref = {
        'b-table-selectable': this.isSelectable
      }, _defineProperty(_ref, "b-table-select-".concat(this.selectMode), this.isSelectable), _defineProperty(_ref, 'b-table-selecting', this.selectableHasSelection), _defineProperty(_ref, 'b-table-selectable-no-click', this.isSelectable && !this.hasSelectableRowClick), _ref;
    },
    selectableTableAttrs: function selectableTableAttrs() {
      return {
        // TODO:
        //   Should this attribute not be included when no-select-on-click is set
        //   since this attribute implies keyboard navigation?
        'aria-multiselectable': !this.isSelectable ? null : this.selectableIsMultiSelect ? 'true' : 'false'
      };
    }
  },
  watch: {
    computedItems: function computedItems(newVal, oldVal) {
      // Reset for selectable
      var equal = false;

      if (this.isSelectable && this.selectedRows.length > 0) {
        // Quick check against array length
        equal = isArray(newVal) && isArray(oldVal) && newVal.length === oldVal.length;

        for (var i = 0; equal && i < newVal.length; i++) {
          // Look for the first non-loosely equal row, after ignoring reserved fields
          equal = looseEqual(sanitizeRow(newVal[i]), sanitizeRow(oldVal[i]));
        }
      }

      if (!equal) {
        this.clearSelected();
      }
    },
    selectable: function selectable(newVal) {
      this.clearSelected();
      this.setSelectionHandlers(newVal);
    },
    selectMode: function selectMode() {
      this.clearSelected();
    },
    hasSelectableRowClick: function hasSelectableRowClick(newVal) {
      this.clearSelected();
      this.setSelectionHandlers(!newVal);
    },
    selectedRows: function selectedRows(_selectedRows, oldVal) {
      var _this = this;

      if (this.isSelectable && !looseEqual(_selectedRows, oldVal)) {
        var items = []; // `.forEach()` skips over non-existent indices (on sparse arrays)

        _selectedRows.forEach(function (v, idx) {
          if (v) {
            items.push(_this.computedItems[idx]);
          }
        });

        this.$emit('row-selected', items);
      }
    }
  },
  beforeMount: function beforeMount() {
    // Set up handlers if needed
    if (this.isSelectable) {
      this.setSelectionHandlers(true);
    }
  },
  methods: {
    // Public methods
    selectRow: function selectRow(index) {
      // Select a particular row (indexed based on computedItems)
      if (this.isSelectable && isNumber(index) && index >= 0 && index < this.computedItems.length && !this.isRowSelected(index)) {
        var selectedRows = this.selectableIsMultiSelect ? this.selectedRows.slice() : [];
        selectedRows[index] = true;
        this.selectedLastClicked = -1;
        this.selectedRows = selectedRows;
      }
    },
    unselectRow: function unselectRow(index) {
      // Un-select a particular row (indexed based on `computedItems`)
      if (this.isSelectable && isNumber(index) && this.isRowSelected(index)) {
        var selectedRows = this.selectedRows.slice();
        selectedRows[index] = false;
        this.selectedLastClicked = -1;
        this.selectedRows = selectedRows;
      }
    },
    selectAllRows: function selectAllRows() {
      var length = this.computedItems.length;

      if (this.isSelectable && length > 0) {
        this.selectedLastClicked = -1;
        this.selectedRows = this.selectableIsMultiSelect ? range(length).map(function () {
          return true;
        }) : [true];
      }
    },
    isRowSelected: function isRowSelected(index) {
      // Determine if a row is selected (indexed based on `computedItems`)
      return !!(isNumber(index) && this.selectedRows[index]);
    },
    clearSelected: function clearSelected() {
      // Clear any active selected row(s)
      this.selectedLastClicked = -1;
      this.selectedRows = [];
    },
    // Internal private methods
    selectableRowClasses: function selectableRowClasses(index) {
      if (this.isSelectable && this.isRowSelected(index)) {
        var variant = this.selectedVariant;
        return _defineProperty({
          'b-table-row-selected': true
        }, "".concat(this.dark ? 'bg' : 'table', "-").concat(variant), variant);
      } else {
        return {};
      }
    },
    selectableRowAttrs: function selectableRowAttrs(index) {
      return {
        'aria-selected': !this.isSelectable ? null : this.isRowSelected(index) ? 'true' : 'false'
      };
    },
    setSelectionHandlers: function setSelectionHandlers(on) {
      var method = on && !this.noSelectOnClick ? '$on' : '$off'; // Handle row-clicked event

      this[method]('row-clicked', this.selectionHandler); // Clear selection on filter, pagination, and sort changes

      this[method]('filtered', this.clearSelected);
      this[method]('context-changed', this.clearSelected);
    },
    selectionHandler: function selectionHandler(item, index, evt) {
      /* istanbul ignore if: should never happen */
      if (!this.isSelectable || this.noSelectOnClick) {
        // Don't do anything if table is not in selectable mode
        this.clearSelected();
        return;
      }

      var selectMode = this.selectMode;
      var selectedRows = this.selectedRows.slice();
      var selected = !selectedRows[index]; // Note 'multi' mode needs no special event handling

      if (selectMode === 'single') {
        selectedRows = [];
      } else if (selectMode === 'range') {
        if (this.selectedLastRow > -1 && evt.shiftKey) {
          // range
          for (var idx = mathMin(this.selectedLastRow, index); idx <= mathMax(this.selectedLastRow, index); idx++) {
            selectedRows[idx] = true;
          }

          selected = true;
        } else {
          if (!(evt.ctrlKey || evt.metaKey)) {
            // Clear range selection if any
            selectedRows = [];
            selected = true;
          }

          this.selectedLastRow = selected ? index : -1;
        }
      }

      selectedRows[index] = selected;
      this.selectedRows = selectedRows;
    }
  }
};