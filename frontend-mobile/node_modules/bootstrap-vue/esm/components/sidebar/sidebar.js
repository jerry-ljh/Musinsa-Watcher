function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_SIDEBAR } from '../../constants/components';
import { CODE_ESC } from '../../constants/key-codes';
import { SLOT_NAME_DEFAULT, SLOT_NAME_FOOTER, SLOT_NAME_TITLE } from '../../constants/slot-names';
import BVTransition from '../../utils/bv-transition';
import { attemptFocus, contains, getActiveElement, getTabables } from '../../utils/dom';
import { makePropsConfigurable } from '../../utils/config';
import { isBrowser } from '../../utils/env';
import { toString } from '../../utils/string';
import attrsMixin from '../../mixins/attrs';
import idMixin from '../../mixins/id';
import listenOnRootMixin from '../../mixins/listen-on-root';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { EVENT_TOGGLE, EVENT_STATE, EVENT_STATE_REQUEST, EVENT_STATE_SYNC } from '../../directives/toggle/toggle';
import { BButtonClose } from '../button/button-close';
import { BIconX } from '../../icons/icons'; // --- Constants ---

var CLASS_NAME = 'b-sidebar'; // --- Render methods ---

var renderHeaderTitle = function renderHeaderTitle(h, ctx) {
  // Render a empty `<span>` when to title was provided
  var title = ctx.computedTile;

  if (!title) {
    return h('span');
  }

  return h('strong', {
    attrs: {
      id: ctx.safeId('__title__')
    }
  }, [title]);
};

var renderHeaderClose = function renderHeaderClose(h, ctx) {
  if (ctx.noHeaderClose) {
    return h();
  }

  var closeLabel = ctx.closeLabel,
      textVariant = ctx.textVariant,
      hide = ctx.hide;
  return h(BButtonClose, {
    ref: 'close-button',
    props: {
      ariaLabel: closeLabel,
      textVariant: textVariant
    },
    on: {
      click: hide
    }
  }, [ctx.normalizeSlot('header-close') || h(BIconX)]);
};

var renderHeader = function renderHeader(h, ctx) {
  if (ctx.noHeader) {
    return h();
  }

  var $title = renderHeaderTitle(h, ctx);
  var $close = renderHeaderClose(h, ctx);
  return h('header', {
    key: 'header',
    staticClass: "".concat(CLASS_NAME, "-header"),
    class: ctx.headerClass
  }, ctx.right ? [$close, $title] : [$title, $close]);
};

var renderBody = function renderBody(h, ctx) {
  return h('div', {
    key: 'body',
    staticClass: "".concat(CLASS_NAME, "-body"),
    class: ctx.bodyClass
  }, [ctx.normalizeSlot(SLOT_NAME_DEFAULT, ctx.slotScope)]);
};

var renderFooter = function renderFooter(h, ctx) {
  var $footer = ctx.normalizeSlot(SLOT_NAME_FOOTER, ctx.slotScope);

  if (!$footer) {
    return h();
  }

  return h('footer', {
    key: 'footer',
    staticClass: "".concat(CLASS_NAME, "-footer"),
    class: ctx.footerClass
  }, [$footer]);
};

var renderContent = function renderContent(h, ctx) {
  // We render the header even if `lazy` is enabled as it
  // acts as the accessible label for the sidebar
  var $header = renderHeader(h, ctx);

  if (ctx.lazy && !ctx.isOpen) {
    return $header;
  }

  return [$header, renderBody(h, ctx), renderFooter(h, ctx)];
};

var renderBackdrop = function renderBackdrop(h, ctx) {
  if (!ctx.backdrop) {
    return h();
  }

  var backdropVariant = ctx.backdropVariant;
  return h('div', {
    directives: [{
      name: 'show',
      value: ctx.localShow
    }],
    staticClass: 'b-sidebar-backdrop',
    class: _defineProperty({}, "bg-".concat(backdropVariant), !!backdropVariant),
    on: {
      click: ctx.onBackdropClick
    }
  });
}; // --- Main component ---
// @vue/component


export var BSidebar = /*#__PURE__*/Vue.extend({
  name: NAME_SIDEBAR,
  // Mixin order is important!
  mixins: [attrsMixin, idMixin, listenOnRootMixin, normalizeSlotMixin],
  inheritAttrs: false,
  model: {
    prop: 'visible',
    event: 'change'
  },
  props: makePropsConfigurable({
    title: {
      type: String // default: null

    },
    right: {
      type: Boolean,
      default: false
    },
    bgVariant: {
      type: String,
      default: 'light'
    },
    textVariant: {
      type: String,
      default: 'dark'
    },
    shadow: {
      type: [Boolean, String],
      default: false
    },
    width: {
      type: String // default: undefined

    },
    zIndex: {
      type: [Number, String] // default: null

    },
    ariaLabel: {
      type: String // default: null

    },
    ariaLabelledby: {
      type: String // default: null

    },
    closeLabel: {
      // `aria-label` for close button
      // Defaults to 'Close'
      type: String // default: undefined

    },
    tag: {
      type: String,
      default: 'div'
    },
    sidebarClass: {
      type: [String, Array, Object] // default: null

    },
    headerClass: {
      type: [String, Array, Object] // default: null

    },
    bodyClass: {
      type: [String, Array, Object] // default: null

    },
    footerClass: {
      type: [String, Array, Object] // default: null

    },
    backdrop: {
      // If `true`, shows a basic backdrop
      type: Boolean,
      default: false
    },
    backdropVariant: {
      type: String,
      default: 'dark'
    },
    noSlide: {
      type: Boolean,
      default: false
    },
    noHeader: {
      type: Boolean,
      default: false
    },
    noHeaderClose: {
      type: Boolean,
      default: false
    },
    noCloseOnEsc: {
      type: Boolean,
      default: false
    },
    noCloseOnBackdrop: {
      type: Boolean,
      default: false
    },
    noCloseOnRouteChange: {
      type: Boolean,
      default: false
    },
    noEnforceFocus: {
      type: Boolean,
      default: false
    },
    lazy: {
      type: Boolean,
      default: false
    },
    visible: {
      type: Boolean,
      default: false
    }
  }, NAME_SIDEBAR),
  data: function data() {
    return {
      // Internal `v-model` state
      localShow: !!this.visible,
      // For lazy render triggering
      isOpen: !!this.visible
    };
  },
  computed: {
    transitionProps: function transitionProps() {
      return this.noSlide ?
      /* istanbul ignore next */
      {
        css: true
      } : {
        css: true,
        enterClass: '',
        enterActiveClass: 'slide',
        enterToClass: 'show',
        leaveClass: 'show',
        leaveActiveClass: 'slide',
        leaveToClass: ''
      };
    },
    slotScope: function slotScope() {
      return {
        visible: this.localShow,
        right: this.right,
        hide: this.hide
      };
    },
    computedTile: function computedTile() {
      return this.normalizeSlot(SLOT_NAME_TITLE, this.slotScope) || toString(this.title) || null;
    },
    titleId: function titleId() {
      return this.computedTile ? this.safeId('__title__') : null;
    },
    computedAttrs: function computedAttrs() {
      return _objectSpread(_objectSpread({}, this.bvAttrs), {}, {
        id: this.safeId(),
        tabindex: '-1',
        role: 'dialog',
        'aria-modal': this.backdrop ? 'true' : 'false',
        'aria-hidden': this.localShow ? null : 'true',
        'aria-label': this.ariaLabel || null,
        'aria-labelledby': this.ariaLabelledby || this.titleId || null
      });
    }
  },
  watch: {
    visible: function visible(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.localShow = newVal;
      }
    },
    localShow: function localShow(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.emitState(newVal);
        this.$emit('change', newVal);
      }
    },

    /* istanbul ignore next */
    $route: function $route() {
      var newVal = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      var oldVal = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

      if (!this.noCloseOnRouteChange && newVal.fullPath !== oldVal.fullPath) {
        this.hide();
      }
    }
  },
  created: function created() {
    // Define non-reactive properties
    this.$_returnFocusEl = null;
  },
  mounted: function mounted() {
    var _this = this;

    // Add `$root` listeners
    this.listenOnRoot(EVENT_TOGGLE, this.handleToggle);
    this.listenOnRoot(EVENT_STATE_REQUEST, this.handleSync); // Send out a gratuitous state event to ensure toggle button is synced

    this.$nextTick(function () {
      _this.emitState(_this.localShow);
    });
  },

  /* istanbul ignore next */
  activated: function activated() {
    this.emitSync();
  },
  beforeDestroy: function beforeDestroy() {
    this.localShow = false;
    this.$_returnFocusEl = null;
  },
  methods: {
    hide: function hide() {
      this.localShow = false;
    },
    emitState: function emitState() {
      var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : this.localShow;
      this.emitOnRoot(EVENT_STATE, this.safeId(), state);
    },
    emitSync: function emitSync() {
      var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : this.localShow;
      this.emitOnRoot(EVENT_STATE_SYNC, this.safeId(), state);
    },
    handleToggle: function handleToggle(id) {
      // Note `safeId()` can be null until after mount
      if (id && id === this.safeId()) {
        this.localShow = !this.localShow;
      }
    },
    handleSync: function handleSync(id) {
      var _this2 = this;

      // Note `safeId()` can be null until after mount
      if (id && id === this.safeId()) {
        this.$nextTick(function () {
          _this2.emitSync(_this2.localShow);
        });
      }
    },
    onKeydown: function onKeydown(evt) {
      var keyCode = evt.keyCode;

      if (!this.noCloseOnEsc && keyCode === CODE_ESC && this.localShow) {
        this.hide();
      }
    },
    onBackdropClick: function onBackdropClick() {
      if (this.localShow && !this.noCloseOnBackdrop) {
        this.hide();
      }
    },

    /* istanbul ignore next */
    onTopTrapFocus: function onTopTrapFocus() {
      var tabables = getTabables(this.$refs.content);
      this.enforceFocus(tabables.reverse()[0]);
    },

    /* istanbul ignore next */
    onBottomTrapFocus: function onBottomTrapFocus() {
      var tabables = getTabables(this.$refs.content);
      this.enforceFocus(tabables[0]);
    },
    onBeforeEnter: function onBeforeEnter() {
      // Returning focus to `document.body` may cause unwanted scrolls,
      // so we exclude setting focus on body
      this.$_returnFocusEl = getActiveElement(isBrowser ? [document.body] : []); // Trigger lazy render

      this.isOpen = true;
    },
    onAfterEnter: function onAfterEnter(el) {
      if (!contains(el, getActiveElement())) {
        this.enforceFocus(el);
      }

      this.$emit('shown');
    },
    onAfterLeave: function onAfterLeave() {
      this.enforceFocus(this.$_returnFocusEl);
      this.$_returnFocusEl = null; // Trigger lazy render

      this.isOpen = false;
      this.$emit('hidden');
    },
    enforceFocus: function enforceFocus(el) {
      if (!this.noEnforceFocus) {
        attemptFocus(el);
      }
    }
  },
  render: function render(h) {
    var _ref;

    var localShow = this.localShow;
    var shadow = this.shadow === '' ? true : this.shadow;
    var $sidebar = h(this.tag, {
      ref: 'content',
      directives: [{
        name: 'show',
        value: localShow
      }],
      staticClass: CLASS_NAME,
      class: [(_ref = {
        shadow: shadow === true
      }, _defineProperty(_ref, "shadow-".concat(shadow), shadow && shadow !== true), _defineProperty(_ref, "".concat(CLASS_NAME, "-right"), this.right), _defineProperty(_ref, "bg-".concat(this.bgVariant), !!this.bgVariant), _defineProperty(_ref, "text-".concat(this.textVariant), !!this.textVariant), _ref), this.sidebarClass],
      attrs: this.computedAttrs,
      style: {
        width: this.width
      }
    }, [renderContent(h, this)]);
    $sidebar = h('transition', {
      props: this.transitionProps,
      on: {
        beforeEnter: this.onBeforeEnter,
        afterEnter: this.onAfterEnter,
        afterLeave: this.onAfterLeave
      }
    }, [$sidebar]);
    var $backdrop = h(BVTransition, {
      props: {
        noFade: this.noSlide
      }
    }, [renderBackdrop(h, this)]);
    var $tabTrapTop = h();
    var $tabTrapBottom = h();

    if (this.backdrop && this.localShow) {
      $tabTrapTop = h('div', {
        attrs: {
          tabindex: '0'
        },
        on: {
          focus: this.onTopTrapFocus
        }
      });
      $tabTrapBottom = h('div', {
        attrs: {
          tabindex: '0'
        },
        on: {
          focus: this.onBottomTrapFocus
        }
      });
    }

    return h('div', {
      staticClass: 'b-sidebar-outer',
      style: {
        zIndex: this.zIndex
      },
      attrs: {
        tabindex: '-1'
      },
      on: {
        keydown: this.onKeydown
      }
    }, [$tabTrapTop, $sidebar, $tabTrapBottom, $backdrop]);
  }
});