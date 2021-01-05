<template>
    <div>
        <b-navbar style="background-color:black; width:100%">
                <b-button
                    v-b-toggle.sidebar-1
                    size="sm"
                    class="my-2 my-sm-0"
                    style="background-color : #000000; text-align:left;">
                    <b-icon icon="list" font-scale="1.2" color="#FFFFFF"></b-icon>
                </b-button >
                <b-navbar-nav class="ml-auto" align="center">
            <a
                href="/"
                style="color :#FFFFFF; text-decoration:none !important; text-align:center !important; margin-left:5px; margin-right:5px;"
                v-if="!searchClicked">
                <h3 style="color :#FFFFFF;">MUSINSA WATCHER</h3>
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
            goToSearch(searchText, page) {
                this.searchClicked = true
                if (searchText.trim().length == 0) {
                    return;
                }
                this.$emit('isLoading', true)
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
                    .catch(() => {});
                this.searchText = ''
            }
        }
    }
</script>