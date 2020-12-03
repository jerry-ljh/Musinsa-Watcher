import Vue from '../../vue';
import { NAME_PAGINATION } from '../../constants/components';
import { BvEvent } from '../../utils/bv-event.class';
import { makePropsConfigurable } from '../../utils/config';
import { attemptFocus, isVisible } from '../../utils/dom';
import { isUndefinedOrNull } from '../../utils/inspect';
import { mathCeil, mathMax } from '../../utils/math';
import { toInteger } from '../../utils/number';
import paginationMixin from '../../mixins/pagination'; // --- Constants ---

var DEFAULT_PER_PAGE = 20;
var DEFAULT_TOTAL_ROWS = 0; // --- Helper methods ---
// Sanitize the provided per page number (converting to a number)

var sanitizePerPage = function sanitizePerPage(val) {
  return mathMax(toInteger(val) || DEFAULT_PER_PAGE, 1);
}; // Sanitize the provided total rows number (converting to a number)


var sanitizeTotalRows = function sanitizeTotalRows(val) {
  return mathMax(toInteger(val) || DEFAULT_TOTAL_ROWS, 0);
}; // --- Main component ---
// The render function is brought in via the `paginationMixin`
// @vue/component


export var BPagination = /*#__PURE__*/Vue.extend({
  name: NAME_PAGINATION,
  mixins: [paginationMixin],
  props: makePropsConfigurable({
    size: {
      type: String // default: null

    },
    perPage: {
      type: [Number, String],
      default: DEFAULT_PER_PAGE
    },
    totalRows: {
      type: [Number, String],
      default: DEFAULT_TOTAL_ROWS
    },
    ariaControls: {
      type: String // default: null

    }
  }, NAME_PAGINATION),
  computed: {
    numberOfPages: function numberOfPages() {
      var result = mathCeil(sanitizeTotalRows(this.totalRows) / sanitizePerPage(this.perPage));
      return result < 1 ? 1 : result;
    },
    pageSizeNumberOfPages: function pageSizeNumberOfPages() {
      // Used for watching changes to `perPage` and `numberOfPages`
      return {
        perPage: sanitizePerPage(this.perPage),
        totalRows: sanitizeTotalRows(this.totalRows),
        numberOfPages: this.numberOfPages
      };
    }
  },
  watch: {
    pageSizeNumberOfPages: function pageSizeNumberOfPages(newVal, oldVal) {
      if (!isUndefinedOrNull(oldVal)) {
        if (newVal.perPage !== oldVal.perPage && newVal.totalRows === oldVal.totalRows) {
          // If the page size changes, reset to page 1
          this.currentPage = 1;
        } else if (newVal.numberOfPages !== oldVal.numberOfPages && this.currentPage > newVal.numberOfPages) {
          // If `numberOfPages` changes and is less than
          // the `currentPage` number, reset to page 1
          this.currentPage = 1;
        }
      }

      this.localNumberOfPages = newVal.numberOfPages;
    }
  },
  created: function created() {
    var _this = this;

    // Set the initial page count
    this.localNumberOfPages = this.numberOfPages; // Set the initial page value

    var currentPage = toInteger(this.value, 0);

    if (currentPage > 0) {
      this.currentPage = currentPage;
    } else {
      this.$nextTick(function () {
        // If this value parses to `NaN` or a value less than `1`
        // trigger an initial emit of `null` if no page specified
        _this.currentPage = 0;
      });
    }
  },
  mounted: function mounted() {
    // Set the initial page count
    this.localNumberOfPages = this.numberOfPages;
  },
  methods: {
    // These methods are used by the render function
    onClick: function onClick(evt, pageNumber) {
      var _this2 = this;

      // Dont do anything if clicking the current active page
      if (pageNumber === this.currentPage) {
        return;
      }

      var target = evt.target; // Emit a user-cancelable `page-click` event

      var clickEvt = new BvEvent('page-click', {
        cancelable: true,
        vueTarget: this,
        target: target
      });
      this.$emit(clickEvt.type, clickEvt, pageNumber);

      if (clickEvt.defaultPrevented) {
        return;
      } // Update the `v-model`


      this.currentPage = pageNumber; // Emit event triggered by user interaction

      this.$emit('change', this.currentPage); // Keep the current button focused if possible

      this.$nextTick(function () {
        if (isVisible(target) && _this2.$el.contains(target)) {
          attemptFocus(target);
        } else {
          _this2.focusCurrent();
        }
      });
    },
    makePage: function makePage(pageNum) {
      return pageNum;
    },

    /* istanbul ignore next */
    linkProps: function linkProps() {
      // No props, since we render a plain button

      /* istanbul ignore next */
      return {};
    }
  }
});