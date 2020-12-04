function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

import { File } from './safe-types'; // --- Convenience inspection utilities ---

export var toType = function toType(val) {
  return _typeof(val);
};
export var toRawType = function toRawType(val) {
  return Object.prototype.toString.call(val).slice(8, -1);
};
export var toRawTypeLC = function toRawTypeLC(val) {
  return toRawType(val).toLowerCase();
};
export var isUndefined = function isUndefined(val) {
  return val === undefined;
};
export var isNull = function isNull(val) {
  return val === null;
};
export var isEmptyString = function isEmptyString(val) {
  return val === '';
};
export var isUndefinedOrNull = function isUndefinedOrNull(val) {
  return isUndefined(val) || isNull(val);
};
export var isUndefinedOrNullOrEmpty = function isUndefinedOrNullOrEmpty(val) {
  return isUndefinedOrNull(val) || isEmptyString(val);
};
export var isFunction = function isFunction(val) {
  return toType(val) === 'function';
};
export var isBoolean = function isBoolean(val) {
  return toType(val) === 'boolean';
};
export var isString = function isString(val) {
  return toType(val) === 'string';
};
export var isNumber = function isNumber(val) {
  return toType(val) === 'number';
}; // Is a value number like (i.e. a number or a number as string)

export var isNumeric = function isNumeric(value) {
  return !isNaN(parseInt(value, 10));
};
export var isPrimitive = function isPrimitive(val) {
  return isBoolean(val) || isString(val) || isNumber(val);
};
export var isArray = function isArray(val) {
  return Array.isArray(val);
}; // Quick object check
// This is primarily used to tell Objects from primitive values
// when we know the value is a JSON-compliant type
// Note object could be a complex type like array, Date, etc.

export var isObject = function isObject(obj) {
  return obj !== null && _typeof(obj) === 'object';
}; // Strict object type check
// Only returns true for plain JavaScript objects

export var isPlainObject = function isPlainObject(obj) {
  return Object.prototype.toString.call(obj) === '[object Object]';
};
export var isDate = function isDate(val) {
  return val instanceof Date;
};
export var isEvent = function isEvent(val) {
  return val instanceof Event;
};
export var isFile = function isFile(val) {
  return val instanceof File;
};
export var isRegExp = function isRegExp(val) {
  return toRawType(val) === 'RegExp';
};
export var isPromise = function isPromise(val) {
  return !isUndefinedOrNull(val) && isFunction(val.then) && isFunction(val.catch);
};