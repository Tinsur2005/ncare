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
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder.controller.admin.platform;


import cn.tinsur.elder.pojo.entity.NewsCategory;
import cn.tinsur.elder.pojo.query.NewsCategoryQuery;
import cn.tinsur.elder.service.INewsCategoryService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 资讯分类表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@RestController
@RequestMapping("/admin/news-categories")
public class NewsCategoryController {
    @Autowired
    private INewsCategoryService newsCategoryService;

    /**
     * 分页查询资讯分类列表
     * GET /news-categories?page=1&limit=10&name=xxx&status=1
     */
    @GetMapping
    public Result<IPage<NewsCategory>> pageList(NewsCategoryQuery newsCategoryQuery) {
        IPage<NewsCategory> page = newsCategoryService.list(newsCategoryQuery);
        return Result.ok(page);
    }

    /**
     * 获取全部启用状态的资讯分类列表List（供资讯编辑时"选分类"下拉框使用）
     * GET /news-categories/list
     */
    @GetMapping("/list")
    public Result<List<NewsCategory>> list() {
        return Result.ok(newsCategoryService.listAll());
    }

    /**
     * 根据ID查询资讯分类
     * GET /news-categories/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(newsCategoryService.getById(id));
    }

    /**
     * 新增资讯分类
     * POST /news-categories
     */
    @PostMapping
    public Result add(@RequestBody NewsCategory newsCategory) {
        if (isExists(newsCategory.getName())) {
            return Result.error("已有同名资讯分类，请修改后重试");
        }
        newsCategoryService.save(newsCategory);
        return Result.ok("新增成功");
    }

    /**
     * 修改资讯分类
     * PUT /news-categories/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody NewsCategory newsCategory) {
        newsCategory.setId(id);
        newsCategoryService.updateById(newsCategory);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除资讯分类
     * DELETE /news-categories/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (newsCategoryService.countNews(Arrays.asList(id)) > 0) {
            return Result.error("该分类下存在资讯，请先删除或移动分类下的资讯");
        }
        newsCategoryService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除资讯分类
     * DELETE /news-categories
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        if (newsCategoryService.countNews(Arrays.asList(ids)) > 0) {
            return Result.error("选中的分类下存在资讯，请先删除或移动分类下的资讯");
        }
        newsCategoryService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断资讯分类名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        NewsCategory newsCategory = newsCategoryService.getOne(new QueryWrapper<NewsCategory>().eq("name", name));
        return newsCategory != null;
    }
}