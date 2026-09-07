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
import {defineStore} from "pinia";

export const useUserInfoStore = defineStore('userInfo', {
    //存储数据点的地方
    state() {
        return {
            user: {},
            // 登录用户的角色（elder老人 / family家属），登录时写入，用于渲染不同的TabBar和首页
            userType: '',
            // 家属绑定的老人列表（家属登录后由接口返回，老人登录时为空数组）
            elders: [],
            // 老人绑定的家属/监护人（老人登录后由接口返回，未绑定或家属登录时为null）
            family: null,
            // 家属当前正在查看的绑定老人id，切换老人弹层选择时更新
            currentElderId: null
        }
    },
    //方法
    actions: {
        setUserInfo(user) {
            this.user = user
        },
        removeUserInfo() {
            this.user = {}
        },
        setUserType(userType) {
            this.userType = userType
        },
        removeUserType() {
            this.userType = ''
        },
        setElders(elders) {
            this.elders = elders
        },
        removeElders() {
            this.elders = []
        },
        setFamily(family) {
            this.family = family
        },
        removeFamily() {
            this.family = null
        },
        setCurrentElderId(currentElderId) {
            this.currentElderId = currentElderId
        },
        removeCurrentElderId() {
            this.currentElderId = null
        }
    },
    persist: {
        enabled: true,  //开启缓存，存储在本地localstorage
    }
})
