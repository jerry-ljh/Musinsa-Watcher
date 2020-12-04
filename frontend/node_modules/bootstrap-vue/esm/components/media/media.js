import Vue, { mergeData } from '../../vue';
import { NAME_MEDIA } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { SLOT_NAME_DEFAULT } from '../../constants/slot-names';
import { normalizeSlot } from '../../utils/normalize-slot';
import { BMediaAside } from './media-aside';
import { BMediaBody } from './media-body'; // --- Props ---

export var props = makePropsConfigurable({
  tag: {
    type: String,
    default: 'div'
  },
  noBody: {
    type: Boolean,
    default: false
  },
  rightAlign: {
    type: Boolean,
    default: false
  },
  verticalAlign: {
    type: String,
    default: 'top'
  }
}, NAME_MEDIA); // --- Main component ---
// @vue/component

export var BMedia = /*#__PURE__*/Vue.extend({
  name: NAME_MEDIA,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var props = _ref.props,
        data = _ref.data,
        slots = _ref.slots,
        scopedSlots = _ref.scopedSlots,
        children = _ref.children;
    var noBody = props.noBody,
        rightAlign = props.rightAlign,
        verticalAlign = props.verticalAlign;
    var $children = noBody ? children : [];

    if (!noBody) {
      var slotScope = {};
      var $slots = slots();
      var $scopedSlots = scopedSlots || {};
      $children.push(h(BMediaBody, normalizeSlot(SLOT_NAME_DEFAULT, slotScope, $scopedSlots, $slots)));
      var $aside = normalizeSlot('aside', slotScope, $scopedSlots, $slots);

      if ($aside) {
        $children[rightAlign ? 'push' : 'unshift'](h(BMediaAside, {
          props: {
            right: rightAlign,
            verticalAlign: verticalAlign
          }
        }, $aside));
      }
    }

    return h(props.tag, mergeData(data, {
      staticClass: 'media'
    }), $children);
  }
});