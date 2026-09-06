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
import Login from '@/views/Login.vue'
import User from '@/views/User.vue'
import Elder from '@/views/Elder.vue'
import Family from '@/views/Family.vue'
import Tag from '@/views/Tag.vue'
import Role from '@/views/Role.vue'
import Permission from '@/views/Permission.vue'
import Contract from '@/views/Contract.vue'
import CareItem from '@/views/CareItem.vue'
import CareLevel from '@/views/CareLevel.vue'
import CarePlan from '@/views/CarePlan.vue'
import CareTask from '@/views/CareTask.vue'
import ExamItem from '@/views/ExamItem.vue'
import ExamPackage from '@/views/ExamPackage.vue'
import ExamAppointment from '@/views/ExamAppointment.vue'
import Announcement from '@/views/Announcement.vue'
import NewsCategory from '@/views/NewsCategory.vue'
import News from '@/views/News.vue'
import Help from '@/views/Help.vue'
import Building from '@/views/Building.vue'
import Floor from '@/views/Floor.vue'
import Room from '@/views/Room.vue'
import Bed from '@/views/Bed.vue'
import CheckInProcess from '@/views/CheckInProcess.vue'
import CheckOutProcess from '@/views/CheckOutProcess.vue'
import CheckRecord from '@/views/CheckRecord.vue'

//创建路由器
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login', component: Login
        },
        {
            path: '/', component: Index, children: [
                {path: '/', component: Home},
                {path: '/user', component: User},
                {path: '/elder', component: Elder},
                {path: '/family', component: Family},
                {path: '/tag', component: Tag},
                {path: '/role', component: Role},
                {path: '/permission', component: Permission},
                {path: '/contract', component: Contract},
                {path: '/careItem', component: CareItem},
                {path: '/careLevel', component: CareLevel},
                {path: '/carePlan', component: CarePlan},
                {path: '/careTask', component: CareTask},
                {path: '/examItem', component: ExamItem},
                {path: '/examPackage', component: ExamPackage},
                {path: '/examAppointment', component: ExamAppointment},
                {path: '/announcement', component: Announcement},
                {path: '/newsCategory', component: NewsCategory},
                {path: '/news', component: News},
                {path: '/help', component: Help},
                {path: '/building', component: Building},
                {path: '/floor', component: Floor},
                {path: '/room', component: Room},
                {path: '/bed', component: Bed},
                {path: '/checkInProcess', component: CheckInProcess},
                {path: '/checkOutProcess', component: CheckOutProcess},
                {path: '/checkRecord', component: CheckRecord}
            ]
        }
    ]
})


//路由守卫
//全局前置守卫
import {useTokenStore} from '@/store/token.js'
import {ElMessage} from "element-plus";

let whiteList = ['/login']; // 白名单
router.beforeEach((to) => {

    const tokenStore = useTokenStore()
    const token = tokenStore.token;

    // 访问登录页，并且已经登录则跳转首页
    if (to.path === '/login' && token) {
        ElMessage.success('已登录，欢迎回来')
        return '/'
    }
    // 访问的不是白名单路径且没有token则跳转登录页
    if (!whiteList.includes(to.path) && !token) {
        return '/login'
    }
    // 其余情况放行
})

// 暴露出去router
export default router