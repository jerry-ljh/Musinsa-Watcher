<template>
    <div>
        <b-navbar style="background-color:black ">
            <b-navbar-brand >
                <router-link to="/" style="color :#FFFFFF">
                    <h2>MUSINSA WATCHER</h2>
                </router-link>
            </b-navbar-brand>
            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
            <b-collapse id="nav-collapse" is-nav="is-nav">
                <b-navbar-nav class="ml-auto">
                    <b-nav-form>
                        <b-form-input size="md" class="mr-sm-2" placeholder="Search"></b-form-input>
                        <b-button
                            size="sm"
                            class="my-2 my-sm-0"
                            type="submit"
                            style="background-color : #000000">
                            <b-icon icon="search" font-scale="1.5" color="#FFFFFF"></b-icon>
                        </b-button >
                    </b-nav-form>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div id="page-wrapper">
            <!-- 사이드바 -->
            <div id="sidebar-wrapper">
                <ul class="sidebar-nav">
                    <li class="sidebar-brand">
                        <a href="#">Jerry</a>
                    </li>
                    <li>
                        <a href="#">전체
                            <small style="color : #b2b2b2">All</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">상의<small style="color : #b2b2b2">Top</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">아우터<small style="color : #b2b2b2">Outer</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">원피스<small style="color : #b2b2b2">Onepiece</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">바지<small style="color : #b2b2b2">Pants</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">스커트<small style="color : #b2b2b2">Skirt</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">가방<small style="color : #b2b2b2">Bag</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">스니커즈<small style="color : #b2b2b2">Sneakers</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">신발<small style="color : #b2b2b2">Shoes</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">모자<small style="color : #b2b2b2">HeadWear</small>
                        </a>
                    </li>
                    <li>
                        <a href="#">양말/레그웨어<small style="color : #b2b2b2">Socks/Legwear</small>
                        </a>
                    </li>
                </ul>
            </div>
            <div id="page-content-wrapper" style="max-height : 900px">
                <router-view style="margin-top:30px"></router-view>
            </div>
        </div>

    </div>
</template>

<script>
import Chart from 'chart.js'
    export default {
        name: 'App',
        data() {
            return {}
        }
    }
</script>

<style src="./assets/css/app.css"></style>