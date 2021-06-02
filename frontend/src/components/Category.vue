<template>
    <div>
        <ul class="category-nav">
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.top)">상의<small style="color : #b2b2b2">Top
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.top)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.top)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.outer)">아우터<small style="color : #b2b2b2">Outer
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.outer)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.outer)}})</span>
                </a>

            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.onepiece)">원피스<small style="color : #b2b2b2">Onepiece
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.onepiece)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.onepiece)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.pants)">바지<small style="color : #b2b2b2">Pants
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.pants)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.pants)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.skirt)">스커트<small style="color : #b2b2b2">Skirt
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.skirt)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.skirt)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.bag)">가방<small style="color : #b2b2b2">Bag
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.bag)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.bag)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.sneakers)">스니커즈<small style="color : #b2b2b2">Sneakers
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.sneakers)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.sneakers)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.shoes)">신발<small style="color : #b2b2b2">Shoes
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.shoes)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.shoes)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.headwear)">모자<small style="color : #b2b2b2">HeadWear
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.headwear)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.headwear)}})</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0)" v-on:click="selector(category.socks)">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear
                    </small>
                    <span v-if="type=='discount'" style="color : #b2b2b2">({{keyToValue(discountCategory, category.socks)}})</span>
                    <span v-if="type=='minimum'" style="color : #b2b2b2">({{keyToValue(minimumCategory, category.socks)}})</span>
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
    import axios from 'axios'
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
                type: '',
                discountCategory: {},
                minimumCategory: {}
            }
        },
        methods: {
            selector(category) {
                if (this.type == 'rank') {
                    this.goToRank(category, 1)
                } else if (this.type == 'discount') {
                    this.goToDiscountList(category, 1)
                } else if (this.type == 'minimum') {
                    this.goToMinimumList(category, 1)
                }
            },
            goToRank(category, page) {
                this.$emit('isLoading', true)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "category": category,
                            "page": page,
                            "size": 40,
                            "type" : 'rank',
                        }
                    })
                    .catch(() => {})
                },
            goToDiscountList(category, page) {
                this.$emit('isLoading', true)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "category": category,
                            "page": page,
                            "size": 40,
                            "type": 'discount',
                            "sort": "percent,desc"
                        }
                    })
                    .catch(() => {})
                },
            goToMinimumList(category, page) {
                this.$emit('isLoading', true)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "category": category,
                            "page": page,
                            "size": 40,
                            "type": 'minimum',
                            "sort": "percent,desc"
                        }
                    })
                    .catch(() => {})
                },
            findDiscountList() {
                axios
                    .get('https://www.musinsa.info/api/v1/product/discount/today/count')
                    .then((response) => {
                        this.discountCategory = response.data.categoryProductMap
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            },
            findMinimumList() {
                axios
                    .get('https://www.musinsa.info/api/v1/product/minimum-price/today/count')
                    .then((response) => {
                        this.minimumCategory = response.data.categoryProductMap
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
            this.type = this.$route.query.type
            if(this.type == "discount"){
            this.findDiscountList();
            }else if(this.type == "minimum"){
            this.findMinimumList();
            }
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