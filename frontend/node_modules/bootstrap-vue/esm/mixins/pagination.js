import { NAME_PAGINATION } from '../constants/components';
import { CODE_DOWN, CODE_LEFT, CODE_RIGHT, CODE_SPACE, CODE_UP } from '../constants/key-codes';
import range from '../utils/range';
import { makePropsConfigurable } from '../utils/config';
import { attemptFocus, getActiveElement, getAttr, isDisabled, isVisible, selectAll } from '../utils/dom';
import { stopEvent } from '../utils/events';
import { isFunction, isNull, isUndefined } from '../utils/inspect';
import { mathFloor, mathMax, mathMin } from '../utils/math';
import { toInteger } from '../utils/number';
import { toString } from '../utils/string';
import { warn } from '../utils/warn';
import normalizeSlotMixin from '../mixins/normalize-slot';
import { BLink } from '../components/link/link'; // Common props, computed, data, render function, and methods
// for `<b-pagination>` and `<b-pagination-nav>`
// --- Constants ---
// Threshold of limit size when we start/stop showing ellipsis

var ELLIPSIS_THRESHOLD = 3; // Default # of buttons limit

var DEFAULT_LIMIT = 5; // --- Helper methods ---
// Make an array of N to N+X

var makePageArray = function makePageArray(startNumber, numberOfPages) {
  return range(numberOfPages).map(function (val, i) {
    return {
      number: startNumber + i,
      classes: null
    };
  });
}; // Sanitize the provided limit value (converting to a number)


var sanitizeLimit = function sanitizeLimit(val) {
  var limit = toInteger(val) || 1;
  return limit < 1 ? DEFAULT_LIMIT : limit;
}; // Sanitize the provided current page number (converting to a number)


var sanitizeCurrentPage = function sanitizeCurrentPage(val, numberOfPages) {
  var page = toInteger(val) || 1;
  return page > numberOfPages ? numberOfPages : page < 1 ? 1 : page;
}; // Links don't normally respond to SPACE, so we add that
// functionality via this handler


var onSpaceKey = function onSpaceKey(evt) {
  if (evt.keyCode === CODE_SPACE) {
    // Stop page from scrolling
    stopEvent(evt, {
      immediatePropagation: true
    }); // Trigger the click event on the link

    evt.currentTarget.click();
    return false;
  }
}; // --- Props ---


export var props = makePropsConfigurable({
  disabled: {
    type: Boolean,
    default: false
  },
  value: {
    type: [Number, String],
    default: null,

    /* istanbul ignore next */
    validator: function validator(value) {
      if (!isNull(value) && toInteger(value, 0) < 1) {
        warn('"v-model" value must be a number greater than "0"', NAME_PAGINATION);
        return false;
      }

      return true;
    }
  },
  limit: {
    type: [Number, String],
    default: DEFAULT_LIMIT,

    /* istanbul ignore next */
    validator: function validator(value) {
      if (toInteger(value, 0) < 1) {
        warn('Prop "limit" must be a number greater than "0"', NAME_PAGINATION);
        return false;
      }

      return true;
    }
  },
  align: {
    type: String,
    default: 'left'
  },
  pills: {
    type: Boolean,
    default: false
  },
  hideGotoEndButtons: {
    type: Boolean,
    default: false
  },
  ariaLabel: {
    type: String,
    default: 'Pagination'
  },
  labelFirstPage: {
    type: String,
    default: 'Go to first page'
  },
  firstText: {
    type: String,
    default: "\xAB" // '«'

  },
  firstNumber: {
    type: Boolean,
    default: false
  },
  firstClass: {
    type: [String, Array, Object],
    default: null
  },
  labelPrevPage: {
    type: String,
    default: 'Go to previous page'
  },
  prevText: {
    type: String,
    default: "\u2039" // '‹'

  },
  prevClass: {
    type: [String, Array, Object],
    default: null
  },
  labelNextPage: {
    type: String,
    default: 'Go to next page'
  },
  nextText: {
    type: String,
    default: "\u203A" // '›'

  },
  nextClass: {
    type: [String, Array, Object] // default: null

  },
  labelLastPage: {
    type: String,
    default: 'Go to last page'
  },
  lastText: {
    type: String,
    default: "\xBB" // '»'

  },
  lastNumber: {
    type: Boolean,
    default: false
  },
  lastClass: {
    type: [String, Array, Object] // default: null

  },
  labelPage: {
    type: [String, Function],
    default: 'Go to page'
  },
  pageClass: {
    type: [String, Array, Object] // default: null

  },
  hideEllipsis: {
    type: Boolean,
    default: false
  },
  ellipsisText: {
    type: String,
    default: "\u2026" // '…'

  },
  ellipsisClass: {
    type: [String, Array, Object] // default: null

  }
}, NAME_PAGINATION); // --- Mixin ---
// @vue/component

export default {
  mixins: [normalizeSlotMixin],
  model: {
    prop: 'value',
    event: 'input'
  },
  props: props,
  data: function data() {
    // `-1` signifies no page initially selected
    var currentPage = toInteger(this.value, 0);
    currentPage = currentPage > 0 ? currentPage : -1;
    return {
      currentPage: currentPage,
      localNumberOfPages: 1,
      localLimit: DEFAULT_LIMIT
    };
  },
  computed: {
    btnSize: function btnSize() {
      return this.size ? "pagination-".concat(this.size) : '';
    },
    alignment: function alignment() {
      var align = this.align;

      if (align === 'center') {
        return 'justify-content-center';
      } else if (align === 'end' || align === 'right') {
        return 'justify-content-end';
      } else if (align === 'fill') {
        // The page-items will also have 'flex-fill' added
        // We add text centering to make the button appearance better in fill mode
        return 'text-center';
      }

      return '';
    },
    styleClass: function styleClass() {
      return this.pills ? 'b-pagination-pills' : '';
    },
    computedCurrentPage: function computedCurrentPage() {
      return sanitizeCurrentPage(this.currentPage, this.localNumberOfPages);
    },
    paginationParams: function paginationParams() {
      // Determine if we should show the the ellipsis
      var limit = this.localLimit,
          numberOfPages = this.localNumberOfPages,
          currentPage = this.computedCurrentPage,
          hideEllipsis = this.hideEllipsis,
          firstNumber = this.firstNumber,
          lastNumber = this.lastNumber;
      var showFirstDots = false;
      var showLastDots = false;
      var numberOfLinks = limit;
      var startNumber = 1;

      if (numberOfPages <= limit) {
        // Special case: Less pages available than the limit of displayed pages
        numberOfLinks = numberOfPages;
      } else if (currentPage < limit - 1 && limit > ELLIPSIS_THRESHOLD) {
        if (!hideEllipsis || lastNumber) {
          showLastDots = true;
          numberOfLinks = limit - (firstNumber ? 0 : 1);
        }

        numberOfLinks = mathMin(numberOfLinks, limit);
      } else if (numberOfPages - currentPage + 2 < limit && limit > ELLIPSIS_THRESHOLD) {
        if (!hideEllipsis || firstNumber) {
          showFirstDots = true;
          numberOfLinks = limit - (lastNumber ? 0 : 1);
        }

        startNumber = numberOfPages - numberOfLinks + 1;
      } else {
        // We are somewhere in the middle of the page list
        if (limit > ELLIPSIS_THRESHOLD) {
          numberOfLinks = limit - (hideEllipsis ? 0 : 2);
          showFirstDots = !!(!hideEllipsis || firstNumber);
          showLastDots = !!(!hideEllipsis || lastNumber);
        }

        startNumber = currentPage - mathFloor(numberOfLinks / 2);
      } // Sanity checks

      /* istanbul ignore if */


      if (startNumber < 1) {
        startNumber = 1;
        showFirstDots = false;
      } else if (startNumber > numberOfPages - numberOfLinks) {
        startNumber = numberOfPages - numberOfLinks + 1;
        showLastDots = false;
      }

      if (showFirstDots && firstNumber && startNumber < 4) {
        numberOfLinks = numberOfLinks + 2;
        startNumber = 1;
        showFirstDots = false;
      }

      var lastPageNumber = startNumber + numberOfLinks - 1;

      if (showLastDots && lastNumber && lastPageNumber > numberOfPages - 3) {
        numberOfLinks = numberOfLinks + (lastPageNumber === numberOfPages - 2 ? 2 : 3);
        showLastDots = false;
      } // Special handling for lower limits (where ellipsis are never shown)


      if (limit <= ELLIPSIS_THRESHOLD) {
        if (firstNumber && startNumber === 1) {
          numberOfLinks = mathMin(numberOfLinks + 1, numberOfPages, limit + 1);
        } else if (lastNumber && numberOfPages === startNumber + numberOfLinks - 1) {
          startNumber = mathMax(startNumber - 1, 1);
          numberOfLinks = mathMin(numberOfPages - startNumber + 1, numberOfPages, limit + 1);
        }
      }

      numberOfLinks = mathMin(numberOfLinks, numberOfPages - startNumber + 1);
      return {
        showFirstDots: showFirstDots,
        showLastDots: showLastDots,
        numberOfLinks: numberOfLinks,
        startNumber: startNumber
      };
    },
    pageList: function pageList() {
      // Generates the pageList array
      var _this$paginationParam = this.paginationParams,
          numberOfLinks = _this$paginationParam.numberOfLinks,
          startNumber = _this$paginationParam.startNumber;
      var currentPage = this.computedCurrentPage; // Generate list of page numbers

      var pages = makePageArray(startNumber, numberOfLinks); // We limit to a total of 3 page buttons on XS screens
      // So add classes to page links to hide them for XS breakpoint
      // Note: Ellipsis will also be hidden on XS screens
      // TODO: Make this visual limit configurable based on breakpoint(s)

      if (pages.length > 3) {
        var idx = currentPage - startNumber; // THe following is a bootstrap-vue custom utility class

        var classes = 'bv-d-xs-down-none';

        if (idx === 0) {
          // Keep leftmost 3 buttons visible when current page is first page
          for (var i = 3; i < pages.length; i++) {
            pages[i].classes = classes;
          }
        } else if (idx === pages.length - 1) {
          // Keep rightmost 3 buttons visible when current page is last page
          for (var _i = 0; _i < pages.length - 3; _i++) {
            pages[_i].classes = classes;
          }
        } else {
          // Hide all except current page, current page - 1 and current page + 1
          for (var _i2 = 0; _i2 < idx - 1; _i2++) {
            // hide some left button(s)
            pages[_i2].classes = classes;
          }

          for (var _i3 = pages.length - 1; _i3 > idx + 1; _i3--) {
            // hide some right button(s)
            pages[_i3].classes = classes;
          }
        }
      }

      return pages;
    }
  },
  watch: {
    value: function value(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.currentPage = sanitizeCurrentPage(newValue, this.localNumberOfPages);
      }
    },
    currentPage: function currentPage(newValue, oldValue) {
      if (newValue !== oldValue) {
        // Emit `null` if no page selected
        this.$emit('input', newValue > 0 ? newValue : null);
      }
    },
    limit: function limit(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.localLimit = sanitizeLimit(newValue);
      }
    }
  },
  created: function created() {
    var _this = this;

    // Set our default values in data
    this.localLimit = sanitizeLimit(this.limit);
    this.$nextTick(function () {
      // Sanity check
      _this.currentPage = _this.currentPage > _this.localNumberOfPages ? _this.localNumberOfPages : _this.currentPage;
    });
  },
  methods: {
    handleKeyNav: function handleKeyNav(evt) {
      var keyCode = evt.keyCode,
          shiftKey = evt.shiftKey;
      /* istanbul ignore if */

      if (this.isNav) {
        // We disable left/right keyboard navigation in `<b-pagination-nav>`
        return;
      }

      if (keyCode === CODE_LEFT || keyCode === CODE_UP) {
        stopEvent(evt, {
          propagation: false
        });
        shiftKey ? this.focusFirst() : this.focusPrev();
      } else if (keyCode === CODE_RIGHT || keyCode === CODE_DOWN) {
        stopEvent(evt, {
          propagation: false
        });
        shiftKey ? this.focusLast() : this.focusNext();
      }
    },
    getButtons: function getButtons() {
      // Return only buttons that are visible
      return selectAll('button.page-link, a.page-link', this.$el).filter(function (btn) {
        return isVisible(btn);
      });
    },
    focusCurrent: function focusCurrent() {
      var _this2 = this;

      // We do this in `$nextTick()` to ensure buttons have finished rendering
      this.$nextTick(function () {
        var btn = _this2.getButtons().find(function (el) {
          return toInteger(getAttr(el, 'aria-posinset'), 0) === _this2.computedCurrentPage;
        });

        if (!attemptFocus(btn)) {
          // Fallback if current page is not in button list
          _this2.focusFirst();
        }
      });
    },
    focusFirst: function focusFirst() {
      var _this3 = this;

      // We do this in `$nextTick()` to ensure buttons have finished rendering
      this.$nextTick(function () {
        var btn = _this3.getButtons().find(function (el) {
          return !isDisabled(el);
        });

        attemptFocus(btn);
      });
    },
    focusLast: function focusLast() {
      var _this4 = this;

      // We do this in `$nextTick()` to ensure buttons have finished rendering
      this.$nextTick(function () {
        var btn = _this4.getButtons().reverse().find(function (el) {
          return !isDisabled(el);
        });

        attemptFocus(btn);
      });
    },
    focusPrev: function focusPrev() {
      var _this5 = this;

      // We do this in `$nextTick()` to ensure buttons have finished rendering
      this.$nextTick(function () {
        var buttons = _this5.getButtons();

        var index = buttons.indexOf(getActiveElement());

        if (index > 0 && !isDisabled(buttons[index - 1])) {
          attemptFocus(buttons[index - 1]);
        }
      });
    },
    focusNext: function focusNext() {
      var _this6 = this;

      // We do this in `$nextTick()` to ensure buttons have finished rendering
      this.$nextTick(function () {
        var buttons = _this6.getButtons();

        var index = buttons.indexOf(getActiveElement());

        if (index < buttons.length - 1 && !isDisabled(buttons[index + 1])) {
          attemptFocus(buttons[index + 1]);
        }
      });
    }
  },
  render: function render(h) {
    var _this7 = this;

    var buttons = [];
    var numberOfPages = this.localNumberOfPages;
    var pageNumbers = this.pageList.map(function (p) {
      return p.number;
    });
    var disabled = this.disabled;
    var _this$paginationParam2 = this.paginationParams,
        showFirstDots = _this$paginationParam2.showFirstDots,
        showLastDots = _this$paginationParam2.showLastDots;
    var currentPage = this.computedCurrentPage;
    var fill = this.align === 'fill'; // Used to control what type of aria attributes are rendered and wrapper

    var isNav = this.isNav; // Helper function and flag

    var isActivePage = function isActivePage(pageNumber) {
      return pageNumber === currentPage;
    };

    var noCurrentPage = this.currentPage < 1; // Factory function for prev/next/first/last buttons

    var makeEndBtn = function makeEndBtn(linkTo, ariaLabel, btnSlot, btnText, btnClass, pageTest, key) {
      var isDisabled = disabled || isActivePage(pageTest) || noCurrentPage || linkTo < 1 || linkTo > numberOfPages;
      var pageNumber = linkTo < 1 ? 1 : linkTo > numberOfPages ? numberOfPages : linkTo;
      var scope = {
        disabled: isDisabled,
        page: pageNumber,
        index: pageNumber - 1
      };
      var $btnContent = _this7.normalizeSlot(btnSlot, scope) || toString(btnText) || h();
      var $inner = h(isDisabled ? 'span' : isNav ? BLink : 'button', {
        staticClass: 'page-link',
        class: {
          'flex-grow-1': !isNav && !isDisabled && fill
        },
        props: isDisabled || !isNav ? {} : _this7.linkProps(linkTo),
        attrs: {
          role: isNav ? null : 'menuitem',
          type: isNav || isDisabled ? null : 'button',
          tabindex: isDisabled || isNav ? null : '-1',
          'aria-label': ariaLabel,
          'aria-controls': _this7.ariaControls || null,
          'aria-disabled': isDisabled ? 'true' : null
        },
        on: isDisabled ? {} : {
          '!click': function click(evt) {
            _this7.onClick(evt, linkTo);
          },
          keydown: onSpaceKey
        }
      }, [$btnContent]);
      return h('li', {
        key: key,
        staticClass: 'page-item',
        class: [{
          disabled: isDisabled,
          'flex-fill': fill,
          'd-flex': fill && !isNav && !isDisabled
        }, btnClass],
        attrs: {
          role: isNav ? null : 'presentation',
          'aria-hidden': isDisabled ? 'true' : null
        }
      }, [$inner]);
    }; // Ellipsis factory


    var makeEllipsis = function makeEllipsis(isLast) {
      return h('li', {
        key: "ellipsis-".concat(isLast ? 'last' : 'first'),
        staticClass: 'page-item',
        class: ['disabled', 'bv-d-xs-down-none', fill ? 'flex-fill' : '', _this7.ellipsisClass],
        attrs: {
          role: 'separator'
        }
      }, [h('span', {
        staticClass: 'page-link'
      }, [_this7.normalizeSlot('ellipsis-text') || toString(_this7.ellipsisText) || h()])]);
    }; // Page button factory


    var makePageButton = function makePageButton(page, idx) {
      var active = isActivePage(page.number) && !noCurrentPage; // Active page will have tabindex of 0, or if no current page and first page button

      var tabIndex = disabled ? null : active || noCurrentPage && idx === 0 ? '0' : '-1';
      var attrs = {
        role: isNav ? null : 'menuitemradio',
        type: isNav || disabled ? null : 'button',
        'aria-disabled': disabled ? 'true' : null,
        'aria-controls': _this7.ariaControls || null,
        'aria-label': isFunction(_this7.labelPage) && !isUndefined(_this7.labelPage(page.number)) ?
        /* istanbul ignore next */
        _this7.labelPage(page.number) : "".concat(_this7.labelPage, " ").concat(page.number),
        'aria-checked': isNav ? null : active ? 'true' : 'false',
        'aria-current': isNav && active ? 'page' : null,
        'aria-posinset': isNav ? null : page.number,
        'aria-setsize': isNav ? null : numberOfPages,
        // ARIA "roving tabindex" method (except in `isNav` mode)
        tabindex: isNav ? null : tabIndex
      };
      var btnContent = toString(_this7.makePage(page.number));
      var scope = {
        page: page.number,
        index: page.number - 1,
        content: btnContent,
        active: active,
        disabled: disabled
      };
      var $inner = h(disabled ? 'span' : isNav ? BLink : 'button', {
        props: disabled || !isNav ? {} : _this7.linkProps(page.number),
        staticClass: 'page-link',
        class: {
          'flex-grow-1': !isNav && !disabled && fill
        },
        attrs: attrs,
        on: disabled ? {} : {
          '!click': function click(evt) {
            _this7.onClick(evt, page.number);
          },
          keydown: onSpaceKey
        }
      }, [_this7.normalizeSlot('page', scope) || btnContent]);
      return h('li', {
        key: "page-".concat(page.number),
        staticClass: 'page-item',
        class: [{
          disabled: disabled,
          active: active,
          'flex-fill': fill,
          'd-flex': fill && !isNav && !disabled
        }, page.classes, _this7.pageClass],
        attrs: {
          role: isNav ? null : 'presentation'
        }
      }, [$inner]);
    }; // Goto first page button
    // Don't render button when `hideGotoEndButtons` or `firstNumber` is set


    var $firstPageBtn = h();

    if (!this.firstNumber && !this.hideGotoEndButtons) {
      $firstPageBtn = makeEndBtn(1, this.labelFirstPage, 'first-text', this.firstText, this.firstClass, 1, 'pagination-goto-first');
    }

    buttons.push($firstPageBtn); // Goto previous page button

    buttons.push(makeEndBtn(currentPage - 1, this.labelPrevPage, 'prev-text', this.prevText, this.prevClass, 1, 'pagination-goto-prev')); // Show first (1) button?

    buttons.push(this.firstNumber && pageNumbers[0] !== 1 ? makePageButton({
      number: 1
    }, 0) : h()); // First ellipsis

    buttons.push(showFirstDots ? makeEllipsis(false) : h()); // Individual page links

    this.pageList.forEach(function (page, idx) {
      var offset = showFirstDots && _this7.firstNumber && pageNumbers[0] !== 1 ? 1 : 0;
      buttons.push(makePageButton(page, idx + offset));
    }); // Last ellipsis

    buttons.push(showLastDots ? makeEllipsis(true) : h()); // Show last page button?

    buttons.push(this.lastNumber && pageNumbers[pageNumbers.length - 1] !== numberOfPages ? makePageButton({
      number: numberOfPages
    }, -1) : h()); // Goto next page button

    buttons.push(makeEndBtn(currentPage + 1, this.labelNextPage, 'next-text', this.nextText, this.nextClass, numberOfPages, 'pagination-goto-next')); // Goto last page button
    // Don't render button when `hideGotoEndButtons` or `lastNumber` is set

    var $lastPageBtn = h();

    if (!this.lastNumber && !this.hideGotoEndButtons) {
      $lastPageBtn = makeEndBtn(numberOfPages, this.labelLastPage, 'last-text', this.lastText, this.lastClass, numberOfPages, 'pagination-goto-last');
    }

    buttons.push($lastPageBtn); // Assemble the pagination buttons

    var $pagination = h('ul', {
      ref: 'ul',
      staticClass: 'pagination',
      class: ['b-pagination', this.btnSize, this.alignment, this.styleClass],
      attrs: {
        role: isNav ? null : 'menubar',
        'aria-disabled': disabled ? 'true' : 'false',
        'aria-label': isNav ? null : this.ariaLabel || null
      },
      // We disable keyboard left/right nav when `<b-pagination-nav>`
      on: isNav ? {} : {
        keydown: this.handleKeyNav
      }
    }, buttons); // If we are `<b-pagination-nav>`, wrap in `<nav>` wrapper

    if (isNav) {
      return h('nav', {
        attrs: {
          'aria-disabled': disabled ? 'true' : null,
          'aria-hidden': disabled ? 'true' : 'false',
          'aria-label': isNav ? this.ariaLabel || null : null
        }
      }, [$pagination]);
    }

    return $pagination;
  }
};