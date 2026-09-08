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


import cn.tinsur.elder.pojo.entity.News;
import cn.tinsur.elder.pojo.query.NewsQuery;
import cn.tinsur.elder.pojo.vo.NewsVO;
import cn.tinsur.elder.service.INewsService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 资讯表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@RestController
@RequestMapping("/admin/news")
public class NewsController {
    @Autowired
    private INewsService newsService;

    /**
     * 分页查询资讯列表
     * GET /news?page=1&limit=10&title=xxx&categoryId=1&status=1
     */
    @GetMapping
    public Result<IPage<NewsVO>> pageList(NewsQuery newsQuery) {
        IPage<NewsVO> page = newsService.list(newsQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询资讯
     * GET /news/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(newsService.getById(id));
    }

    /**
     * 新增资讯
     * POST /news
     */
    @PostMapping
    public Result add(@RequestBody News news) {
        newsService.save(news);
        return Result.ok("新增成功");
    }

    /**
     * 修改资讯
     * PUT /news/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        newsService.updateById(news);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除资讯
     * DELETE /news/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除资讯
     * DELETE /news
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        newsService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}