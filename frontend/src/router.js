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
// 添加全局前置守卫 - 前台
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
// 添加全局前置守卫 - 后台
router.beforeEach(async (to, from, next) => {
    // 仅处理/admin路径且排除登录页
    if (!to.path.startsWith('/admin') || to.path === '/admin') {
        return next()
    }

    // 获取存储的管理员用户名
    const adminUsername = localStorage.getItem('adminUsername')
    
    // 未登录时直接跳转后台登录页
    if (!adminUsername) {
        return next('/admin')
    }

    try {
        const doubleEncoded = btoa(btoa(adminUsername))
        console.log('加密字符串:', doubleEncoded)
        
        // 创建表单数据对象
        const formData = new FormData()
        formData.append('encryptedUsername', doubleEncoded)

        // 发送表单数据
        const { data } = await axios.post('http://localhost:9150/api/admin/loginVerify', formData)
        console.log(data)

        // 验证结果处理
        if (data == '1') {
            next()
        } else {
            // 清除无效登录状态
            localStorage.removeItem('adminUsername')
            next('/admin')
        }
    } catch (error) {
        console.error('管理员验证失败:', error)
        // 清除登录状态并跳转后台登录页
        localStorage.removeItem('adminUsername')
        next('/admin')
    }
})

export default router;