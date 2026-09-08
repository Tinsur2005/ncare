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


import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.query.FloorQuery;
import cn.tinsur.elder.service.IFloorService;
import cn.tinsur.elder.service.IRoomService;
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
 * 楼层表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/floors")
public class FloorController {
    @Autowired
    private IFloorService floorService;
    @Autowired
    private IRoomService roomService;

    /**
     * 分页查询楼层列表
     * GET /floors?page=1&limit=10&buildingId=1
     */
    @GetMapping
    public Result<IPage> pageList(FloorQuery floorQuery) {
        IPage page = floorService.list(floorQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询楼层
     * GET /floors/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(floorService.getById(id));
    }

    /**
     * 获取全部楼层列表List（供房间、床位等页面的楼层下拉框使用）
     * GET /floors/list
     */
    @GetMapping("/list")
    public Result<List<Floor>> list() {
        return Result.ok(floorService.listAll());
    }

    /**
     * 新增楼层
     * POST /floors
     */
    @PostMapping
    public Result add(@RequestBody Floor floor) {
        if (isFloorNoExists(floor.getBuildingId(), floor.getFloorNo(), null)) {
            return Result.error("该楼栋下已存在相同楼层号，请修改后重试");
        }
        floorService.save(floor);
        return Result.ok("新增成功");
    }

    /**
     * 修改楼层
     * PUT /floors/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Floor floor) {
        if (isFloorNoExists(floor.getBuildingId(), floor.getFloorNo(), id)) {
            return Result.error("该楼栋下已存在相同楼层号，请修改后重试");
        }
        floor.setId(id);
        floorService.updateById(floor);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除楼层（名下有房间时不允许删除）
     * DELETE /floors/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (hasRoom(id)) {
            return Result.error("该楼层下存在房间，不允许删除");
        }
        floorService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除楼层（名下有房间时不允许删除）
     * DELETE /floors
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            if (hasRoom(id)) {
                return Result.error("选中楼层下存在房间，不允许删除");
            }
        }
        floorService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断同一楼栋下楼层号是否已存在（编辑时排除自己）
     */
    private boolean isFloorNoExists(Long buildingId, Integer floorNo, Long excludeId) {
        if (ObjectUtils.isEmpty(buildingId) || ObjectUtils.isEmpty(floorNo)) {
            return false;
        }
        LambdaQueryWrapper<Floor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Floor::getBuildingId, buildingId)
                .eq(Floor::getFloorNo, floorNo)
                .ne(!ObjectUtils.isEmpty(excludeId), Floor::getId, excludeId);
        return floorService.count(lambdaQueryWrapper) > 0;
    }

    /**
     * 判断楼层下是否存在房间
     */
    private boolean hasRoom(Long floorId) {
        return roomService.count(new LambdaQueryWrapper<Room>().eq(Room::getFloorId, floorId)) > 0;
    }
}