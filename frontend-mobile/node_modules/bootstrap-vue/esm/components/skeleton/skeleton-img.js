function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_SKELETON_IMG } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { BAspect } from '../aspect';
import { BSkeleton } from './skeleton'; // @vue/component

export var BSkeletonImg = /*#__PURE__*/Vue.extend({
  name: NAME_SKELETON_IMG,
  functional: true,
  props: makePropsConfigurable({
    animation: {
      type: String
    },
    aspect: {
      type: String,
      default: '16:9'
    },
    noAspect: {
      type: Boolean,
      default: false
    },
    height: {
      type: String
    },
    width: {
      type: String
    },
    variant: {
      type: String
    },
    cardImg: {
      type: String
    }
  }, NAME_SKELETON_IMG),
  render: function render(h, _ref) {
    var props = _ref.props;
    var aspect = props.aspect,
        width = props.width,
        height = props.height,
        animation = props.animation,
        variant = props.variant,
        cardImg = props.cardImg;
    var $img = h(BSkeleton, {
      props: {
        type: 'img',
        width: width,
        height: height,
        animation: animation,
        variant: variant
      },
      class: _defineProperty({}, "card-img-".concat(cardImg), cardImg)
    });
    return props.noAspect ? $img : h(BAspect, {
      props: {
        aspect: aspect
      }
    }, [$img]);
  }
});