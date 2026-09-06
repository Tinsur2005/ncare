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
import request from '@/utils/request.js'

const userApi = {
    // 登录（userType：elder老人 / family家属，与登录页Tab对应）
    login(user) {
        return request.post("/users/login", user)
    },
    // 根据Token查询登录用户信息（老人或家属），家属同时返回绑定的老人列表
    userInfo() {
        return request.get("/users/userInfo")
    },
    // 查询老人当前入住的床位信息（楼栋楼层房间床位），未入住时data为null
    bedInfo(elderId) {
        return request.get("/users/bedInfo", {params: {elderId}})
    }
}
export default userApi