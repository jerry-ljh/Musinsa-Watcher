function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { mergeData } from '../../vue';
import { NAME_COL } from '../../constants/components';
import { RX_COL_CLASS } from '../../constants/regex';
import identity from '../../utils/identity';
import memoize from '../../utils/memoize';
import { arrayIncludes } from '../../utils/array';
import { getBreakpointsUpCached } from '../../utils/config';
import { isUndefinedOrNull } from '../../utils/inspect';
import { assign, create, keys } from '../../utils/object';
import { suffixPropName } from '../../utils/props';
import { lowerCase } from '../../utils/string'; // --- Constants ---

var ALIGN_SELF_VALUES = ['auto', 'start', 'end', 'center', 'baseline', 'stretch']; // Generates a prop object with a type of `[Boolean, String, Number]`

var boolStrNum = function boolStrNum() {
  return {
    type: [Boolean, String, Number],
    default: false
  };
}; // Generates a prop object with a type of `[String, Number]`


var strNum = function strNum() {
  return {
    type: [String, Number],
    default: null
  };
}; // Compute a breakpoint class name


var computeBreakpoint = function computeBreakpoint(type, breakpoint, val) {
  var className = type;

  if (isUndefinedOrNull(val) || val === false) {
    return undefined;
  }

  if (breakpoint) {
    className += "-".concat(breakpoint);
  } // Handling the boolean style prop when accepting [Boolean, String, Number]
  // means Vue will not convert <b-col sm></b-col> to sm: true for us.
  // Since the default is false, an empty string indicates the prop's presence.


  if (type === 'col' && (val === '' || val === true)) {
    // .col-md
    return lowerCase(className);
  } // .order-md-6


  className += "-".concat(val);
  return lowerCase(className);
}; // Memoized function for better performance on generating class names


var computeBreakpointClass = memoize(computeBreakpoint); // Cached copy of the breakpoint prop names

var breakpointPropMap = create(null); // Lazy evaled props factory for BCol

var generateProps = function generateProps() {
  // Grab the breakpoints from the cached config (exclude the '' (xs) breakpoint)
  var breakpoints = getBreakpointsUpCached().filter(identity); // Supports classes like: .col-sm, .col-md-6, .col-lg-auto

  var breakpointCol = breakpoints.reduce(function (propMap, breakpoint) {
    if (breakpoint) {
      // We filter out the '' breakpoint (xs), as making a prop name ''
      // would not work. The `cols` prop is used for `xs`
      propMap[breakpoint] = boolStrNum();
    }

    return propMap;
  }, create(null)); // Supports classes like: .offset-md-1, .offset-lg-12

  var breakpointOffset = breakpoints.reduce(function (propMap, breakpoint) {
    propMap[suffixPropName(breakpoint, 'offset')] = strNum();
    return propMap;
  }, create(null)); // Supports classes like: .order-md-1, .order-lg-12

  var breakpointOrder = breakpoints.reduce(function (propMap, breakpoint) {
    propMap[suffixPropName(breakpoint, 'order')] = strNum();
    return propMap;
  }, create(null)); // For loop doesn't need to check hasOwnProperty
  // when using an object created from null

  breakpointPropMap = assign(create(null), {
    col: keys(breakpointCol),
    offset: keys(breakpointOffset),
    order: keys(breakpointOrder)
  }); // Return the generated props

  return _objectSpread(_objectSpread(_objectSpread(_objectSpread({
    // Generic flexbox .col (xs)
    col: {
      type: Boolean,
      default: false
    },
    // .col-[1-12]|auto  (xs)
    cols: strNum()
  }, breakpointCol), {}, {
    offset: strNum()
  }, breakpointOffset), {}, {
    order: strNum()
  }, breakpointOrder), {}, {
    // Flex alignment
    alignSelf: {
      type: String,
      default: null,
      validator: function validator(value) {
        return arrayIncludes(ALIGN_SELF_VALUES, value);
      }
    },
    tag: {
      type: String,
      default: 'div'
    }
  });
}; // We do not use Vue.extend here as that would evaluate the props
// immediately, which we do not want to happen
// @vue/component


export var BCol = {
  name: NAME_COL,
  functional: true,

  get props() {
    // Allow props to be lazy evaled on first access and
    // then they become a non-getter afterwards.
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/get#Smart_self-overwriting_lazy_getters
    delete this.props; // eslint-disable-next-line no-return-assign

    return this.props = generateProps();
  },

  render: function render(h, _ref) {
    var _classList$push;

    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var classList = []; // Loop through `col`, `offset`, `order` breakpoint props

    for (var type in breakpointPropMap) {
      // Returns colSm, offset, offsetSm, orderMd, etc.
      var _keys = breakpointPropMap[type];

      for (var i = 0; i < _keys.length; i++) {
        // computeBreakpoint(col, colSm => Sm, value=[String, Number, Boolean])
        var c = computeBreakpointClass(type, _keys[i].replace(type, ''), props[_keys[i]]); // If a class is returned, push it onto the array.

        if (c) {
          classList.push(c);
        }
      }
    }

    var hasColClasses = classList.some(function (className) {
      return RX_COL_CLASS.test(className);
    });
    classList.push((_classList$push = {
      // Default to .col if no other col-{bp}-* classes generated nor `cols` specified.
      col: props.col || !hasColClasses && !props.cols
    }, _defineProperty(_classList$push, "col-".concat(props.cols), props.cols), _defineProperty(_classList$push, "offset-".concat(props.offset), props.offset), _defineProperty(_classList$push, "order-".concat(props.order), props.order), _defineProperty(_classList$push, "align-self-".concat(props.alignSelf), props.alignSelf), _classList$push));
    return h(props.tag, mergeData(data, {
      class: classList
    }), children);
  }
};