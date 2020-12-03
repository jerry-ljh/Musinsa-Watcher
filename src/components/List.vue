<template>
    <div>
        <b-card-group deck="deck" style="margin-bottom : 100px">
            <b-card
                @click="goToDetail()"
                img-src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_125.jpg"
                img-alt="Image"
                align="right">
                <b-card-text >
                    <h6 style="text-align : center">
                        <strong>네셔널지오그래픽</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
                    </div>
                    <div style="text-align : center">
                        <small class="text-muted">updated 3 mins ago</small>
                    </div>
                </b-card-text>
            </b-card>
            <b-card
                @click="goToDetail()"
                img-src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_125.jpg"
                img-alt="Image"
                align="right">
                <b-card-text >
                    <h6 style="text-align : center">
                        <strong>네셔널지오그래픽</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
                    </div>
                    <div style="text-align : center">
                        <small class="text-muted">updated 3 mins ago</small>
                    </div>
                </b-card-text>
            </b-card>
            <b-card
                @click="goToDetail()"
                img-src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_125.jpg"
                img-alt="Image"
                align="right">
                <b-card-text >
                    <h6 style="text-align : center">
                        <strong>네셔널지오그래픽</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
                    </div>
                    <div style="text-align : center">
                        <small class="text-muted">updated 3 mins ago</small>
                    </div>
                </b-card-text>
            </b-card>
            <b-card
                @click="goToDetail()"
                img-src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_125.jpg"
                img-alt="Image"
                align="right">
                <b-card-text >
                    <h6 style="text-align : center">
                        <strong>네셔널지오그래픽</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
                    </div>
                    <div style="text-align : center">
                        <small class="text-muted">updated 3 mins ago</small>
                    </div>
                </b-card-text>
            </b-card>
            <b-card
                @click="goToDetail()"
                img-src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_125.jpg"
                img-alt="Image"
                align="right">
                <b-card-text >
                    <h6 style="text-align : center">
                        <strong>네셔널지오그래픽</strong>
                    </h6>
                    <div style="text-align : left">
                        <span style="font-size : 13px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
                    </div>
                    <div style="text-align : center">
                        <small class="text-muted">updated 3 mins ago</small>
                    </div>
                </b-card-text>
            </b-card>
        </b-card-group>

        <div class="mt-3">
            <b-pagination
                v-model="currentPage"
                :total-rows="rows"
                align="center"
                :per-page="perpage"
                :limit="limit"></b-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            return {rows: 3000, currentPage: 1, perpage: 25, limit: 10}
        },
        methods: {
            goToDetail() {
                this
                    .$router
                    .push({name: 'Detail'})
            }
        }
    }
</script>

<style>
    .card > img {
        max-width: 125px;
        max-height: 125px;
        margin: 10px auto 0;
    }

    .item-name {
        text-align: left;
    }

    .card {
        width: 125px;
        height: 300px;
    }

    .card:hover {
        background: #b2b2b21c;
    }
</style>