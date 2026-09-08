/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
 */

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
package cn.tinsur.elder.controller.admin.exam;


import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.ExamAppointmentItem;
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
import cn.tinsur.elder.service.IExamAppointmentService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 老人预约/体检记录表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/admin/exam-appointments")
public class ExamAppointmentController {
    @Autowired
    private IExamAppointmentService examAppointmentService;

    /**
     * 分页查询体检预约列表
     * GET /exam-appointments?page=1&limit=10&elderId=1&packageName=xxx&status=1
     */
    @GetMapping
    public Result<IPage<ExamAppointmentVO>> pageList(ExamAppointmentQuery examAppointmentQuery) {
        IPage<ExamAppointmentVO> page = examAppointmentService.list(examAppointmentQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询体检预约
     * GET /exam-appointments/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(examAppointmentService.getById(id));
    }

    /**
     * 新增体检预约（快照套餐价格，并把套餐下的体检项目复制为体检记录明细）
     * POST /exam-appointments
     */
    @PostMapping
    public Result add(@RequestBody ExamAppointment examAppointment) {
        return examAppointmentService.addAppointment(examAppointment);
    }

    /**
     * 修改体检预约（仅限待体检状态，换套餐时重新快照价格并重建明细）
     * PUT /exam-appointments/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ExamAppointment examAppointment) {
        return examAppointmentService.updateAppointment(id, examAppointment);
    }

    /**
     * 开始体检（待体检 → 体检中）
     * PUT /exam-appointments/start/1
     */
    @PutMapping("/start/{id}")
    public Result start(@PathVariable Long id) {
        return examAppointmentService.startAppointment(id);
    }

    /**
     * 取消预约（待体检/体检中 → 已取消）
     * PUT /exam-appointments/cancel/1
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        return examAppointmentService.cancelAppointment(id);
    }

    /**
     * 暂存体检结果（先删后插，仅限体检中状态）
     * PUT /exam-appointments/updateAppointmentItems/1
     */
    @PutMapping("/updateAppointmentItems/{id}")
    public Result updateAppointmentItems(@PathVariable Long id, @RequestBody List<ExamAppointmentItem> examAppointmentItems) {
        return examAppointmentService.updateAppointmentItems(id, examAppointmentItems);
    }

    /**
     * 完成体检（保存全部明细结果并流转为已完成，数值型结果自动判定是否异常）
     * PUT /exam-appointments/complete/1
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id, @RequestBody List<ExamAppointmentItem> examAppointmentItems) {
        return examAppointmentService.completeAppointment(id, examAppointmentItems);
    }

    /**
     * 获取指定体检记录包含的所有明细（附上参考范围，供结果录入/展示）
     * GET /exam-appointments/getAppointmentItemsById/1
     */
    @GetMapping("/getAppointmentItemsById/{id}")
    public Result<List<ExamAppointmentItemVO>> getAppointmentItemsById(@PathVariable Long id) {
        return examAppointmentService.getAppointmentItemsById(id);
    }

    /**
     * 根据ID删除体检预约（级联删除该记录的全部明细）
     * DELETE /exam-appointments/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        examAppointmentService.deleteAppointmentById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除体检预约（级联删除各记录的全部明细）
     * DELETE /exam-appointments
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        examAppointmentService.deleteAppointmentBatch(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}
