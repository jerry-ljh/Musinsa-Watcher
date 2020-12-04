<template>
    <div>
        <b-container class="bv-example-row">
            <h3 style="text-align : left">
                <strong>{{product.brand}}</strong>
            </h3>
            <div style="text-align : left">
                <span style="font-size : 20px; color :#b2b2b2;">분류 :
                    {{$parent.numToCategory[category]}}</span>
            </div>
            <div style="text-align : left; margin-bottom : 30px">
                <span style="font-size : 20px;">{{product.productName}}</span>
            </div>
            <div style="text-align : left">
                <a v-bind:href="'https://store.musinsa.com/app/goods/' + product.productId">상품 링크</a>
                <span>
                    /
                </span>
                <a v-bind:href="product.brandUrl">브랜드 링크</a>
            </div>
            <b-row>
                <b-col style="width:600px; margin-bottom : 100px;">
                    <img v-bind:src="product.bigImg"/></b-col>
                <b-col style="width:600px; margin-bottom : 100px;">
                    <div style="margin-bottom: 10px">
                        <b-icon icon="trophy"></b-icon>
                        Ranking :
                        {{lastPrice.rank}}위 
                        <small class="text-muted" style="color :#b2b2b2;">
                              * {{lastPrice.createdDate}}일 기준</small>
                    </div>
                    <div style=" margin-bottom: 10px">
                        <h3 style="text-decoration: line-through; color :#b2b2b2 ">
                            <span v-if="lastPrice.delPrice!=0">
                                {{numberToPrice(lastPrice.delPrice)}}원
                            </span>
                            <span v-if="lastPrice.delPrice==0">
                                {{numberToPrice(lastPrice.price)}}원
                            </span>
                        </h3>
                    </div>
                    <div style=" margin-bottom: 10px">
                        <h3>
                            {{numberToPrice(lastPrice.price)}}원</h3>
                    </div>
                    <div v-if="lastPrice.coupon==0" style="color :#b2b2b2;">
                        <b-icon icon="sticky"></b-icon>
                        추가 쿠폰 없음</div>
                    <div v-if="lastPrice.coupon!=0">
                        <b-icon icon="sticky"></b-icon>
                        추가 쿠폰 :
                        {{numberToPrice(lastPrice.coupon)}}원</div>
                    <div style="vertical-align : middle">
                        <b-form-rating
                            id="rating-inline"
                            inline="inline"
                            :value="lastPrice.rating/20"
                            :readonly="true"
                            :show-value="true"
                            :no-border="true"></b-form-rating>
                        <small style="vertical-align : middle">({{lastPrice.ratingCount}}개의 평가)</small>
                    </div>
                    <line-chart ref="chart" :datacollection="datacollection" :options="options"></line-chart>
                    <div style=" margin-top: 10px">
                        <small class="text-muted">
                            * MUSINSA WATCHER는 카테고리별 1~3,000위 까지의 자료를 수집합니다. 순위 미진입시 데이터가 없을 수 있습니다.
                        </small>
                    </div>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>
<script>
    import LineChart from './LineChart'
    import BarChart from './BarChart'
    import axios from 'axios'
    import Vue from 'vue'

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
                lastPrice: Object,
                dateList: [],
                priceList: [],
                realPriceList: [],
                couponList: [],
                datasets: [
                    {
                        label: '',
                        backgroundColor: '#f87979',
                        pointBackgroundColor: 'white',
                        borderWidth: 1,
                        pointBorderColor: '#249EBF',
                        data: []
                    }
                ],
                datacollection: {},
                options: {}
            }
        },
        methods: {
            generatePriceData() {
                for (var i = this.prices.length - 1; i >= 0; i--) {
                    this
                        .priceList
                        .push(this.prices[i].price)
                    this
                        .couponList
                        .push(this.prices[i].coupon)
                    this
                        .realPriceList
                        .push(this.prices[i].price + this.prices[i].coupon)
                    this
                        .dateList
                        .push(this.prices[i].createdDate)
                }
                this.chartRender();
            },
            chartRender() {
                this
                    .datasets[0]
                    .data = this
                    .priceList
                this.datacollection = {
                    labels: this.dateList,
                    datasets: [
                        {
                            label: '가격',
                            pointBackgroundColor: 'white',
                            borderWidth: 2,
                            borderColor: '#249EBF',
                            pointBorderColor: '#249EBF',
                            fill: false,
                            data: this.priceList
                        }, {
                            label: '쿠폰',
                            pointBackgroundColor: 'white',
                            borderWidth: 2,
                            borderColor: '#b2b2b2',
                            pointBorderColor: '#b2b2b2',
                            fill: false,
                            data: this.couponList
                        }, {
                            label: '실 구매가',
                            pointBackgroundColor: 'white',
                            borderWidth: 2,
                            borderColor: 'blue',
                            pointBorderColor: 'blue',
                            fill: false,
                            data: this.realPriceList
                        }
                    ]
                },

                this.options = {
                    scales: {
                        yAxes: [
                            {
                                gridLines: {
                                    display: true
                                }
                            }
                        ],
                        xAxes: [
                            {
                                gridLines: {
                                    display: false
                                }
                            }
                        ]
                    },
                    legend: {
                        display: true
                    },
                    title: {
                        display: true,
                        text: '가격 차트'
                    },
                    responsive: true,
                    maintainAspectRatio: false
                }
                this
                    .$refs
                    .chart
                    .renderChart(this.datacollection, this.options)
            },
            numberToPrice(number) {
                if (number == null) {
                    return number
                }
                return number.toLocaleString();
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
                    self.prices = response.data.prices
                    self.product = response.data
                    self.lastPrice = self.prices[0]
                    self.generatePriceData()
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>
<style></style>