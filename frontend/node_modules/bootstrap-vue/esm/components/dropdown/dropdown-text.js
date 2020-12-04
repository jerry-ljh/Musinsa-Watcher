function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_DROPDOWN_TEXT } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config'; // @vue/component

export var BDropdownText = /*#__PURE__*/Vue.extend({
  name: NAME_DROPDOWN_TEXT,
  functional: true,
  props: makePropsConfigurable({
    tag: {
      type: String,
      default: 'p'
    },
    textClass: {
      type: [String, Array, Object] // default: null

    },
    variant: {
      type: String // default: null

    }
  }, NAME_DROPDOWN_TEXT),
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var tag = props.tag,
        textClass = props.textClass,
        variant = props.variant;
    var attrs = data.attrs || {};
    data.attrs = {};
    return h('li', mergeData(data, {
      attrs: {
        role: 'presentation'
      }
    }), [h(tag, {
      staticClass: 'b-dropdown-text',
      class: [textClass, _defineProperty({}, "text-".concat(variant), variant)],
      props: props,
      attrs: attrs,
      ref: 'text'
    }, children)]);
  }
});