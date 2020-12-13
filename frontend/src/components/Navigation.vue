<template>
    <div>
        <b-navbar style="background-color:black ">
            <b-navbar-brand >
                <a href="/" style="color :#FFFFFF; text-decoration:none !important">
                    <h2 style="color :#FFFFFF;">MUSINSA WATCHER</h2>
                </a>
            </b-navbar-brand>
            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
            <b-collapse id="nav-collapse" is-nav="is-nav">
                <b-navbar-nav class="ml-auto">
                    <b-form-input
                        size="md"
                        class="mr-sm-2"
                        placeholder="Search"
                        v-on:keyup.enter="goToSearch(searchText, 1)"
                        v-model="searchText"></b-form-input>
                    <b-button
                        size="sm"
                        class="my-2 my-sm-0"
                        style="background-color : #000000"
                        v-on:click="goToSearch(searchText, 1)">
                        <b-icon icon="search" font-scale="1.5" color="#FFFFFF"></b-icon>
                    </b-button >
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
    </div>
</template>

<script>
    import EventBus from "../utils/event-bus"
    export default {
        data() {
            return {searchText: ''}
        },
        methods: {
            goToSearch(searchText, page) {
                EventBus.$emit("goToSearch", searchText, page)
                this
                    .$router
                    .push({
                        name: 'ProductList',
                        query: {
                            "search": searchText,
                            "page": page
                        }
                    })
                    .catch(() => {})
                this.searchText = ''
            }
        }
    }
</script>