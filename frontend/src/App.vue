<template>
    <div style="min-width:1300px;">
        <b-overlay
            :show="loading"
            :no-fade=true
            spinner-type="null"
            @shown="onShown"
            @hidden="onHidden">
            <div v-if="loading" style="position:fixed; top : 50%; left:50%; z-index: 1000;">
                <b-icon icon="stopwatch" font-scale="3" animation="cylon"></b-icon>
                <p id="cancel-label">Please wait...</p>
            </div>
            <navigation v-on:isLoading="isLoading"></navigation>
            <div id="page-wrapper">
                <sidebar v-on:isLoading="isLoading"></sidebar>
                <div id="page-content-wrapper">
                    <router-view v-on:isLoading="isLoading" :updatedAt="updatedAt"></router-view>
                </div>
            </div>
        </b-overlay>
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
                if (value) {
                    this.onShown()
                } else {
                    this.onHidden();
                }
                this.loading = value
            },
            onShown() {
                // Focus the cancel button when the overlay is showing
                this.loading = true;
            },
            onHidden() {
                // Focus the show button when the overlay is removed
                this.loading = false;
            }
        },
        created() {
            let self = this
            axios
                .get('https://www.musinsa.cf/api/v1/product/cache/last-modified')
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