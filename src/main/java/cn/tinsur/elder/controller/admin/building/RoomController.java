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


import cn.tinsur.elder.pojo.entity.Bed;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.query.RoomQuery;
import cn.tinsur.elder.service.IBedService;
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
 * 房间表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/rooms")
public class RoomController {
    @Autowired
    private IRoomService roomService;
    @Autowired
    private IBedService bedService;

    /**
     * 分页查询房间列表
     * GET /rooms?page=1&limit=10&buildingId=1&floorId=1
     */
    @GetMapping
    public Result<IPage> pageList(RoomQuery roomQuery) {
        IPage page = roomService.list(roomQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询房间
     * GET /rooms/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(roomService.getVOById(id));
    }

    /**
     * 获取全部房间列表List（供床位管理等页面的房间下拉框使用）
     * GET /rooms/list
     */
    @GetMapping("/list")
    public Result<List<Room>> list() {
        return Result.ok(roomService.listAll());
    }

    /**
     * 新增房间
     * POST /rooms
     */
    @PostMapping
    public Result add(@RequestBody Room room) {
        if (isRoomNoExists(room.getFloorId(), room.getRoomNo(), null)) {
            return Result.error("该楼层下已存在相同房间号，请修改后重试");
        }
        roomService.save(room);
        return Result.ok("新增成功");
    }

    /**
     * 修改房间
     * PUT /rooms/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Room room) {
        if (isRoomNoExists(room.getFloorId(), room.getRoomNo(), id)) {
            return Result.error("该楼层下已存在相同房间号，请修改后重试");
        }
        room.setId(id);
        roomService.updateById(room);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除房间（名下有床位时不允许删除）
     * DELETE /rooms/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (hasBed(id)) {
            return Result.error("该房间下存在床位，不允许删除");
        }
        roomService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除房间（名下有床位时不允许删除）
     * DELETE /rooms
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            if (hasBed(id)) {
                return Result.error("选中房间下存在床位，不允许删除");
            }
        }
        roomService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断同一楼层下房间号是否已存在（编辑时排除自己）
     */
    private boolean isRoomNoExists(Long floorId, String roomNo, Long excludeId) {
        if (ObjectUtils.isEmpty(floorId) || ObjectUtils.isEmpty(roomNo)) {
            return false;
        }
        LambdaQueryWrapper<Room> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Room::getFloorId, floorId)
                .eq(Room::getRoomNo, roomNo)
                .ne(!ObjectUtils.isEmpty(excludeId), Room::getId, excludeId);
        return roomService.count(lambdaQueryWrapper) > 0;
    }

    /**
     * 判断房间下是否存在床位
     */
    private boolean hasBed(Long roomId) {
        return bedService.count(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId)) > 0;
    }
}