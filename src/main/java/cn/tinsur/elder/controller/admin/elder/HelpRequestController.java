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
package cn.tinsur.elder.controller.admin.elder;

import cn.tinsur.elder.pojo.query.HelpRequestQuery;
import cn.tinsur.elder.pojo.vo.HelpRequestVO;
import cn.tinsur.elder.service.IHelpRequestService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * <p>
 * 求助记录表 前端控制器
 * </p>
 * 老人在前台发起求助写入未处理记录，管理端在此分页查看并处理：提交处理、忽略、删除
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/help-requests")
public class HelpRequestController {
    @Autowired
    private IHelpRequestService helpRequestService;

    /**
     * 分页查询求助列表
     * GET /help-requests?elderId=1&type=0&urgency=1&status=0&page=1&limit=10
     */
    @GetMapping
    public Result<IPage<HelpRequestVO>> pageList(HelpRequestQuery helpRequestQuery) {
        IPage<HelpRequestVO> page = helpRequestService.list(helpRequestQuery);
        return Result.ok(page);
    }

    /**
     * 提交处理，填写处理结果后将该求助置为已处理
     * PUT /help-requests/handle/1，请求体 {"result":"处理结果说明"}
     */
    @PutMapping("/handle/{id}")
    public Result handle(@PathVariable Long id,
                         @RequestBody Map<String, String> body,
                         @RequestHeader("Authorization") String token) {
        //解析当前登录管理员id，作为处理人记录
        Long handlerId = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        String result = body.get("result");
        return helpRequestService.handle(id, result, handlerId);
    }

    /**
     * 忽略求助，将该求助置为已忽略
     * PUT /help-requests/ignore/1
     */
    @PutMapping("/ignore/{id}")
    public Result ignore(@PathVariable Long id,
                         @RequestHeader("Authorization") String token) {
        //解析当前登录管理员id，作为处理人记录
        Long handlerId = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        return helpRequestService.ignore(id, handlerId);
    }

    /**
     * 根据ID删除求助
     * DELETE /help-requests/1
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        helpRequestService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除求助
     * DELETE /help-requests
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        helpRequestService.removeByIds(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}