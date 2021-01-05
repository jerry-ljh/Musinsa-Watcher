function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { makePropsConfigurable } from '../utils/config';
import { attemptFocus, isVisible, matches, requestAF, select } from '../utils/dom'; // --- Constants ---

var SELECTOR = 'input, textarea, select'; // --- Props ---

export var props = _objectSpread({
  id: {
    type: String // default: undefined

  },
  name: {
    type: String // default: undefined

  }
}, makePropsConfigurable({
  disabled: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  form: {
    type: String // default: null

  },
  autofocus: {
    type: Boolean,
    default: false
  }
}, 'formControls')); // --- Mixin ---
// @vue/component

export default {
  props: props,
  mounted: function mounted() {
    this.handleAutofocus();
  },

  /* istanbul ignore next */
  activated: function activated() {
    this.handleAutofocus();
  },
  methods: {
    handleAutofocus: function handleAutofocus() {
      var _this = this;

      this.$nextTick(function () {
        requestAF(function () {
          var el = _this.$el;

          if (_this.autofocus && isVisible(el)) {
            if (!matches(el, SELECTOR)) {
              el = select(SELECTOR, el);
            }

            attemptFocus(el);
          }
        });
      });
    }
  }
};