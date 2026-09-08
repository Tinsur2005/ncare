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
package cn.tinsur.elder.service.impl.customer;

import cn.tinsur.elder.mapper.ContractMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.query.ContractQuery;
import cn.tinsur.elder.pojo.vo.ContractVO;
import cn.tinsur.elder.service.IContractService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 合同表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ElderMapper elderMapper;

    /**
     * 获取合同列表（分页），返回ContractVO，并在每个ContractVO中填充绑定的老人姓名elderName
     * @param contractQuery
     * @return
     */
    @Override
    public IPage<ContractVO> list(ContractQuery contractQuery) {
        // 1.先查合同分页
        IPage<Contract> page = new Page<>(contractQuery.getPage(), contractQuery.getLimit());
        LambdaQueryWrapper<Contract> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(contractQuery.getContractName()),
                        Contract::getContractName, contractQuery.getContractName())
                .eq(!ObjectUtils.isEmpty(contractQuery.getElderId()),
                        Contract::getElderId, contractQuery.getElderId())
                .between(!ObjectUtils.isEmpty(contractQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(contractQuery.getEndCreateTime()),
                        Contract::getCreateTime, contractQuery.getBeginCreateTime(),
                        contractQuery.getEndCreateTime())
                .orderByDesc(Contract::getCreateTime);
        IPage<Contract> contractPage = contractMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的Contract转成ContractVO
        List<ContractVO> contractVOList = contractPage.getRecords().stream()
                .map(contract -> {
                    ContractVO vo = new ContractVO();
                    BeanUtils.copyProperties(contract, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名，调用下面的fillElderName方法
        fillElderName(contractVOList);

        // 4.返回ContractVO类型的分页
        IPage<ContractVO> voPage = new Page<>(contractPage.getCurrent(), contractPage.getSize(), contractPage.getTotal());
        voPage.setRecords(contractVOList);
        return voPage;
    }

    /**
     * 批量给ContractVO填充老人姓名
     */
    private void fillElderName(List<ContractVO> contractVOList) {
        if (contractVOList.isEmpty()) return;

        // 1.当前页所有老人id，使用stream流先把所有老人id取出去重
        List<Long> elderIds = contractVOList.stream()
                .map(ContractVO::getElderId)
                .distinct()
                .toList();

        // 2.一次查出这些老人的姓名，组装成Map<Long, String>，key是老人id，value是老人姓名
        Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(Elder::getId, Elder::getRealName));

        // 3.回填：每个合同的老人姓名
        contractVOList.forEach(vo -> vo.setElderName(elderNameMap.get(vo.getElderId())));
    }
}