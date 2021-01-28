<template>
    <div>
        <b-navbar style="background-color:#FFFFFF; width:100%">
        <b-form-input
                size="md"
                class="mr-sm-2"
                placeholder="브랜드명을 입력해주세요"
                v-on:keyup.enter="searchBrand(searchText)"
                @update="searchBrand(searchText)"
                v-model="searchText"></b-form-input>
        </b-navbar>
        <table style="width:100%;text-align:center; margin-bottom:20px">
            <tr >
                <td class="brand" v-on:click="findBrandList(1)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(1)">ㄱ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(2)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(2)">ㄴ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(3)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(3)">ㄷ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(4)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(4)">ㄹ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(5)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(5)">ㅁ</a>
                </td>
            </tr>
            <tr >
                <td class="brand" v-on:click="findBrandList(6)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(6)">ㅂ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(7)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(7)">ㅅ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(8)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(8)">ㅇ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(9)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(9)">ㅈ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(10)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(10)">ㅊ</a>
                </td>
            </tr>
            <tr >
                <td class="brand" v-on:click="findBrandList(11)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(11)">ㅋ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(12)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(12)">ㅌ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(13)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(13)">ㅍ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(14)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(14)">ㅎ</a>
                </td>
                <td class="brand" v-on:click="findBrandList(15)">
                    <a href="javascript:void(0)" v-on:click="findBrandList(15)">etc</a>
                </td>
            </tr>
        </table>
        <ul
            class="category-nav"
            style="text-align:left; max-height: 1000px; overflow: auto;">
            <li v-for="brand in Object.keys(brands)" v-bind:key="brand">
                <a href="javascript:void(0)" v-on:click="goToBrand(brand, 1)">{{brand}}
                    <span style="color : #b2b2b2">({{brands[brand]}})</span>
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
    import axios from 'axios'
    import EventBus from "../utils/event-bus"
    export default {
        data() {
            return {
                brands : [],
                searchText: ''
            }
        },
        methods: {
            searchBrand(searchText) {
                if (searchText.trim().length == 0) {
                    return;
                }
                axios
                    .get('https://www.musinsa.cf/api/v1/search/brand', {
                        params: {
                            "name": searchText
                        }
                    })
                    .then((response) => {
                        this.brands = response.data
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            goToBrand(name, page) {
                this.$emit('isLoading', true)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "brand": name,
                            "page": page,
                            "type" : 'brand'
                        }
                    })
                    .catch(() => {})
                },
            findBrandList(typeNumber) {
                axios
                    .get('https://www.musinsa.cf/api/v1/search/brands', {
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
        },
        created() {
            const brandType = 1
            this.findBrandList(brandType)
        }
    }
</script>

<style>
    .category-nav {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .category-nav li {
        text-indent: 2em;
        line-height: 4em;
        border-bottom: 1px solid #b2b2b260;
    }

    .category-nav li a {
        display: block;
        text-decoration: none;
        color: #000;
    }

    .category-nav li:hover {
        background: #b2b2b21c;
    }

    .category-nav > .category-brand {
        font-size: 1.3em;
        line-height: 3em;
    }
</style>