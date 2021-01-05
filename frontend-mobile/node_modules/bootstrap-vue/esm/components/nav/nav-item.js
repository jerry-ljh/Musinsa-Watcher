function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_NAV_ITEM } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { omit } from '../../utils/object';
import { BLink, props as BLinkProps } from '../link/link'; // --- Props ---

export var props = makePropsConfigurable(_objectSpread(_objectSpread({}, omit(BLinkProps, ['event', 'routerTag'])), {}, {
  linkAttrs: {
    type: Object,
    default: function _default() {}
  },
  linkClasses: {
    type: [String, Object, Array],
    default: null
  }
}), NAME_NAV_ITEM); // --- Main component ---
// @vue/component

export var BNavItem = /*#__PURE__*/Vue.extend({
  name: NAME_NAV_ITEM,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        listeners = _ref.listeners,
        children = _ref.children;
    // We transfer the listeners to the link
    delete data.on;
    return h('li', mergeData(data, {
      staticClass: 'nav-item'
    }), [h(BLink, {
      staticClass: 'nav-link',
      class: props.linkClasses,
      attrs: props.linkAttrs,
      props: props,
      on: listeners
    }, children)]);
  }
});