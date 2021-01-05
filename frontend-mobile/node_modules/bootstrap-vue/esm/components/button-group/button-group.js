function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_BUTTON_GROUP } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { pick } from '../../utils/object';
import { props as buttonProps } from '../button/button';
export var props = makePropsConfigurable(_objectSpread({
  vertical: {
    type: Boolean,
    default: false
  },
  size: {
    type: String // default: null

  },
  tag: {
    type: String,
    default: 'div'
  },
  ariaRole: {
    type: String,
    default: 'group'
  }
}, pick(buttonProps, ['size'])), NAME_BUTTON_GROUP); // @vue/component

export var BButtonGroup = /*#__PURE__*/Vue.extend({
  name: NAME_BUTTON_GROUP,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.tag, mergeData(data, {
      class: _defineProperty({
        'btn-group': !props.vertical,
        'btn-group-vertical': props.vertical
      }, "btn-group-".concat(props.size), props.size),
      attrs: {
        role: props.ariaRole
      }
    }), children);
  }
});