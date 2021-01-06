function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_INPUT_GROUP_ADDON } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { BInputGroupText } from './input-group-text';
export var commonProps = {
  id: {
    type: String,
    default: null
  },
  tag: {
    type: String,
    default: 'div'
  },
  isText: {
    type: Boolean,
    default: false
  }
}; // @vue/component

export var BInputGroupAddon = /*#__PURE__*/Vue.extend({
  name: NAME_INPUT_GROUP_ADDON,
  functional: true,
  props: makePropsConfigurable(_objectSpread(_objectSpread({}, commonProps), {}, {
    append: {
      type: Boolean,
      default: false
    }
  }), NAME_INPUT_GROUP_ADDON),
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.tag, mergeData(data, {
      class: {
        'input-group-append': props.append,
        'input-group-prepend': !props.append
      },
      attrs: {
        id: props.id
      }
    }), props.isText ? [h(BInputGroupText, children)] : children);
  }
});