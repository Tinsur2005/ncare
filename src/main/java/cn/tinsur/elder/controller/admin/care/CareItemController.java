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


import cn.tinsur.elder.pojo.entity.CareItem;
import cn.tinsur.elder.pojo.query.CareItemQuery;
import cn.tinsur.elder.service.ICareItemService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 护理项目表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/admin/care-items")
public class CareItemController {
    @Autowired
    private ICareItemService careItemService;

    /**
     * 分页查询护理项目列表
     * GET /care-items?page=1&limit=10&name=xxx&status=1
     */
    @GetMapping
    public Result<IPage<CareItem>> pageList(CareItemQuery careItemQuery) {
        IPage<CareItem> page = careItemService.list(careItemQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理项目
     * GET /care-items/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(careItemService.getById(id));
    }

    /**
     * 新增护理项目
     * POST /care-items
     */
    @PostMapping
    public Result add(@RequestBody CareItem careItem) {
        if (isExists(careItem.getName())) {
            return Result.error("已有同名护理项目，请修改后重试");
        }
        careItemService.save(careItem);
        return Result.ok("新增成功");
    }

    /**
     * 修改护理项目
     * PUT /care-items/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody CareItem careItem) {
        careItem.setId(id);
        careItemService.updateById(careItem);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除护理项目
     * DELETE /care-items/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        careItemService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除护理项目
     * DELETE /care-items
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        careItemService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 获取全部启用状态的护理项目列表List（供护理计划等"选护理项目"下拉框使用）
     * GET /care-items/list
     */
    @GetMapping("/list")
    public Result<List<CareItem>> list() {
        return Result.ok(careItemService.listAll());
    }

    /**
     * 判断护理项目名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        CareItem careItem = careItemService.getOne(new QueryWrapper<CareItem>().eq("name", name));
        return careItem != null;
    }
}
