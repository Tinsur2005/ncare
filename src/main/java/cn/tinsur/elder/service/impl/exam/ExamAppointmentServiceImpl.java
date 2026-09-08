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
package cn.tinsur.elder.service.impl.exam;

import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.ExamAppointmentItemMapper;
import cn.tinsur.elder.mapper.ExamAppointmentMapper;
import cn.tinsur.elder.mapper.ExamItemMapper;
import cn.tinsur.elder.mapper.ExamPackageItemMapper;
import cn.tinsur.elder.mapper.ExamPackageMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.ExamAppointmentItem;
import cn.tinsur.elder.pojo.entity.ExamItem;
import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
import cn.tinsur.elder.service.IExamAppointmentService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 老人预约和体检记录表 服务实现类
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@Service
public class ExamAppointmentServiceImpl extends ServiceImpl<ExamAppointmentMapper, ExamAppointment> implements IExamAppointmentService {

    @Autowired
    private ExamAppointmentMapper examAppointmentMapper;

    @Autowired
    private ExamAppointmentItemMapper examAppointmentItemMapper;

    @Autowired
    private ExamPackageMapper examPackageMapper;

    @Autowired
    private ExamPackageItemMapper examPackageItemMapper;

    @Autowired
    private ExamItemMapper examItemMapper;

    @Autowired
    private ElderMapper elderMapper;

    /**
     * 获取体检预约列表（分页），返回 ExamAppointmentVO，并给每个 VO 填充老人姓名 elderName、套餐名称 packageName
     * @param examAppointmentQuery
     * @return
     */
    @Override
    public IPage<ExamAppointmentVO> list(ExamAppointmentQuery examAppointmentQuery) {
        // 1.先查体检预约分页（按套餐名称搜索时，先查出名称匹配的套餐id，再用id集合过滤预约）
        IPage<ExamAppointment> page = new Page<>(examAppointmentQuery.getPage(), examAppointmentQuery.getLimit());
        List<Long> packageIds = null;
        if (!ObjectUtils.isEmpty(examAppointmentQuery.getPackageName())) {
            packageIds = examPackageMapper.selectList(new LambdaQueryWrapper<ExamPackage>()
                            .like(ExamPackage::getName, examAppointmentQuery.getPackageName()))
                    .stream().map(ExamPackage::getId).toList();
            //没有名称匹配的套餐时直接返回空分页，避免in空集合生成错误SQL
            if (packageIds.isEmpty()) {
                return new Page<>(examAppointmentQuery.getPage(), examAppointmentQuery.getLimit());
            }
        }
        LambdaQueryWrapper<ExamAppointment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!ObjectUtils.isEmpty(examAppointmentQuery.getElderId()), ExamAppointment::getElderId, examAppointmentQuery.getElderId())
                .in(packageIds != null, ExamAppointment::getPackageId, packageIds)
                .eq(!ObjectUtils.isEmpty(examAppointmentQuery.getStatus()), ExamAppointment::getStatus, examAppointmentQuery.getStatus())
                .between(!ObjectUtils.isEmpty(examAppointmentQuery.getBeginAppointmentDate())
                                && !ObjectUtils.isEmpty(examAppointmentQuery.getEndAppointmentDate()),
                        ExamAppointment::getAppointmentDate, examAppointmentQuery.getBeginAppointmentDate(),
                        examAppointmentQuery.getEndAppointmentDate())
                .orderByDesc(ExamAppointment::getCreateTime);
        IPage<ExamAppointment> examAppointmentPage = examAppointmentMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的ExamAppointment转成ExamAppointmentVO
        List<ExamAppointmentVO> examAppointmentVOList = examAppointmentPage.getRecords().stream()
                .map(examAppointment -> {
                    ExamAppointmentVO vo = new ExamAppointmentVO();
                    BeanUtils.copyProperties(examAppointment, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、套餐名称
        fillNames(examAppointmentVOList);

        // 4.返回ExamAppointmentVO类型的分页
        IPage<ExamAppointmentVO> voPage = new Page<>(examAppointmentPage.getCurrent(), examAppointmentPage.getSize(), examAppointmentPage.getTotal());
        voPage.setRecords(examAppointmentVOList);
        return voPage;
    }

    /**
     * 批量给ExamAppointmentVO填充老人姓名、套餐名称
     */
    private void fillNames(List<ExamAppointmentVO> examAppointmentVOList) {
        if (examAppointmentVOList.isEmpty()) return;

        // 1.老人姓名：取当前页所有老人id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> elderIds = examAppointmentVOList.stream()
                .map(ExamAppointmentVO::getElderId)
                .distinct()
                .toList();
        Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(Elder::getId, Elder::getRealName));

        // 2.套餐名称：取当前页所有套餐id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> packageIds = examAppointmentVOList.stream()
                .map(ExamAppointmentVO::getPackageId)
                .distinct()
                .toList();
        Map<Long, String> packageNameMap = examPackageMapper.selectBatchIds(packageIds).stream()
                .collect(Collectors.toMap(ExamPackage::getId, ExamPackage::getName));

        // 3.回填：每条体检预约的老人姓名、套餐名称
        examAppointmentVOList.forEach(vo -> {
            vo.setElderName(elderNameMap.get(vo.getElderId()));
            vo.setPackageName(packageNameMap.get(vo.getPackageId()));
        });
    }

    /**
     * 新增体检预约
     * 注意：在保存预约时快照套餐价格，并把套餐下的体检项目复制为体检记录明细
     * @param examAppointment
     * @return
     */
    @Override
    @Transactional
    public Result addAppointment(ExamAppointment examAppointment) {
        // 1.校验套餐：必须存在且为上架状态
        ExamPackage examPackage = examPackageMapper.selectById(examAppointment.getPackageId());
        if (examPackage == null) {
            return Result.error("体检套餐不存在，请重新选择");
        }
        if (examPackage.getStatus() != 1) {
            return Result.error("该体检套餐已下架，无法预约");
        }
        // 2.保存预约：价格快照自套餐，状态默认待体检
        examAppointment.setPrice(examPackage.getPrice());
        examAppointment.setStatus(0);
        examAppointmentMapper.insert(examAppointment);
        // 3.把套餐下的体检项目一次性复制为体检记录明细（项目名称快照，结果待录入）
        copyPackageItems(examAppointment.getId(), examAppointment.getPackageId());
        return Result.ok("新增成功", examAppointment.getId());
    }

    /**
     * 修改体检预约：可修改预约日期/时间/备注，换套餐时重新快照价格并重建明细
     * 仅限待体检状态
     * @param id
     * @param examAppointment
     * @return
     */
    @Override
    @Transactional
    public Result updateAppointment(Long id, ExamAppointment examAppointment) {
        // 1.仅待体检状态的预约可以修改
        ExamAppointment dbAppointment = examAppointmentMapper.selectById(id);
        if (dbAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        if (dbAppointment.getStatus() != 0) {
            return Result.error("仅待体检状态的预约可以修改");
        }
        // 2.换了套餐时：重新快照套餐价格并重建明细
        if (!dbAppointment.getPackageId().equals(examAppointment.getPackageId())) {
            ExamPackage examPackage = examPackageMapper.selectById(examAppointment.getPackageId());
            if (examPackage == null) {
                return Result.error("体检套餐不存在，请重新选择");
            }
            if (examPackage.getStatus() != 1) {
                return Result.error("该体检套餐已下架，无法预约");
            }
            examAppointment.setPrice(examPackage.getPrice());
            deleteAllAppointmentItemsById(id);
            copyPackageItems(id, examAppointment.getPackageId());
        }
        // 3.更新预约本身
        examAppointment.setId(id);
        examAppointmentMapper.updateById(examAppointment);
        return Result.ok("修改成功");
    }

    /**
     * 把套餐下的体检项目复制为指定体检记录的明细（项目名称快照，结果待录入）
     * @param appointmentId
     * @param packageId
     */
    private void copyPackageItems(Long appointmentId, Long packageId) {
        // 1.查出套餐包含的全部项目关联
        List<ExamPackageItem> packageItems = examPackageItemMapper.selectList(
                new LambdaQueryWrapper<ExamPackageItem>().eq(ExamPackageItem::getPackageId, packageId));
        if (packageItems.isEmpty()) return;

        // 2.一次查出全部体检项目，组装项目id -> 体检项目的Map
        List<Long> examItemIds = packageItems.stream()
                .map(ExamPackageItem::getExamItemId)
                .distinct()
                .toList();
        Map<Long, ExamItem> examItemMap = examItemMapper.selectBatchIds(examItemIds).stream()
                .collect(Collectors.toMap(ExamItem::getId, examItem -> examItem));

        // 3.逐条生成体检记录明细：项目名称快照自体检项目，结果默认待检查
        for (ExamPackageItem packageItem : packageItems) {
            ExamItem examItem = examItemMap.get(packageItem.getExamItemId());
            if (examItem == null) continue;
            ExamAppointmentItem appointmentItem = new ExamAppointmentItem();
            appointmentItem.setAppointmentId(appointmentId);
            appointmentItem.setExamItemId(examItem.getId());
            appointmentItem.setItemName(examItem.getName());
            appointmentItem.setStatus(0);
            appointmentItem.setAbnormal(0);
            examAppointmentItemMapper.insert(appointmentItem);
        }
    }

    /**
     * 开始体检：将状态从待体检变为体检中
     * @param id
     * @return
     */
    @Override
    public Result startAppointment(Long id) {
        ExamAppointment dbAppointment = examAppointmentMapper.selectById(id);
        if (dbAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        if (dbAppointment.getStatus() != 0) {
            return Result.error("仅待体检状态的预约可以开始体检");
        }
        ExamAppointment examAppointment = new ExamAppointment();
        examAppointment.setId(id);
        examAppointment.setStatus(1);
        examAppointmentMapper.updateById(examAppointment);
        return Result.ok("已开始体检");
    }

    /**
     * 取消预约：把状态从待体检或者体检中变为已取消
     * @param id
     * @return
     */
    @Override
    public Result cancelAppointment(Long id) {
        ExamAppointment dbAppointment = examAppointmentMapper.selectById(id);
        if (dbAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        if (dbAppointment.getStatus() != 0 && dbAppointment.getStatus() != 1) {
            return Result.error("仅待体检、体检中状态的预约可以取消");
        }
        ExamAppointment examAppointment = new ExamAppointment();
        examAppointment.setId(id);
        examAppointment.setStatus(3);
        examAppointmentMapper.updateById(examAppointment);
        return Result.ok("已取消预约");
    }

    /**
     * 获取指定体检记录包含的所有明细
     * 附上体检项目的参考范围，供结果录入/展示
     * @param id
     * @return
     */
    @Override
    public Result<List<ExamAppointmentItemVO>> getAppointmentItemsById(Long id) {
        // 1.查出该记录的全部明细
        List<ExamAppointmentItem> appointmentItems = examAppointmentItemMapper.selectList(
                new LambdaQueryWrapper<ExamAppointmentItem>().eq(ExamAppointmentItem::getAppointmentId, id));
        if (appointmentItems.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        // 2.一次查出全部体检项目，组装项目id -> 体检项目的Map
        List<Long> examItemIds = appointmentItems.stream()
                .map(ExamAppointmentItem::getExamItemId)
                .distinct()
                .toList();
        Map<Long, ExamItem> examItemMap = examItemMapper.selectBatchIds(examItemIds).stream()
                .collect(Collectors.toMap(ExamItem::getId, examItem -> examItem));

        // 3.转成ExamAppointmentItemVO，填充结果类型和参考范围
        List<ExamAppointmentItemVO> voList = appointmentItems.stream()
                .map(appointmentItem -> {
                    ExamAppointmentItemVO vo = new ExamAppointmentItemVO();
                    BeanUtils.copyProperties(appointmentItem, vo);
                    ExamItem examItem = examItemMap.get(appointmentItem.getExamItemId());
                    if (examItem != null) {
                        vo.setResultType(examItem.getResultType());
                        vo.setReferenceMin(examItem.getReferenceMin());
                        vo.setReferenceMax(examItem.getReferenceMax());
                        vo.setReferenceUnit(examItem.getReferenceUnit());
                    }
                    return vo;
                })
                .toList();
        return Result.ok(voList);
    }

    /**
     * 根据体检记录id删除这个记录的全部明细
     * @param id
     */
    public void deleteAllAppointmentItemsById(Long id) {
        LambdaQueryWrapper<ExamAppointmentItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamAppointmentItem::getAppointmentId, id);
        examAppointmentItemMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 暂存体检结果：先删除该记录所有明细，再插入新数据
     * （仅限体检中状态）
     * @param id
     * @param examAppointmentItems
     * @return
     */
    @Override
    @Transactional
    public Result updateAppointmentItems(Long id, List<ExamAppointmentItem> examAppointmentItems) {
        //仅体检中状态的记录可以暂存结果
        ExamAppointment dbAppointment = examAppointmentMapper.selectById(id);
        if (dbAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        if (dbAppointment.getStatus() != 1) {
            return Result.error("仅体检中状态的记录可以录入结果");
        }
        deleteAllAppointmentItemsById(id);
        addAppointmentItems(id, examAppointmentItems);
        return Result.ok("暂存成功");
    }

    /**
     * 根据体检记录id添加体检明细，传入的第二个参数应该是体检明细实体组成的List列表
     * @param id
     * @param examAppointmentItems
     */
    public void addAppointmentItems(Long id, List<ExamAppointmentItem> examAppointmentItems) {
        for (ExamAppointmentItem examAppointmentItem : examAppointmentItems) {
            examAppointmentItem.setAppointmentId(id);
            examAppointmentItemMapper.insert(examAppointmentItem);
        }
    }

    /**
     * 完成体检：保存全部明细结果并流转为已完成（数值型结果自动与参考范围比对判定是否异常）
     * @param id
     * @param examAppointmentItems
     * @return
     */
    @Override
    @Transactional
    public Result completeAppointment(Long id, List<ExamAppointmentItem> examAppointmentItems) {
        // 1.仅体检中状态的记录可以完成体检
        ExamAppointment dbAppointment = examAppointmentMapper.selectById(id);
        if (dbAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        if (dbAppointment.getStatus() != 1) {
            return Result.error("仅体检中状态的记录可以完成体检");
        }
        // 2.一次查出全部体检项目，组装项目id -> 体检项目的Map
        List<Long> examItemIds = examAppointmentItems.stream()
                .map(ExamAppointmentItem::getExamItemId)
                .distinct()
                .toList();
        Map<Long, ExamItem> examItemMap = examItemMapper.selectBatchIds(examItemIds).stream()
                .collect(Collectors.toMap(ExamItem::getId, examItem -> examItem));

        // 3.逐条判定结果状态：数值型与参考范围比对自动判定是否异常，文本型按人工标记
        for (ExamAppointmentItem appointmentItem : examAppointmentItems) {
            ExamItem examItem = examItemMap.get(appointmentItem.getExamItemId());
            if (examItem == null) continue;
            //结果单位快照自体检项目
            appointmentItem.setResultUnit(examItem.getUnit());
            if (examItem.getResultType() == 1) {
                //数值型：有结果时与参考范围比对，超出范围即为异常
                if (appointmentItem.getResultValue() != null) {
                    boolean abnormal = isOutOfRange(appointmentItem.getResultValue(), examItem);
                    appointmentItem.setAbnormal(abnormal ? 1 : 0);
                    appointmentItem.setStatus(abnormal ? 2 : 1);
                } else {
                    //没有数值结果时按未完成处理
                    appointmentItem.setAbnormal(0);
                    appointmentItem.setStatus(3);
                }
            } else {
                //文本型：按人工标记的状态（1正常 2异常 3未完成），默认未完成
                if (appointmentItem.getStatus() == null) {
                    appointmentItem.setStatus(3);
                }
                appointmentItem.setAbnormal(appointmentItem.getStatus() == 2 ? 1 : 0);
            }
        }
        // 4.先删后插保存全部明细
        deleteAllAppointmentItemsById(id);
        addAppointmentItems(id, examAppointmentItems);
        // 5.流转为已完成
        ExamAppointment examAppointment = new ExamAppointment();
        examAppointment.setId(id);
        examAppointment.setStatus(2);
        examAppointmentMapper.updateById(examAppointment);
        return Result.ok("体检完成");
    }

    /**
     * 判断数值型结果是否超出参考范围（未配置参考范围时不判定，视为正常）
     * @param resultValue
     * @param examItem
     * @return
     */
    private boolean isOutOfRange(BigDecimal resultValue, ExamItem examItem) {
        if (examItem.getReferenceMin() == null && examItem.getReferenceMax() == null) {
            return false;
        }
        if (examItem.getReferenceMin() != null && resultValue.compareTo(examItem.getReferenceMin()) < 0) {
            return true;
        }
        if (examItem.getReferenceMax() != null && resultValue.compareTo(examItem.getReferenceMax()) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据id删除体检预约，级联删除该记录的全部明细
     * @param id 预约id
     */
    @Override
    @Transactional
    public void deleteAppointmentById(Long id) {
        //1.删该记录全部明细
        deleteAllAppointmentItemsById(id);
        //2.删预约本身
        examAppointmentMapper.deleteById(id);
    }

    /**
     * 批量删除体检预约，同样级联删除各记录的全部明细
     * @param ids 预约id集合
     */
    @Override
    @Transactional
    public void deleteAppointmentBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        //1.删各记录全部明细
        examAppointmentItemMapper.delete(new LambdaQueryWrapper<ExamAppointmentItem>().in(ExamAppointmentItem::getAppointmentId, ids));
        //2.删预约本身
        removeByIds(ids);
    }
}