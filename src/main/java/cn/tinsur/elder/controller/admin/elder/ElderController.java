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


import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.pojo.vo.ElderVO;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.PasswordUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 老人们信息表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-25
 */
@RestController
@RequestMapping("/admin/elders")
public class ElderController {

    @Autowired
    private IElderService elderService;
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 分页查询老人列表
     * GET /elders?page=1&limit=10&name=xxx&phone=xxx
     */
    @GetMapping
    public Result<IPage<ElderVO>> list(ElderQuery elderQuery) {
        IPage<ElderVO> page = elderService.list(elderQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询老人
     * GET /elders/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(elderService.getById(id));
    }

    /**
     * 新增老人
     * POST /elders
     */
    @PostMapping
    public Result add(@RequestBody Elder elder) {
        if(isExists(elder.getName())) {
            return Result.error("已有同名老人存在，请修改姓名后重试");
        }
        elder.setPassword(PasswordUtil.hash(elder.getPassword()));
        elderService.save(elder);
        return Result.ok("新增成功");
    }

    /**
     * 修改老人
     * PUT /elders/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Elder elder) {
        //密码留空表示不修改，置null让MyBatis-Plus跳过该列；填了则BCrypt加密后再更新
        if (elder.getPassword() == null || elder.getPassword().isEmpty()) {
            elder.setPassword(null);
        } else {
            elder.setPassword(PasswordUtil.hash(elder.getPassword()));
        }
        elder.setId(id);
        elderService.updateById(elder);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除老人（逻辑删除）
      * DELETE /elders/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        elderService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除老人
      * DELETE /elders
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        elderService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断老人是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        Elder elder = elderService.getOne(new QueryWrapper<Elder>().eq("name", name));
        return elder != null;
    }

    /**
     * 根据真实姓名（realName）模糊查询老人，供合同选老人等"远程搜索"下拉框使用
     * GET /elders/searchByName?name=张
     */
    @GetMapping("/searchByName")
    public Result<List<Elder>> searchByName(@RequestParam String name) {
        return Result.ok(elderService.searchByName(name));
    }

    /**
     * 获取指定老人Tags标注列表，
     * result.data中存放老人所有的Tag组成的List列表
     */
    @GetMapping("/getTagsById/{id}")
    public Result<List<Tag>> getTagsById(@PathVariable Long id) {
        return elderService.getTagsById(id);
    }

    /**
     * 修改更新老人的Tags标注列表
     */
    @PutMapping("/updateTags/{id}")
    public Result updateTags (@PathVariable Long id, @RequestBody Long[] tags) {
        return elderService.updateTags(id, tags);
    }

    //导出Excel
    @GetMapping("/exportExcel")
    public void exportExcel (HttpServletResponse response) {
        elderService.exportExcel(response);
    }

    //导入Excel
    @PostMapping("/importExcel")
    public Result importExcel (MultipartFile file) {
        elderService.importExcel(file);
        return Result.ok("导入成功");
    }
}
