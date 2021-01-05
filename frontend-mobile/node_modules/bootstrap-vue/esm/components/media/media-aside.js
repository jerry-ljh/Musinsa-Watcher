function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_MEDIA_ASIDE } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config'; // --- Props ---

export var props = makePropsConfigurable({
  tag: {
    type: String,
    default: 'div'
  },
  right: {
    type: Boolean,
    default: false
  },
  verticalAlign: {
    type: String,
    default: 'top'
  }
}, NAME_MEDIA_ASIDE); // --- Main component ---
// @vue/component

export var BMediaAside = /*#__PURE__*/Vue.extend({
  name: NAME_MEDIA_ASIDE,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var verticalAlign = props.verticalAlign;
    var align = verticalAlign === 'top' ? 'start' : verticalAlign === 'bottom' ? 'end' :
    /* istanbul ignore next */
    verticalAlign;
    return h(props.tag, mergeData(data, {
      staticClass: 'media-aside',
      class: _defineProperty({
        'media-aside-right': props.right
      }, "align-self-".concat(align), align)
    }), children);
  }
});