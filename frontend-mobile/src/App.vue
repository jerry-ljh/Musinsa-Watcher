<template>
    <div>
        <div
            style="display: block;
                z-index: 1000;
                position: fixed;
                width: 100%; height: 100%;
                left: 0; top: 0;
                background-color: rgba(0,0,0, 0.4);
                overflow-x: hidden;"
            v-if="loading"></div>
        <navigation
            v-on:isLoading="isLoading"
            style="width: 100%; position : fixed; top:0px; z-index:10"></navigation>
        <sidebar v-on:isLoading="isLoading"></sidebar>

        <router-view
            v-on:isLoading="isLoading"
            :updatedAt="updatedAt"
            style="width:100%; margin-top:75px"></router-view>
    </div>
</template>

<script>
    import Chart from 'chart.js'
    import axios from 'axios'
    import Sidebar from './components/SideBar'
    import Navigation from './components/Navigation.vue'

    export default {
        name: 'App',
        components: {
            sidebar: Sidebar,
            navigation: Navigation
        },
        data() {
            return {loading: false, updatedAt: ''}
        },
        methods: {
            isLoading(value) {
                this.loading = value
            }
        },
        created() {
            let self = this
            axios
                .get('http://www.musinsa.cf/api/v1/product/cache/last-modified')
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