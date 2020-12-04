function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_DROPDOWN_ITEM_BUTTON } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import attrsMixin from '../../mixins/attrs';
import normalizeSlotMixin from '../../mixins/normalize-slot';
export var props = makePropsConfigurable({
  active: {
    type: Boolean,
    default: false
  },
  activeClass: {
    type: String,
    default: 'active'
  },
  buttonClass: {
    type: [String, Array, Object] // default: null

  },
  disabled: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String // default: null

  }
}, NAME_DROPDOWN_ITEM_BUTTON); // @vue/component

export var BDropdownItemButton = /*#__PURE__*/Vue.extend({
  name: NAME_DROPDOWN_ITEM_BUTTON,
  mixins: [attrsMixin, normalizeSlotMixin],
  inject: {
    bvDropdown: {
      default: null
    }
  },
  inheritAttrs: false,
  props: props,
  computed: {
    computedAttrs: function computedAttrs() {
      return _objectSpread(_objectSpread({}, this.bvAttrs), {}, {
        role: 'menuitem',
        type: 'button',
        disabled: this.disabled
      });
    }
  },
  methods: {
    closeDropdown: function closeDropdown() {
      if (this.bvDropdown) {
        this.bvDropdown.hide(true);
      }
    },
    onClick: function onClick(evt) {
      this.$emit('click', evt);
      this.closeDropdown();
    }
  },
  render: function render(h) {
    var _ref;

    return h('li', {
      attrs: {
        role: 'presentation'
      }
    }, [h('button', {
      staticClass: 'dropdown-item',
      class: [this.buttonClass, (_ref = {}, _defineProperty(_ref, this.activeClass, this.active), _defineProperty(_ref, "text-".concat(this.variant), this.variant && !(this.active || this.disabled)), _ref)],
      attrs: this.computedAttrs,
      on: {
        click: this.onClick
      },
      ref: 'button'
    }, this.normalizeSlot())]);
  }
});