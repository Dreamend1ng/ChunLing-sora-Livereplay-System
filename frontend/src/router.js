import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        { path: '/', redirect: '/home' },
        { path: '/home', component: () => import('./components/MainPage.vue') },
        { path: '/login', component: () => import('./components/LogIn.vue') },
        { path: '/register', component: () => import('./components/UserRegister.vue') },
        { path: '/record', component: () => import('./components/LiveRecord.vue') },
        { path: '/admin', component: () => import('./components/admin/AdminLogin.vue')},
        { path: '/admin/index', component: () => import('./components/admin/AdminHome.vue')},
        { path: '/admin/user', component: () => import('./components/admin/AdminUser.vue')},
        { path: '/admin/record', component: () => import('./components/admin/AdminRecord.vue')},
        { path: '/admin/comment', component: () => import('./components/admin/AdminComment.vue')},
        { path: '/admin/service', component: () => import('./components/admin/AdminStatus.vue')},
        { path: '/admin/about', component: () => import('./components/admin/AdminAbout.vue')}
    ]  // 补全 routes 数组的闭合括号
})

export default router;