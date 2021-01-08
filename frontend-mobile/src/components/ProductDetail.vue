<template>
    <div style="margin-top:50px; margin-bottom:50px">
        <b-container class="bv-example-row">
            <h3 style="text-align : left">
                <strong>{{product.brand}}</strong>
            </h3>
            <div style="text-align : left">
                <span style="font-size : 20px; color :#b2b2b2;">분류 :
                    {{numToCategory[product.category]}}</span>
            </div>
            <div style="text-align : left; margin-bottom : 30px">
                <span style="font-size : 20px;">{{product.productName}}</span>
            </div>
            <div style="text-align : left">
                <span
                    style="color: #007bff;"
                    v-on:click="outbound(product.productId, 'https://store.musinsa.com/app/goods/' + product.productId)">상품 링크</span>
                <span>
                    /
                </span>
                <span
                    style="color: #007bff;"
                    v-on:click="outbound(product.productId, product.brandUrl)">브랜드 링크</span>
            </div>
            <img v-bind:src="product.bigImg" style="width: 100%;"/>
            <div style="margin-bottom: 10px">
                <b-icon icon="trophy"></b-icon>
                Ranking :
                {{lastPrice.rank}}위
                <small class="text-muted" style="color :#b2b2b2;">
                    *
                    {{lastPrice.createdDate}}일 기준</small>
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
                    {{numberToPrice(lastPrice.price)}}원
                </h3>
            </div>
            <div v-if="lastPrice.coupon==0" style="color :#b2b2b2;">
                <b-icon icon="sticky"></b-icon>
                추가 쿠폰 없음</div>
            <div v-if="lastPrice.coupon!=0">
                <b-icon icon="sticky"></b-icon>
                추가 쿠폰 :
                {{numberToPrice(lastPrice.coupon)}}원</div>
            <span style="color:rgb(234 7 7)">과거 최저가(쿠폰 포함) :
                {{numberToPrice(computeMin(this.realPriceList))}}원</span><br/>
            <span style="color:rgb(234 7 7)">과거 평균가(쿠폰 포함) :
                {{numberToPrice(computeAvg(this.realPriceList))}}원</span><br/>
            <span style="color:rgb(234 7 7)" v-if="order==100  && isTodayUpdated()">
                <strong>오늘은 역대 최고가입니다.</strong>
            </span>
            <span style="color:rgb(234 7 7)" v-if="order==0  && isTodayUpdated()">
                <strong>오늘은 역대 가장 낮은 가격입니다.</strong>
            </span>
            <span
                style="color:rgb(234 7 7) "
                v-if="order!=0 &&order!=100 && isTodayUpdated()">오늘은 역대 상위
                {{numberToPrice(order)}}%로 낮은 가격입니다.</span><br/>
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
                    * MUSINSA WATCHER는 카테고리별 일간 랭킹 데이터를 수집합니다. 순위 미진입시 데이터가 없을 수 있습니다.
                </small>
            </div>
        </b-container>
    </div>
</template>
<script>
    import LineChart from './LineChart'
    import axios from 'axios'
    import EventBus from '../utils/event-bus';

    export default {
        components: {
            LineChart
        },
        props: ['updatedAt'],
        data() {
            return {
                product: Object,
                prices: [],
                category: this.$parent.curCategory,
                lastPrice: Object,
                dateList: [],
                priceList: [],
                realPriceList: [],
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
                order: 0,
                datacollection: {},
                options: {},
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
                }
            }
        },
        methods: {
            generatePriceData() {
                var end = this
                    .prices
                    .length
                    for (var i = 0; i < end; i++) {
                        this
                            .priceList
                            .push(this.prices[end - 1 - i].price)
                        this
                            .realPriceList
                            .push(this.prices[end - 1 - i].price + this.prices[end - 1 - i].coupon)
                        this
                            .dateList
                            .push(this.prices[end - 1 - i].createdDate)
                    }
                    this
                    .chartRender();
                this.order = this.computeOrder(this.realPriceList);
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
                            borderColor: 'rgba(255, 99, 132, 1)',
                            pointBorderColor: 'rgba(255, 99, 132, 1)',
                            fill: false,
                            data: this.priceList
                        }, {
                            label: '가격(쿠폰 포함)',
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
                                },
                                ticks: {
                                    callback: function (value, index, values) {
                                        return value.toLocaleString() + "원";
                                    },
                                    beginAtZero: true
                                }
                            }
                        ],
                        xAxes: [
                            {
                                gridLines: {
                                    display: false
                                },
                                ticks: {
                                    callback: function (value, index, values) {
                                        if (values.length <= 7) {
                                            return value
                                        } else {
                                            var label = index % (parseInt(values.length / 7)) == 0
                                                ? value
                                                : ''
                                            return label
                                        }
                                    }
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
            },
            computeMin(list) {
                if (list.length == 1) {
                    return list[0]
                }
                return Math
                    .min
                    .apply(null, this.realPriceList.slice(0, this.realPriceList.length - 1))
            },
            computeAvg(list) {
                if (list.length == 1) {
                    return list[0]
                }
                var sum = 0;
                for (var i = 0; i < list.length - 1; i++) {
                    sum += parseInt(list[i], 10);
                }
                var avg = sum / (list.length - 1);
                return Math.ceil(avg)
            },
            computeOrder(list) {
                if (list.length == 1) {
                    return 100;
                }
                var count = 0;
                var max = 0;
                for (var i = 0; i < list.length - 1; i++) {
                    max = max <= list[i]
                        ? list[i]
                        : max;
                    if (list[list.length - 1] > list[i]) {
                        count += 1
                    }
                }
                if (max == list[list.length - 1]) {
                    return 100;
                }
                return Math.ceil(count / (list.length - 1) * 100)
            },
            isTodayUpdated() {
                if (this.lastPrice.createdDate == null || this.updatedAt == null) {
                    return false
                }
                var lastUpdateArr = this
                    .lastPrice
                    .createdDate
                    .split('-')
                var lastUpdate = new Date(
                    lastUpdateArr[0],
                    lastUpdateArr[1] - 1,
                    lastUpdateArr[2]
                ).toLocaleDateString()
                var updatedAtArr = this
                    .updatedAt
                    .split('-')
                var updatedAt = new Date(updatedAtArr[0], updatedAtArr[1] - 1, updatedAtArr[2]).toLocaleDateString()
                return lastUpdate == updatedAt
            },
            outbound(productId, href) {
                axios
                    .get('https://api.musinsa.cf/api/product/link', {
                        params: {
                            'id': productId
                        }
                    })
                    .then(function (response) {
                        window.location.href = href;
                    });

            }
        },
        created() {
            let self = this
            axios
                .get('https://api.musinsa.cf/api/v1/product', {
                    params: {
                        "id": this.$route.query.id
                    }
                })
                .then(function (response) {
                    self.prices = response.data.prices
                    self.product = response.data
                    self.lastPrice = self.prices[0]
                    self.curCategory = self
                        .product
                        .category
                        self
                        .generatePriceData()
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>
<style></style>