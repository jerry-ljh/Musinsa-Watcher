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
                        <b-form-input size="md" class="mr-sm-2" placeholder="Search"></b-form-input>
                        <b-button
                            size="sm"
                            class="my-2 my-sm-0"
                            type="submit"
                            style="background-color : #000000">
                            <b-icon icon="search" font-scale="1.5" color="#FFFFFF"></b-icon>
                        </b-button >
                    </b-nav-form>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div id="page-wrapper">
            <!-- 사이드바 -->
            <div id="sidebar-wrapper">
                <ul class="sidebar-nav">
                    <li class="sidebar-brand">
                        <a href="javascript:void(0)">Jerry</a>
                    </li>
                    <li>
                        <a href="javascript:void(0)">전체
                            <small style="color : #b2b2b2">All</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.top)">상의<small style="color : #b2b2b2">Top</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.outer)">아우터<small style="color : #b2b2b2">Outer</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.onepiece)">원피스<small style="color : #b2b2b2">Onepiece</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.pants)">바지<small style="color : #b2b2b2">Pants</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.skirt)">스커트<small style="color : #b2b2b2">Skirt</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.bag)">가방<small style="color : #b2b2b2">Bag</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.sneakers)">스니커즈<small style="color : #b2b2b2">Sneakers</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.shoes)">신발<small style="color : #b2b2b2">Shoes</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.headwear)">모자<small style="color : #b2b2b2">HeadWear</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.socks)">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear</small>
                        </a>
                    </li>
                </ul>
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
                products: [],
                currentPage: 1,
                curCategory: '',
                rows: 0,
                perpage: 25
            }
        },
        methods: {
            goNewPage(page) {
                let self = this
                axios
                    .get('http://localhost:8080/api/v1/product/list', {
                        params: {
                            "category": this.curCategory,
                            "page": page
                        }
                    })
                    .then((response) => {
                        console.log(response);
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToCategory(category) {
                let self = this
                axios
                    .get('http://localhost:8080/api/v1/product/list', {
                        params: {
                            "category": category
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
            }
        },
        created() {
            let self = this
            self.curCategory = '001'
            axios
                .get('http://localhost:8080/api/v1/product/list', {
                    params: {
                        "category": self.curCategory
                    }
                })
                .then((response) => {
                    console.log(response);
                    self.products = response.data.content
                    self.currentPage = response.data.pageable.pageNumber + 1
                    self.rows = response.data.totalElements
                    self.perpage = response.data.pageable.pageSize
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    }
</script>

<style src="./assets/css/app.css"></style>