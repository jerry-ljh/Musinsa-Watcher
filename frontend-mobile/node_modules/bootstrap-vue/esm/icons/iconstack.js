function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../vue';
import { NAME_ICONSTACK } from '../constants/components';
import { makePropsConfigurable } from '../utils/config';
import { commonIconProps, BVIconBase } from './helpers/icon-base'; // @vue/component

export var BIconstack = /*#__PURE__*/Vue.extend({
  name: NAME_ICONSTACK,
  functional: true,
  props: makePropsConfigurable(commonIconProps, NAME_ICONSTACK),
  render: function render(h, _ref) {
    var data = _ref.data,
        props = _ref.props,
        children = _ref.children;
    return h(BVIconBase, mergeData(data, {
      staticClass: 'b-iconstack',
      props: _objectSpread(_objectSpread({}, props), {}, {
        stacked: false
      })
    }), children);
  }
});