<template>
    <div style="margin-top:50px; margin-bottom:50px">
        <b-container class="bv-example-row">
            <h3 style="text-align : left; margin-top:50px;">
                <a :href="'/product/list?brand='+product.brand" style="color : #000000"><strong>{{product.brand}}</strong></a>
            </h3>
            <div style="text-align : left">
                <span style="font-size : 20px; color :#b2b2b2;">분류 :
                    {{numToCategory[product.category]}}</span>
            </div>
            <div style="text-align : left; margin-bottom : 30px">
                <span style="font-size : 20px;">{{product.productName}}</span>
            </div>
            <div style="text-align : left">
                <a
                    style="color: #007bff;"
                    :href="'https://store.musinsa.com/app/goods/' + product.productId"
                    target="_blank">상품 링크</a>
                <span>
                    /
                </span>
                <a
                    style="color: #007bff;"
                    :href="product.brandUrl"
                    target="_blank">브랜드 링크</a>
            </div>
            <div style=" text-align : center">
            <img v-bind:src="product.bigImg" style="max-width: 500px;"/>
            </div>
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
            <span style="color:rgb(234 7 7)">최근 한달 평균가(쿠폰 포함) :
                {{numberToPrice(getAvgPriceForMonth())}}원</span><br/>
            <span style="color:rgb(234 7 7)">과거 평균가(쿠폰 포함) :
                {{numberToPrice(avgPrice)}}원</span><br/>
            <span style="color:rgb(234 7 7)">과거 최저가(쿠폰 포함) :
                {{numberToPrice(minPrice)}}원</span><br/>
            <span style="color:rgb(234 7 7)" v-if="order==100  && isTodayUpdated()">
                <strong>오늘은 역대 최고가입니다.</strong>
            </span>
            <span style="color:rgb(234 7 7)" v-if="order==0  && isTodayUpdated()">
                <strong>오늘은 역대 가장 낮은 가격입니다.</strong>
            </span><br/>
            <div>
                <b-form-rating
                    id="rating-inline"
                    inline="inline"
                    :value="lastPrice.rating/20"
                    :readonly="true"
                    :show-value="true"
                    :no-border="true"></b-form-rating>
                <small >({{lastPrice.ratingCount}}개의 평가)</small>
            </div>
            <b-form-select v-model="selected" :options="date_selector" @change="generatePriceData(selected)"></b-form-select>
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
                selected: 30,
                date_selector: [
                    { value: 7, text: '최근 일주일 데이터조회' },
                    { value: 30, text: '최근 한 달 데이터조회' },
                    { value: 365, text: '전체 데이터조회'}
                ],
                product: Object,
                prices: [],
                category: this.$parent.curCategory,
                lastPrice: Object,
                avgPrice : 0,
                minPrice : 0,
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
            generatePriceData(range) {
                this.dateList= []
                this.priceList= []
                this.realPriceList= []
                var end = this.prices.length
                var updatedAtArr = this.updatedAt.split('-')
                var updatedAt = new Date(updatedAtArr[0], updatedAtArr[1] - 1, updatedAtArr[2].slice(0,2) - range)
                    for (var i = 0; i < end; i++) {
                        if(updatedAt.getTime() > new Date(this.prices[end-1-i].createdDate).getTime()){
                            continue;
                        }
                        this.priceList.push(this.prices[end - 1 - i].price)
                        this.realPriceList.push(this.prices[end - 1 - i].price + this.prices[end - 1 - i].coupon)
                        this.dateList.push(this.prices[end - 1 - i].createdDate)
                    }
                    this.chartRender();
            },
            chartRender() {
                this.datasets[0].data = this.priceList
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
                                            return value
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
                this.$refs.chart.renderChart(this.datacollection, this.options)
            },
            getAvgPriceForMonth() {
                var sum = 0;
                var count = 0;
                var end = this.prices.length
                var updatedAtArr = this.updatedAt.split('-')
                var updatedAt = new Date(updatedAtArr[0], updatedAtArr[1] - 1, updatedAtArr[2] - 30)
                    for (var i = 0; i < end; i++) {
                        if(updatedAt.getTime() > new Date(this.prices[end-1-i].createdDate).getTime()){
                            continue;
                        }
                        sum += (this.prices[end - 1 - i].price + this.prices[end - 1 - i].coupon)
                        count++
                    }   
                return Math.ceil(sum/count);
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
                return Math.min.apply(null, list.slice(1, list.length))
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
                    max = max <= list[i] ? list[i] : max;
                    if (list[0] > list[i]) {
                        count += 1
                    }
                }
                if (max == list[0]) {
                    return 100;
                }
                return Math.ceil(count / (list.length - 1) * 100)
            },
            isTodayUpdated() {
                return new Date(this.updatedAt).toISOString().slice(0, 10) == this.lastPrice.createdDate
            },
        },
        created() {
            let self = this;
            axios
                .get('https://www.musinsa.info/api/v1/product', {
                    params: {
                        "id": this.$route.query.id
                    }
                })
                .then(function (response) {
                    self.prices = response.data.prices
                    self.product = response.data
                    self.lastPrice = self.prices[0]
                    self.curCategory = self.product.category
                    self.generatePriceData(30)
                    var realPriceList = []
                    for (var i = 0; i < self.prices.length; i++) {
                        realPriceList.push(self.prices[i].price + self.prices[i].coupon)
                    }    
                    self.avgPrice = self.computeAvg(realPriceList)
                    self.minPrice = self.computeMin(realPriceList)
                    self.order = self.computeOrder(realPriceList);
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>
<style></style>