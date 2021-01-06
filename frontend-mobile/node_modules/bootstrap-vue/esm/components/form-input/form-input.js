function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_INPUT } from '../../constants/components';
import { arrayIncludes } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { attemptBlur } from '../../utils/dom';
import { eventOn, eventOff, eventOnOff, stopEvent } from '../../utils/events';
import formControlMixin, { props as formControlProps } from '../../mixins/form-control';
import formSelectionMixin from '../../mixins/form-selection';
import formSizeMixin, { props as formSizeProps } from '../../mixins/form-size';
import formStateMixin, { props as formStateProps } from '../../mixins/form-state';
import formTextMixin, { props as formTextProps } from '../../mixins/form-text';
import formValidityMixin from '../../mixins/form-validity';
import idMixin from '../../mixins/id';
import listenersMixin from '../../mixins/listeners'; // --- Constants ---
// Valid supported input types

var TYPES = ['text', 'password', 'email', 'number', 'url', 'tel', 'search', 'range', 'color', 'date', 'time', 'datetime', 'datetime-local', 'month', 'week']; // --- Main component ---
// @vue/component

export var BFormInput = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_INPUT,
  // Mixin order is important!
  mixins: [listenersMixin, idMixin, formControlMixin, formSizeMixin, formStateMixin, formTextMixin, formSelectionMixin, formValidityMixin],
  props: makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formSizeProps), formStateProps), formTextProps), {}, {
    // `value` prop is defined in form-text mixin
    type: {
      type: String,
      default: 'text',
      validator: function validator(type) {
        return arrayIncludes(TYPES, type);
      }
    },
    noWheel: {
      // Disable mousewheel to prevent wheel from
      // changing values (i.e. number/date)
      type: Boolean,
      default: false
    },
    min: {
      type: [String, Number] // default: null

    },
    max: {
      type: [String, Number] // default: null

    },
    step: {
      type: [String, Number] // default: null

    },
    list: {
      type: String // default: null

    }
  }), NAME_FORM_INPUT),
  computed: {
    localType: function localType() {
      // We only allow certain types
      return arrayIncludes(TYPES, this.type) ? this.type : 'text';
    },
    computedAttrs: function computedAttrs() {
      var type = this.localType,
          disabled = this.disabled,
          placeholder = this.placeholder,
          required = this.required,
          min = this.min,
          max = this.max,
          step = this.step;
      return {
        id: this.safeId(),
        name: this.name || null,
        form: this.form || null,
        type: type,
        disabled: disabled,
        placeholder: placeholder,
        required: required,
        autocomplete: this.autocomplete || null,
        readonly: this.readonly || this.plaintext,
        min: min,
        max: max,
        step: step,
        list: type !== 'password' ? this.list : null,
        'aria-required': required ? 'true' : null,
        'aria-invalid': this.computedAriaInvalid
      };
    },
    computedListeners: function computedListeners() {
      return _objectSpread(_objectSpread({}, this.bvListeners), {}, {
        input: this.onInput,
        change: this.onChange,
        blur: this.onBlur
      });
    }
  },
  watch: {
    noWheel: function noWheel(newVal) {
      this.setWheelStopper(newVal);
    }
  },
  mounted: function mounted() {
    this.setWheelStopper(this.noWheel);
  },

  /* istanbul ignore next */
  deactivated: function deactivated() {
    // Turn off listeners when keep-alive component deactivated

    /* istanbul ignore next */
    this.setWheelStopper(false);
  },

  /* istanbul ignore next */
  activated: function activated() {
    // Turn on listeners (if no-wheel) when keep-alive component activated

    /* istanbul ignore next */
    this.setWheelStopper(this.noWheel);
  },
  beforeDestroy: function beforeDestroy() {
    /* istanbul ignore next */
    this.setWheelStopper(false);
  },
  methods: {
    setWheelStopper: function setWheelStopper(on) {
      var input = this.$el; // We use native events, so that we don't interfere with propagation

      eventOnOff(on, input, 'focus', this.onWheelFocus);
      eventOnOff(on, input, 'blur', this.onWheelBlur);

      if (!on) {
        eventOff(document, 'wheel', this.stopWheel);
      }
    },
    onWheelFocus: function onWheelFocus() {
      eventOn(document, 'wheel', this.stopWheel);
    },
    onWheelBlur: function onWheelBlur() {
      eventOff(document, 'wheel', this.stopWheel);
    },
    stopWheel: function stopWheel(evt) {
      stopEvent(evt, {
        propagation: false
      });
      attemptBlur(this.$el);
    }
  },
  render: function render(h) {
    return h('input', {
      ref: 'input',
      class: this.computedClass,
      attrs: this.computedAttrs,
      domProps: {
        value: this.localValue
      },
      on: this.computedListeners
    });
  }
});