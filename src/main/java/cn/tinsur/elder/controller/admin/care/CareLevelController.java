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


import cn.tinsur.elder.pojo.entity.CareLevel;
import cn.tinsur.elder.pojo.query.CareLevelQuery;
import cn.tinsur.elder.service.ICareLevelService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 护理等级表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/admin/care-levels")
public class CareLevelController {
    @Autowired
    private ICareLevelService careLevelService;

    /**
     * 分页查询护理等级列表
     * GET /care-levels?page=1&limit=10&name=xxx&status=1
     */
    @GetMapping
    public Result<IPage<CareLevel>> pageList(CareLevelQuery careLevelQuery) {
        IPage<CareLevel> page = careLevelService.list(careLevelQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理等级
     * GET /care-levels/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(careLevelService.getById(id));
    }

    /**
     * 新增护理等级
     * POST /care-levels
     */
    @PostMapping
    public Result add(@RequestBody CareLevel careLevel) {
        if (isExists(careLevel.getName())) {
            return Result.error("已有同名护理等级，请修改后重试");
        }
        careLevelService.save(careLevel);
        return Result.ok("新增成功");
    }

    /**
     * 修改护理等级
     * PUT /care-levels/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody CareLevel careLevel) {
        careLevel.setId(id);
        careLevelService.updateById(careLevel);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除护理等级
     * DELETE /care-levels/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        careLevelService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除护理等级
     * DELETE /care-levels
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        careLevelService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 获取全部启用状态的护理等级列表List（供护理计划等"选护理等级"下拉框使用）
     * GET /care-levels/list
     */
    @GetMapping("/list")
    public Result<List<CareLevel>> list() {
        return Result.ok(careLevelService.listAll());
    }

    /**
     * 判断护理等级名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        CareLevel careLevel = careLevelService.getOne(new QueryWrapper<CareLevel>().eq("name", name));
        return careLevel != null;
    }
}
