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
package cn.tinsur.elder.controller.admin.customer;


import cn.tinsur.elder.mapper.FamilyMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Family;
import cn.tinsur.elder.pojo.query.FamilyQuery;
import cn.tinsur.elder.pojo.vo.FamilyVO;
import cn.tinsur.elder.service.IFamilyService;
import cn.tinsur.elder.util.PasswordUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 家属表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-01
 */
@RestController
@RequestMapping("/admin/families")
public class FamilyController {

    @Autowired
    private IFamilyService familyService;
    @Autowired
    private FamilyMapper familyMapper;

    /**
     * 分页查询家属列表
     * GET /family?page=1&limit=10&name=xxx&realName=xxx&phone=xxx
     */
    @GetMapping
    public Result<IPage<FamilyVO>> list(FamilyQuery familyQuery) {
        IPage<FamilyVO> page = familyService.list(familyQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询家属
     * GET /families/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(familyService.getById(id));
    }

    /**
     * 新增家属
     * POST /family
     */
    @PostMapping
    public Result add(@RequestBody Family family) {
        if(isExists(family.getName())) {
            return Result.error("已有同名家属存在，请修改用户名后重试");
        }
        family.setPassword(PasswordUtil.hash(family.getPassword()));
        familyService.save(family);
        // save后MyBatis-Plus会把自增id回填到family对象中，返回给前端供新增后直接绑定老人
        return Result.ok("新增成功", family.getId());
    }

    /**
     * 修改家属
     * PUT /families/1
     * 注意：密码字段传null时MyBatis-Plus不会更新该列，前端编辑时密码留空即不修改密码
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Family family) {
        //密码留空表示不修改，置null让MyBatis-Plus跳过该列；填了则BCrypt加密后再更新
        if (family.getPassword() == null || family.getPassword().isEmpty()) {
            family.setPassword(null);
        } else {
            family.setPassword(PasswordUtil.hash(family.getPassword()));
        }
        family.setId(id);
        familyService.updateById(family);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除家属（逻辑删除），并同步删除elder-family中间表中的关联数据
      * DELETE /families/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        familyService.deleteFamilyById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除家属，并同步删除elder-family中间表中的关联数据
      * DELETE /family
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        familyService.deleteFamilyBatch(ids);
        return Result.ok("批量删除成功");
    }

    /**
     * 判断家属是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        Family family = familyService.getOne(new QueryWrapper<Family>().eq("name", name));
        return family != null;
    }

    /**
     * 根据真实姓名（realName）模糊查询家属，供"远程搜索"下拉框使用
     * GET /families/searchByName?name=张
     */
    @GetMapping("/searchByName")
    public Result<List<Family>> searchByName(@RequestParam String name) {
        return Result.ok(familyService.searchByName(name));
    }

    /**
     * 获取指定家属关联的老人的列表，
     * result.data中存放该家属所有关联的老人组成的List列表
     */
    @GetMapping("/getEldersById/{id}")
    public Result<List<Elder>> getEldersById(@PathVariable Long id) {
        return familyService.getEldersById(id);
    }

    /**
     * 修改更新家属关联的老人的列表
     */
    @PutMapping("/updateElders/{id}")
    public Result updateElders (@PathVariable Long id, @RequestBody Long[] elderIds) {
        return familyService.updateElders(id, elderIds);
    }
}
