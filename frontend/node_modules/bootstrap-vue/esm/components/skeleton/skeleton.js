function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_SKELETON } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config'; // @vue/component

export var BSkeleton = /*#__PURE__*/Vue.extend({
  name: NAME_SKELETON,
  functional: true,
  props: makePropsConfigurable({
    animation: {
      type: String,
      default: 'wave'
    },
    type: {
      type: String,
      default: 'text'
    },
    width: {
      type: String // default: null

    },
    height: {
      type: String // default: null

    },
    size: {
      type: String // default: null

    },
    variant: {
      type: String // default: null

    }
  }, NAME_SKELETON),
  render: function render(h, _ref) {
    var _class;

    var data = _ref.data,
        props = _ref.props;
    var size = props.size,
        animation = props.animation,
        variant = props.variant;
    return h('div', mergeData(data, {
      staticClass: 'b-skeleton',
      style: {
        width: size || props.width,
        height: size || props.height
      },
      class: (_class = {}, _defineProperty(_class, "b-skeleton-".concat(props.type), true), _defineProperty(_class, "b-skeleton-animate-".concat(animation), animation), _defineProperty(_class, "bg-".concat(variant), variant), _class)
    }));
  }
});