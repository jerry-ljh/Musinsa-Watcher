function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { mergeData } from '../../vue';
import { NAME_ROW } from '../../constants/components';
import identity from '../../utils/identity';
import memoize from '../../utils/memoize';
import { arrayIncludes, concat } from '../../utils/array';
import { getBreakpointsUpCached, makePropsConfigurable } from '../../utils/config';
import { create, keys } from '../../utils/object';
import { suffixPropName } from '../../utils/props';
import { lowerCase, toString, trim } from '../../utils/string';
var COMMON_ALIGNMENT = ['start', 'end', 'center']; // Generates a prop object with a type of `[String, Number]`

var strNum = function strNum() {
  return {
    type: [String, Number],
    default: null
  };
}; // Compute a `row-cols-{breakpoint}-{cols}` class name
// Memoized function for better performance on generating class names


var computeRowColsClass = memoize(function (breakpoint, cols) {
  cols = trim(toString(cols));
  return cols ? lowerCase(['row-cols', breakpoint, cols].filter(identity).join('-')) : null;
}); // Get the breakpoint name from the `rowCols` prop name
// Memoized function for better performance on extracting breakpoint names

var computeRowColsBreakpoint = memoize(function (prop) {
  return lowerCase(prop.replace('cols', ''));
}); // Cached copy of the `row-cols` breakpoint prop names
// Will be populated when the props are generated

var rowColsPropList = []; // Lazy evaled props factory for <b-row> (called only once,
// the first time the component is used)

var generateProps = function generateProps() {
  // Grab the breakpoints from the cached config (including the '' (xs) breakpoint)
  var breakpoints = getBreakpointsUpCached(); // Supports classes like: `row-cols-2`, `row-cols-md-4`, `row-cols-xl-6`

  var rowColsProps = breakpoints.reduce(function (props, breakpoint) {
    props[suffixPropName(breakpoint, 'cols')] = strNum();
    return props;
  }, create(null)); // Cache the row-cols prop names

  rowColsPropList = keys(rowColsProps); // Return the generated props

  return makePropsConfigurable(_objectSpread({
    tag: {
      type: String,
      default: 'div'
    },
    noGutters: {
      type: Boolean,
      default: false
    },
    alignV: {
      type: String,
      default: null,
      validator: function validator(value) {
        return arrayIncludes(concat(COMMON_ALIGNMENT, 'baseline', 'stretch'), value);
      }
    },
    alignH: {
      type: String,
      default: null,
      validator: function validator(value) {
        return arrayIncludes(concat(COMMON_ALIGNMENT, 'between', 'around'), value);
      }
    },
    alignContent: {
      type: String,
      default: null,
      validator: function validator(value) {
        return arrayIncludes(concat(COMMON_ALIGNMENT, 'between', 'around', 'stretch'), value);
      }
    }
  }, rowColsProps), NAME_ROW);
}; // We do not use `Vue.extend()` here as that would evaluate the props
// immediately, which we do not want to happen
// @vue/component


export var BRow = {
  name: NAME_ROW,
  functional: true,

  get props() {
    // Allow props to be lazy evaled on first access and
    // then they become a non-getter afterwards
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/get#Smart_self-overwriting_lazy_getters
    delete this.props;
    this.props = generateProps();
    return this.props;
  },

  render: function render(h, _ref) {
    var _classList$push;

    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var classList = []; // Loop through row-cols breakpoint props and generate the classes

    rowColsPropList.forEach(function (prop) {
      var c = computeRowColsClass(computeRowColsBreakpoint(prop), props[prop]); // If a class is returned, push it onto the array

      if (c) {
        classList.push(c);
      }
    });
    classList.push((_classList$push = {
      'no-gutters': props.noGutters
    }, _defineProperty(_classList$push, "align-items-".concat(props.alignV), props.alignV), _defineProperty(_classList$push, "justify-content-".concat(props.alignH), props.alignH), _defineProperty(_classList$push, "align-content-".concat(props.alignContent), props.alignContent), _classList$push));
    return h(props.tag, mergeData(data, {
      staticClass: 'row',
      class: classList
    }), children);
  }
};