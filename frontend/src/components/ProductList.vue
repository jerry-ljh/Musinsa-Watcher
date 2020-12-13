<template>
    <div>
        <div v-if="products.length>0">
            <b-card-group
                deck="deck"
                style="margin-bottom : 30px;"
                v-for="(productDeck, idx) in itemListToCardDeck(products)"
                v-bind:key="idx">
                <b-card
                    @click="goToDetail(product)"
                    :img-src="product.img"
                    align="center"
                    v-for="product in productDeck"
                    v-bind:key="product.productId">
                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 0px; left: 0px; background-color:#FFF"
                        v-if="product.discount !=null">-{{Math.ceil(product.percent)}}%</h6>
                    <h6
                        style="position: absolute; top: 0; left: 5px; background-color:#FFF"
                        v-if="product.discount ==null">
                        {{product.rank}}위
                    </h6>
                    <b-card-text>
                        <h6 style="text-align : center">
                            <strong>{{product.brand}}</strong>
                        </h6>
                        <div style="text-align : left">
                            <span style="font-size : 13px;">{{truncateProductName(product.productName)}}</span>
                        </div>
                    </b-card-text>
                    <template #footer>
                        <em>
                            <div v-if="product.discount !=null">
                                <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.discount + product.price)}}원</span>
                                <span style="color:#ae0000">
                                    <strong>{{numberToPrice(product.price)}}원</strong>
                                </span>
                            </div>
                            <small v-if="product.discount ==null" class="text-muted">updated
                                {{product.modifiedDate}}</small>
                        </em>
                    </template>
                </b-card>
            </b-card-group>

            <div class="mt-3" style="margin-right:70px;">
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
                columnCount: 5,
                rows: 0,
                perpage: 25,
                productDeck: [],
                currentListTopic: "",
                products: [],
                curCategory: '',
                curBrand: '',
                curSearchTopic: '',
                API: 'http://localhost:8080/'
            }
        },
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
            },
            truncateProductName(productName) {
                return productName.length > 60
                    ? productName.substr(0, 60) + '...'
                    : productName;

            },
            newPage(page) {
                if (this.bufferPage == page || page == null) 
                    return
                if (this.currentListTopic == "category") {
                    this.goToCategory(this.curCategory, page)
                } else if (this.currentListTopic == "brand") {
                    this.goToBrand(this.curBrand, page)
                } else if (this.currentListTopic == "search") {
                    this.goToSearch(this.curSearchTopic, page)
                } else if (this.currentListTopic == "discount") {
                    this.goToDiscountList(this.curCategory, page)
                }
                window.scrollTo(0, 0)
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
                for (var i = 0; i < this.columnCount; i++) {
                    productDeck.push(list.slice(i * this.columnCount, (i + 1) * this.columnCount))
                }
                return productDeck
            },
            goToCategory(category, page) {
                 console.log("goToCategory")
                let self = this
                self.currentListTopic = "category"
                window.scrollTo(0, 0);
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
                            .catch(() => {})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToDiscountList(category, page) {
                console.log("goToDiscountList")
                let self = this
                self.currentListTopic = "discount"
                window.scrollTo(0, 0);
                axios
                    .get(this.API + '/api/v1/product/discount', {
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
                                    "page": page,
                                    "type": 'discount'
                                }
                            })
                            .catch(() => {})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToBrand(name, page) {
                console.log("goToBrand")
                let self = this
                self.currentListTopic = "brand"
                self.curBrand = name;
                window.scrollTo(0, 0);
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
                            .catch(() => {})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToSearch(topic, page) {
                 console.log("goToSearch")
                if (topic.trim().length == 0) {
                    return;
                }
                this.currentListTopic = "search"
                this.curSearchTopic = topic;
                window.scrollTo(0, 0);
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
                            .catch(() => {})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        },
        created() {
            EventBus.$on("goToCategory", (category, page) => {
                this.goToCategory(category, page)
            })
            EventBus.$on("goToDiscountList", (category, page) => {
                this.goToDiscountList(category, page)
            })
            EventBus.$on("goToBrand", (name, page) => {
                this.goToBrand(name, page)
            })
            EventBus.$on("goToSearch", (searchText, page) => {
                this.goToSearch(searchText, page)
            })
            if (this.$route.path == '/') {
                this.curCategory = '001'
                this.goToCategory(this.curCategory, 1)
                return
            }
            if (this.$route.query.category != null) {
                this.curCategory = this.$route.query.category
                if (this.$route.query.type == 'discount') {
                    this.goToDiscountList(
                        this.curCategory,
                        this.$route.query.page
                            ? this.$route.query.page
                            : 1
                    )
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
            EventBus.$off("goToBrand")
            EventBus.$off("goToSearch")
        }
    }
</script>

<style src="../assets/css/itemList.css"></style>