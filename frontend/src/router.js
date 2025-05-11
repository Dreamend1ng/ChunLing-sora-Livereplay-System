import { createRouter, createWebHashHistory } from 'vue-router'
import axios from 'axios';
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
// 添加全局前置守卫
router.beforeEach(async (to, from, next) => {
    const isExcluded = ['/home', '/login', '/register'].includes(to.path) || to.path.startsWith('/admin')
    if (isExcluded) {
        return next()
    }

    try {
        const { data } = await axios.get('/api/user/currentUser')
        if (data === '00000') {
            window.currentUser = null
            next('/login')
        } else {
            window.currentUser = data  // 修改后的变量名
            next()
        }
    } catch (error) {
        window.currentUser = null
        console.error('用户状态验证失败:', error)
        // 打印详细错误信息
        console.error('错误详情:', error.response ? error.response.data : error.message)
        next('/login')
    }
})

export default router;