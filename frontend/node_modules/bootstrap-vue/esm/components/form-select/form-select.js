function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_SELECT } from '../../constants/components';
import { SLOT_NAME_FIRST } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import { from as arrayFrom } from '../../utils/array';
import { attemptBlur, attemptFocus } from '../../utils/dom';
import { htmlOrText } from '../../utils/html';
import { isArray } from '../../utils/inspect';
import formControlMixin, { props as formControlProps } from '../../mixins/form-control';
import formCustomMixin, { props as formCustomProps } from '../../mixins/form-custom';
import formSizeMixin, { props as formSizeProps } from '../../mixins/form-size';
import formStateMixin, { props as formStateProps } from '../../mixins/form-state';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import optionsMixin from './helpers/mixin-options';
import { BFormSelectOption } from './form-select-option';
import { BFormSelectOptionGroup } from './form-select-option-group'; // @vue/component

export var BFormSelect = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_SELECT,
  mixins: [idMixin, normalizeSlotMixin, formControlMixin, formSizeMixin, formStateMixin, formCustomMixin, optionsMixin],
  model: {
    prop: 'value',
    event: 'input'
  },
  props: makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formCustomProps), formSizeProps), formStateProps), {}, {
    value: {// type: [Object, Array, String, Number, Boolean],
      // default: undefined
    },
    multiple: {
      type: Boolean,
      default: false
    },
    selectSize: {
      // Browsers default size to 0, which shows 4 rows in most browsers in multiple mode
      // Size of 1 can bork out Firefox
      type: Number,
      default: 0
    },
    ariaInvalid: {
      type: [Boolean, String],
      default: false
    }
  }), NAME_FORM_SELECT),
  data: function data() {
    return {
      localValue: this.value
    };
  },
  computed: {
    computedSelectSize: function computedSelectSize() {
      // Custom selects with a size of zero causes the arrows to be hidden,
      // so dont render the size attribute in this case
      return !this.plain && this.selectSize === 0 ? null : this.selectSize;
    },
    inputClass: function inputClass() {
      return [this.plain ? 'form-control' : 'custom-select', this.size && this.plain ? "form-control-".concat(this.size) : null, this.size && !this.plain ? "custom-select-".concat(this.size) : null, this.stateClass];
    }
  },
  watch: {
    value: function value(newVal) {
      this.localValue = newVal;
    },
    localValue: function localValue() {
      this.$emit('input', this.localValue);
    }
  },
  methods: {
    focus: function focus() {
      attemptFocus(this.$refs.input);
    },
    blur: function blur() {
      attemptBlur(this.$refs.input);
    },
    onChange: function onChange(evt) {
      var _this = this;

      var target = evt.target;
      var selectedVal = arrayFrom(target.options).filter(function (o) {
        return o.selected;
      }).map(function (o) {
        return '_value' in o ? o._value : o.value;
      });
      this.localValue = target.multiple ? selectedVal : selectedVal[0];
      this.$nextTick(function () {
        _this.$emit('change', _this.localValue);
      });
    }
  },
  render: function render(h) {
    var name = this.name,
        disabled = this.disabled,
        required = this.required,
        size = this.computedSelectSize,
        value = this.localValue;
    var $options = this.formOptions.map(function (option, index) {
      var value = option.value,
          label = option.label,
          options = option.options,
          disabled = option.disabled;
      var key = "option_".concat(index);
      return isArray(options) ? h(BFormSelectOptionGroup, {
        props: {
          label: label,
          options: options
        },
        key: key
      }) : h(BFormSelectOption, {
        props: {
          value: value,
          disabled: disabled
        },
        domProps: htmlOrText(option.html, option.text),
        key: key
      });
    });
    return h('select', {
      class: this.inputClass,
      attrs: {
        id: this.safeId(),
        name: name,
        form: this.form || null,
        multiple: this.multiple || null,
        size: size,
        disabled: disabled,
        required: required,
        'aria-required': required ? 'true' : null,
        'aria-invalid': this.computedAriaInvalid
      },
      on: {
        change: this.onChange
      },
      directives: [{
        name: 'model',
        value: value
      }],
      ref: 'input'
    }, [this.normalizeSlot(SLOT_NAME_FIRST), $options, this.normalizeSlot()]);
  }
});