function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_SPINNER } from '../../constants/components';
import { SLOT_NAME_LABEL } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import { normalizeSlot } from '../../utils/normalize-slot'; // @vue/component

export var BSpinner = /*#__PURE__*/Vue.extend({
  name: NAME_SPINNER,
  functional: true,
  props: makePropsConfigurable({
    type: {
      type: String,
      default: 'border' // SCSS currently supports 'border' or 'grow'

    },
    label: {
      type: String // default: null

    },
    variant: {
      type: String // default: undefined

    },
    small: {
      type: Boolean,
      default: false
    },
    role: {
      type: String,
      default: 'status'
    },
    tag: {
      type: String,
      default: 'span'
    }
  }, NAME_SPINNER),
  render: function render(h, _ref) {
    var _class;

    var props = _ref.props,
        data = _ref.data,
        slots = _ref.slots,
        scopedSlots = _ref.scopedSlots;
    var $slots = slots();
    var $scopedSlots = scopedSlots || {};
    var label = normalizeSlot(SLOT_NAME_LABEL, {}, $scopedSlots, $slots) || props.label;

    if (label) {
      label = h('span', {
        staticClass: 'sr-only'
      }, label);
    }

    return h(props.tag, mergeData(data, {
      attrs: {
        role: label ? props.role || 'status' : null,
        'aria-hidden': label ? null : 'true'
      },
      class: (_class = {}, _defineProperty(_class, "spinner-".concat(props.type), props.type), _defineProperty(_class, "spinner-".concat(props.type, "-sm"), props.small), _defineProperty(_class, "text-".concat(props.variant), props.variant), _class)
    }), [label || h()]);
  }
});