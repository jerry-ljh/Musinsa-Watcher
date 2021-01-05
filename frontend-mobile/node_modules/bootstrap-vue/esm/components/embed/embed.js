function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_EMBED } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { arrayIncludes } from '../../utils/array'; // --- Constants ---

var TYPES = ['iframe', 'embed', 'video', 'object', 'img', 'b-img', 'b-img-lazy']; // --- Props ---

export var props = makePropsConfigurable({
  type: {
    type: String,
    default: 'iframe',
    validator: function validator(value) {
      return arrayIncludes(TYPES, value);
    }
  },
  tag: {
    type: String,
    default: 'div'
  },
  aspect: {
    type: String,
    default: '16by9'
  }
}, NAME_EMBED); // --- Main component ---
// @vue/component

export var BEmbed = /*#__PURE__*/Vue.extend({
  name: NAME_EMBED,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.tag, {
      ref: data.ref,
      staticClass: 'embed-responsive',
      class: _defineProperty({}, "embed-responsive-".concat(props.aspect), props.aspect)
    }, [h(props.type, mergeData(data, {
      ref: '',
      staticClass: 'embed-responsive-item'
    }), children)]);
  }
});