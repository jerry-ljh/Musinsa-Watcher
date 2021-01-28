<template>
    <div>
        <b-navbar style="background-color:black;">
            <b-button   
                @click="goBack()"
                size="sm"
                class="my-2 my-sm-0"
                style="background-color : #000000; text-align:left;">
                <b-icon icon="arrow-left" font-scale="1.2" color="#FFFFFF"></b-icon>
            </b-button >
            <b-navbar-nav class="ml-auto" align="center">
                <a
                    @click="goHome()"
                    style="color :#FFFFFF; text-decoration:none !important; text-align:center !important; margin-left:5px; margin-right:5px;"
                    v-if="!searchClicked">
                    <h5 style="color :#FFFFFF; margin-top:6px">MUSINSA WATCHER</h5>
                </a>
            </b-navbar-nav>
            <b-form-input
                size="md"
                class="mr-sm-2"
                placeholder="Search"
                v-on:keyup.enter="goToSearch(searchText, 1)"
                v-model="searchText"
                v-if="searchClicked"></b-form-input>
            <b-navbar-nav class="ml-auto">

                <b-button
                    size="sm"
                    class="my-2 my-sm-0"
                    style="background-color : #000000"
                    v-on:click="goToSearch(searchText, 1)">
                    <b-icon icon="search" font-scale="1.2" color="#FFFFFF"></b-icon>
                </b-button >
                <b-button
                    size="sm"
                    class="my-2 my-sm-0"
                    style="background-color : #000000"
                    v-on:click="searchClicked=!searchClicked"
                    v-if="searchClicked">
                    <b-icon icon="backspace" font-scale="1.5" color="#FFFFFF text-align:right;"></b-icon>
                </b-button >
            </b-navbar-nav>
        </b-navbar>
    </div>
</template>

<script>
    import EventBus from "../utils/event-bus"
    export default {
        data() {
            return {searchText: '', searchClicked: false}
        },
        methods: {
            goBack() {
                this.searchClicked=false;
                this.$router.go(-1);
            },
            goHome() {
                window.scrollTo(0, 0);
                this
                    .$router
                    .push({name: 'Main'})
                    .catch(() => {});
            },
            goToSearch(searchText, page) {
                this.searchClicked = true
                if (searchText.trim().length == 0) {
                    return;
                }
                this.$emit('isLoading', true)
                EventBus.$emit("goToSearch", searchText, page)
                this
                    .$router
                    .replace({
                        name: 'ProductList',
                        query: {
                            "topic": searchText,
                            "type" : 'search',
                            "page": page
                        }
                    })
                    .catch(() => {});
                this.searchText = ''
            }
        }
    }
</script>