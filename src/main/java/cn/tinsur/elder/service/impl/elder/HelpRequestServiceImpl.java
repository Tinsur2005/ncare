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
package cn.tinsur.elder.service.impl.elder;

import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.HelpRequestMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.HelpRequest;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.HelpRequestQuery;
import cn.tinsur.elder.pojo.vo.HelpRequestVO;
import cn.tinsur.elder.service.IHelpRequestService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 求助记录表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class HelpRequestServiceImpl extends ServiceImpl<HelpRequestMapper, HelpRequest> implements IHelpRequestService {

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取求助列表（分页），返回 HelpRequestVO，并给每个 VO 填充老人姓名和处理人姓名
     * @param helpRequestQuery
     * @return
     */
    @Override
    public IPage<HelpRequestVO> list(HelpRequestQuery helpRequestQuery) {
        // 1.先查求助记录分页
        IPage<HelpRequest> page = new Page<>(helpRequestQuery.getPage(), helpRequestQuery.getLimit());
        LambdaQueryWrapper<HelpRequest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!ObjectUtils.isEmpty(helpRequestQuery.getElderId()), HelpRequest::getElderId, helpRequestQuery.getElderId())
                .eq(!ObjectUtils.isEmpty(helpRequestQuery.getType()), HelpRequest::getType, helpRequestQuery.getType())
                .eq(!ObjectUtils.isEmpty(helpRequestQuery.getUrgency()), HelpRequest::getUrgency, helpRequestQuery.getUrgency())
                .eq(!ObjectUtils.isEmpty(helpRequestQuery.getStatus()), HelpRequest::getStatus, helpRequestQuery.getStatus())
                .between(!ObjectUtils.isEmpty(helpRequestQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(helpRequestQuery.getEndCreateTime()),
                        HelpRequest::getCreateTime, helpRequestQuery.getBeginCreateTime(),
                        helpRequestQuery.getEndCreateTime())
                .orderByDesc(HelpRequest::getCreateTime);
        IPage<HelpRequest> helpRequestPage = helpRequestMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的HelpRequest转成HelpRequestVO
        List<HelpRequestVO> helpRequestVOList = helpRequestPage.getRecords().stream()
                .map(helpRequest -> {
                    HelpRequestVO vo = new HelpRequestVO();
                    BeanUtils.copyProperties(helpRequest, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、处理人姓名
        fillNames(helpRequestVOList);

        // 4.返回HelpRequestVO类型的分页
        IPage<HelpRequestVO> voPage = new Page<>(helpRequestPage.getCurrent(), helpRequestPage.getSize(), helpRequestPage.getTotal());
        voPage.setRecords(helpRequestVOList);
        return voPage;
    }

    /**
     * 批量给HelpRequestVO填充老人姓名、处理人姓名
     */
    private void fillNames(List<HelpRequestVO> helpRequestVOList) {
        if (helpRequestVOList.isEmpty()) return;

        // 1.老人姓名：取当前页所有老人id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> elderIds = helpRequestVOList.stream()
                .map(HelpRequestVO::getElderId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!elderIds.isEmpty()) {
            Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                    .collect(Collectors.toMap(Elder::getId, Elder::getRealName));
            helpRequestVOList.forEach(vo -> vo.setElderName(elderNameMap.get(vo.getElderId())));
        }

        // 2.处理人姓名：取当前页所有处理人id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> handlerIds = helpRequestVOList.stream()
                .map(HelpRequestVO::getHandlerId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!handlerIds.isEmpty()) {
            Map<Long, String> handlerNameMap = userMapper.selectBatchIds(handlerIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getRealName));
            helpRequestVOList.forEach(vo -> vo.setHandlerName(handlerNameMap.get(vo.getHandlerId())));
        }
    }

    /**
     * 提交处理，校验该求助存在且仍为未处理，填写处理结果后置为已处理
     * @param id
     * @param result
     * @param handlerId
     * @return
     */
    @Override
    public Result handle(Long id, String result, Long handlerId) {
        HelpRequest helpRequest = helpRequestMapper.selectById(id);
        if (helpRequest == null) {
            return Result.error("该求助记录不存在");
        }
        //只有未处理的求助才能提交处理，已处理或已忽略的不允许再次处理
        if (helpRequest.getStatus() != null && helpRequest.getStatus() != 0) {
            return Result.error("该求助已处理，不允许再次处理");
        }
        helpRequest.setStatus(1);
        helpRequest.setResult(result);
        helpRequest.setHandlerId(handlerId);
        helpRequest.setHandleTime(new Date());
        helpRequestMapper.updateById(helpRequest);
        return Result.ok("处理成功");
    }

    /**
     * 忽略求助，校验该求助存在且仍为未处理，置为已忽略
     * @param id
     * @param handlerId
     * @return
     */
    @Override
    public Result ignore(Long id, Long handlerId) {
        HelpRequest helpRequest = helpRequestMapper.selectById(id);
        if (helpRequest == null) {
            return Result.error("该求助记录不存在");
        }
        //只有未处理的求助才能忽略，已处理或已忽略的不允许再次处理
        if (helpRequest.getStatus() != null && helpRequest.getStatus() != 0) {
            return Result.error("该求助已处理，不允许再次处理");
        }
        helpRequest.setStatus(2);
        helpRequest.setHandlerId(handlerId);
        helpRequest.setHandleTime(new Date());
        helpRequestMapper.updateById(helpRequest);
        return Result.ok("已忽略该求助");
    }
}