import { hasPassiveEventSupport } from './env';
import { isObject } from './inspect'; // --- Utils ---
// Normalize event options based on support of passive option
// Exported only for testing purposes

export var parseEventOptions = function parseEventOptions(options) {
  /* istanbul ignore else: can't test in JSDOM, as it supports passive */
  if (hasPassiveEventSupport) {
    return isObject(options) ? options : {
      capture: !!options || false
    };
  } else {
    // Need to translate to actual Boolean value
    return !!(isObject(options) ? options.capture : options);
  }
}; // Attach an event listener to an element

export var eventOn = function eventOn(el, evtName, handler, options) {
  if (el && el.addEventListener) {
    el.addEventListener(evtName, handler, parseEventOptions(options));
  }
}; // Remove an event listener from an element

export var eventOff = function eventOff(el, evtName, handler, options) {
  if (el && el.removeEventListener) {
    el.removeEventListener(evtName, handler, parseEventOptions(options));
  }
}; // Utility method to add/remove a event listener based on first argument (boolean)
// It passes all other arguments to the `eventOn()` or `eventOff` method

export var eventOnOff = function eventOnOff(on) {
  var method = on ? eventOn : eventOff;

  for (var _len = arguments.length, args = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    args[_key - 1] = arguments[_key];
  }

  method.apply(void 0, args);
}; // Utility method to prevent the default event handling and propagation

export var stopEvent = function stopEvent(evt) {
  var _ref = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {},
      _ref$preventDefault = _ref.preventDefault,
      preventDefault = _ref$preventDefault === void 0 ? true : _ref$preventDefault,
      _ref$propagation = _ref.propagation,
      propagation = _ref$propagation === void 0 ? true : _ref$propagation,
      _ref$immediatePropaga = _ref.immediatePropagation,
      immediatePropagation = _ref$immediatePropaga === void 0 ? false : _ref$immediatePropaga;

  if (preventDefault) {
    evt.preventDefault();
  }

  if (propagation) {
    evt.stopPropagation();
  }

  if (immediatePropagation) {
    evt.stopImmediatePropagation();
  }
};