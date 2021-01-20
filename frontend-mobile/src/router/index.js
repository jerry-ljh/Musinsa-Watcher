import Vue from 'vue'
import Router from 'vue-router'
import ProductList from '@/components/ProductList'
import ProductDetail from '@/components/ProductDetail'
import Main from '@/components/Main'
import Category from '@/components/Category'
import Brand from '@/components/Brand'
import VueChartJS from '@/views/VueChartJS'

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            name: 'Main',
            component: Main 
        },
        {
            path: '/product/category',
            name: 'Category',
            component: Category 
        },
        {
            path: '/product/brand',
            name: 'Brand',
            component: Brand 
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
