<template>
    <div>
        <b-container class="bv-example-row">
            <h3 style="text-align : left">
                <strong>{{product.brand}}</strong>
            </h3>
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
                    <line-chart></line-chart>
                    <bar-chart></bar-chart>
                </b-col>
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
            return {product: Object, prices: [], priceChartData: [], rankChartData: [], ratingChartData: []}
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
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>
<style></style>