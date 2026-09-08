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

package cn.tinsur.elder.controller.admin.building;


import cn.tinsur.elder.pojo.dto.CheckInAddDTO;
import cn.tinsur.elder.pojo.entity.CheckInRecord;
import cn.tinsur.elder.pojo.query.CheckInRecordQuery;
import cn.tinsur.elder.pojo.vo.CheckInElderVO;
import cn.tinsur.elder.pojo.vo.CheckInRecordVO;
import cn.tinsur.elder.service.ICheckInRecordService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 入住退住办理表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@RestController
@RequestMapping("/admin/check-ins")
public class CheckInRecordController {
    @Autowired
    private ICheckInRecordService checkInRecordService;

    /**
     * 分页查询办理单列表
     * GET /check-ins?page=1&limit=10&type=0&status=0&elderId=1
     */
    @GetMapping
    public Result<IPage<CheckInRecordVO>> list(CheckInRecordQuery checkInRecordQuery) {
        IPage<CheckInRecordVO> page = checkInRecordService.list(checkInRecordQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询办理单详情
     * GET /check-ins/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(checkInRecordService.getVOById(id));
    }

    /**
     * 远程搜索全部老人（不过滤状态，附带床位占用和老人状态），供入住、退住办理第一步选择老人共用，由前端标注并禁选不可办理的老人
     * GET /check-ins/check-in-elders?name=张
     */
    @GetMapping("/check-in-elders")
    public Result<List<CheckInElderVO>> listCheckInElders(@RequestParam(required = false) String name) {
        return Result.ok(checkInRecordService.listCheckInElders(name));
    }

    /**
     * 第一步完成时创建办理单（入住：登记客户和老人；退住：选择在住老人并把老人置为退住中）
     * POST /check-ins
     */
    @PostMapping
    public Result add(@RequestBody CheckInAddDTO checkInAddDTO) {
        return checkInRecordService.add(checkInAddDTO);
    }

    /**
     * 保存第n步的办理数据并把步骤+1
     * PUT /check-ins/1/step/2
     */
    @PutMapping("/{id}/step/{step}")
    public Result saveStep(@PathVariable Long id, @PathVariable Integer step, @RequestBody CheckInRecord checkInRecord) {
        return checkInRecordService.saveStep(id, step, checkInRecord);
    }

    /**
     * 确认入住或确认退住，按办理类型走不同的事务
     * PUT /check-ins/confirm/1
     */
    @PutMapping("/confirm/{id}")
    public Result confirm(@PathVariable Long id,
                          @RequestHeader("Authorization") String token) {
        //解析当前登录管理员id，作为确认办理人记录
        Long handlerId = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        return checkInRecordService.confirm(id, handlerId);
    }

    /**
     * 取消办理（退住单需同时把老人从退住中恢复为入住中）
     * PUT /check-ins/cancel/1
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        return checkInRecordService.cancel(id);
    }

    /**
     * 根据ID删除办理单（仅已完成或已取消的办理单允许删除）
     * DELETE /check-ins/1
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        CheckInRecord checkInRecord = checkInRecordService.getById(id);
        if (ObjectUtils.isEmpty(checkInRecord)) {
            return Result.error("办理单不存在");
        }
        //办理中的单子要先取消才能删除，已完成和已取消的直接删
        if (checkInRecord.getStatus() == 0) {
            return Result.error("办理中的单子不允许删除，请先取消办理");
        }
        checkInRecordService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除办理单（仅已完成或已取消的办理单允许删除）
     * DELETE /check-ins
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            CheckInRecord checkInRecord = checkInRecordService.getById(id);
            if (!ObjectUtils.isEmpty(checkInRecord) && checkInRecord.getStatus() == 0) {
                return Result.error("选中记录中存在办理中的单子，不允许删除");
            }
        }
        checkInRecordService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}