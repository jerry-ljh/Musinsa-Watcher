<template>
    <div>
        <div style="padding-bottom: 10px; border-bottom: 10px solid #b2b2b260;">
            <b-tabs content-class="mt-3">
                <b-tab title="카테고리">
                    <b-container class="bv-example-row">
                        <b-row>
                            <b-col>
                                <b-form-checkbox-group
                                id="checkbox-group-1"
                                v-model="curCategory"
                                @change="addFilter"
                                :options="categoryOptions1"
                                name="flavour-1"
                                stacked>
                                </b-form-checkbox-group>
                            </b-col>
                            <b-col>
                                <b-form-checkbox-group
                                id="checkbox-group-2"
                                v-model="curCategory"
                                @change="addFilter"
                                :options="categoryOptions2"
                                name="flavour-2"
                                stacked>
                                </b-form-checkbox-group>
                            </b-col>
                            <b-col>
                                <b-form-checkbox-group
                                id="checkbox-group-3"
                                v-model="curCategory"
                                @change="addFilter"
                                :options="categoryOptions3"
                                name="flavour-3"
                                stacked>
                                </b-form-checkbox-group>
                            </b-col>
                        </b-row>
                    </b-container>
                </b-tab>
                <b-tab title="브랜드">
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
                        style="text-align:left; max-height: 180px; overflow: auto;">
                        <li v-for="brand in Object.keys(brands)" v-bind:key="brand">
                            <a href="javascript:void(0)" v-on:click="addBrand(brand)">{{brand}}</a>
                        </li>
                    </ul>
                </b-tab>
                <b-tab title="가격" style="text-align:left">
                    <b-form-group label="" v-slot="{ ariaDescribedby }">
                    <b-form-radio style="float:left; margin-left:30px" 
                        v-model="price_selected" 
                        @change="addFilter" 
                        :aria-describedby="ariaDescribedby" 
                        name="some-radios" 
                        :value=curPriceRange.A>2만원 이하
                    </b-form-radio>
                    <b-form-radio style="float:left; margin-left:30px" 
                        v-model="price_selected" 
                        @change="addFilter" 
                        :aria-describedby="ariaDescribedby" 
                        name="some-radios" 
                        :value=curPriceRange.B>2 ~ 4만원
                    </b-form-radio>
                    <b-form-radio style="float:left; margin-left:30px" 
                        v-model="price_selected" 
                        @change="addFilter" 
                        :aria-describedby="ariaDescribedby"
                        name="some-radios" 
                        :value=curPriceRange.C>4 ~ 6만원
                    </b-form-radio>
                    <b-form-radio style="float:left; margin-left:30px" 
                        v-model="price_selected" 
                        @change="addFilter" 
                        :aria-describedby="ariaDescribedby" 
                        name="some-radios" 
                        :value=curPriceRange.D>6 ~ 7만원
                    </b-form-radio>
                    <b-form-radio style="float:left; margin-left:30px" 
                        v-model="price_selected" 
                        @change="addFilter" 
                        :aria-describedby="ariaDescribedby" 
                        name="some-radios" 
                        :value=curPriceRange.E>7만원 이상
                    </b-form-radio>
                    </b-form-group>
                </b-tab>
            </b-tabs>
        </div>   
        <div style="margin-top:15px">
            <b-form-tags v-if="curCategory.length > 0" id="tags-with-dropdown" v-model="curCategory" @input="addFilter" no-outer-focus class="mb-2">
            <template v-slot="{ tags, disabled, addTag, removeTag }">
                <ul class="list-inline d-inline-block mb-2">
                    <li v-for="tag in curCategory" :key="tag" class="list-inline-item">
                        <b-form-tag
                            @remove="removeTag(tag)"
                            :title="tag"
                            :disabled="disabled"
                            variant="primary"
                        >{{ numToCategory[tag] }}</b-form-tag>
                    </li>
                </ul>
            </template>
            </b-form-tags>
            <b-form-tags v-if="curBrand.length > 0" id="tags-with-dropdown" v-model="curBrand" @input="addFilter" no-outer-focus class="mb-2">
                <template v-slot="{ tags, disabled, addTag, removeTag }">
                    <ul class="list-inline d-inline-block mb-2">
                        <li v-for="tag in curBrand" :key="tag" class="list-inline-item">
                            <b-form-tag
                                @remove="removeTag(tag)"
                                :title="tag"
                                :disabled="disabled"
                                variant="primary"
                            >{{ tag }}</b-form-tag>
                        </li>
                    </ul>
                </template>
            </b-form-tags>
            <b-form-tags v-if="price_selected.length != ''" id="tags-with-dropdown" v-model="price_selected" @input="addFilter" no-outer-focus class="mb-2">
                <template v-slot="{ tags, disabled, addTag, removeTag }">
                    <ul class="list-inline d-inline-block mb-2">
                        <li v-for="(tag, idx) in price_selected" :key="idx" class="list-inline-item">
                            <b-form-tag
                                @remove="removeTag(tag)"
                                :disabled="disabled"
                                variant="primary"
                            >{{ price_type_to_text[tag] }}</b-form-tag>
                        </li>
                    </ul>
                </template>
            </b-form-tags>
        </div>
        <div style="float : left; margin-top : 8px">
            <span
                v-if="currentListTopic == 'minimum'"
                style="color :#b2b2b2; margin-left:20px">
                오늘 최저가 상품
                <b-icon icon="question-octagon" variant="primary" id="question"></b-icon>
                <b-tooltip
                    v-if="currentListTopic == 'minimum'"
                    target="question"
                    triggers="hover">
                    오늘 최저가 상품은 최근 한달을 기준 가장 낮은 가격을 보인 상품을 조회합니다. 할인율은 최근 한달 평균가와 오늘 가격을 비교합니다. 
                </b-tooltip>
            </span>
            <span
                v-if="currentListTopic == 'discount'"
                style="color :#b2b2b2; margin-left:20px">
                오늘 할인 상품
                <b-icon icon="question-octagon" variant="primary" id="question"></b-icon>
                <b-tooltip
                    v-if="currentListTopic == 'discount'"
                    target="question"
                    triggers="hover">
                    오늘 할인 상품은 어제 가격보다 낮은 상품을 조회합니다.
                </b-tooltip>
            </span>
        </div>
        <div v-if="currentListTopic=='search'" style="margin-left:15px; margin-bottom :15px; font-size: 1.2em; font-weight: bold;">
        '{{curSearchTopic}}'로 검색한 결과입니다.
        </div>
        <b-form-checkbox
            id="checkbox-2"
            v-model="onlyTodayUpdatedData"
            @change="addDateFilter"
            :value="!onlyTodayUpdatedData"
            v-if="currentListTopic == 'brand' || currentListTopic == 'search'"
            name="checkbox-1"
            style="margin-left:15px; margin-top:1.7%; float:left "
        > 오늘 업데이트된 상품 
        </b-form-checkbox>
        <div
            style="text-align : right; margin-right :6%">
            
            <b-dropdown
                id="dropdown-1"
                right="right"
                variant="outline-dark "
                :text="curSortText"
                class="m-md-2">
                <b-dropdown-item v-on:click="changeSort('rank,asc')">인기 높은순</b-dropdown-item>
                <b-dropdown-item v-if="currentListTopic == 'discount' || currentListTopic == 'minimum'" v-on:click="changeSort('percent,desc')">할인율 높은순</b-dropdown-item>
                <b-dropdown-item v-on:click="changeSort('price,desc')">높은 가격순</b-dropdown-item>
                <b-dropdown-item v-on:click="changeSort('price,asc')">낮은 가격순</b-dropdown-item>
                <b-dropdown-divider></b-dropdown-divider>
            </b-dropdown>
        </div>
        <div v-if="products.length>0">
            <b-card-group
                deck
                style="margin : 5px; text-align:center!important;"
                v-for="(productDeck, idx) in itemListToCardDeck(products)"
                v-bind:key="idx">
                <b-card
                    @click="goToDetail(product)"
                    align="center"
                    v-for="product in productDeck"
                    v-bind:key="product.productId"
                    style="margin-bottom:30px; margin:10px; width:44%;">
                    <b-card-img :src="product.img"></b-card-img>

                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount !=null && product!=Object">{{Math.ceil(product.percent)}}% OFF</h6>
                    <h6
                        style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="currentListTopic == 'minimum' && product.todayPrice != null && product!=Object">{{Math.ceil((product.avgPrice - product.todayPrice) * 100/product.avgPrice)}}% OFF</h6>
                    <h6
                        style="position: absolute; top: 10px; left: 10px; background-color:#FFF; background-color: rgba( 255, 255, 255, 0.5 );"
                        v-if="product.discount ==null && currentListTopic == 'rank' && product!=Object">
                        {{product.rank}}위
                    </h6>
                    <b-card-text>
                        <h6 style="text-align : center; min-height:30px; margin-top:5px;">
                            <strong>{{product.brand}}</strong>
                        </h6>
                        <div
                            style="text-align : left; height : 48px; overflow:hidden; margin-bottom : 10px">
                            <span style="font-size : 13px;">{{product.productName}}</span>
                        </div>

                        <div v-if="product.discount !=null && product!=Object">
                            <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.discount + product.price)}}원</span><br/>
                            <span style="color:#ae0000">
                                <strong>{{numberToPrice(product.price)}}원</strong>
                            </span>
                        </div>
                        <div v-if="currentListTopic == 'minimum' && product.todayPrice != null && product!=Object">
                            <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.avgPrice)}}원</span><br/>
                            <span style="color:#ae0000">
                                <strong>{{numberToPrice(product.todayPrice)}}원</strong>
                            </span>
                        </div>
                        <div v-if="currentListTopic != 'minimum' && currentListTopic != 'discount' && product!=Object">
                            <span v-if="isTodayUpdated(product.modifiedDate)" style="color:#ae0000">
                                <strong>{{numberToPrice(product.realPrice)}}원</strong>
                            </span>
                            <span v-if="!isTodayUpdated(product.modifiedDate)" style="color:#b2b2b2">
                                <strong>{{numberToPrice(product.realPrice)}}원</strong>
                            </span>
                        </div>
                        <span v-if="product!=Object" style="font-size: 0.7em; color:#b2b2b2">{{product.modifiedDate}}
                            updated</span>
                    </b-card-text>
                </b-card>
            </b-card-group>
            <div class="mt-3">
                <b-pagination
                    v-model="currentPage"
                    @input="newPage(currentPage)"
                    :total-rows="rows"
                    align="center"
                    :per-page="perpage"
                    :limit="limit"
                    style="margin-bottom : 100px"></b-pagination>
            </div>
        </div>
        <div v-if="products.length==0 && $parent.loading == false">
            <h2 style="text-align:center">일치하는 결과가 없습니다!</h2>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'
    import EventBus from '../utils/event-bus';
    import Brand from './Brand'

    export default {
        components: {
            Brand
        },
        data() {
            return {
                limit: 10,
                bufferPage: 1,
                currentPage: 1,
                columnCount: 4,
                rows: 0,
                perpage: 40,
                searchText : "",
                currentListTopic: "",
                products: [],
                brands : [],
                curCategory: [],
                curBrand: [],
                curSearchTopic: '',
                curDateFilter : [],
                onlyTodayUpdatedData: false,
                curSort: 'percent,desc',
                curSortText: '',
                filter_category: [],
                API: "https://www.musinsa.info",
                category_dict: {
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
                categoryOptions: [
                { text: 'Top', value: '001' },
                { text: 'Outer', value: '002' },
                { text: 'Pants', value: '003' },
                { text: 'Bag', value: '004'},
                { text: 'Sneakers', value: '018' },
                { text: 'Shoes', value: '005' },
                { text: 'Headwear', value: '007' },
                { text: 'Skirt', value: '022' },
                { text: 'Onepiece', value: '020' },
                { text: 'Socks', value: '008' },
                ], 
                categoryOptions1: [
                { text: 'Top', value: '001' },
                { text: 'Outer', value: '002' },
                { text: 'Pants', value: '003' },
                { text: 'Socks', value: '008' },
                ], 
                categoryOptions2: [
                { text: 'Onepiece', value: '020' },
                { text: 'Sneakers', value: '018' },
                { text: 'Shoes', value: '005' },
                ], 
                categoryOptions3: [
                { text: 'Headwear', value: '007' },
                { text: 'Skirt', value: '022' },
                { text: 'Bag', value: '004'},
                ],
                price_type : {
                    "A" : [0, 20000],
                    "B" : [20000, 40000],
                    "C" : [40000, 60000],
                    "D" : [60000, 70000],
                    "E" : [70000, 1000000000],
                },
                curPriceRange : {
                    A : ["A"],
                    B : ["B"],
                    C : ["C"],
                    D : ["D"],
                    E : ["E"],
                },
                price_type_to_text : {
                    "A" : "2만원 이하",
                    "B" : "2만원 ~ 4만원",
                    "C" : "4만원 ~ 6만원",
                    "D" : "6만원 ~ 7만원",
                    "E" : "7만원 이상",
                },
                price_selected : [],
                selected : [],

            }
        },
        props: ['updatedAt'],
        methods: {
            filterClear(){
              this.curBrand = []
              this.curCategory = []
              this.curDateFilter = []
              this.addFilter()  
            },
            addBrand(brand){
                this.curBrand.push(brand);
                this.addFilter()
            },
            addDateFilter(){
                this.addFilter()
            },
            addFilter(){
                this.newPage(1)
            },
            goToDetail(product) {
                if(product == Object){
                    return
                }
                this.$router.push({name: 'Product', query: { "id": product.productId }, params: product})
                window.scrollTo(0, 0)
            },
            isTodayUpdated(date) {
                return new Date(this.updatedAt).toISOString().slice(0, 10) == date
            },
            sortToText(sort) {
                if (sort == 'price,asc') 
                    this.curSortText = '낮은 가격순';
                else if (sort == 'price,desc') 
                    this.curSortText = '높은 가격순';
                else if (sort == 'percent,desc')
                    this.curSortText = '할인율 높은순';
                else if (sort == 'rank,asc')
                    this.curSortText = '인기 높은순';
            },
            changeSort(sort) {
                this.sortToText(sort)
                this.$emit('isLoading', true)
                this.curSort = sort
                if (this.currentListTopic == "discount") {
                    this.goToDiscountList(1, this.curSort)
                } else if (this.currentListTopic == "minimum") {
                    this.goToMinimumList(1, this.curSort);
                } else if (this.currentListTopic == "rank") {
                    this.goToRank(1, this.curSort);
                } else if (this.currentListTopic == "brand") {
                    this.goToBrand(1, this.curSort);
                } else if (this.currentListTopic == "search") {
                    this.goToSearch(this.curSearchTopic, 1, this.curSort);
                }
                this.bufferPage = 1
            },
            newPage(page) {
                if (page == null) 
                    return
                this.$emit('isLoading', true)
                if (this.currentListTopic == "rank") {
                    this.goToRank(page, this.curSort)
                } else if (this.currentListTopic == "brand") {
                    this.goToBrand(page, this.curSort)
                } else if (this.currentListTopic == "search") {
    
                    this.goToSearch(this.curSearchTopic, page, this.curSort)
                } else if (this.currentListTopic == "discount") {
                    this.goToDiscountList(page, this.curSort)
                } else if (this.currentListTopic == "minimum") {
                    this.goToMinimumList(page, this.curSort)
                }
                this.bufferPage = page
            },
            numberToPrice(number) {
                if (number == null) {
                    return number
                }
                return number.toLocaleString();
            },
            itemListToCardDeck(list) {
                var productDeck = []
                for (var i = 0; i < this.perpage / this.columnCount; i++) {
                    var tempList = list.slice(i * this.columnCount, (i + 1) * this.columnCount)
                    const length = tempList.length
                    if(length%this.columnCount != 0){
                        for(var j = 0; j < this.columnCount - length; j++){
                            tempList.push(Object)
                        }
                    }
                    productDeck.push(tempList)
                }
                return productDeck
            },
            filter(){
                var query = {}
                if(this.curCategory.length > 0){
                    query["category"] = this.curCategory.toString()
                }
                if(this.curBrand.length > 0){
                    query["brand"] = this.curBrand.toString()
                }
                if(this.price_type[this.price_selected] != null){
                    query["minprice"] = this.price_type[this.price_selected][0]
                    query["maxprice"] = this.price_type[this.price_selected][1]
                } 
                query["size"] = 40
                return query
            },
            goToRank(page, sort) {
                let self = this
                self.currentListTopic = "rank"
                var query = this.filter()
                query["page"] = Math.max(page - 1, 0)
                query["sort"] = sort
                axios.get(this.API + '/api/v1/product/list', 
                    {
                        params: query
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        query["type"] = 'rank'
                        query["page"] = self.currentPage
                        this.$router.replace({name: 'ProductList', query: query}).catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0)
                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToMinimumList(page, sort) {
                let self = this
                self.currentListTopic = "minimum"
                var query = this.filter()
                query["page"] = Math.max(page - 1, 0)
                query["sort"] = sort
                axios.get(this.API + '/api/v1/product/minimum-price/today/list', 
                    {
                        params: query
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        query["type"] = 'minimum'
                        this.$router.replace({name: 'ProductList',
                                 query: query
                            }).catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);
                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToDiscountList(page, sort) {
                let self = this
                self.currentListTopic = "discount"
                var query = this.filter()
                query["page"] = Math.max(page - 1, 0)
                query["sort"] = sort
                axios.get(this.API + '/api/v1/product/discount/today/list', {
                        params: query
                    })
                    .then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        self.perpage = response.data.pageable.pageSize
                        query["type"] = 'discount'
                        this.$router.replace({
                                name: 'ProductList',
                                query: query
                            }).catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);

                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToBrand(page, sort) {
                let self = this
                var query = this.filter()
                query["page"] = Math.max(page - 1, 0)
                query["sort"] = sort
                query["onlyTodayUpdatedData"] = this.onlyTodayUpdatedData
                this.currentListTopic = "brand"
                axios
                    .get(this.API + '/api/v1/product/brand', {
                        params: query
                    }).then((response) => {
                        self.products = response.data.content
                        self.currentPage = response.data.pageable.pageNumber + 1
                        self.rows = response.data.totalElements
                        query["page"] = self.currentPage
                        query["type"] = 'brand'
                        self.perpage = response.data.pageable.pageSize
                        this.$router.replace({
                                name: 'ProductList',
                                query: query
                            }).catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);

                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            goToSearch(topic, page, sort) {
                if (topic.trim().length == 0) return;
                var query = this.filter()
                query["topic"] = topic.trim()
                query["page"] = Math.max(page - 1, 0)
                query["sort"] = sort
                query["onlyTodayUpdatedData"] = this.onlyTodayUpdatedData
                this.currentListTopic = "search"
                this.curSearchTopic = topic;
                axios.get(this.API + '/api/v1/search/product', {
                        params: query
                    })
                    .then((response) => {
                        this.products = response.data.content
                        this.currentPage = response.data.pageable.pageNumber + 1
                        this.rows = response.data.totalElements
                        this.perpage = response.data.pageable.pageSize;
                        query["page"] = this.currentPage
                        query["type"] = 'search'
                        this.$router.replace({
                                name: 'ProductList',
                                query: query
                            }).catch(() => {});
                        this.$emit('isLoading', false)
                        window.scrollTo(0, 0);
                    })
                    .catch((error) => {
                        this.$emit('isLoading', false)
                        console.log(error);
                    });
            },
            searchBrand(searchText) {
                if (searchText.trim().length == 0) return;
                axios.get(this.API+ '/api/v1/search/brand', {
                        params: {
                            "name": searchText
                        }
                    }).then((response) => {
                        this.brands = response.data.brandMap
                    }).catch((error) => {
                        console.log(error);
                    });
            },
            findBrandList(typeNumber) {
                axios.get(this.API+ '/api/v1/search/brand-initial', {
                        params: {
                            "type": typeNumber
                        }
                    }).then((response) => {
                        this.brands = response.data.brandMap
                    }).catch((error) => {
                        console.log(error);
                    });
            },
        },
        created() {
            this.$emit('isLoading', true)
            EventBus.$on("goToRank", (category, page) => {
                this.goToRank(category, page)
            })
            EventBus.$on("goToDiscountList", (category, page, sort) => {
                this.goToDiscountList(category, page, sort)
                this.sortToText(sort)
            })
            EventBus.$on("goToBrand", (name, page, updated, category) => {
                this.goToBrand(name, page, updated, category)
            })
            EventBus.$on("goToSearch", (searchText, page) => {
                this.filterClear()
                this.goToSearch(searchText, page)
            })
            EventBus.$on("goToMinimumList", (category, page, sort) => {
                this.goToMinimumList(category, page, sort)
                this.sortToText(sort)
            })
            this.curCategory = this.$route.query.category != null ? this.$route.query.category.split(",") : []
            this.curBrand = this.$route.query.brand != null ? this.$route.query.brand.split(",") :[]
            if (this.$route.query.type == 'rank') {
                this.goToRank(this.$route.query.page ? this.$route.query.page : 1)
                this.curSort = this.$route.query.sort ? this.$route.query.sort : "rank,asc"
            }
            if (this.$route.query.type == 'discount') {
                this.goToDiscountList(this.$route.query.page? this.$route.query.page : 1,
                    this.$route.query.sort ? this.$route.query.sort : "percent,desc")
                this.curSort = this.$route.query.sort ? this.$route.query.sort : "percent,desc" 
            }  
            if (this.$route.query.type == 'minimum') {
                this.goToMinimumList(this.$route.query.page ? this.$route.query.page : 1,
                    this.$route.query.sort ? this.$route.query.sort : "percent_desc")
                this.curSort = this.$route.query.sort ? this.$route.query.sort : "percent,desc" 
            }
            if (this.$route.query.type == 'search') {
                this.curSearchTopic = this.$route.query.topic
                this.goToSearch(this.$route.query.topic, this.$route.query.page ? this.$route.query.page : 1)
                this.curSort = this.$route.query.sort ? this.$route.query.sort : "rank,asc"
            }
            if (this.$route.query.type == 'brand') {
                this.goToBrand(this.$route.query.page ? this.$route.query.page : 1)
                this.curSort = this.$route.query.sort ? this.$route.query.sort : "rank,asc"
            }
            this.sortToText(this.curSort);
        },
        destroyed() {
            EventBus.$off("goToRank")
            EventBus.$off("goToDiscountList")
            EventBus.$off("goToMinimumList")
            EventBus.$off("goToBrand")
            EventBus.$off("goToSearch")
        }
    }
</script>

<style src="../assets/css/itemList.css"></style>
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