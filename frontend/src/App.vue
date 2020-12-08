<template>
    <div>
        <b-navbar style="background-color:black ">
            <b-navbar-brand >
                <a href="/" style="color :#FFFFFF; text-decoration:none !important">
                    <h2 style="color :#FFFFFF;">MUSINSA WATCHER</h2>
                </a>
            </b-navbar-brand>
            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
            <b-collapse id="nav-collapse" is-nav="is-nav">
                <b-navbar-nav class="ml-auto">
                    <b-form-input
                        size="md"
                        class="mr-sm-2"
                        placeholder="Search"
                        v-on:keyup.enter="search(searchText, 0)"
                        v-model="searchText"></b-form-input>
                    <b-button
                        size="sm"
                        class="my-2 my-sm-0"
                        style="background-color : #000000"
                        v-on:click="search(searchText, 0)">
                        <b-icon icon="search" font-scale="1.5" color="#FFFFFF"></b-icon>
                    </b-button >
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div id="page-wrapper">
            <!-- 사이드바 -->
            <sidebar
                v-on:goToDiscountList="goToDiscountList"
                v-on:goToCategory="goToCategory"
                v-on:goToBrand="goToBrand"
                v-on:findBrandList="findBrandList"
                :brands="brands"></sidebar>
            <div id="page-content-wrapper">
                <router-view
                    style="margin-top:30px"
                    :products="products"
                    :currentPage="currentPage"
                    :rows="rows"
                    :perpage="perpage"
                    v-on:goNewPage="goNewPage"></router-view>
            </div>
        </div>

    </div>
</template>

<script>
    import Chart from 'chart.js'
    import axios from 'axios'
    import Sidebar from './components/SideBar'
    export default {
        name: 'App',
        components: {
            sidebar: Sidebar
        },
        data() {
            return {
                category: {
                    top: '001',
                    outer: '002',
                    pants: '003',
                    bag: '004',
                    sneakers: '018',
                    shoes: '005',
                    headwear: '007',
                    skirt: '022',
                    onepiece: '020',
                    socks: '008'
                },
                numToCategory: {
                    '001': 'Top',
                    '002': 'Outer',
                    '003': 'Pants',
                    '004': 'Bag',
                    '018': 'Sneakers',
                    '005': 'Shoes',
                    '007': 'Headwear',
                    '022': 'Skirt',
                    '020': 'Onepiece',
                    '008': 'Socks/Legwear'
                },
                currentListTopic: "",
                products: [],
                currentPage: 1,
                curCategory: '',
                curBrand: '',
                curSearchTopic: '',
                rows: 0,
                perpage: 25,
                brands: [],
                searchText: ''
            }
        },
        methods: {
            goNewPage(page) {
                let self = this
                if (self.currentListTopic == "category") {
                    self.goToCategory(self.curCategory, page)
                } else if (self.currentListTopic == "brand") {
                    self.goToBrand(self.curBrand, page)
                } else if (self.currentListTopic == "search") {
                    self.search(self.curSearchTopic, page)
                } else if (self.currentListTopic == "discount") {
                    self.goToDiscountList(self.curCategory, page)
                }
            },
            goToCategory(category, page) {
                let self = this
                self.currentListTopic = "category"
                window.scrollTo(0, 0);
                axios
                    .get('http://15.164.229.12:8080/api/v1/product/list', {
                        params: {
                            "category": category,
                            "page": page
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        self.curCategory = category
                        if (this.$route.path !== '/') 
                            this
                                .$router
                                .push({name: 'List'})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToDiscountList(category, page) {
                let self = this
                self.currentListTopic = "discount"
                window.scrollTo(0, 0);
                axios
                    .get('http://15.164.229.12:8080/api/v1/product/discount', {
                        params: {
                            "category": category,
                            "page": page
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        self.curCategory = category
                        if (this.$route.path !== '/') 
                            this
                                .$router
                                .push({name: 'List'})
                        })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToBrand(name, page) {
                let self = this
                self.currentListTopic = "brand"
                self.curBrand = name;
                window.scrollTo(0, 0);
                axios
                    .get('http://15.164.229.12:8080/api/v1/product/brand', {
                        params: {
                            "name": name,
                            "page": page
                        }
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        if (this.$route.path !== '/') {
                            this
                                .$router
                                .push({name: 'List'})
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            search(topic, page) {
                if (topic.trim().length == 0) {
                    return;
                }
                let self = this
                self.currentListTopic = "search"
                self.curSearchTopic = topic;
                window.scrollTo(0, 0);
                axios
                    .get('http://15.164.229.12:8080/api/v1/search', {
                        params: {
                            "topic": topic
                                .trim()
                                .replace(" ", ""),
                            "page": page
                        }
                    })
                    .then((response) => {
                        self.searchText=''
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        if (this.$route.path !== '/') {
                            this
                                .$router
                                .push({name: 'List'})
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            findBrandList(typeNumber) {
                let self = this
                axios
                    .get('http://15.164.229.12:8080/api/v1/search/brands', {
                        params: {
                            "type": typeNumber
                        }
                    })
                    .then((response) => {
                        self.brands = response.data
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        },
        created() {
            let self = this
            self.currentListTopic = "category"
            self.curCategory = '001'
            self.goToCategory(self.curCategory, 0)
            self.findBrandList(1)
        }
    }
</script>

<style src="./assets/css/app.css"></style>