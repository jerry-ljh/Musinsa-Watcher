<template>
    <div>
        <b-container class="bv-example-row">
            <h3 style="text-align : left">
                <strong>{{product.brand}}</strong>
            </h3>
            <div style="text-align : left">
                <span style="font-size : 20px;">분류 :
                    {{category}}</span>
            </div>
            <div style="text-align : left">
                <span style="font-size : 20px;">{{product.productName}}</span>
            </div>
            <div style="text-align : left">
                <small class="text-muted">updated
                    {{product.modifiedDate}}</small>
            </div>
            <b-row>
                <b-col>
                    <img v-bind:src="product.bigImg"/></b-col>
                <b-col>
                    <div>쿠폰 :
                        {{lastPrice.coupon}}</div>
                    <div>
                        <span style="text-decoration: line-through; color :#b2b2b2 ">
                            {{lastPrice.delPrice}}원</span>
                    </div>
                    <div>할인가 :
                        {{lastPrice.price}}</div>
                    <div>랭킹 :
                        {{lastPrice.rank}}</div>
                    <b-form-rating
                        id="rating-inline"
                        inline="inline"
                        :value="lastPrice.rating/20"
                        readonly="true"
                        show-value="true"
                        no-border="true"></b-form-rating>
                    <div>별점 수 :
                        {{lastPrice.ratingCount}}</div>
                    <a v-bind:href="'https://store.musinsa.com/app/goods/' + product.productId">상품 링크</a>
                    <a v-bind:href="product.brandUrl">브랜드 링크</a>
                </b-col>
            </b-row>
            <b-row>
                <line-chart></line-chart>
                <bar-chart></bar-chart>
            </b-row>
        </b-container>
    </div>
</template>
<script>
    import LineChart from './LineChart'
    import BarChart from './BarChart'
    import axios from 'axios'

    export default {
        components: {
            LineChart,
            BarChart
        },
        data() {
            return {
                product: Object,
                prices: [],
                priceChartData: [],
                rankChartData: [],
                ratingChartData: [],
                category: this.$parent.curCategory,
                lastPrice: Object
            }
        },
        methods: {
            generatePriceData() {
                this.prices
            }
        },
        created() {
            let self = this
            axios
                .get('http://localhost:8080/api/v1/product', {
                    params: {
                        "id": this.$route.params.productId
                    }
                })
                .then(function (response) {
                    console.log(response.data)
                    self.prices = response.data.prices
                    self.product = response.data
                    self.lastPrice = self.prices[0]
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>
<style></style>