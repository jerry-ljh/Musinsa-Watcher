import Vue from '../../vue';
import { NAME_BUTTON_TOOLBAR } from '../../constants/components';
import { CODE_DOWN, CODE_LEFT, CODE_RIGHT, CODE_UP } from '../../constants/key-codes';
import { makePropsConfigurable } from '../../utils/config';
import { attemptFocus, contains, isVisible, selectAll } from '../../utils/dom';
import { stopEvent } from '../../utils/events';
import normalizeSlotMixin from '../../mixins/normalize-slot'; // --- Constants ---

var ITEM_SELECTOR = ['.btn:not(.disabled):not([disabled]):not(.dropdown-item)', '.form-control:not(.disabled):not([disabled])', 'select:not(.disabled):not([disabled])', 'input[type="checkbox"]:not(.disabled)', 'input[type="radio"]:not(.disabled)'].join(','); // --- Main component ---
// @vue/component

export var BButtonToolbar = /*#__PURE__*/Vue.extend({
  name: NAME_BUTTON_TOOLBAR,
  mixins: [normalizeSlotMixin],
  props: makePropsConfigurable({
    justify: {
      type: Boolean,
      default: false
    },
    keyNav: {
      type: Boolean,
      default: false
    }
  }, NAME_BUTTON_TOOLBAR),
  mounted: function mounted() {
    // Pre-set the tabindexes if the markup does not include
    // `tabindex="-1"` on the toolbar items
    if (this.keyNav) {
      this.getItems();
    }
  },
  methods: {
    getItems: function getItems() {
      var items = selectAll(ITEM_SELECTOR, this.$el); // Ensure `tabindex="-1"` is set on every item

      items.forEach(function (item) {
        item.tabIndex = -1;
      });
      return items.filter(function (el) {
        return isVisible(el);
      });
    },
    focusFirst: function focusFirst() {
      var items = this.getItems();
      attemptFocus(items[0]);
    },
    focusPrev: function focusPrev(evt) {
      var items = this.getItems();
      var index = items.indexOf(evt.target);

      if (index > -1) {
        items = items.slice(0, index).reverse();
        attemptFocus(items[0]);
      }
    },
    focusNext: function focusNext(evt) {
      var items = this.getItems();
      var index = items.indexOf(evt.target);

      if (index > -1) {
        items = items.slice(index + 1);
        attemptFocus(items[0]);
      }
    },
    focusLast: function focusLast() {
      var items = this.getItems().reverse();
      attemptFocus(items[0]);
    },
    onFocusin: function onFocusin(evt) {
      var $el = this.$el;

      if (evt.target === $el && !contains($el, evt.relatedTarget)) {
        stopEvent(evt);
        this.focusFirst(evt);
      }
    },
    onKeydown: function onKeydown(evt) {
      var keyCode = evt.keyCode,
          shiftKey = evt.shiftKey;

      if (keyCode === CODE_UP || keyCode === CODE_LEFT) {
        stopEvent(evt);
        shiftKey ? this.focusFirst(evt) : this.focusPrev(evt);
      } else if (keyCode === CODE_DOWN || keyCode === CODE_RIGHT) {
        stopEvent(evt);
        shiftKey ? this.focusLast(evt) : this.focusNext(evt);
      }
    }
  },
  render: function render(h) {
    return h('div', {
      staticClass: 'btn-toolbar',
      class: {
        'justify-content-between': this.justify
      },
      attrs: {
        role: 'toolbar',
        tabindex: this.keyNav ? '0' : null
      },
      on: this.keyNav ? {
        focusin: this.onFocusin,
        keydown: this.onKeydown
      } : {}
    }, [this.normalizeSlot()]);
  }
});