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
package cn.tinsur.elder.controller.admin.care;


import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 护理任务与打卡记录表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/admin/care-tasks")
public class CareTaskController {

    @Autowired
    private ICareTaskService careTaskService;

    /**
     * 分页查询护理任务列表
     * GET /care-task?page=1&limit=10&elderId=1&status=0&beginPlanExecuteDate=xxx&endPlanExecuteDate=xxx&viewScope=mine|all
     * 查看范围由后端根据 Authorization 解析当前用户并校验 careTask:viewAll 权限后兜底强制
     */
    @GetMapping
    public Result<IPage<CareTaskVO>> pageList(CareTaskQuery careTaskQuery,
                                              @RequestHeader("Authorization") String token) {
        IPage<CareTaskVO> page = careTaskService.list(careTaskQuery, token);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理任务详情
     * GET /care-tasks/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(careTaskService.getDetail(id));
    }

    /**
     * 完成任务：记录实际完成时间、执行结果、打卡照片、备注、执行人
     * PUT /care-tasks/complete
     */
    @PutMapping("/complete")
    public Result complete(@RequestBody CareTask careTask) {
        return careTaskService.complete(careTask.getId(), careTask);
    }

    /**
     * 跳过/取消任务
     * PUT /care-tasks/skip/1
     */
    @PutMapping("/skip/{id}")
    public Result skip(@PathVariable Long id) {
        return careTaskService.skip(id);
    }

    /**
     * 根据ID删除任务
     * DELETE /care-tasks/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        careTaskService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除任务
     * DELETE /care-task
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        careTaskService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}
