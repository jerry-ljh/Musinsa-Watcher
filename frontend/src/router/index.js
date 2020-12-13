import Vue from 'vue'
import Router from 'vue-router'
import ProductList from '@/components/ProductList'
import ProductDetail from '@/components/ProductDetail'
import VueChartJS from '@/views/VueChartJS'

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            name: 'List',
            component: ProductList 
        },
        {
            path: '/product/list',
            name: 'ProductList',
            component: ProductList
        },
        {
            path: '/product',
            name: 'Product',
            component: ProductDetail
        }, 
        {
            path: '/chartjs',
            name: 'VueChartJS',
            component: VueChartJS
        }
    ]
})
