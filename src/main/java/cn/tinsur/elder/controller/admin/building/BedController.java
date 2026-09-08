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
import cn.tinsur.elder.pojo.query.BedQuery;
import cn.tinsur.elder.service.IBedService;
import cn.tinsur.elder.service.impl.building.BedServiceImpl;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 床位表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/beds")
public class BedController {
    @Autowired
    private IBedService bedService;

    /**
     * 分页查询床位列表
     * GET /beds?page=1&limit=10&buildingId=1&floorId=1&roomId=1&status=0
     */
    @GetMapping
    public Result<IPage> pageList(BedQuery bedQuery) {
        IPage page = bedService.list(bedQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询床位
     * GET /beds/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(bedService.getVOById(id));
    }

    /**
     * 获取全部床位列表List（供入住办理等页面的床位下拉框使用）
     * GET /beds/list
     */
    @GetMapping("/list")
    public Result<List<Bed>> list() {
        return Result.ok(bedService.listAll());
    }

    /**
     * 获取全部空闲床位列表（供入住办理选床使用）
     * GET /beds/free
     */
    @GetMapping("/free")
    public Result<List<Bed>> freeList() {
        return Result.ok(bedService.listFree());
    }

    /**
     * 新增床位
     * POST /beds
     */
    @PostMapping
    public Result add(@RequestBody Bed bed) {
        if (isBedNoExists(bed.getRoomId(), bed.getBedNo(), null)) {
            return Result.error("该房间下已存在相同床位号，请修改后重试");
        }
        //已占用床位必须选择入住老人且该老人不能已住在其他床位上，空闲和维修时不存老人
        if (BedServiceImpl.STATUS_OCCUPIED.equals(bed.getStatus())) {
            if (ObjectUtils.isEmpty(bed.getElderId())) {
                return Result.error("床位状态为已占用时必须选择入住老人");
            }
            if (isElderOccupyingOtherBed(bed.getElderId(), null)) {
                return Result.error("该老人已入住其他床位，请修改后重试");
            }
        } else {
            bed.setElderId(null);
        }
        bedService.save(bed);
        return Result.ok("新增成功");
    }

    /**
     * 修改床位
     * PUT /beds/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Bed bed) {
        if (isBedNoExists(bed.getRoomId(), bed.getBedNo(), id)) {
            return Result.error("该房间下已存在相同床位号，请修改后重试");
        }
        //已占用床位必须选择入住老人且该老人不能已住在其他床位上，空闲和维修时不存老人
        if (BedServiceImpl.STATUS_OCCUPIED.equals(bed.getStatus())) {
            if (ObjectUtils.isEmpty(bed.getElderId())) {
                return Result.error("床位状态为已占用时必须选择入住老人");
            }
            if (isElderOccupyingOtherBed(bed.getElderId(), id)) {
                return Result.error("该老人已入住其他床位，请修改后重试");
            }
        } else {
            bed.setElderId(null);
        }
        bed.setId(id);
        bedService.updateById(bed);
        //updateById会忽略null字段，elder_id要用UpdateWrapper显式更新，保证床位从已占用改成空闲时能清空老人
        bedService.update(new LambdaUpdateWrapper<Bed>().eq(Bed::getId, id).set(Bed::getElderId, bed.getElderId()));
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除床位（已被占用时不允许删除）
     * DELETE /beds/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        Bed bed = bedService.getById(id);
        if (isOccupied(bed)) {
            return Result.error("床位已被占用，不允许删除");
        }
        bedService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除床位（存在已占用床位时不允许删除）
     * DELETE /beds
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        List<Bed> bedList = bedService.listByIds(Arrays.asList(ids));
        for (Bed bed : bedList) {
            if (isOccupied(bed)) {
                return Result.error("选中床位中存在已占用床位，不允许删除");
            }
        }
        bedService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断同一房间下床位号是否已存在（编辑时排除自己）
     */
    private boolean isBedNoExists(Long roomId, String bedNo, Long excludeId) {
        if (ObjectUtils.isEmpty(roomId) || ObjectUtils.isEmpty(bedNo)) {
            return false;
        }
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Bed::getRoomId, roomId)
                .eq(Bed::getBedNo, bedNo)
                .ne(!ObjectUtils.isEmpty(excludeId), Bed::getId, excludeId);
        return bedService.count(lambdaQueryWrapper) > 0;
    }

    /**
     * 判断床位是否已被占用
     */
    private boolean isOccupied(Bed bed) {
        return !ObjectUtils.isEmpty(bed) && BedServiceImpl.STATUS_OCCUPIED.equals(bed.getStatus());
    }

    /**
     * 判断老人是否已住在其他占用床位上（编辑时排除自己）
     */
    private boolean isElderOccupyingOtherBed(Long elderId, Long excludeId) {
        if (ObjectUtils.isEmpty(elderId)) {
            return false;
        }
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Bed::getElderId, elderId)
                .eq(Bed::getStatus, BedServiceImpl.STATUS_OCCUPIED)
                .ne(!ObjectUtils.isEmpty(excludeId), Bed::getId, excludeId);
        return bedService.count(lambdaQueryWrapper) > 0;
    }
}