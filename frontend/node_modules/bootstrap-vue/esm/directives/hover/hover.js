// v-b-hover directive
import { EVENT_OPTIONS_NO_CAPTURE } from '../../constants/events';
import { isBrowser } from '../../utils/env';
import { eventOnOff } from '../../utils/events';
import { isFunction } from '../../utils/inspect'; // --- Constants ---

var PROP = '__BV_hover_handler__';
var MOUSEENTER = 'mouseenter';
var MOUSELEAVE = 'mouseleave'; // --- Utility methods ---

var createListener = function createListener(handler) {
  var listener = function listener(evt) {
    handler(evt.type === MOUSEENTER, evt);
  };

  listener.fn = handler;
  return listener;
};

var updateListeners = function updateListeners(on, el, listener) {
  eventOnOff(on, el, MOUSEENTER, listener, EVENT_OPTIONS_NO_CAPTURE);
  eventOnOff(on, el, MOUSELEAVE, listener, EVENT_OPTIONS_NO_CAPTURE);
}; // --- Directive bind/unbind/update handler ---


var directive = function directive(el, _ref) {
  var _ref$value = _ref.value,
      handler = _ref$value === void 0 ? null : _ref$value;

  if (isBrowser) {
    var listener = el[PROP];
    var hasListener = isFunction(listener);
    var handlerChanged = !(hasListener && listener.fn === handler);

    if (hasListener && handlerChanged) {
      updateListeners(false, el, listener);
      delete el[PROP];
    }

    if (isFunction(handler) && handlerChanged) {
      el[PROP] = createListener(handler);
      updateListeners(true, el, el[PROP]);
    }
  }
}; // VBHover directive


export var VBHover = {
  bind: directive,
  componentUpdated: directive,
  unbind: function unbind(el) {
    directive(el, {
      value: null
    });
  }
};