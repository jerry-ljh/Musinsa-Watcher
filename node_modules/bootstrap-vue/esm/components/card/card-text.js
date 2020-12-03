import Vue, { mergeData } from '../../vue';
import { NAME_CARD_TEXT } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
export var props = makePropsConfigurable({
  textTag: {
    type: String,
    default: 'p'
  }
}, NAME_CARD_TEXT); // @vue/component

export var BCardText = /*#__PURE__*/Vue.extend({
  name: NAME_CARD_TEXT,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    return h(props.textTag, mergeData(data, {
      staticClass: 'card-text'
    }), children);
  }
});