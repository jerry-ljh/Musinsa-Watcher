<template>
    <div>
        <div class="child-page-listing">
            <div class="grid-container">
                <article @click="route('category')" id="3688" class="location-listing">
                    <span class="location-title">
                        Rank
                    </span>
                    <div class="location-image">
                        <img
                            width="300"
                            height="169"
                            src="https://musinsa-watcher.s3.ap-northeast-2.amazonaws.com/rank.png"
                            alt="Rank"/>
                    </div>
                </article>
                <article @click="goBrand()" id="3685" class="location-listing">
                    <span class="location-title">Brand</span>
                    <div class="location-image">
                        <img
                            width="300"
                            height="169"
                            src="https://musinsa-watcher.s3.ap-northeast-2.amazonaws.com/brand.png"
                            alt="Brand"/>
                    </div>
                </article>
                <article @click="route('discount')" id="3691" class="location-listing">
                    <span class="location-title">
                        오늘 깜짝 할인
                    </span>
                    <div class="location-image">
                        <img
                            width="300"
                            height="169"
                            src="https://musinsa-watcher.s3.ap-northeast-2.amazonaws.com/discount.png"
                            alt="오늘 깜짝 할인"/>
                    </div>
                </article>
                <article @click="route('minimum')" id="3694" class="location-listing">
                    <span class="location-title">
                        오늘 역대 최저가
                    </span>
                    <div class="location-image">
                        <img
                            width="300"
                            height="169"
                            src="https://musinsa-watcher.s3.ap-northeast-2.amazonaws.com/minimum.png"
                            alt="오늘 역대 최저가"/>
                    </div>
                </article>
            </div>
        </div>
    </div>
</template>
<script>
    import axios from 'axios'

    export default {
        data() {
            return {}
        },
        methods: {
            route(type) {
                window.scrollTo(0, 0);
                this
                    .$router
                    .push({
                        name: 'Category',
                        query: {
                            "type": type
                        }
                    })
            },
            goBrand() {
                window.scrollTo(0, 0);
                this
                    .$router
                    .push({name: 'Brand'})
            }
        },
        created() {
            this.$emit('isLoading', true)
            if (this.$route.path == '/') {
                this.$emit('isLoading', false)
                return
            }
        }
    }
</script>
<style>

    img {
        width: 100%;
        /* need to overwrite inline dimensions */
        height: auto;
    }
    h2 {
        margin-bottom: 0.5em;
    }
    .grid-container {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        grid-gap: 1em;
    }

    /* hover styles */
    .location-listing {
        position: relative;
    }

    .location-image {
        line-height: 0;
        overflow: hidden;
    }

    .location-image img {
        transition: filter 0.3s ease-in;
        transform: scale(1.1);
    }

    .location-title {
        font-size: 1.5em;
        font-weight: bold;
        text-decoration: none;
        z-index: 1;
        position: absolute;
        height: 100%;
        width: 100%;
        top: 0;
        left: 0;
        opacity: 0;
        transition: opacity 0.5s;
        background: rgba(68, 66, 66, 0.411);
        color: white;
        /* position the text in t’ middle*/
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .location-listing:hover .location-title {
        opacity: 1;

    }
    /* for touch screen devices */
    @media (hover: none) {
        .location-title {
            opacity: 1;
        }
    }
</style>