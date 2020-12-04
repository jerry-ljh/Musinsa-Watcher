function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _unsupportedIterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _iterableToArray(iter) { if (typeof Symbol !== "undefined" && Symbol.iterator in Object(iter)) return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) return _arrayLikeToArray(arr); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_CARD_BODY } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { copyProps, pluckProps, prefixPropName } from '../../utils/props';
import { props as cardProps } from '../../mixins/card';
import { BCardTitle, props as titleProps } from './card-title';
import { BCardSubTitle, props as subTitleProps } from './card-sub-title';
export var props = makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, copyProps(cardProps, prefixPropName.bind(null, 'body'))), {}, {
  bodyClass: {
    type: [String, Object, Array] // default: null

  }
}, titleProps), subTitleProps), {}, {
  overlay: {
    type: Boolean,
    default: false
  }
}), NAME_CARD_BODY); // @vue/component

export var BCardBody = /*#__PURE__*/Vue.extend({
  name: NAME_CARD_BODY,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var _ref2;

    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var cardTitle = h();
    var cardSubTitle = h();
    var cardContent = children || [h()];

    if (props.title) {
      cardTitle = h(BCardTitle, {
        props: pluckProps(titleProps, props)
      });
    }

    if (props.subTitle) {
      cardSubTitle = h(BCardSubTitle, {
        props: pluckProps(subTitleProps, props),
        class: ['mb-2']
      });
    }

    return h(props.bodyTag, mergeData(data, {
      staticClass: 'card-body',
      class: [(_ref2 = {
        'card-img-overlay': props.overlay
      }, _defineProperty(_ref2, "bg-".concat(props.bodyBgVariant), props.bodyBgVariant), _defineProperty(_ref2, "border-".concat(props.bodyBorderVariant), props.bodyBorderVariant), _defineProperty(_ref2, "text-".concat(props.bodyTextVariant), props.bodyTextVariant), _ref2), props.bodyClass || {}]
    }), [cardTitle, cardSubTitle].concat(_toConsumableArray(cardContent)));
  }
});