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


import cn.tinsur.elder.pojo.entity.ExamItem;
import cn.tinsur.elder.pojo.query.ExamItemQuery;
import cn.tinsur.elder.service.IExamItemService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 体检项目表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/admin/exam-items")
public class ExamItemController {
    @Autowired
    private IExamItemService examItemService;

    /**
     * 分页查询体检项目列表
     * GET /exam-items?page=1&limit=10&name=xxx&status=1
     */
    @GetMapping
    public Result<IPage<ExamItem>> pageList(ExamItemQuery examItemQuery) {
        IPage<ExamItem> page = examItemService.list(examItemQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询体检项目
     * GET /exam-items/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(examItemService.getById(id));
    }

    /**
     * 新增体检项目
     * POST /exam-items
     */
    @PostMapping
    public Result add(@RequestBody ExamItem examItem) {
        if (isExists(examItem.getName())) {
            return Result.error("已有同名体检项目，请修改后重试");
        }
        examItemService.save(examItem);
        return Result.ok("新增成功");
    }

    /**
     * 修改体检项目
     * PUT /exam-items/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ExamItem examItem) {
        examItem.setId(id);
        examItemService.updateById(examItem);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除体检项目
     * DELETE /exam-items/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (examItemService.countInPackage(Arrays.asList(id)) > 0) {
            return Result.error("该体检项目已被套餐引用，请先从套餐中移除后再删除");
        }
        examItemService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除体检项目
     * DELETE /exam-items
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        if (examItemService.countInPackage(Arrays.asList(ids)) > 0) {
            return Result.error("选中的体检项目已被套餐引用，请先从套餐中移除后再删除");
        }
        examItemService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 获取全部启用状态的体检项目列表List（供体检套餐等"选体检项目"下拉框使用）
     * GET /exam-items/list
     */
    @GetMapping("/list")
    public Result<List<ExamItem>> list() {
        return Result.ok(examItemService.listAll());
    }

    /**
     * 判断体检项目名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        ExamItem examItem = examItemService.getOne(new QueryWrapper<ExamItem>().eq("name", name));
        return examItem != null;
    }
}
