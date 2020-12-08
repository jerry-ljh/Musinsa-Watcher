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
                    <b-nav-form>
                        <b-form-input
                            size="md"
                            class="mr-sm-2"
                            placeholder="Search"
                            v-model="searchText"></b-form-input>
                        <b-button
                            size="sm"
                            class="my-2 my-sm-0"
                            style="background-color : #000000"
                            v-on:click="search(searchText, 0)">
                            <b-icon icon="search" font-scale="1.5" color="#FFFFFF"></b-icon>
                        </b-button >
                    </b-nav-form>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div id="page-wrapper">
            <!-- 사이드바 -->
            <div id="sidebar-wrapper">
                <b-tabs content-class="mt-3" style="margin-top : 20px;">
                    <b-tab title="품목" active="active" align="left">
                        <ul class="sidebar-nav">
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.top, 0)">상의<small style="color : #b2b2b2">Top</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.outer, 0)">아우터<small style="color : #b2b2b2">Outer</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.onepiece, 0)">원피스<small style="color : #b2b2b2">Onepiece</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.pants, 0)">바지<small style="color : #b2b2b2">Pants</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.skirt, 0)">스커트<small style="color : #b2b2b2">Skirt</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.bag, 0)">가방<small style="color : #b2b2b2">Bag</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.sneakers, 0)">스니커즈<small style="color : #b2b2b2">Sneakers</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.shoes, 0)">신발<small style="color : #b2b2b2">Shoes</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.headwear, 0)">모자<small style="color : #b2b2b2">HeadWear</small>
                                </a>
                            </li>
                            <li>
                                <a href="javascript:void(0)" v-on:click="goToCategory(category.socks, 0)">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear</small>
                                </a>
                            </li>
                        </ul>
                    </b-tab>
                    <b-tab title="브랜드" align="center">
                        <table style="width:250px; text-align:center">
                            <tr >
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(1)">ㄱ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(2)">ㄴ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(3)">ㄷ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(4)">ㄹ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(5)">ㅁ</a>
                                </td>
                            </tr>
                            <tr >
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(6)">ㅂ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(7)">ㅅ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(8)">ㅇ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(9)">ㅈ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(10)">ㅊ</a>
                                </td>
                            </tr>
                            <tr >
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(11)">ㅋ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(12)">ㅌ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(13)">ㅍ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(14)">ㅎ</a>
                                </td>
                                <td class="brand">
                                    <a href="javascript:void(0)" v-on:click="findBrandList(15)">etc</a>
                                </td>
                            </tr>
                        </table>
                        <ul
                            class="sidebar-nav"
                            style="text-align:left; max-height: 1000px; overflow: auto;">
                            <li v-for="brand in brands" v-bind:key="brand">
                                <a href="javascript:void(0)" v-on:click="goToBrand(brand)">{{brand}}</a>
                            </li>
                        </ul>
                    </b-tab>
                </b-tabs>
            </div>
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
    export default {
        name: 'App',
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