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
import request from "@/utils/request.js";

const checkInApi = {
    // 分页查询办理单列表（可按类型、状态、老人、时间范围筛选）
    list(checkInRecordQuery) {
        return request.get("/check-ins", {params: checkInRecordQuery});
    },
    // 根据ID查询办理单详情
    selectById(id) {
        return request.get(`/check-ins/${id}`);
    },
    // 远程搜索在住老人（仅入住中状态），供退住办理第一步选择老人使用
    listInElders(name) {
        return request.get("/check-ins/in-elders", {params: {name}});
    },
    // 第一步完成时创建办理单（入住：登记客户和老人；退住：选择在住老人）
    add(checkInAddDTO) {
        return request.post("/check-ins", checkInAddDTO)
    },
    // 保存第n步的办理数据并把步骤+1
    saveStep(id, step, data) {
        return request.put(`/check-ins/${id}/step/${step}`, data)
    },
    // 确认入住/确认退住，按办理类型走不同的后端事务
    confirm(id) {
        return request.put(`/check-ins/confirm/${id}`)
    },
    // 取消办理（退住单会同时把老人恢复为入住中）
    cancel(id) {
        return request.put(`/check-ins/cancel/${id}`)
    },
    // 根据ID删除办理单（仅已完成或已取消的办理单允许删除）
    deleteById(id) {
        return request.delete(`/check-ins/${id}`);
    },
    // 批量删除办理单
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/check-ins", {data: ids});
    }
}
export default checkInApi