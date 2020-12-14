<template>
    <div id="sidebar-wrapper">
        <b-tabs content-class="mt-3" style="margin-top : 20px;">
            <b-tab title="품목" active="active" align="left">
                <ul class="sidebar-nav">
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.top, 1)">상의<small style="color : #b2b2b2">Top</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.outer, 1)">아우터<small style="color : #b2b2b2">Outer</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.onepiece, 1)">원피스<small style="color : #b2b2b2">Onepiece</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.pants, 1)">바지<small style="color : #b2b2b2">Pants</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.skirt, 1)">스커트<small style="color : #b2b2b2">Skirt</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.bag, 1)">가방<small style="color : #b2b2b2">Bag</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.sneakers, 1)">스니커즈<small style="color : #b2b2b2">Sneakers</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.shoes, 1)">신발<small style="color : #b2b2b2">Shoes</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.headwear, 1)">모자<small style="color : #b2b2b2">HeadWear</small>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToCategory(category.socks, 1)">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear</small>
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
                    <li v-for="brand in Object.keys(brands)" v-bind:key="brand">
                        <a href="javascript:void(0)" v-on:click="goToBrand(brand, 1)">{{brand}}
                            <span style="color : #b2b2b2">({{brands[brand]}})</span>
                        </a>
                    </li>
                </ul>
            </b-tab>
            <b-tab title="오늘 할인 상품">
                <ul class="sidebar-nav">
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.top, 1)">상의<small style="color : #b2b2b2">Top</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.top)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.outer, 1)">아우터<small style="color : #b2b2b2">Outer</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.outer)}})</span>
                        </a>
                    </li>
                    <li>
                        <a
                            href="javascript:void(0)"
                            v-on:click="goToDiscountList(category.onepiece, 1)">원피스<small style="color : #b2b2b2">Onepiece</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.onepiece)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.pants, 1)">바지<small style="color : #b2b2b2">Pants</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.pants)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.skirt, 1)">스커트<small style="color : #b2b2b2">Skirt</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.skirt)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.bag, 1)">가방<small style="color : #b2b2b2">Bag</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.bag)}})</span>
                        </a>
                    </li>
                    <li>
                        <a
                            href="javascript:void(0)"
                            v-on:click="goToDiscountList(category.sneakers, 1)">스니커즈<small style="color : #b2b2b2">Sneakers</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.sneakers)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.shoes, 1)">신발<small style="color : #b2b2b2">Shoes</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.shoes)}})</span>
                        </a>
                    </li>
                    <li>
                        <a
                            href="javascript:void(0)"
                            v-on:click="goToDiscountList(category.headwear, 1)">모자<small style="color : #b2b2b2">HeadWear</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.headwear)}})</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" v-on:click="goToDiscountList(category.socks, 1)">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear</small>
                            <span style="color : #b2b2b2">({{keyToValue(discountCategory, category.socks)}})</span>
                        </a>
                    </li>
                </ul>
            </b-tab>
        </b-tabs>
    </div>
</template>

<script>
    import axios from 'axios'
    import EventBus from "../utils/event-bus"
    export default {
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
                brands: {},
                discountCategory: {}
            }
        },
        methods: {
            goToCategory(category, page) {
                this.$emit('isLoading', true)
                EventBus.$emit("goToCategory", category, page)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "category": category,
                            "page": page
                        }
                    })
                    .catch(() => {})
                },
            goToDiscountList(category, page) {
                this.$emit('isLoading', true)
                EventBus.$emit("goToDiscountList", category, page)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "category": category,
                            "page": page,
                            "type": 'discount'
                        }
                    })
                    .catch(() => {})
                },
            goToBrand(name, page) {
                this.$emit('isLoading', true)
                EventBus.$emit("goToBrand", name, page)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "brand": name,
                            "page": page
                        }
                    })
                    .catch(() => {})
                },
            findBrandList(typeNumber) {
                axios
                    .get('http://www.musinsa.cf/api/v1/search/brands', {
                        params: {
                            "type": typeNumber
                        }
                    })
                    .then((response) => {
                        this.brands = response.data
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            findDiscountList() {
                axios
                    .get('http://www.musinsa.cf/api/v1/product/discount/list')
                    .then((response) => {
                        this.discountCategory = response.data
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            keyToValue(dict, key) {
                return key in dict
                    ? dict[key]
                    : 0
            }
        },
        created() {
            const brandType = 1
            this.findBrandList(brandType)
            this.findDiscountList();
        }
    }
</script>

<style src="../assets/css/app.css"></style>