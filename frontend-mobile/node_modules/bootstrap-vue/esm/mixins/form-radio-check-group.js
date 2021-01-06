function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { SLOT_NAME_FIRST } from '../constants/slot-names';
import looseEqual from '../utils/loose-equal';
import { makePropsConfigurable } from '../utils/config';
import { htmlOrText } from '../utils/html';
import { BFormCheckbox } from '../components/form-checkbox/form-checkbox';
import { BFormRadio } from '../components/form-radio/form-radio';
import formControlMixin, { props as formControlProps } from './form-control';
import formCustomMixin, { props as formCustomProps } from './form-custom';
import formOptionsMixin, { props as formOptionsProps } from './form-options';
import formSizeMixin, { props as formSizeProps } from './form-size';
import formStateMixin, { props as formStateProps } from './form-state';
import idMixin from './id';
import normalizeSlotMixin from './normalize-slot'; // --- Props ---

export var props = makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formOptionsProps), formSizeProps), formStateProps), formCustomProps), {}, {
  checked: {
    // type: [Boolean, Number, Object, String]
    default: null
  },
  validated: {
    type: Boolean,
    default: false
  },
  ariaInvalid: {
    type: [Boolean, String],
    default: false
  },
  stacked: {
    type: Boolean,
    default: false
  },
  buttons: {
    // Render as button style
    type: Boolean,
    default: false
  },
  buttonVariant: {
    // Only applicable when rendered with button style
    type: String // default: null

  }
}), 'formRadioCheckGroups'); // --- Mixin ---
// @vue/component

export default {
  mixins: [idMixin, normalizeSlotMixin, formControlMixin, formOptionsMixin, formSizeMixin, formStateMixin, formCustomMixin],
  model: {
    prop: 'checked',
    event: 'input'
  },
  props: props,
  data: function data() {
    return {
      localChecked: this.checked
    };
  },
  computed: {
    inline: function inline() {
      return !this.stacked;
    },
    groupName: function groupName() {
      // Checks/Radios tied to the same model must have the same name,
      // especially for ARIA accessibility.
      return this.name || this.safeId();
    },
    groupClasses: function groupClasses() {
      var inline = this.inline,
          size = this.size,
          validated = this.validated;
      var classes = {
        'was-validated': validated
      };

      if (this.buttons) {
        classes = [classes, 'btn-group-toggle', _defineProperty({
          'btn-group': inline,
          'btn-group-vertical': !inline
        }, "btn-group-".concat(size), !!size)];
      }

      return classes;
    }
  },
  watch: {
    checked: function checked(newValue) {
      if (!looseEqual(newValue, this.localChecked)) {
        this.localChecked = newValue;
      }
    },
    localChecked: function localChecked(newValue, oldValue) {
      if (!looseEqual(newValue, oldValue)) {
        this.$emit('input', newValue);
      }
    }
  },
  render: function render(h) {
    var _this = this;

    var isRadioGroup = this.isRadioGroup;
    var optionComponent = isRadioGroup ? BFormRadio : BFormCheckbox;
    var $inputs = this.formOptions.map(function (option, index) {
      var key = "BV_option_".concat(index);
      return h(optionComponent, {
        props: {
          id: _this.safeId(key),
          value: option.value,
          // Individual radios or checks can be disabled in a group
          disabled: option.disabled || false // We don't need to include these, since the input's will know they are inside here
          // name: this.groupName,
          // form: this.form || null,
          // required: Boolean(this.name && this.required)

        },
        key: key
      }, [h('span', {
        domProps: htmlOrText(option.html, option.text)
      })]);
    });
    return h('div', {
      class: [this.groupClasses, 'bv-no-focus-ring'],
      attrs: {
        id: this.safeId(),
        role: isRadioGroup ? 'radiogroup' : 'group',
        // Add `tabindex="-1"` to allow group to be focused if needed by screen readers
        tabindex: '-1',
        'aria-required': this.required ? 'true' : null,
        'aria-invalid': this.computedAriaInvalid
      }
    }, [this.normalizeSlot(SLOT_NAME_FIRST), $inputs, this.normalizeSlot()]);
  }
};