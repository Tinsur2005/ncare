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

import cn.tinsur.elder.mapper.ExamAppointmentMapper;
import cn.tinsur.elder.mapper.ExamPackageItemMapper;
import cn.tinsur.elder.mapper.ExamPackageMapper;
import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.query.ExamPackageQuery;
import cn.tinsur.elder.pojo.vo.ExamPackageVO;
import cn.tinsur.elder.service.IExamPackageService;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 体检套餐表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@Service
public class ExamPackageServiceImpl extends ServiceImpl<ExamPackageMapper, ExamPackage> implements IExamPackageService {

    @Autowired
    private ExamPackageMapper examPackageMapper;

    @Autowired
    private ExamPackageItemMapper examPackageItemMapper;

    @Autowired
    private ExamAppointmentMapper examAppointmentMapper;

    /**
     * 获取体检套餐列表（分页），返回 ExamPackageVO，并给每个 VO 填充套餐包含的体检项目数量 itemCount
     * @param examPackageQuery
     * @return
     */
    @Override
    public IPage<ExamPackageVO> list(ExamPackageQuery examPackageQuery) {
        // 1.先查体检套餐分页
        IPage<ExamPackage> page = new Page<>(examPackageQuery.getPage(), examPackageQuery.getLimit());
        LambdaQueryWrapper<ExamPackage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(examPackageQuery.getName()), ExamPackage::getName, examPackageQuery.getName())
                .eq(!ObjectUtils.isEmpty(examPackageQuery.getStatus()), ExamPackage::getStatus, examPackageQuery.getStatus())
                .between(!ObjectUtils.isEmpty(examPackageQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(examPackageQuery.getEndCreateTime()),
                        ExamPackage::getCreateTime, examPackageQuery.getBeginCreateTime(),
                        examPackageQuery.getEndCreateTime())
                .orderByAsc(ExamPackage::getSort);
        IPage<ExamPackage> examPackagePage = examPackageMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的ExamPackage转成ExamPackageVO
        List<ExamPackageVO> examPackageVOList = examPackagePage.getRecords().stream()
                .map(examPackage -> {
                    ExamPackageVO vo = new ExamPackageVO();
                    BeanUtils.copyProperties(examPackage, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上套餐包含的体检项目数量
        fillItemCount(examPackageVOList);

        // 4.返回ExamPackageVO类型的分页
        IPage<ExamPackageVO> voPage = new Page<>(examPackagePage.getCurrent(), examPackagePage.getSize(), examPackagePage.getTotal());
        voPage.setRecords(examPackageVOList);
        return voPage;
    }

    /**
     * 批量给ExamPackageVO填充套餐包含的体检项目数量
     */
    private void fillItemCount(List<ExamPackageVO> examPackageVOList) {
        if (examPackageVOList.isEmpty()) return;

        // 1.取当前页所有套餐id，一次查出这些套餐的全部项目关联
        List<Long> packageIds = examPackageVOList.stream()
                .map(ExamPackageVO::getId)
                .distinct()
                .toList();
        List<ExamPackageItem> packageItems = examPackageItemMapper.selectList(
                new LambdaQueryWrapper<ExamPackageItem>().in(ExamPackageItem::getPackageId, packageIds));

        // 2.按套餐id分组统计项目数量，组装成 Map<Long, Long>
        Map<Long, Long> itemCountMap = packageItems.stream()
                .collect(Collectors.groupingBy(ExamPackageItem::getPackageId, Collectors.counting()));

        // 3.回填：每个套餐的项目数量，没有项目关联的套餐填0
        examPackageVOList.forEach(vo ->
                vo.setItemCount(itemCountMap.getOrDefault(vo.getId(), 0L).intValue()));
    }

    /**
     * 获取全部上架状态的体检套餐列表，供体检预约选择套餐时使用
     * @return 体检套餐的List列表
     */
    @Override
    public List<ExamPackage> listAll() {
        return examPackageMapper.selectList(new LambdaQueryWrapper<ExamPackage>()
                .eq(ExamPackage::getStatus, 1)
                .orderByAsc(ExamPackage::getSort));
    }

    /**
     * 根据id获取该套餐包含的体检项目列表
     * @param id
     * @return
     */
    @Override
    public Result<List<ExamPackageItem>> getPackageItemsById(Long id) {
        LambdaQueryWrapper<ExamPackageItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamPackageItem::getPackageId, id).orderByAsc(ExamPackageItem::getSort);
        List<ExamPackageItem> list = examPackageItemMapper.selectList(lambdaQueryWrapper);
        return Result.ok(list);
    }

    /**
     * 根据套餐id删除这个套餐的全部项目关联
     * @param id
     */
    public void deleteAllPackageItemsById(Long id) {
        LambdaQueryWrapper<ExamPackageItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamPackageItem::getPackageId, id);
        examPackageItemMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据套餐id添加体检项目，传入的第二个参数应该是体检项目关联实体组成的List列表
     * @param id
     * @param examPackageItems
     */
    public void addPackageItemById(Long id, List<ExamPackageItem> examPackageItems) {
        for (ExamPackageItem examPackageItem : examPackageItems) {
            examPackageItem.setPackageId(id);
            examPackageItemMapper.insert(examPackageItem);
        }
    }

    /**
     * 根据套餐id更新体检项目，传入的第二个参数应该是体检项目关联实体组成的List列表
     * 这个方法的实现方法是，先根据id删除套餐项目中间表中所有数据，再根据id和examPackageItems列表插入新的数据
     * @param id
     * @param examPackageItems
     * @return
     */
    @Override
    @Transactional
    public Result updatePackageItems(Long id, List<ExamPackageItem> examPackageItems) {
        deleteAllPackageItemsById(id);
        addPackageItemById(id, examPackageItems);
        return Result.ok("更新成功");
    }

    /**
     * 根据套餐id删除体检套餐，级联删除该套餐的全部项目关联
     * @param id 套餐id
     */
    @Override
    @Transactional
    public void deletePackageById(Long id) {
        //1.删该套餐全部项目关联
        deleteAllPackageItemsById(id);
        //2.删套餐本身
        examPackageMapper.deleteById(id);
    }

    /**
     * 批量删除体检套餐，同样级联删除各套餐的全部项目关联
     * @param ids 套餐id集合
     */
    @Override
    @Transactional
    public void deletePackageBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        //1.删各套餐全部项目关联
        examPackageItemMapper.delete(new LambdaQueryWrapper<ExamPackageItem>().in(ExamPackageItem::getPackageId, ids));
        //2.删套餐本身
        removeByIds(ids);
    }

    /**
     * 统计体检套餐被体检预约引用的数量
     * @param ids 体检套餐ID集合
     * @return 引用数量
     */
    @Override
    public Long countInAppointment(List<Long> ids) {
        return examAppointmentMapper.selectCount(new LambdaQueryWrapper<ExamAppointment>()
                .in(ExamAppointment::getPackageId, ids));
    }
}