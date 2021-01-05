function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_CHECKBOX } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import looseEqual from '../../utils/loose-equal';
import looseIndexOf from '../../utils/loose-index-of';
import { isArray } from '../../utils/inspect';
import formControlMixin, { props as formControlProps } from '../../mixins/form-control';
import formRadioCheckMixin, { props as formRadioCheckProps } from '../../mixins/form-radio-check';
import formSizeMixin, { props as formSizeProps } from '../../mixins/form-size';
import formStateMixin, { props as formStateProps } from '../../mixins/form-state';
import idMixin from '../../mixins/id'; // @vue/component

export var BFormCheckbox = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_CHECKBOX,
  mixins: [formRadioCheckMixin, // Includes shared render function
  idMixin, formControlMixin, formSizeMixin, formStateMixin],
  inject: {
    bvGroup: {
      from: 'bvCheckGroup',
      default: false
    }
  },
  props: makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formRadioCheckProps), formSizeProps), formStateProps), {}, {
    value: {
      // type: [String, Number, Boolean, Object],
      default: true
    },
    uncheckedValue: {
      // type: [String, Number, Boolean, Object],
      // Not applicable in multi-check mode
      default: false
    },
    indeterminate: {
      // Not applicable in multi-check mode
      type: Boolean,
      default: false
    },
    switch: {
      // Custom switch styling
      type: Boolean,
      default: false
    },
    checked: {
      // v-model (Array when multiple checkboxes have same name)
      // type: [String, Number, Boolean, Object, Array],
      default: null
    }
  }), NAME_FORM_CHECKBOX),
  computed: {
    isChecked: function isChecked() {
      var value = this.value,
          checked = this.computedLocalChecked;
      return isArray(checked) ? looseIndexOf(checked, value) > -1 : looseEqual(checked, value);
    },
    isRadio: function isRadio() {
      return false;
    },
    isCheck: function isCheck() {
      return true;
    }
  },
  watch: {
    computedLocalChecked: function computedLocalChecked(newValue, oldValue) {
      if (!looseEqual(newValue, oldValue)) {
        this.$emit('input', newValue);
        var $input = this.$refs.input;

        if ($input) {
          this.$emit('update:indeterminate', $input.indeterminate);
        }
      }
    },
    indeterminate: function indeterminate(newVal) {
      this.setIndeterminate(newVal);
    }
  },
  mounted: function mounted() {
    // Set initial indeterminate state
    this.setIndeterminate(this.indeterminate);
  },
  methods: {
    handleChange: function handleChange(_ref) {
      var _this = this;

      var _ref$target = _ref.target,
          checked = _ref$target.checked,
          indeterminate = _ref$target.indeterminate;
      var value = this.value,
          uncheckedValue = this.uncheckedValue; // Update `computedLocalChecked`

      var localChecked = this.computedLocalChecked;

      if (isArray(localChecked)) {
        var index = looseIndexOf(localChecked, value);

        if (checked && index < 0) {
          // Add value to array
          localChecked = localChecked.concat(value);
        } else if (!checked && index > -1) {
          // Remove value from array
          localChecked = localChecked.slice(0, index).concat(localChecked.slice(index + 1));
        }
      } else {
        localChecked = checked ? value : uncheckedValue;
      }

      this.computedLocalChecked = localChecked; // Fire events in a `$nextTick()` to ensure the `v-model` is updated

      this.$nextTick(function () {
        // Change is only emitted on user interaction
        _this.$emit('change', localChecked); // If this is a child of `<form-checkbox-group>`,
        // we emit a change event on it as well


        if (_this.isGroup) {
          _this.bvGroup.$emit('change', localChecked);
        }

        _this.$emit('update:indeterminate', indeterminate);
      });
    },
    setIndeterminate: function setIndeterminate(state) {
      // Indeterminate only supported in single checkbox mode
      if (isArray(this.computedLocalChecked)) {
        state = false;
      }

      var $input = this.$refs.input;

      if ($input) {
        $input.indeterminate = state; // Emit update event to prop

        this.$emit('update:indeterminate', state);
      }
    }
  }
});