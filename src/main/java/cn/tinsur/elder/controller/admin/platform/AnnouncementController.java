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
package cn.tinsur.elder.controller.admin.platform;


import cn.tinsur.elder.pojo.entity.Announcement;
import cn.tinsur.elder.pojo.query.AnnouncementQuery;
import cn.tinsur.elder.service.IAnnouncementService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 公告表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@RestController
@RequestMapping("/admin/announcements")
public class AnnouncementController {
    @Autowired
    private IAnnouncementService announcementService;

    /**
     * 分页查询公告列表
     * GET /announcements?page=1&limit=10&title=xxx&status=1
     */
    @GetMapping
    public Result<IPage<Announcement>> pageList(AnnouncementQuery announcementQuery) {
        IPage<Announcement> page = announcementService.list(announcementQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询公告
     * GET /announcements/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(announcementService.getById(id));
    }

    /**
     * 新增公告
     * POST /announcements
     */
    @PostMapping
    public Result add(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return Result.ok("新增成功");
    }

    /**
     * 修改公告
     * PUT /announcements/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.updateById(announcement);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除公告
     * DELETE /announcements/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除公告
     * DELETE /announcements
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        announcementService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}
