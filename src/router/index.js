import Vue from 'vue'
import Router from 'vue-router'
import List from '@/components/List'
import Detail from '@/components/Detail'
import VueChartJS from '@/views/VueChartJS'

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            name: 'List',
            component: List
        }, {
            path: '/detail',
            name: 'Detail',
            component: Detail
        }, {
            path: '/chartjs',
            name: 'VueChartJS',
            component: VueChartJS
        }
    ]
})
