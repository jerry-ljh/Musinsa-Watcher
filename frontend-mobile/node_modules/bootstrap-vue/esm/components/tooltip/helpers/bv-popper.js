// Base on-demand component for tooltip / popover templates
//
// Currently:
//   Responsible for positioning and transitioning the template
//   Templates are only instantiated when shown, and destroyed when hidden
//
import Popper from 'popper.js';
import Vue from '../../../vue';
import { NAME_POPPER } from '../../../constants/components';
import { BVTransition } from '../../../utils/bv-transition';
import { getCS, requestAF, select } from '../../../utils/dom';
import { toFloat } from '../../../utils/number';
import { HTMLElement, SVGElement } from '../../../utils/safe-types';
var AttachmentMap = {
  AUTO: 'auto',
  TOP: 'top',
  RIGHT: 'right',
  BOTTOM: 'bottom',
  LEFT: 'left',
  TOPLEFT: 'top',
  TOPRIGHT: 'top',
  RIGHTTOP: 'right',
  RIGHTBOTTOM: 'right',
  BOTTOMLEFT: 'bottom',
  BOTTOMRIGHT: 'bottom',
  LEFTTOP: 'left',
  LEFTBOTTOM: 'left'
};
var OffsetMap = {
  AUTO: 0,
  TOPLEFT: -1,
  TOP: 0,
  TOPRIGHT: +1,
  RIGHTTOP: -1,
  RIGHT: 0,
  RIGHTBOTTOM: +1,
  BOTTOMLEFT: -1,
  BOTTOM: 0,
  BOTTOMRIGHT: +1,
  LEFTTOP: -1,
  LEFT: 0,
  LEFTBOTTOM: +1
}; // @vue/component

export var BVPopper = /*#__PURE__*/Vue.extend({
  name: NAME_POPPER,
  props: {
    target: {
      // Element that the tooltip/popover is positioned relative to
      type: [HTMLElement, SVGElement] // default: null

    },
    placement: {
      type: String,
      default: 'top'
    },
    fallbackPlacement: {
      type: [String, Array],
      default: 'flip'
    },
    offset: {
      type: Number,
      default: 0
    },
    boundary: {
      // 'scrollParent', 'viewport', 'window', or Element
      type: [String, HTMLElement],
      default: 'scrollParent'
    },
    boundaryPadding: {
      // Tooltip/popover will try and stay away from
      // boundary edge by this many pixels
      type: Number,
      default: 5
    },
    arrowPadding: {
      // The minimum distance (in `px`) from the edge of the
      // tooltip/popover that the arrow can be positioned
      type: Number,
      default: 6
    }
  },
  data: function data() {
    return {
      // reactive props set by parent
      noFade: false,
      // State related data
      localShow: true,
      attachment: this.getAttachment(this.placement)
    };
  },
  computed: {
    /* istanbul ignore next */
    templateType: function templateType() {
      // Overridden by template component
      return 'unknown';
    },
    popperConfig: function popperConfig() {
      var _this = this;

      var placement = this.placement;
      return {
        placement: this.getAttachment(placement),
        modifiers: {
          offset: {
            offset: this.getOffset(placement)
          },
          flip: {
            behavior: this.fallbackPlacement
          },
          // `arrow.element` can also be a reference to an HTML Element
          // maybe we should make this a `$ref` in the templates?
          arrow: {
            element: '.arrow'
          },
          preventOverflow: {
            padding: this.boundaryPadding,
            boundariesElement: this.boundary
          }
        },
        onCreate: function onCreate(data) {
          // Handle flipping arrow classes
          if (data.originalPlacement !== data.placement) {
            /* istanbul ignore next: can't test in JSDOM */
            _this.popperPlacementChange(data);
          }
        },
        onUpdate: function onUpdate(data) {
          // Handle flipping arrow classes
          _this.popperPlacementChange(data);
        }
      };
    }
  },
  created: function created() {
    var _this2 = this;

    // Note: We are created on-demand, and should be guaranteed that
    // DOM is rendered/ready by the time the created hook runs
    this.$_popper = null; // Ensure we show as we mount

    this.localShow = true; // Create popper instance before shown

    this.$on('show', function (el) {
      _this2.popperCreate(el);
    }); // Self destruct handler

    var handleDestroy = function handleDestroy() {
      _this2.$nextTick(function () {
        // In a `requestAF()` to release control back to application
        requestAF(function () {
          _this2.$destroy();
        });
      });
    }; // Self destruct if parent destroyed


    this.$parent.$once('hook:destroyed', handleDestroy); // Self destruct after hidden

    this.$once('hidden', handleDestroy);
  },
  beforeMount: function beforeMount() {
    // Ensure that the attachment position is correct before mounting
    // as our propsData is added after `new Template({...})`
    this.attachment = this.getAttachment(this.placement);
  },
  updated: function updated() {
    // Update popper if needed
    // TODO: Should this be a watcher on `this.popperConfig` instead?
    this.updatePopper();
  },
  beforeDestroy: function beforeDestroy() {
    this.destroyPopper();
  },
  destroyed: function destroyed() {
    // Make sure template is removed from DOM
    var el = this.$el;
    el && el.parentNode && el.parentNode.removeChild(el);
  },
  methods: {
    // "Public" method to trigger hide template
    hide: function hide() {
      this.localShow = false;
    },
    // Private
    getAttachment: function getAttachment(placement) {
      return AttachmentMap[String(placement).toUpperCase()] || 'auto';
    },
    getOffset: function getOffset(placement) {
      if (!this.offset) {
        // Could set a ref for the arrow element
        var arrow = this.$refs.arrow || select('.arrow', this.$el);
        var arrowOffset = toFloat(getCS(arrow).width, 0) + toFloat(this.arrowPadding, 0);

        switch (OffsetMap[String(placement).toUpperCase()] || 0) {
          /* istanbul ignore next: can't test in JSDOM */
          case +1:
            /* istanbul ignore next: can't test in JSDOM */
            return "+50%p - ".concat(arrowOffset, "px");

          /* istanbul ignore next: can't test in JSDOM */

          case -1:
            /* istanbul ignore next: can't test in JSDOM */
            return "-50%p + ".concat(arrowOffset, "px");

          default:
            return 0;
        }
      }
      /* istanbul ignore next */


      return this.offset;
    },
    popperCreate: function popperCreate(el) {
      this.destroyPopper(); // We use `el` rather than `this.$el` just in case the original
      // mountpoint root element type was changed by the template

      this.$_popper = new Popper(this.target, el, this.popperConfig);
    },
    destroyPopper: function destroyPopper() {
      this.$_popper && this.$_popper.destroy();
      this.$_popper = null;
    },
    updatePopper: function updatePopper() {
      this.$_popper && this.$_popper.scheduleUpdate();
    },
    popperPlacementChange: function popperPlacementChange(data) {
      // Callback used by popper to adjust the arrow placement
      this.attachment = this.getAttachment(data.placement);
    },

    /* istanbul ignore next */
    renderTemplate: function renderTemplate(h) {
      // Will be overridden by templates
      return h('div');
    }
  },
  render: function render(h) {
    var _this3 = this;

    // Note: `show` and 'fade' classes are only appled during transition
    return h(BVTransition, {
      // Transitions as soon as mounted
      props: {
        appear: true,
        noFade: this.noFade
      },
      on: {
        // Events used by parent component/instance
        beforeEnter: function beforeEnter(el) {
          return _this3.$emit('show', el);
        },
        afterEnter: function afterEnter(el) {
          return _this3.$emit('shown', el);
        },
        beforeLeave: function beforeLeave(el) {
          return _this3.$emit('hide', el);
        },
        afterLeave: function afterLeave(el) {
          return _this3.$emit('hidden', el);
        }
      }
    }, [this.localShow ? this.renderTemplate(h) : h()]);
  }
});