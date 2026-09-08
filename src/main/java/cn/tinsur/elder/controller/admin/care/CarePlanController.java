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


import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.query.CarePlanQuery;
import cn.tinsur.elder.pojo.vo.CarePlanVO;
import cn.tinsur.elder.service.ICarePlanService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 护理计划表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/admin/care-plans")
public class CarePlanController {
    @Autowired
    private ICarePlanService carePlanService;

    /**
     * 分页查询护理计划列表
     * GET /care-plans?page=1&limit=10&name=xxx&elderId=1&status=1
     */
    @GetMapping
    public Result<IPage<CarePlanVO>> pageList(CarePlanQuery carePlanQuery) {
        IPage<CarePlanVO> page = carePlanService.list(carePlanQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理计划
     * GET /care-plans/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(carePlanService.getById(id));
    }

    /**
     * 新增护理计划
     * POST /care-plans
     */
    @PostMapping
    public Result add(@RequestBody CarePlan carePlan) {
        //一次性生成整个计划周期的任务要求计划必须有结束日期
        if (carePlan.getEndDate() == null) {
            return Result.error("结束日期必填：创建计划后需一次性生成整个计划周期内的任务");
        }
        if (isExists(carePlan.getName())) {
            return Result.error("已有同名护理计划，请修改后重试");
        }
        carePlanService.save(carePlan);
        //把新增后自动生成的主键id返回给前端，用于随后保存该计划包含的护理项目（先删后插）
        return Result.ok("新增成功", carePlan.getId());
    }

    /**
     * 修改护理计划
     * PUT /care-plans/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody CarePlan carePlan) {
        carePlan.setId(id);
        carePlanService.updateById(carePlan);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除护理计划（级联删除该计划的全部任务和护理项目）
     * DELETE /care-plans/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        carePlanService.deletePlanById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除护理计划（级联删除各计划的全部任务和护理项目）
     * DELETE /care-plans
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        carePlanService.deletePlanBatch(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断护理计划名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        CarePlan carePlan = carePlanService.getOne(new QueryWrapper<CarePlan>().eq("name", name));
        return carePlan != null;
    }

    /**
     * 获取指定护理计划包含的所有护理项目
     * GET /care-plans/getCareItemsById/1
     */
    @GetMapping("/getCareItemsById/{id}")
    public Result<List<CarePlanItem>> getCareItemsById(@PathVariable Long id) {
        return carePlanService.getCareItemsById(id);
    }

    /**
     * 修改更新护理计划包含的护理项目
     * PUT /care-plans/updateCareItems/1
     */
    @PutMapping("/updateCareItems/{id}")
    public Result updateCareItems(@PathVariable Long id, @RequestBody List<CarePlanItem> carePlanItems) {
        return carePlanService.updateCareItems(id, carePlanItems);
    }
}
