function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_NAVBAR_NAV } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { pluckProps } from '../../utils/props';
import { props as BNavProps } from '../nav/nav'; // -- Constants --

export var props = makePropsConfigurable(pluckProps(['tag', 'fill', 'justified', 'align', 'small'], BNavProps), NAME_NAVBAR_NAV); // -- Utils --

var computeJustifyContent = function computeJustifyContent(value) {
  // Normalize value
  value = value === 'left' ? 'start' : value === 'right' ? 'end' : value;
  return "justify-content-".concat(value);
}; // @vue/component


export var BNavbarNav = /*#__PURE__*/Vue.extend({
  name: NAME_NAVBAR_NAV,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var _class;

    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.tag, mergeData(data, {
      staticClass: 'navbar-nav',
      class: (_class = {
        'nav-fill': props.fill,
        'nav-justified': props.justified
      }, _defineProperty(_class, computeJustifyContent(props.align), props.align), _defineProperty(_class, "small", props.small), _class)
    }), children);
  }
});