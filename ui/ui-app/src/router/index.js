/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
// 创建一个路由器，并暴露出去
// 第一步：引入createRouter
import {createRouter, createWebHistory} from 'vue-router'
// 引入一个一个可能要呈现组件
import Index from '@/views/Index.vue'
import Home from '@/views/Home.vue'
import Exam from '@/views/Exam.vue'
import Profile from '@/views/Profile.vue'
import Login from '@/views/Login.vue'
import ElderInfo from '@/views/ElderInfo.vue'
import ExamPackageList from '@/views/ExamPackageList.vue'
import ExamPackageDetail from '@/views/ExamPackageDetail.vue'
import ExamRecordDetail from '@/views/ExamRecordDetail.vue'
import CarePlanList from '@/views/CarePlanList.vue'
import CarePlanDetail from '@/views/CarePlanDetail.vue'
import CareTaskList from '@/views/CareTaskList.vue'
import CareTaskDetail from '@/views/CareTaskDetail.vue'
import ContractList from '@/views/ContractList.vue'
import ContractDetail from '@/views/ContractDetail.vue'
import AnnouncementList from '@/views/AnnouncementList.vue'
import AnnouncementDetail from '@/views/AnnouncementDetail.vue'
import AIChat from '@/views/AIChat.vue'
import NewsList from '@/views/NewsList.vue'
import NewsDetail from '@/views/NewsDetail.vue'
import HelpList from '@/views/HelpList.vue'
import HelpSubmit from '@/views/HelpSubmit.vue'
import About from '@/views/About.vue'
import Beian from '@/views/Beian.vue'

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/login', component: Login
        },
        {
            path: '/', component: Index, children: [
                {path: '/', component: Home},
                {path: '/home', component: Home},
                {path: '/exam', component: Exam},
                {path: '/profile', component: Profile},
                {path: '/elderInfo', component: ElderInfo},
                {path: '/examPackageList', component: ExamPackageList},
                {path: '/examPackageDetail', component: ExamPackageDetail},
                {path: '/examRecordDetail', component: ExamRecordDetail},
                {path: '/carePlan', component: CarePlanList},
                {path: '/carePlanDetail', component: CarePlanDetail},
                {path: '/careTask', component: CareTaskList},
                {path: '/careTaskDetail', component: CareTaskDetail},
                {path: '/contract', component: ContractList},
                {path: '/contractDetail', component: ContractDetail},
                {path: '/announcement', component: AnnouncementList},
                {path: '/announcementDetail', component: AnnouncementDetail},
                {path: '/aiChat', component: AIChat},
                {path: '/news', component: NewsList},
                {path: '/newsDetail', component: NewsDetail},
                {path: '/help', component: HelpList},
                {path: '/helpSubmit', component: HelpSubmit},
                {path: '/about', component: About},
                {path: '/beian', component: Beian}
            ]
        }
    ]
})


//路由守卫
//全局前置守卫
import {useTokenStore} from '@/store/token.js'

let whiteList = ['/login']; // 白名单
router.beforeEach((to) => {

    const tokenStore = useTokenStore()
    const token = tokenStore.token;

    // 访问登录页，并且已经登录则跳转首页
    if (to.path === '/login' && token) {
        return '/home'
    }
    // 访问的不是白名单路径且没有token则跳转登录页
    if (!whiteList.includes(to.path) && !token) {
        return '/login'
    }
    // 其余情况放行
})

// 暴露出去router
export default router
