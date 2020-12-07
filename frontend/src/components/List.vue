<template>
    <div>
        <b-card-group
            columns="columns"
            style="width:1300px; margin-bottom : 100px; margin-right : 50px">
            <b-card
                @click="goToDetail(product)"
                :img-src="product.img"
                align="center"
                v-for="product in products"
                v-bind:key="product.productId">
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
                        <small class="text-muted">updated
                            {{product.modifiedDate}}</small>
                    </em>
                </template>
            </b-card>
        </b-card-group>

        <div class="mt-3">
            <b-pagination
                v-model="page"
                :change="newPage(page)"
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
            return {limit: 10, page: this.currentPage}
        },
        props: [
            'products', 'currentPage', 'perpage', 'rows'
        ],
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
                if (page > 0 && page != null && page != this.currentPage) {
                    this.$emit('goNewPage', page - 1)
                    window.scrollTo(0, 0);
                }

            }
        }
    }
</script>

<style src="../assets/css/itemList.css"></style>