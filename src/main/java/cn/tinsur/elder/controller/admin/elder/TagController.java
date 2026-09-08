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
 *  源码作者 : Tinsur
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder.controller.admin.elder;


import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.TagQuery;
import cn.tinsur.elder.service.ITagService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
@RestController
@RequestMapping("/admin/tags")
public class TagController {
    @Autowired
    private ITagService tagService;

    /**
     * 获取全部标签列表List
     * @return
     */
    @GetMapping("/list")
    public Result<List<Tag>> list() {
        List<Tag> list = tagService.list();
        return Result.ok(list);
    }

    /**
     * 分页查询标签列表
     * GET /tags?page=1&limit=10&name=xxx&code=xxx
     */
    @GetMapping
    public Result<IPage<Tag>> pageList(TagQuery tagQuery) {
        IPage<Tag> page = tagService.list(tagQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询标签
     * GET /tags/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(tagService.getById(id));
    }

    /**
     * 新增标签
     * POST /tags
     */
    @PostMapping
    public Result add(@RequestBody Tag tag) {
        if(isExists(tag.getName())) {
            return Result.error("已有同名标签存在，请修改后重试");
        }
        tagService.save(tag);
        return Result.ok("新增成功");
    }

    /**
     * 修改标签
     * PUT /tags/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        tagService.updateById(tag);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除标签（逻辑删除）
     * DELETE /tags/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if(tagService.getCount(id) > 0){
            return Result.error("标签下存在绑定数据，不允许删除");
        }
        tagService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除标签
     * DELETE /tags
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {

        tagService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断标签是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        Tag tag = tagService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag>().eq("name", name));
        return tag != null;
    }
}
