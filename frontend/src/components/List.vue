<template>
    <div>
        <b-card-group deck style="width:1300px; margin-bottom : 30px;" v-for="(productDeck, idx) in itemListToCardDeck(products)" v-bind:key="idx">
            <b-card
                @click="goToDetail(product)"
                :img-src="product.img"
                align="center"
                v-for="product in productDeck"
                v-bind:key="product.productId">
                <h5
                    style="color : rgb(234 7 7); position: absolute; top: 10px; left: 10px"
                    v-if="product.discount !=null">-{{Math.ceil(product.percent)}}%</h5>
                <b-card-text>
                    <h6 style="text-align : center">
                        <strong>{{product.brand}}</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">{{truncateProductName(product.productName)}}</span>
                    </div>
                </b-card-text>
                <template #footer>
                    <em>
                        <div v-if="product.discount !=null">
                            <span style="text-decoration: line-through; color :#b2b2b2; margin-right:5px">{{numberToPrice(product.discount + product.price)}}원</span>
                            <span style="color:#ae0000">
                                <strong>{{numberToPrice(product.price)}}원</strong>
                            </span>
                        </div>
                        <small v-if="product.discount ==null" class="text-muted">updated
                            {{product.modifiedDate}}</small>
                    </em>
                </template>
            </b-card>
        </b-card-group>

        <div class="mt-3">
            <b-pagination
                v-model="page"
                @input="newPage(page)"
                :value="refreshPage"
                :total-rows="rows"
                align="center"
                :per-page="perpage"
                :limit="limit"
                style="margin-bottom : 100px"></b-pagination>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {limit: 10, page: this.currentPage, columnCount :5, productList : []}
        },
        props: [
            'products', 'currentPage', 'perpage', 'rows'
        ],
        computed: {
            refreshPage() {
                this.page = this.currentPage
                return this.page
            }
        },
        methods: {
            goToDetail(product) {
                this
                    .$router
                    .push({name: 'Detail', params: product})
            },
            truncateProductName(productName) {
                return productName.length > 40
                    ? productName.substr(0, 40) + '...'
                    : productName;

            },
            newPage(page) {
                if (page != this.currentPage) {
                    this.$emit('goNewPage', this.page - 1)
                    window.scrollTo(0, 0);
                }
            },
            numberToPrice(number) {
                if (number == null) {
                    return number
                }
                return number.toLocaleString();
            },
            itemListToCardDeck(list){
                var productList = []
                for(var i=0; i< this.columnCount; i++){
                    productList.push(list.slice(i*this.columnCount, (i+1)*this.columnCount))
                }
                return productList
            }
        }
    }
</script>

<style src="../assets/css/itemList.css"></style>