import Vue, { mergeData } from '../../vue';
import { NAME_FORM_ROW } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
export var props = makePropsConfigurable({
  tag: {
    type: String,
    default: 'div'
  }
}, NAME_FORM_ROW); // @vue/component

export var BFormRow = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_ROW,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.tag, mergeData(data, {
      staticClass: 'form-row'
    }), children);
  }
});