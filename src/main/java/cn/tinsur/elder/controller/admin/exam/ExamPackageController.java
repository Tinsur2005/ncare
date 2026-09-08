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
package cn.tinsur.elder.controller.admin.exam;


import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.query.ExamPackageQuery;
import cn.tinsur.elder.pojo.vo.ExamPackageVO;
import cn.tinsur.elder.service.IExamPackageService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 体检套餐表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/admin/exam-packages")
public class ExamPackageController {
    @Autowired
    private IExamPackageService examPackageService;

    /**
     * 分页查询体检套餐列表
     * GET /exam-packages?page=1&limit=10&name=xxx&status=1
     */
    @GetMapping
    public Result<IPage<ExamPackageVO>> pageList(ExamPackageQuery examPackageQuery) {
        IPage<ExamPackageVO> page = examPackageService.list(examPackageQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询体检套餐
     * GET /exam-packages/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(examPackageService.getById(id));
    }

    /**
     * 新增体检套餐
     * POST /exam-packages
     */
    @PostMapping
    public Result add(@RequestBody ExamPackage examPackage) {
        if (isExists(examPackage.getName())) {
            return Result.error("已有同名体检套餐，请修改后重试");
        }
        examPackageService.save(examPackage);
        //把新增后自动生成的主键id返回给前端，用于随后保存该套餐包含的体检项目（先删后插）
        return Result.ok("新增成功", examPackage.getId());
    }

    /**
     * 修改体检套餐
     * PUT /exam-packages/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ExamPackage examPackage) {
        examPackage.setId(id);
        examPackageService.updateById(examPackage);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除体检套餐（级联删除该套餐的全部项目关联）
     * DELETE /exam-packages/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        if (examPackageService.countInAppointment(Arrays.asList(id)) > 0) {
            return Result.error("该体检套餐已有预约记录，无法删除");
        }
        examPackageService.deletePackageById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除体检套餐（级联删除各套餐的全部项目关联）
     * DELETE /exam-packages
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        if (examPackageService.countInAppointment(Arrays.asList(ids)) > 0) {
            return Result.error("选中的体检套餐已有预约记录，无法删除");
        }
        examPackageService.deletePackageBatch(Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 获取全部上架状态的体检套餐列表List（供体检预约等"选体检套餐"下拉框使用）
     * GET /exam-packages/list
     */
    @GetMapping("/list")
    public Result<List<ExamPackage>> list() {
        return Result.ok(examPackageService.listAll());
    }

    /**
     * 判断体检套餐名称是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        ExamPackage examPackage = examPackageService.getOne(new QueryWrapper<ExamPackage>().eq("name", name));
        return examPackage != null;
    }

    /**
     * 获取指定体检套餐包含的所有体检项目
     * GET /exam-packages/getPackageItemsById/1
     */
    @GetMapping("/getPackageItemsById/{id}")
    public Result<List<ExamPackageItem>> getPackageItemsById(@PathVariable Long id) {
        return examPackageService.getPackageItemsById(id);
    }

    /**
     * 修改更新体检套餐包含的体检项目
     * PUT /exam-packages/updatePackageItems/1
     */
    @PutMapping("/updatePackageItems/{id}")
    public Result updatePackageItems(@PathVariable Long id, @RequestBody List<ExamPackageItem> examPackageItems) {
        return examPackageService.updatePackageItems(id, examPackageItems);
    }
}
