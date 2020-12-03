import Vue, { mergeData } from '../../vue';
import { NAME_FORM_SELECT_OPTION } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
export var props = makePropsConfigurable({
  value: {
    // type: [String, Number, Boolean, Object],
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
}, NAME_FORM_SELECT_OPTION); // @vue/component

export var BFormSelectOption = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_SELECT_OPTION,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var value = props.value,
        disabled = props.disabled;
    return h('option', mergeData(data, {
      attrs: {
        disabled: disabled
      },
      domProps: {
        value: value
      }
    }), children);
  }
});