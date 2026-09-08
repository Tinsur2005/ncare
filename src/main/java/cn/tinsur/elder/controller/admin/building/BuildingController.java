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
package cn.tinsur.elder.controller.admin.building;


import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.query.BuildingQuery;
import cn.tinsur.elder.service.IBuildingService;
import cn.tinsur.elder.service.IFloorService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 楼栋表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/buildings")
public class BuildingController {
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IFloorService floorService;

    /**
     * 分页查询楼栋列表
     * GET /buildings?page=1&limit=10&name=xxx
     */
    @GetMapping
    public Result<IPage<Building>> pageList(BuildingQuery buildingQuery) {
        IPage<Building> page = buildingService.list(buildingQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询楼栋
     * GET /buildings/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(buildingService.getById(id));
    }

    /**
     * 获取全部楼栋列表List（供楼层、房间、床位等页面的楼栋下拉框使用）
     * GET /buildings/list
     */
    @GetMapping("/list")
    public Result<List<Building>> list() {
        return Result.ok(buildingService.listAll());
    }

    /**
     * 新增楼栋
     * POST /buildings
     */
    @PostMapping
    public Result add(@RequestBody Building building) {
        if (isNameExists(building.getName(), null)) {
            return Result.error("已有同名楼栋，请修改后重试");
        }
        buildingService.save(building);
        return Result.ok("新增成功");
    }

    /**
     * 修改楼栋
     * PUT /buildings/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Building building) {
        if (isNameExists(building.getName(), id)) {
            return Result.error("已有同名楼栋，请修改后重试");
        }
        building.setId(id);
        buildingService.updateById(building);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除楼栋（名下有楼层时不允许删除）
     * DELETE /buildings/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (hasFloor(id)) {
            return Result.error("该楼栋下存在楼层，不允许删除");
        }
        buildingService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除楼栋（名下有楼层时不允许删除）
     * DELETE /buildings
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            if (hasFloor(id)) {
                return Result.error("选中楼栋下存在楼层，不允许删除");
            }
        }
        buildingService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断楼栋名称是否已存在（编辑时排除自己）
     */
    private boolean isNameExists(String name, Long excludeId) {
        if (ObjectUtils.isEmpty(name)) {
            return false;
        }
        LambdaQueryWrapper<Building> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Building::getName, name)
                .ne(!ObjectUtils.isEmpty(excludeId), Building::getId, excludeId);
        return buildingService.count(lambdaQueryWrapper) > 0;
    }

    /**
     * 判断楼栋下是否存在楼层
     */
    private boolean hasFloor(Long buildingId) {
        return floorService.count(new LambdaQueryWrapper<Floor>().eq(Floor::getBuildingId, buildingId)) > 0;
    }
}