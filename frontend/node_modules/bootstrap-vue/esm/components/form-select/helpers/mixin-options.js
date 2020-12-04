function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import get from '../../../utils/get';
import { makePropsConfigurable } from '../../../utils/config';
import { isNull, isPlainObject, isUndefined } from '../../../utils/inspect';
import formOptionsMixin, { props as formOptionsProps } from '../../../mixins/form-options'; // @vue/component

export default {
  mixins: [formOptionsMixin],
  props: makePropsConfigurable(_objectSpread(_objectSpread({}, formOptionsProps), {}, {
    labelField: {
      type: String,
      default: 'label'
    },
    optionsField: {
      type: String,
      default: 'options'
    }
  }), 'formOptions'),
  methods: {
    normalizeOption: function normalizeOption(option) {
      var key = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;

      // When the option is an object, normalize it
      if (isPlainObject(option)) {
        var value = get(option, this.valueField);
        var text = get(option, this.textField);
        var options = get(option, this.optionsField, null); // When it has options, create an `<optgroup>` object

        if (!isNull(options)) {
          return {
            label: String(get(option, this.labelField) || text),
            options: this.normalizeOptions(options)
          };
        } // Otherwise create an `<option>` object


        return {
          value: isUndefined(value) ? key || text : value,
          text: String(isUndefined(text) ? key : text),
          html: get(option, this.htmlField),
          disabled: Boolean(get(option, this.disabledField))
        };
      } // Otherwise create an `<option>` object from the given value


      return {
        value: key || option,
        text: String(option),
        disabled: false
      };
    }
  }
};