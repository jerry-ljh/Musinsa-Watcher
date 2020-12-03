function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_CHECKBOX_GROUP } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import formRadioCheckGroupMixin, { props as formRadioCheckGroupProps } from '../../mixins/form-radio-check-group'; // --- Props ---

export var props = makePropsConfigurable(_objectSpread(_objectSpread({}, formRadioCheckGroupProps), {}, {
  checked: {
    type: Array,
    default: function _default() {
      return [];
    }
  },
  switches: {
    // Custom switch styling
    type: Boolean,
    default: false
  }
}), NAME_FORM_CHECKBOX_GROUP); // --- Main component ---
// @vue/component

export var BFormCheckboxGroup = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_CHECKBOX_GROUP,
  // Includes render function
  mixins: [formRadioCheckGroupMixin],
  provide: function provide() {
    return {
      bvCheckGroup: this
    };
  },
  props: props,
  computed: {
    isRadioGroup: function isRadioGroup() {
      return false;
    }
  }
});