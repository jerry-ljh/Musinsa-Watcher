<template>
    <div>
        <div style="float : left; margin-top : 8px">
            <span
                v-if="currentListTopic == 'minimum'"
                style="color :#b2b2b2; margin-left:20px">
                오늘 역대 최저가 <b-icon icon="question-octagon" variant="primary" id="question"></b-icon>
                <b-tooltip
                    v-if="currentListTopic == 'minimum'"
                    target="question"
                    triggers="hover">
                    오늘 역대 최저가는 과거 평균 가격과 오늘 가격를 비교합니다.
                </b-tooltip>
            </span>
            <span
                v-if="currentListTopic == 'discount'"
                style="color :#b2b2b2; margin-left:20px">
                오늘 깜짝 할인 <b-icon icon="question-octagon" variant="primary" id="question"></b-icon>
                <b-tooltip
                    v-if="currentListTopic == 'discount'"
                    target="question"
                    triggers="hover">
                    오늘 깜짝 할인은 어제 가격과 오늘 가격을 비교합니다.
                </b-tooltip>
            </span>
        </div>
        <div
            style="text-align : right; margin-right :6%"
            v-if="currentListTopic == 'minimum' || currentListTopic == 'discount'">
            <b-dropdown id="dropdown-1" right="right" variant="outline-dark " :text="curSortText" class="m-md-2">
                <b-dropdown-item v-on:click="changeSort('percent_desc')">할인율 높은 순</b-dropdown-item>
                <b-dropdown-item v-on:click="changeSort('percent_asc')">할인율 낮은 순</b-dropdown-item>
                <b-dropdown-item v-on:click="changeSort('price_desc')">높은 가격순</b-dropdown-item>
                <b-dropdown-item v-on:click="changeSort('price_asc')">낮은 가격순</b-dropdown-item>
                <b-dropdown-divider></b-dropdown-divider>
            </b-dropdown>
        </div>
        <div v-if="products.length>0">
            <b-card-group
                deck="deck"
                style="margin : 5px; text-align:center!important;"
                v-for="(productDeck, idx) in itemListToCardDeck(products)"
                v-bind:key="idx">
                <b-card
                    @click="goToDetail(product)"
                    align="center"
                    v-for="product in productDeck"
                    v-bind:key="product.productId"
                    style="margin-bottom:30px; margin:10px; width:44%;">
                    <b-card-img :src="product.img"></b-card-img>

                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount !=null">{{Math.ceil(product.percent)}}% OFF</h6>
                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="currentListTopic == 'minimum' && product.today_price != null">{{Math.ceil((product.avgPrice - product.today_price) * 100/product.avgPrice)}}% OFF</h6>
                    <h6
                        style="position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount ==null && currentListTopic == 'category'">
                        {{product.rank}}위
                    </h6>
                    <b-card-text>
                        <h6 style="text-align : center; min-height:30px; margin-top:5px;">
                            <strong>{{product.brand}}</strong>
                        </h6>
                        <div style="text-align : left; height : 48px; overflow:hidden; margin-bottom : 10px">
                            <span style="font-size : 13px;">{{truncateProductName(product.productName)}}</span>
                        </div>

                        <div v-if="product.discount !=null">
                            <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.discount + product.price)}}원</span><br/>
                            <span style="color:#ae0000">
                                <strong>{{numberToPrice(product.price)}}원</strong>
                            </span>
                        </div>
                        <div v-if="currentListTopic == 'minimum' && product.today_price != null">
                            <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.avgPrice)}}원</span><br/>
                            <span style="color:#ae0000">
                                <strong>{{numberToPrice(product.today_price)}}원</strong>
                            </span>
                        </div>
                        <div v-if="currentListTopic == 'brand' || currentListTopic == 'category'">
                            <span v-if="isTodayUpdated(product.modifiedDate)" style="color:#ae0000">
                                <strong>{{numberToPrice(product.real_price)}}원</strong>
                            </span>
                            <span v-if="!isTodayUpdated(product.modifiedDate)" style="color:#b2b2b2">
                                <strong>{{numberToPrice(product.real_price)}}원</strong>
                            </span>
                        </div>
                        <span style="font-size: 0.7em; color:#b2b2b2">{{product.modifiedDate}} updated</span>
                    </b-card-text>

                </b-card>
            </b-card-group>
            <div class="mt-3">
                <b-pagination
                    v-model="currentPage"
                    @input="newPage(currentPage)"
                    :total-rows="rows"
                    align="center"
                    :per-page="perpage"
                    :limit="limit"
                    style="margin-bottom : 100px"></b-pagination>
            </div>
        </div>
        <div v-if="products.length==0 && $parent.loading == false">
            <h2 style="text-align:center">일치하는 결과가 없습니다!</h2>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'
    import EventBus from '../utils/event-bus';

    export default {
        data() {
            return {
                limit: 10,
                bufferPage: 1,
                currentPage: 1,
                columnCount: 2,
                rows: 0,
                perpage: 25,
                currentListTopic: "",
                products: [],
                curCategory: '',
                curBrand: '',
                curSearchTopic: '',
                curSort : '',
                curSortText : '',
                API: "https://www.musinsa.cf/"
            }
        },
        props: ['updatedAt'],
        methods: {
            goToDetail(product) {
                this
                    .$router
                    .push({
                        name: 'Product',
                        query: {
                            "id": product.productId
                        },
                        params: product
                    })
                window.scrollTo(0, 0)
            },
            isTodayUpdated(date) {
                if (date == null || this.updatedAt == null) {
                    return false
                }
                var lastUpdateArr = date.split('-')
                var lastUpdate = new Date(
                    lastUpdateArr[0],
                    lastUpdateArr[1] - 1,
                    lastUpdateArr[2]
                ).toLocaleDateString()
                var updatedAtArr = this
                    .updatedAt
                    .split('-')
                var updatedAt = new Date(updatedAtArr[0], updatedAtArr[1] - 1, updatedAtArr[2]).toLocaleDateString()
                return lastUpdate == updatedAt
            },
            truncateProductName(productName) {
                return productName;
            },
            sortToText(sort){
                if(sort=='price_asc') this.curSortText = '낮은 가격순';
                else if(sort =='price_desc') this.curSortText = '높은 가격순';
                else if(sort=='percent_asc') this.curSortText = '할인율 낮은 순';
                else if(sort=='percent_desc') this.curSortText = '할인율 높은 순';
            },
            changeSort(sort) {
                this.sortToText(sort)
                this.$emit('isLoading', true)
                this.curSort = sort
                if (this.currentListTopic == "discount") {
                    this.goToDiscountList(this.curCategory, 1, this.curSort)
                } else if (this.currentListTopic == "minimum") {
                    this.goToMinimumList(this.curCategory, 1, this.curSort);
                }
                this.bufferPage = 1
            },
            newPage(page) {
                if (this.bufferPage == page || page == null) 
                    return
                this.$emit('isLoading', true)
                if (this.currentListTopic == "category") {
                    this.goToCategory(this.curCategory, page)
                } else if (this.currentListTopic == "brand") {
                    this.goToBrand(this.curBrand, page)
                } else if (this.currentListTopic == "search") {
                    this.goToSearch(this.curSearchTopic, page)
                } else if (this.currentListTopic == "discount") {
                    this.goToDiscountList(this.curCategory, page, this.curSort)
                } else if (this.currentListTopic == "minimum") {
                    this.goToMinimumList(this.curCategory, page, this.curSort)
                }
                this.bufferPage = page
            },
            numberToPrice(number) {
                if (number == null) {
                    return number
                }
                return number.toLocaleString();
            },
            itemListToCardDeck(list) {
                var productDeck = []
                for (var i = 0; i < this.perpage / this.columnCount; i++) {
                    productDeck.push(list.slice(i * this.columnCount, (i + 1) * this.columnCount))
                }
                return productDeck
            },
            goToCategory(category, page) {
                let self = this
                self.currentListTopic = "category"
                axios
                    .get(this.API + '/api/v1/product/list', {
                        params: {
                            "category": category,
                            "page": page - 1
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        self.curCategory = category
                        this
                            .$router
                            .push({
                                name: 'ProductList',
                                query: {
                                    "category": this.curCategory,
                                    "page": page
                                }
                            })
                            .catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0)
                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToMinimumList(category, page, sort) {
                let self = this
                self.currentListTopic = "minimum"
                axios
                    .get(this.API + '/api/v1/product/minimum', {
                        params: {
                            "category": category,
                            "page": page - 1,
                            "sort": sort
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        self.curCategory = category
                        this
                            .$router
                            .push({
                                name: 'ProductList',
                                query: {
                                    "category": this.curCategory,
                                    "page": page,
                                    "type": 'minimum',
                                    "sort": sort
                                }
                            })
                            .catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);

                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToDiscountList(category, page, sort) {
                let self = this
                self.currentListTopic = "discount"
                axios
                    .get(this.API + '/api/v1/product/discount', {
                        params: {
                            "category": category,
                            "page": page - 1,
                            "sort": sort
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        self.curCategory = category
                        this
                            .$router
                            .push({
                                name: 'ProductList',
                                query: {
                                    "category": this.curCategory,
                                    "page": page,
                                    "type": 'discount',
                                    "sort": sort
                                }
                            })
                            .catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);

                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToBrand(name, page) {
                let self = this
                self.currentListTopic = "brand"
                self.curBrand = name;
                axios
                    .get(this.API + '/api/v1/product/brand', {
                        params: {
                            "name": name,
                            "page": page - 1
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response
                            .data
                            .pageable
                            .pageSize
                            this
                            .$router
                            .push({
                                name: 'ProductList',
                                query: {
                                    "brand": this.curBrand,
                                    "page": page
                                }
                            })
                            .catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);

                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToSearch(topic, page) {
                if (topic.trim().length == 0) {
                    return;
                }
                this.currentListTopic = "search"
                this.curSearchTopic = topic;
                axios
                    .get(this.API + '/api/v1/search', {
                        params: {
                            "topic": topic.trim(),
                            "page": page - 1
                        }
                    })
                    .then((response) => {
                        this.products = response.data.content
                        this.currentPage = response.data.pageable.pageNumber + 1
                        this.rows = response.data.totalElements
                        this.perpage = response
                            .data
                            .pageable
                            .pageSize
                            this
                            .$router
                            .push({
                                name: 'ProductList',
                                query: {
                                    "search": this.curSearchTopic,
                                    "page": page
                                }
                            })
                            .catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);
                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            }
        },
        created() {
            this.$emit('isLoading', true)
            EventBus.$on("goToCategory", (category, page) => {
                this.goToCategory(category, page)
            })
            EventBus.$on("goToDiscountList", (category, page, sort) => {
                this.goToDiscountList(category, page, sort)
                this.sortToText(sort)
            })
            EventBus.$on("goToBrand", (name, page) => {
                this.goToBrand(name, page)
            })
            EventBus.$on("goToSearch", (searchText, page) => {
                this.goToSearch(searchText, page)
            })
            EventBus.$on("goToMinimumList", (category, page, sort) => {
                this.goToMinimumList(category, page, sort)
                this.sortToText(sort)
            })
            if (this.$route.query.category != null) {
                this.curCategory = this.$route.query.category
                if (this.$route.query.type == 'discount') {
                    this.goToDiscountList(
                        this.curCategory,
                        this.$route.query.page
                            ? this.$route.query.page
                            : 1,
                        this.$route.query.sort
                            ? this.$route.query.sort
                            : "percent_desc"
                    )
                    this.sortToText(this.$route.query.sort
                            ? this.$route.query.sort
                            : "percent_desc");
                } else if (this.$route.query.type == 'minimum') {
                    this.goToMinimumList(
                        this.curCategory,
                        this.$route.query.page
                            ? this.$route.query.page
                            : 1,
                        this.$route.query.sort
                            ? this.$route.query.sort
                            : "percent_desc"
                    )
                    this.sortToText(this.$route.query.sort
                            ? this.$route.query.sort
                            : "percent_desc");
                    this.curBrand = this.$route.query.brand
                } else {
                    this.goToCategory(
                        this.curCategory,
                        this.$route.query.page
                            ? this.$route.query.page
                            : 1
                    )
                }
            }
            if (this.$route.query.search != null) {
                this.goToSearch(
                    this.$route.query.search,
                    this.$route.query.page
                        ? this.$route.query.page
                        : 1
                )
                this.curSearchTopic = this.$route.query.search
            }
            if (this.$route.query.brand != null) {
                this.goToBrand(
                    this.$route.query.brand,
                    this.$route.query.page
                        ? this.$route.query.page
                        : 1
                )
                this.curBrand = this.$route.query.brand
            }

        },
        destroyed() {
            EventBus.$off("goToCategory")
            EventBus.$off("goToDiscountList")
            EventBus.$off("goToMinimumList")
            EventBus.$off("goToBrand")
            EventBus.$off("goToSearch")
        }
    }
</script>

<style src="../assets/css/itemList.css"></style>