function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_CAROUSEL_SLIDE } from '../../constants/components';
import { makePropsConfigurable } from '../../utils/config';
import { hasTouchSupport } from '../../utils/env';
import { stopEvent } from '../../utils/events';
import { htmlOrText } from '../../utils/html';
import { pluckProps, unprefixPropName } from '../../utils/props';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { BImg } from '../image/img'; // --- Props ---

var imgProps = {
  imgSrc: {
    type: String // default: undefined

  },
  imgAlt: {
    type: String // default: undefined

  },
  imgWidth: {
    type: [Number, String] // default: undefined

  },
  imgHeight: {
    type: [Number, String] // default: undefined

  },
  imgBlank: {
    type: Boolean,
    default: false
  },
  imgBlankColor: {
    type: String,
    default: 'transparent'
  }
};
export var props = makePropsConfigurable(_objectSpread(_objectSpread({}, imgProps), {}, {
  contentVisibleUp: {
    type: String
  },
  contentTag: {
    type: String,
    default: 'div'
  },
  caption: {
    type: String
  },
  captionHtml: {
    type: String
  },
  captionTag: {
    type: String,
    default: 'h3'
  },
  text: {
    type: String
  },
  textHtml: {
    type: String
  },
  textTag: {
    type: String,
    default: 'p'
  },
  background: {
    type: String
  }
}), NAME_CAROUSEL_SLIDE); // --- Main component ---
// @vue/component

export var BCarouselSlide = /*#__PURE__*/Vue.extend({
  name: NAME_CAROUSEL_SLIDE,
  mixins: [idMixin, normalizeSlotMixin],
  inject: {
    bvCarousel: {
      default: function _default() {
        return {
          // Explicitly disable touch if not a child of carousel
          noTouch: true
        };
      }
    }
  },
  props: props,
  computed: {
    contentClasses: function contentClasses() {
      return [this.contentVisibleUp ? 'd-none' : '', this.contentVisibleUp ? "d-".concat(this.contentVisibleUp, "-block") : ''];
    },
    computedWidth: function computedWidth() {
      // Use local width, or try parent width
      return this.imgWidth || this.bvCarousel.imgWidth || null;
    },
    computedHeight: function computedHeight() {
      // Use local height, or try parent height
      return this.imgHeight || this.bvCarousel.imgHeight || null;
    }
  },
  render: function render(h) {
    var $img = this.normalizeSlot('img');

    if (!$img && (this.imgSrc || this.imgBlank)) {
      var on = {}; // Touch support event handler

      /* istanbul ignore if: difficult to test in JSDOM */

      if (!this.bvCarousel.noTouch && hasTouchSupport) {
        on.dragstart = function (evt) {
          return stopEvent(evt, {
            propagation: false
          });
        };
      }

      $img = h(BImg, {
        props: _objectSpread(_objectSpread({}, pluckProps(imgProps, this.$props, unprefixPropName.bind(null, 'img'))), {}, {
          width: this.computedWidth,
          height: this.computedHeight,
          fluidGrow: true,
          block: true
        }),
        on: on
      });
    }

    var $contentChildren = [// Caption
    this.caption || this.captionHtml ? h(this.captionTag, {
      domProps: htmlOrText(this.captionHtml, this.caption)
    }) : false, // Text
    this.text || this.textHtml ? h(this.textTag, {
      domProps: htmlOrText(this.textHtml, this.text)
    }) : false, // Children
    this.normalizeSlot() || false];
    var $content = h();

    if ($contentChildren.some(Boolean)) {
      $content = h(this.contentTag, {
        staticClass: 'carousel-caption',
        class: this.contentClasses
      }, $contentChildren.map(function ($child) {
        return $child || h();
      }));
    }

    return h('div', {
      staticClass: 'carousel-item',
      style: {
        background: this.background || this.bvCarousel.background || null
      },
      attrs: {
        id: this.safeId(),
        role: 'listitem'
      }
    }, [$img, $content]);
  }
});