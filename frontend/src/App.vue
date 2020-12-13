<template>
    <div style="min-width:1300px;">
        <navigation></navigation>
        <div id="page-wrapper">
            <sidebar></sidebar>
            <div id="page-content-wrapper">
                <router-view style="margin-top:30px"></router-view>
            </div>
        </div>
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
            return {}
        },
        methods: {},
    }
</script>

<style src="./assets/css/app.css"></style>