function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { CODE_DOWN, CODE_END, CODE_ENTER, CODE_HOME, CODE_SPACE, CODE_UP } from '../../../constants/key-codes';
import { arrayIncludes, from as arrayFrom } from '../../../utils/array';
import { attemptFocus, closest, isActiveElement, isElement } from '../../../utils/dom';
import { stopEvent } from '../../../utils/events';
import { props as tbodyProps, BTbody } from '../tbody';
import filterEvent from './filter-event';
import textSelectionActive from './text-selection-active';
import tbodyRowMixin from './mixin-tbody-row';

var props = _objectSpread(_objectSpread({}, tbodyProps), {}, {
  tbodyClass: {
    type: [String, Array, Object] // default: undefined

  }
});

export default {
  mixins: [tbodyRowMixin],
  props: props,
  beforeDestroy: function beforeDestroy() {
    this.$_bodyFieldSlotNameCache = null;
  },
  methods: {
    // Helper methods
    getTbodyTrs: function getTbodyTrs() {
      // Returns all the item TR elements (excludes detail and spacer rows)
      // `this.$refs.itemRows` is an array of item TR components/elements
      // Rows should all be B-TR components, but we map to TR elements
      // Also note that `this.$refs.itemRows` may not always be in document order
      var refs = this.$refs || {};
      var tbody = refs.tbody ? refs.tbody.$el || refs.tbody : null;
      var trs = (refs.itemRows || []).map(function (tr) {
        return tr.$el || tr;
      });
      return tbody && tbody.children && tbody.children.length > 0 && trs && trs.length > 0 ? arrayFrom(tbody.children).filter(function (tr) {
        return arrayIncludes(trs, tr);
      }) :
      /* istanbul ignore next */
      [];
    },
    getTbodyTrIndex: function getTbodyTrIndex(el) {
      // Returns index of a particular TBODY item TR
      // We set `true` on closest to include self in result

      /* istanbul ignore next: should not normally happen */
      if (!isElement(el)) {
        return -1;
      }

      var tr = el.tagName === 'TR' ? el : closest('tr', el, true);
      return tr ? this.getTbodyTrs().indexOf(tr) : -1;
    },
    emitTbodyRowEvent: function emitTbodyRowEvent(type, evt) {
      // Emits a row event, with the item object, row index and original event
      if (type && this.hasListener(type) && evt && evt.target) {
        var rowIndex = this.getTbodyTrIndex(evt.target);

        if (rowIndex > -1) {
          // The array of TRs correlate to the `computedItems` array
          var item = this.computedItems[rowIndex];
          this.$emit(type, item, rowIndex, evt);
        }
      }
    },
    tbodyRowEvtStopped: function tbodyRowEvtStopped(evt) {
      return this.stopIfBusy && this.stopIfBusy(evt);
    },
    // Delegated row event handlers
    onTbodyRowKeydown: function onTbodyRowKeydown(evt) {
      // Keyboard navigation and row click emulation
      var target = evt.target;

      if (this.tbodyRowEvtStopped(evt) || target.tagName !== 'TR' || !isActiveElement(target) || target.tabIndex !== 0) {
        // Early exit if not an item row TR
        return;
      }

      var keyCode = evt.keyCode;

      if (arrayIncludes([CODE_ENTER, CODE_SPACE], keyCode)) {
        // Emulated click for keyboard users, transfer to click handler
        stopEvent(evt);
        this.onTBodyRowClicked(evt);
      } else if (arrayIncludes([CODE_UP, CODE_DOWN, CODE_HOME, CODE_END], keyCode)) {
        // Keyboard navigation
        var rowIndex = this.getTbodyTrIndex(target);

        if (rowIndex > -1) {
          stopEvent(evt);
          var trs = this.getTbodyTrs();
          var shift = evt.shiftKey;

          if (keyCode === CODE_HOME || shift && keyCode === CODE_UP) {
            // Focus first row
            attemptFocus(trs[0]);
          } else if (keyCode === CODE_END || shift && keyCode === CODE_DOWN) {
            // Focus last row
            attemptFocus(trs[trs.length - 1]);
          } else if (keyCode === CODE_UP && rowIndex > 0) {
            // Focus previous row
            attemptFocus(trs[rowIndex - 1]);
          } else if (keyCode === CODE_DOWN && rowIndex < trs.length - 1) {
            // Focus next row
            attemptFocus(trs[rowIndex + 1]);
          }
        }
      }
    },
    onTBodyRowClicked: function onTBodyRowClicked(evt) {
      if (this.tbodyRowEvtStopped(evt)) {
        // If table is busy, then don't propagate
        return;
      } else if (filterEvent(evt) || textSelectionActive(this.$el)) {
        // Clicked on a non-disabled control so ignore
        // Or user is selecting text, so ignore
        return;
      }

      this.emitTbodyRowEvent('row-clicked', evt);
    },
    onTbodyRowMiddleMouseRowClicked: function onTbodyRowMiddleMouseRowClicked(evt) {
      if (!this.tbodyRowEvtStopped(evt) && evt.which === 2) {
        this.emitTbodyRowEvent('row-middle-clicked', evt);
      }
    },
    onTbodyRowContextmenu: function onTbodyRowContextmenu(evt) {
      if (!this.tbodyRowEvtStopped(evt)) {
        this.emitTbodyRowEvent('row-contextmenu', evt);
      }
    },
    onTbodyRowDblClicked: function onTbodyRowDblClicked(evt) {
      if (!this.tbodyRowEvtStopped(evt) && !filterEvent(evt)) {
        this.emitTbodyRowEvent('row-dblclicked', evt);
      }
    },
    // Note: Row hover handlers are handled by the tbody-row mixin
    // As mouseenter/mouseleave events do not bubble
    //
    // Render Helper
    renderTbody: function renderTbody() {
      var _this = this;

      // Render the tbody element and children
      var items = this.computedItems; // Shortcut to `createElement` (could use `this._c()` instead)

      var h = this.$createElement;
      var hasRowClickHandler = this.hasListener('row-clicked') || this.hasSelectableRowClick; // Prepare the tbody rows

      var $rows = []; // Add the item data rows or the busy slot

      var $busy = this.renderBusy ? this.renderBusy() : null;

      if ($busy) {
        // If table is busy and a busy slot, then return only the busy "row" indicator
        $rows.push($busy);
      } else {
        // Table isn't busy, or we don't have a busy slot
        // Create a slot cache for improved performance when looking up cell slot names
        // Values will be keyed by the field's `key` and will store the slot's name
        // Slots could be dynamic (i.e. `v-if`), so we must compute on each render
        // Used by tbody-row mixin render helper
        var cache = {};
        var defaultSlotName = this.hasNormalizedSlot('cell()') ? 'cell()' : null;
        this.computedFields.forEach(function (field) {
          var key = field.key;
          var fullName = "cell(".concat(key, ")");
          var lowerName = "cell(".concat(key.toLowerCase(), ")");
          cache[key] = _this.hasNormalizedSlot(fullName) ? fullName : _this.hasNormalizedSlot(lowerName) ?
          /* istanbul ignore next */
          lowerName : defaultSlotName;
        }); // Created as a non-reactive property so to not trigger component updates
        // Must be a fresh object each render

        this.$_bodyFieldSlotNameCache = cache; // Add static top row slot (hidden in visibly stacked mode
        // as we can't control `data-label` attr)

        $rows.push(this.renderTopRow ? this.renderTopRow() : h()); // Render the rows

        items.forEach(function (item, rowIndex) {
          // Render the individual item row (rows if details slot)
          $rows.push(_this.renderTbodyRow(item, rowIndex));
        }); // Empty items / empty filtered row slot (only shows if `items.length < 1`)

        $rows.push(this.renderEmpty ? this.renderEmpty() : h()); // Static bottom row slot (hidden in visibly stacked mode
        // as we can't control `data-label` attr)

        $rows.push(this.renderBottomRow ? this.renderBottomRow() : h());
      } // Note: these events will only emit if a listener is registered


      var handlers = {
        auxclick: this.onTbodyRowMiddleMouseRowClicked,
        // TODO:
        //   Perhaps we do want to automatically prevent the
        //   default context menu from showing if there is a
        //   `row-contextmenu` listener registered
        contextmenu: this.onTbodyRowContextmenu,
        // The following event(s) is not considered A11Y friendly
        dblclick: this.onTbodyRowDblClicked // Hover events (`mouseenter`/`mouseleave`) are handled by `tbody-row` mixin

      }; // Add in click/keydown listeners if needed

      if (hasRowClickHandler) {
        handlers.click = this.onTBodyRowClicked;
        handlers.keydown = this.onTbodyRowKeydown;
      } // Assemble rows into the tbody


      var $tbody = h(BTbody, {
        ref: 'tbody',
        class: this.tbodyClass || null,
        props: {
          tbodyTransitionProps: this.tbodyTransitionProps,
          tbodyTransitionHandlers: this.tbodyTransitionHandlers
        },
        // BTbody transfers all native event listeners to the root element
        // TODO: Only set the handlers if the table is not busy
        on: handlers
      }, $rows); // Return the assembled tbody

      return $tbody;
    }
  }
};