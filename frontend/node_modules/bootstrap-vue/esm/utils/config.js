function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../vue';
import { DEFAULT_BREAKPOINT, PROP_NAME } from '../constants/config';
import cloneDeep from './clone-deep';
import memoize from './memoize';
import { isFunction } from './inspect';
import { keys } from './object'; // --- Constants ---

var VueProto = Vue.prototype; // --- Getter methods ---
// All methods return a deep clone (immutable) copy of the config value,
// to prevent mutation of the user config object
// Get the current config

export var getConfig = function getConfig() {
  var bvConfig = VueProto[PROP_NAME];
  return bvConfig ? bvConfig.getConfig() : {};
}; // Method to grab a config value based on a dotted/array notation key

export var getConfigValue = function getConfigValue(key) {
  var defaultValue = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : undefined;
  var bvConfig = VueProto[PROP_NAME];
  return bvConfig ? bvConfig.getConfigValue(key, defaultValue) : cloneDeep(defaultValue);
}; // Method to grab a config value for a particular component

export var getComponentConfig = function getComponentConfig(key) {
  var propKey = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;
  var defaultValue = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : undefined;
  // Return the particular config value for key if specified,
  // otherwise we return the full config (or an empty object if not found)
  return propKey ? getConfigValue("".concat(key, ".").concat(propKey), defaultValue) : getConfigValue(key, {});
}; // Get all breakpoint names

export var getBreakpoints = function getBreakpoints() {
  return getConfigValue('breakpoints', DEFAULT_BREAKPOINT);
}; // Private method for caching breakpoint names

var _getBreakpointsCached = memoize(function () {
  return getBreakpoints();
}); // Get all breakpoint names (cached)


export var getBreakpointsCached = function getBreakpointsCached() {
  return cloneDeep(_getBreakpointsCached());
}; // Get breakpoints with the smallest breakpoint set as ''
// Useful for components that create breakpoint specific props

export var getBreakpointsUp = function getBreakpointsUp() {
  var breakpoints = getBreakpoints();
  breakpoints[0] = '';
  return breakpoints;
}; // Get breakpoints with the smallest breakpoint set as '' (cached)
// Useful for components that create breakpoint specific props

export var getBreakpointsUpCached = memoize(function () {
  var breakpoints = getBreakpointsCached();
  breakpoints[0] = '';
  return breakpoints;
}); // Get breakpoints with the largest breakpoint set as ''

export var getBreakpointsDown = function getBreakpointsDown() {
  var breakpoints = getBreakpoints();
  breakpoints[breakpoints.length - 1] = '';
  return breakpoints;
}; // Get breakpoints with the largest breakpoint set as '' (cached)
// Useful for components that create breakpoint specific props

/* istanbul ignore next: we don't use this method anywhere, yet */

export var getBreakpointsDownCached = function getBreakpointsDownCached() {
  var breakpoints = getBreakpointsCached();
  breakpoints[breakpoints.length - 1] = '';
  return breakpoints;
}; // Make a props object configurable by global configuration
// Replaces the current `default` key of each prop with a `getComponentConfig()`
// call that falls back to the current default value of the prop

export var makePropsConfigurable = function makePropsConfigurable(props, componentKey) {
  return keys(props).reduce(function (result, prop) {
    var currentProp = props[prop];
    var defaultValue = currentProp.default;
    result[prop] = _objectSpread(_objectSpread({}, cloneDeep(currentProp)), {}, {
      default: function _default() {
        return getComponentConfig(componentKey, prop, isFunction(defaultValue) ? defaultValue() : defaultValue);
      }
    });
    return result;
  }, {});
};