<template>
    <div>
        <b-container class="bv-example-row">
            <h3 style="text-align : left">
                <strong>네셔널지오그래픽</strong>
            </h3>
            <div style="text-align : left">
                <span style="font-size : 20px;">N194UDW890 카이만 3 구스 다운 벤치 롱패딩 점퍼 BLACK</span>
            </div>
            <div style="text-align : left">
                <small class="text-muted">updated 3 mins ago</small>
            </div>
            <b-row>
                <b-col>
                    <img
                        src="https://image.msscdn.net/images/goods_img/20200113/1268654/1268654_2_500.jpg"/></b-col>
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

    export default {
        components: {
            LineChart,
            BarChart
        },
        data() {
            return {}
        }
    }
</script>
<style></style>