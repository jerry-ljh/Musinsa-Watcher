import Vue, { mergeData } from '../../vue';
import { NAME_FORM_INVALID_FEEDBACK } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
export var props = makePropsConfigurable({
  id: {
    type: String // default: null

  },
  tag: {
    type: String,
    default: 'div'
  },
  tooltip: {
    type: Boolean,
    default: false
  },
  forceShow: {
    type: Boolean,
    default: false
  },
  state: {
    // Tri-state prop: `true`, `false`, or `null`
    type: Boolean,
    default: null
  },
  ariaLive: {
    type: String // default: null

  },
  role: {
    type: String // default: null

  }
}, NAME_FORM_INVALID_FEEDBACK); // @vue/component

export var BFormInvalidFeedback = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_INVALID_FEEDBACK,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        children = _ref.children;
    var show = props.forceShow === true || props.state === false;
    return h(props.tag, mergeData(data, {
      class: {
        'invalid-feedback': !props.tooltip,
        'invalid-tooltip': props.tooltip,
        'd-block': show
      },
      attrs: {
        id: props.id || null,
        role: props.role || null,
        'aria-live': props.ariaLive || null,
        'aria-atomic': props.ariaLive ? 'true' : null
      }
    }), children);
  }
});