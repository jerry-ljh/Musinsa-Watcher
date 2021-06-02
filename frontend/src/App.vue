<template>
    <div style="margin: 0 auto; max-width:1200px; width:100%;">
        <div>
            <navigation
                v-on:isLoading="isLoading"
                style="max-width:1200px; width:100%; position : fixed; top:0px; z-index:10;"></navigation>
        <b-overlay
            :show="loading"
            :no-fade=true
            spinner-type="null"
            @shown="onShown"
            @hidden="onHidden">
            <b-spinner v-if="loading"  style="position:fixed; top : 50%; left:47%; z-index: 1000; width: 3rem; height: 3rem;" label="Large Spinner"></b-spinner>
            <router-view
                v-on:isLoading="isLoading"
                :updatedAt="updatedAt"
                style="margin-top:20px; border: 1px solid #b2b2b260; margin-top : 75px"></router-view>
            <div style="text-align:center; color :#b2b2b2; margin-top : 20px; margin-bottom:20px">contact : gurwns5580@gmail.com</div>
        </b-overlay>
        </div>
    </div>
</template>

<script>
    import Chart from 'chart.js'
    import axios from 'axios'
    import Navigation from './components/Navigation.vue'

    export default {
        name: 'App',
        components: {
            navigation: Navigation
        },
        data() {
            return {loading: false, updatedAt: ''}
        },
        methods: {
            isLoading(value) {
                if (value) {
                    this.onShown()
                } else {
                    this.onHidden();
                }
                this.loading = value
            },
            onShown() {
                this.loading = true;
            },
            onHidden() {
                this.loading = false;
            }
        },
        created() {
            let self = this
            axios
                .get('https://www.musinsa.info/api/v1/product/cache/last-modified')
                .then(function (response) {
                    self.updatedAt = response.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }
</script>

<style src="./assets/css/app.css"></style>