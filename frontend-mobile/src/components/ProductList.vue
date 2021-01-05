<template>
    <div>
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
                    <b-card-img :src="product.img">

                    </b-card-img>

                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount !=null">{{Math.ceil(product.percent)}}% OFF</h6>
                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="currentListTopic == 'minimum' && product.today_price != null">{{Math.ceil((product.maxPrice - product.today_price) * 100/product.maxPrice)}}% OFF</h6>
                    <h6
                        style="position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount ==null && currentListTopic != 'minimum'">
                        {{product.rank}}위
                    </h6>
                    <b-card-text>
                        <h6 style="text-align : center">
                            <strong>{{product.brand}}</strong>
                        </h6>
                        <div style="text-align : left; height : 48px; overflow:hidden">
                            <span style="font-size : 13px;">{{truncateProductName(product.productName)}}</span>
                        </div>
                        
                       <div v-if="product.discount !=null">
                                <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.discount + product.price)}}원</span><br/>
                                <span style="color:#ae0000">
                                    <strong>{{numberToPrice(product.price)}}원</strong>
                                </span>
                            </div>
                            <div v-if="currentListTopic == 'minimum' && product.today_price != null">
                                <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.maxPrice)}}원</span><br/>
                                <span style="color:#ae0000">
                                    <strong>{{numberToPrice(product.today_price)}}원</strong>
                                </span>
                            </div>
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
                API: "http://www.musinsa.cf/"
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
                window.scrollTo(0, 0)
            },
            truncateProductName(productName) {
                return   productName;

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
                    this.goToDiscountList(this.curCategory, page)
                } else if (this.currentListTopic == "minimum") {
                    this.goToMinimumList(this.curCategory, page);
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
                for (var i = 0; i < this.perpage/this.columnCount; i++) {
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
            goToMinimumList(category, page) {
                let self = this
                self.currentListTopic = "minimum"
                axios
                    .get(this.API + '/api/v1/product/minimum', {
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
                                    "type": 'minimum'
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
            goToDiscountList(category, page) {
                let self = this
                self.currentListTopic = "discount"
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
            EventBus.$on("goToDiscountList", (category, page) => {
                this.goToDiscountList(category, page)
            })
            EventBus.$on("goToBrand", (name, page) => {
                this.goToBrand(name, page)
            })
            EventBus.$on("goToSearch", (searchText, page) => {
                this.goToSearch(searchText, page)
            })
            EventBus.$on("goToMinimumList", (category, page) => {
                this.goToMinimumList(category, page)
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
                } else if (this.$route.query.type == 'minimum') {
                    this.goToMinimumList(
                        this.curCategory,
                        this.$route.query.page
                            ? this.$route.query.page
                            : 1
                    )
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