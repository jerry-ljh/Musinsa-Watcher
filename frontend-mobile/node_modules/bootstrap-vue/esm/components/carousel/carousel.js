import Vue from '../../vue';
import { NAME_CAROUSEL } from '../../constants/components';
import { EVENT_OPTIONS_NO_CAPTURE } from '../../constants/events';
import { CODE_ENTER, CODE_LEFT, CODE_RIGHT, CODE_SPACE } from '../../constants/key-codes';
import noop from '../../utils/noop';
import observeDom from '../../utils/observe-dom';
import { makePropsConfigurable } from '../../utils/config';
import { addClass, getActiveElement, reflow, removeClass, requestAF, selectAll, setAttr } from '../../utils/dom';
import { isBrowser, hasTouchSupport, hasPointerEventSupport } from '../../utils/env';
import { eventOn, eventOff, stopEvent } from '../../utils/events';
import { isUndefined } from '../../utils/inspect';
import { mathAbs, mathFloor, mathMax, mathMin } from '../../utils/math';
import { toInteger } from '../../utils/number';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot'; // Slide directional classes

var DIRECTION = {
  next: {
    dirClass: 'carousel-item-left',
    overlayClass: 'carousel-item-next'
  },
  prev: {
    dirClass: 'carousel-item-right',
    overlayClass: 'carousel-item-prev'
  }
}; // Fallback Transition duration (with a little buffer) in ms

var TRANS_DURATION = 600 + 50; // Time for mouse compat events to fire after touch

var TOUCH_EVENT_COMPAT_WAIT = 500; // Number of pixels to consider touch move a swipe

var SWIPE_THRESHOLD = 40; // PointerEvent pointer types

var PointerType = {
  TOUCH: 'touch',
  PEN: 'pen'
}; // Transition Event names

var TransitionEndEvents = {
  WebkitTransition: 'webkitTransitionEnd',
  MozTransition: 'transitionend',
  OTransition: 'otransitionend oTransitionEnd',
  transition: 'transitionend'
}; // Return the browser specific transitionEnd event name

var getTransitionEndEvent = function getTransitionEndEvent(el) {
  for (var name in TransitionEndEvents) {
    if (!isUndefined(el.style[name])) {
      return TransitionEndEvents[name];
    }
  } // Fallback

  /* istanbul ignore next */


  return null;
}; // @vue/component


export var BCarousel = /*#__PURE__*/Vue.extend({
  name: NAME_CAROUSEL,
  mixins: [idMixin, normalizeSlotMixin],
  provide: function provide() {
    return {
      bvCarousel: this
    };
  },
  model: {
    prop: 'value',
    event: 'input'
  },
  props: makePropsConfigurable({
    labelPrev: {
      type: String,
      default: 'Previous slide'
    },
    labelNext: {
      type: String,
      default: 'Next slide'
    },
    labelGotoSlide: {
      type: String,
      default: 'Goto slide'
    },
    labelIndicators: {
      type: String,
      default: 'Select a slide to display'
    },
    interval: {
      type: Number,
      default: 5000
    },
    indicators: {
      type: Boolean,
      default: false
    },
    controls: {
      type: Boolean,
      default: false
    },
    noAnimation: {
      // Disable slide/fade animation
      type: Boolean,
      default: false
    },
    fade: {
      // Enable cross-fade animation instead of slide animation
      type: Boolean,
      default: false
    },
    noWrap: {
      // Disable wrapping/looping when start/end is reached
      type: Boolean,
      default: false
    },
    noTouch: {
      // Sniffed by carousel-slide
      type: Boolean,
      default: false
    },
    noHoverPause: {
      // Disable pause on hover
      type: Boolean,
      default: false
    },
    imgWidth: {
      // Sniffed by carousel-slide
      type: [Number, String] // default: undefined

    },
    imgHeight: {
      // Sniffed by carousel-slide
      type: [Number, String] // default: undefined

    },
    background: {
      type: String // default: undefined

    },
    value: {
      type: Number,
      default: 0
    }
  }, NAME_CAROUSEL),
  data: function data() {
    return {
      index: this.value || 0,
      isSliding: false,
      transitionEndEvent: null,
      slides: [],
      direction: null,
      isPaused: !(toInteger(this.interval, 0) > 0),
      // Touch event handling values
      touchStartX: 0,
      touchDeltaX: 0
    };
  },
  computed: {
    numSlides: function numSlides() {
      return this.slides.length;
    }
  },
  watch: {
    value: function value(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.setSlide(toInteger(newVal, 0));
      }
    },
    interval: function interval(newVal, oldVal) {
      if (newVal === oldVal) {
        /* istanbul ignore next */
        return;
      }

      if (!newVal) {
        // Pausing slide show
        this.pause(false);
      } else {
        // Restarting or Changing interval
        this.pause(true);
        this.start(false);
      }
    },
    isPaused: function isPaused(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit(newVal ? 'paused' : 'unpaused');
      }
    },
    index: function index(to, from) {
      if (to === from || this.isSliding) {
        /* istanbul ignore next */
        return;
      }

      this.doSlide(to, from);
    }
  },
  created: function created() {
    // Create private non-reactive props
    this.$_interval = null;
    this.$_animationTimeout = null;
    this.$_touchTimeout = null;
    this.$_observer = null; // Set initial paused state

    this.isPaused = !(toInteger(this.interval, 0) > 0);
  },
  mounted: function mounted() {
    // Cache current browser transitionend event name
    this.transitionEndEvent = getTransitionEndEvent(this.$el) || null; // Get all slides

    this.updateSlides(); // Observe child changes so we can update slide list

    this.setObserver(true);
  },
  beforeDestroy: function beforeDestroy() {
    this.clearInterval();
    this.clearAnimationTimeout();
    this.clearTouchTimeout();
    this.setObserver(false);
  },
  methods: {
    clearInterval: function (_clearInterval) {
      function clearInterval() {
        return _clearInterval.apply(this, arguments);
      }

      clearInterval.toString = function () {
        return _clearInterval.toString();
      };

      return clearInterval;
    }(function () {
      clearInterval(this.$_interval);
      this.$_interval = null;
    }),
    clearAnimationTimeout: function clearAnimationTimeout() {
      clearTimeout(this.$_animationTimeout);
      this.$_animationTimeout = null;
    },
    clearTouchTimeout: function clearTouchTimeout() {
      clearTimeout(this.$_touchTimeout);
      this.$_touchTimeout = null;
    },
    setObserver: function setObserver() {
      var on = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : false;
      this.$_observer && this.$_observer.disconnect();
      this.$_observer = null;

      if (on) {
        this.$_observer = observeDom(this.$refs.inner, this.updateSlides.bind(this), {
          subtree: false,
          childList: true,
          attributes: true,
          attributeFilter: ['id']
        });
      }
    },
    // Set slide
    setSlide: function setSlide(slide) {
      var _this = this;

      var direction = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;

      // Don't animate when page is not visible

      /* istanbul ignore if: difficult to test */
      if (isBrowser && document.visibilityState && document.hidden) {
        return;
      }

      var noWrap = this.noWrap;
      var numSlides = this.numSlides; // Make sure we have an integer (you never know!)

      slide = mathFloor(slide); // Don't do anything if nothing to slide to

      if (numSlides === 0) {
        return;
      } // Don't change slide while transitioning, wait until transition is done


      if (this.isSliding) {
        // Schedule slide after sliding complete
        this.$once('sliding-end', function () {
          // Wrap in `requestAF()` to allow the slide to properly finish to avoid glitching
          requestAF(function () {
            return _this.setSlide(slide, direction);
          });
        });
        return;
      }

      this.direction = direction; // Set new slide index
      // Wrap around if necessary (if no-wrap not enabled)

      this.index = slide >= numSlides ? noWrap ? numSlides - 1 : 0 : slide < 0 ? noWrap ? 0 : numSlides - 1 : slide; // Ensure the v-model is synched up if no-wrap is enabled
      // and user tried to slide pass either ends

      if (noWrap && this.index !== slide && this.index !== this.value) {
        this.$emit('input', this.index);
      }
    },
    // Previous slide
    prev: function prev() {
      this.setSlide(this.index - 1, 'prev');
    },
    // Next slide
    next: function next() {
      this.setSlide(this.index + 1, 'next');
    },
    // Pause auto rotation
    pause: function pause(evt) {
      if (!evt) {
        this.isPaused = true;
      }

      this.clearInterval();
    },
    // Start auto rotate slides
    start: function start(evt) {
      if (!evt) {
        this.isPaused = false;
      }
      /* istanbul ignore next: most likely will never happen, but just in case */


      this.clearInterval(); // Don't start if no interval, or less than 2 slides

      if (this.interval && this.numSlides > 1) {
        this.$_interval = setInterval(this.next, mathMax(1000, this.interval));
      }
    },
    // Restart auto rotate slides when focus/hover leaves the carousel

    /* istanbul ignore next */
    restart: function restart() {
      if (!this.$el.contains(getActiveElement())) {
        this.start();
      }
    },
    doSlide: function doSlide(to, from) {
      var _this2 = this;

      var isCycling = Boolean(this.interval); // Determine sliding direction

      var direction = this.calcDirection(this.direction, from, to);
      var overlayClass = direction.overlayClass;
      var dirClass = direction.dirClass; // Determine current and next slides

      var currentSlide = this.slides[from];
      var nextSlide = this.slides[to]; // Don't do anything if there aren't any slides to slide to

      if (!currentSlide || !nextSlide) {
        /* istanbul ignore next */
        return;
      } // Start animating


      this.isSliding = true;

      if (isCycling) {
        this.pause(false);
      }

      this.$emit('sliding-start', to); // Update v-model

      this.$emit('input', this.index);

      if (this.noAnimation) {
        addClass(nextSlide, 'active');
        removeClass(currentSlide, 'active');
        this.isSliding = false; // Notify ourselves that we're done sliding (slid)

        this.$nextTick(function () {
          return _this2.$emit('sliding-end', to);
        });
      } else {
        addClass(nextSlide, overlayClass); // Trigger a reflow of next slide

        reflow(nextSlide);
        addClass(currentSlide, dirClass);
        addClass(nextSlide, dirClass); // Transition End handler

        var called = false;
        /* istanbul ignore next: difficult to test */

        var onceTransEnd = function onceTransEnd() {
          if (called) {
            return;
          }

          called = true;
          /* istanbul ignore if: transition events cant be tested in JSDOM */

          if (_this2.transitionEndEvent) {
            var events = _this2.transitionEndEvent.split(/\s+/);

            events.forEach(function (evt) {
              return eventOff(nextSlide, evt, onceTransEnd, EVENT_OPTIONS_NO_CAPTURE);
            });
          }

          _this2.clearAnimationTimeout();

          removeClass(nextSlide, dirClass);
          removeClass(nextSlide, overlayClass);
          addClass(nextSlide, 'active');
          removeClass(currentSlide, 'active');
          removeClass(currentSlide, dirClass);
          removeClass(currentSlide, overlayClass);
          setAttr(currentSlide, 'aria-current', 'false');
          setAttr(nextSlide, 'aria-current', 'true');
          setAttr(currentSlide, 'aria-hidden', 'true');
          setAttr(nextSlide, 'aria-hidden', 'false');
          _this2.isSliding = false;
          _this2.direction = null; // Notify ourselves that we're done sliding (slid)

          _this2.$nextTick(function () {
            return _this2.$emit('sliding-end', to);
          });
        }; // Set up transitionend handler

        /* istanbul ignore if: transition events cant be tested in JSDOM */


        if (this.transitionEndEvent) {
          var events = this.transitionEndEvent.split(/\s+/);
          events.forEach(function (event) {
            return eventOn(nextSlide, event, onceTransEnd, EVENT_OPTIONS_NO_CAPTURE);
          });
        } // Fallback to setTimeout()


        this.$_animationTimeout = setTimeout(onceTransEnd, TRANS_DURATION);
      }

      if (isCycling) {
        this.start(false);
      }
    },
    // Update slide list
    updateSlides: function updateSlides() {
      this.pause(true); // Get all slides as DOM elements

      this.slides = selectAll('.carousel-item', this.$refs.inner);
      var numSlides = this.slides.length; // Keep slide number in range

      var index = mathMax(0, mathMin(mathFloor(this.index), numSlides - 1));
      this.slides.forEach(function (slide, idx) {
        var n = idx + 1;

        if (idx === index) {
          addClass(slide, 'active');
          setAttr(slide, 'aria-current', 'true');
        } else {
          removeClass(slide, 'active');
          setAttr(slide, 'aria-current', 'false');
        }

        setAttr(slide, 'aria-posinset', String(n));
        setAttr(slide, 'aria-setsize', String(numSlides));
      }); // Set slide as active

      this.setSlide(index);
      this.start(this.isPaused);
    },
    calcDirection: function calcDirection() {
      var direction = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : null;
      var curIndex = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
      var nextIndex = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;

      if (!direction) {
        return nextIndex > curIndex ? DIRECTION.next : DIRECTION.prev;
      }

      return DIRECTION[direction];
    },
    handleClick: function handleClick(evt, fn) {
      var keyCode = evt.keyCode;

      if (evt.type === 'click' || keyCode === CODE_SPACE || keyCode === CODE_ENTER) {
        stopEvent(evt);
        fn();
      }
    },

    /* istanbul ignore next: JSDOM doesn't support touch events */
    handleSwipe: function handleSwipe() {
      var absDeltaX = mathAbs(this.touchDeltaX);

      if (absDeltaX <= SWIPE_THRESHOLD) {
        return;
      }

      var direction = absDeltaX / this.touchDeltaX; // Reset touch delta X
      // https://github.com/twbs/bootstrap/pull/28558

      this.touchDeltaX = 0;

      if (direction > 0) {
        // Swipe left
        this.prev();
      } else if (direction < 0) {
        // Swipe right
        this.next();
      }
    },

    /* istanbul ignore next: JSDOM doesn't support touch events */
    touchStart: function touchStart(evt) {
      if (hasPointerEventSupport && PointerType[evt.pointerType.toUpperCase()]) {
        this.touchStartX = evt.clientX;
      } else if (!hasPointerEventSupport) {
        this.touchStartX = evt.touches[0].clientX;
      }
    },

    /* istanbul ignore next: JSDOM doesn't support touch events */
    touchMove: function touchMove(evt) {
      // Ensure swiping with one touch and not pinching
      if (evt.touches && evt.touches.length > 1) {
        this.touchDeltaX = 0;
      } else {
        this.touchDeltaX = evt.touches[0].clientX - this.touchStartX;
      }
    },

    /* istanbul ignore next: JSDOM doesn't support touch events */
    touchEnd: function touchEnd(evt) {
      if (hasPointerEventSupport && PointerType[evt.pointerType.toUpperCase()]) {
        this.touchDeltaX = evt.clientX - this.touchStartX;
      }

      this.handleSwipe(); // If it's a touch-enabled device, mouseenter/leave are fired as
      // part of the mouse compatibility events on first tap - the carousel
      // would stop cycling until user tapped out of it;
      // here, we listen for touchend, explicitly pause the carousel
      // (as if it's the second time we tap on it, mouseenter compat event
      // is NOT fired) and after a timeout (to allow for mouse compatibility
      // events to fire) we explicitly restart cycling

      this.pause(false);
      this.clearTouchTimeout();
      this.$_touchTimeout = setTimeout(this.start, TOUCH_EVENT_COMPAT_WAIT + mathMax(1000, this.interval));
    }
  },
  render: function render(h) {
    var _this3 = this;

    // Wrapper for slides
    var inner = h('div', {
      ref: 'inner',
      class: ['carousel-inner'],
      attrs: {
        id: this.safeId('__BV_inner_'),
        role: 'list'
      }
    }, [this.normalizeSlot()]); // Prev and next controls

    var controls = h();

    if (this.controls) {
      var prevHandler = function prevHandler(evt) {
        /* istanbul ignore next */
        if (!_this3.isSliding) {
          _this3.handleClick(evt, _this3.prev);
        } else {
          stopEvent(evt, {
            propagation: false
          });
        }
      };

      var nextHandler = function nextHandler(evt) {
        /* istanbul ignore next */
        if (!_this3.isSliding) {
          _this3.handleClick(evt, _this3.next);
        } else {
          stopEvent(evt, {
            propagation: false
          });
        }
      };

      controls = [h('a', {
        class: ['carousel-control-prev'],
        attrs: {
          href: '#',
          role: 'button',
          'aria-controls': this.safeId('__BV_inner_'),
          'aria-disabled': this.isSliding ? 'true' : null
        },
        on: {
          click: prevHandler,
          keydown: prevHandler
        }
      }, [h('span', {
        class: ['carousel-control-prev-icon'],
        attrs: {
          'aria-hidden': 'true'
        }
      }), h('span', {
        class: ['sr-only']
      }, [this.labelPrev])]), h('a', {
        class: ['carousel-control-next'],
        attrs: {
          href: '#',
          role: 'button',
          'aria-controls': this.safeId('__BV_inner_'),
          'aria-disabled': this.isSliding ? 'true' : null
        },
        on: {
          click: nextHandler,
          keydown: nextHandler
        }
      }, [h('span', {
        class: ['carousel-control-next-icon'],
        attrs: {
          'aria-hidden': 'true'
        }
      }), h('span', {
        class: ['sr-only']
      }, [this.labelNext])])];
    } // Indicators


    var indicators = h('ol', {
      class: ['carousel-indicators'],
      directives: [{
        name: 'show',
        rawName: 'v-show',
        value: this.indicators,
        expression: 'indicators'
      }],
      attrs: {
        id: this.safeId('__BV_indicators_'),
        'aria-hidden': this.indicators ? 'false' : 'true',
        'aria-label': this.labelIndicators,
        'aria-owns': this.safeId('__BV_inner_')
      }
    }, this.slides.map(function (slide, n) {
      return h('li', {
        key: "slide_".concat(n),
        class: {
          active: n === _this3.index
        },
        attrs: {
          role: 'button',
          id: _this3.safeId("__BV_indicator_".concat(n + 1, "_")),
          tabindex: _this3.indicators ? '0' : '-1',
          'aria-current': n === _this3.index ? 'true' : 'false',
          'aria-label': "".concat(_this3.labelGotoSlide, " ").concat(n + 1),
          'aria-describedby': _this3.slides[n].id || null,
          'aria-controls': _this3.safeId('__BV_inner_')
        },
        on: {
          click: function click(evt) {
            _this3.handleClick(evt, function () {
              _this3.setSlide(n);
            });
          },
          keydown: function keydown(evt) {
            _this3.handleClick(evt, function () {
              _this3.setSlide(n);
            });
          }
        }
      });
    }));
    var on = {
      mouseenter: this.noHoverPause ? noop : this.pause,
      mouseleave: this.noHoverPause ? noop : this.restart,
      focusin: this.pause,
      focusout: this.restart,
      keydown: function keydown(evt) {
        if (/input|textarea/i.test(evt.target.tagName)) {
          /* istanbul ignore next */
          return;
        }

        var keyCode = evt.keyCode;

        if (keyCode === CODE_LEFT || keyCode === CODE_RIGHT) {
          stopEvent(evt);

          _this3[keyCode === CODE_LEFT ? 'prev' : 'next']();
        }
      }
    }; // Touch support event handlers for environment

    if (!this.noTouch && hasTouchSupport) {
      // Attach appropriate listeners (prepend event name with '&' for passive mode)

      /* istanbul ignore next: JSDOM doesn't support touch events */
      if (hasPointerEventSupport) {
        on['&pointerdown'] = this.touchStart;
        on['&pointerup'] = this.touchEnd;
      } else {
        on['&touchstart'] = this.touchStart;
        on['&touchmove'] = this.touchMove;
        on['&touchend'] = this.touchEnd;
      }
    } // Return the carousel


    return h('div', {
      staticClass: 'carousel',
      class: {
        slide: !this.noAnimation,
        'carousel-fade': !this.noAnimation && this.fade,
        'pointer-event': !this.noTouch && hasTouchSupport && hasPointerEventSupport
      },
      style: {
        background: this.background
      },
      attrs: {
        role: 'region',
        id: this.safeId(),
        'aria-busy': this.isSliding ? 'true' : 'false'
      },
      on: on
    }, [inner, controls, indicators]);
  }
});