function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_NAVBAR } from '../../constants/components';
import { makePropsConfigurable, getBreakpoints } from '../../utils/config';
import { isTag } from '../../utils/dom';
import { isString } from '../../utils/inspect';
import normalizeSlotMixin from '../../mixins/normalize-slot'; // --- Props ---

export var props = makePropsConfigurable({
  tag: {
    type: String,
    default: 'nav'
  },
  type: {
    type: String,
    default: 'light'
  },
  variant: {
    type: String // default: undefined

  },
  toggleable: {
    type: [Boolean, String],
    default: false
  },
  fixed: {
    type: String
  },
  sticky: {
    type: Boolean,
    default: false
  },
  print: {
    type: Boolean,
    default: false
  }
}, NAME_NAVBAR); // --- Main component ---
// @vue/component

export var BNavbar = /*#__PURE__*/Vue.extend({
  name: NAME_NAVBAR,
  mixins: [normalizeSlotMixin],
  provide: function provide() {
    return {
      bvNavbar: this
    };
  },
  props: props,
  computed: {
    breakpointClass: function breakpointClass() {
      var breakpoint = null;
      var xs = getBreakpoints()[0];
      var toggleable = this.toggleable;

      if (toggleable && isString(toggleable) && toggleable !== xs) {
        breakpoint = "navbar-expand-".concat(toggleable);
      } else if (toggleable === false) {
        breakpoint = 'navbar-expand';
      }

      return breakpoint;
    }
  },
  render: function render(h) {
    var _ref;

    return h(this.tag, {
      staticClass: 'navbar',
      class: [(_ref = {
        'd-print': this.print,
        'sticky-top': this.sticky
      }, _defineProperty(_ref, "navbar-".concat(this.type), this.type), _defineProperty(_ref, "bg-".concat(this.variant), this.variant), _defineProperty(_ref, "fixed-".concat(this.fixed), this.fixed), _ref), this.breakpointClass],
      attrs: {
        role: isTag(this.tag, 'nav') ? null : 'navigation'
      }
    }, [this.normalizeSlot()]);
  }
});