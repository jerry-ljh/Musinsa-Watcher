function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_SKELETON_ICON } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { BIcon } from '../../icons'; // @vue/component

export var BSkeletonIcon = /*#__PURE__*/Vue.extend({
  name: NAME_SKELETON_ICON,
  functional: true,
  props: makePropsConfigurable({
    animation: {
      type: String,
      default: 'wave'
    },
    icon: {
      type: String
    },
    iconProps: {
      type: Object,
      default: function _default() {}
    }
  }, NAME_SKELETON_ICON),
  render: function render(h, _ref) {
    var props = _ref.props;
    var icon = props.icon,
        animation = props.animation;
    var $icon = h(BIcon, {
      props: _objectSpread({
        icon: icon
      }, props.iconProps),
      staticClass: 'b-skeleton-icon'
    });
    return h('div', {
      staticClass: 'b-skeleton-icon-wrapper position-relative d-inline-block overflow-hidden',
      class: _defineProperty({}, "b-skeleton-animate-".concat(animation), animation)
    }, [$icon]);
  }
});