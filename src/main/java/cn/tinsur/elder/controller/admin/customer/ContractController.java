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


import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.query.ContractQuery;
import cn.tinsur.elder.pojo.vo.ContractVO;
import cn.tinsur.elder.service.IContractService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 合同表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@RestController
@RequestMapping("/admin/contracts")
public class ContractController {

    @Autowired
    private IContractService contractService;

    /**
     * 分页查询合同列表
     * GET /contracts?page=1&limit=10&beginCreateTime=xxx&endCreateTime=xxx
     */
    @GetMapping
    public Result<IPage<ContractVO>> list(ContractQuery contractQuery) {
        IPage<ContractVO> page = contractService.list(contractQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询合同
     * GET /contracts/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(contractService.getById(id));
    }

    /**
     * 新增合同
     * POST /contracts
     */
    @PostMapping
    public Result add(@RequestBody Contract contract) {
        if(isExists(contract.getContractNo())) {
            return Result.error("已存在相同合同编号，请修改后重试");
        }
        contractService.save(contract);
        return Result.ok("新增成功");
    }

    /**
     * 修改合同
     * PUT /contracts/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Contract contract) {
        contract.setId(id);
        contractService.updateById(contract);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除合同（逻辑删除）
     * DELETE /contracts/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        contractService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除合同（逻辑删除）
     * DELETE /contracts
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        contractService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断合同编号是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String contractNo) {
        Contract contract = contractService.getOne(new QueryWrapper<Contract>().eq("contract_no", contractNo));
        return contract != null;
    }
}
