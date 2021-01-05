function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_SELECT_OPTION_GROUP } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { SLOT_NAME_FIRST } from '../../constants/slot-names';
import { htmlOrText } from '../../utils/html';
import formOptionsMixin, { props as formOptionsProps } from '../../mixins/form-options';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { BFormSelectOption } from './form-select-option'; // @vue/component

var BFormSelectOptionGroup = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_SELECT_OPTION_GROUP,
  mixins: [normalizeSlotMixin, formOptionsMixin],
  props: makePropsConfigurable(_objectSpread(_objectSpread({}, formOptionsProps), {}, {
    label: {
      type: String,
      required: true
    }
  }), NAME_FORM_SELECT_OPTION_GROUP),
  render: function render(h) {
    var $options = this.formOptions.map(function (option, index) {
      var value = option.value,
          text = option.text,
          html = option.html,
          disabled = option.disabled;
      return h(BFormSelectOption, {
        attrs: {
          value: value,
          disabled: disabled
        },
        domProps: htmlOrText(html, text),
        key: "option_".concat(index)
      });
    });
    return h('optgroup', {
      attrs: {
        label: this.label
      }
    }, [this.normalizeSlot(SLOT_NAME_FIRST), $options, this.normalizeSlot()]);
  }
});
export { BFormSelectOptionGroup };