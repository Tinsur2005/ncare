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
package cn.tinsur.elder.service.impl.care;

import cn.tinsur.elder.mapper.CareLevelMapper;
import cn.tinsur.elder.mapper.CarePlanItemMapper;
import cn.tinsur.elder.mapper.CarePlanMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.CareLevel;
import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.CarePlanQuery;
import cn.tinsur.elder.pojo.vo.CarePlanVO;
import cn.tinsur.elder.service.ICarePlanService;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.Result;
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
 * 护理计划表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class CarePlanServiceImpl extends ServiceImpl<CarePlanMapper, CarePlan> implements ICarePlanService {

    @Autowired
    private CarePlanMapper carePlanMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CareLevelMapper careLevelMapper;

    @Autowired
    private CarePlanItemMapper carePlanItemMapper;

    @Autowired
    private ICareTaskService careTaskService;

    /**
     * 获取护理计划列表（分页），返回 CarePlanVO，并给每个 VO 填充老人姓名 elName、护理人员姓名 uName、护理等级名称 lName
     * @param carePlanQuery
     * @return
     */
    @Override
    public IPage<CarePlanVO> list(CarePlanQuery carePlanQuery) {
        // 1.先查护理计划分页
        IPage<CarePlan> page = new Page<>(carePlanQuery.getPage(), carePlanQuery.getLimit());
        LambdaQueryWrapper<CarePlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(carePlanQuery.getName()), CarePlan::getName, carePlanQuery.getName())
                .eq(!ObjectUtils.isEmpty(carePlanQuery.getElderId()), CarePlan::getElderId, carePlanQuery.getElderId())
                .between(!ObjectUtils.isEmpty(carePlanQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(carePlanQuery.getEndCreateTime()),
                        CarePlan::getCreateTime, carePlanQuery.getBeginCreateTime(),
                        carePlanQuery.getEndCreateTime())
                .orderByDesc(CarePlan::getCreateTime);
        IPage<CarePlan> carePlanPage = carePlanMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的CarePlan转成CarePlanVO
        List<CarePlanVO> carePlanVOList = carePlanPage.getRecords().stream()
                .map(carePlan -> {
                    CarePlanVO vo = new CarePlanVO();
                    BeanUtils.copyProperties(carePlan, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、护理人员姓名、护理等级名称
        fillNames(carePlanVOList);

        // 4.返回CarePlanVO类型的分页
        IPage<CarePlanVO> voPage = new Page<>(carePlanPage.getCurrent(), carePlanPage.getSize(), carePlanPage.getTotal());
        voPage.setRecords(carePlanVOList);
        return voPage;
    }

    /**
     * 批量给CarePlanVO填充老人姓名、护理人员姓名、护理等级名称
     */
    private void fillNames(List<CarePlanVO> carePlanVOList) {
        if (carePlanVOList.isEmpty()) return;

        // 1.老人姓名：取当前页所有老人id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> elderIds = carePlanVOList.stream()
                .map(CarePlanVO::getElderId)
                .distinct()
                .toList();
        Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(Elder::getId, Elder::getRealName));

        // 2.护理人员姓名：取当前页所有护理人员id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> userIds = carePlanVOList.stream()
                .map(CarePlanVO::getUserId)
                .distinct()
                .toList();
        Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getRealName));

        // 3.护理等级名称：取当前页所有护理等级id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> careLevelIds = carePlanVOList.stream()
                .map(CarePlanVO::getCareLevelId)
                .distinct()
                .toList();
        Map<Long, String> careLevelNameMap = careLevelMapper.selectBatchIds(careLevelIds).stream()
                .collect(Collectors.toMap(CareLevel::getId, CareLevel::getName));

        // 4.回填：每条护理计划的老人/护理人员/护理等级姓名
        carePlanVOList.forEach(vo -> {
            vo.setElderName(elderNameMap.get(vo.getElderId()));
            vo.setUserName(userNameMap.get(vo.getUserId()));
            vo.setCareLevelName(careLevelNameMap.get(vo.getCareLevelId()));
        });
    }

    /**
     * 根据id获取该计划包含的护理项目列表
     * @param id
     * @return
     */
    @Override
    public Result<List<CarePlanItem>> getCareItemsById(Long id) {
        LambdaQueryWrapper<CarePlanItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CarePlanItem::getCarePlanId, id);
        List<CarePlanItem> list = carePlanItemMapper.selectList(lambdaQueryWrapper);
        return Result.ok(list);
    }

    /**
     * 根据计划id删除这个计划的全部护理项目
     * @param id
     */
    public void deleteAllCareItemsById(Long id) {
        LambdaQueryWrapper<CarePlanItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CarePlanItem::getCarePlanId, id);
        carePlanItemMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据计划id添加护理项目，传入的第二个参数应该是护理项目实体组成的List列表
     * @param id
     * @param carePlanItems
     */
    public void addCareItemById(Long id, List<CarePlanItem> carePlanItems) {
        for (CarePlanItem carePlanItem : carePlanItems) {
            carePlanItem.setCarePlanId(id);
            carePlanItemMapper.insert(carePlanItem);
        }
    }

    /**
     * 根据计划id更新护理项目，传入的第二个参数应该是护理项目实体组成的List列表
     * 这个方法的实现方法是，先根据id删除计划项目的中间表中所有数据，再根据id和carePlanItems列表插入新的数据，
     * 保存完后按新配置重新生成该计划的任务（删除未执行任务重算，已完成/已跳过的历史打卡保留）
     * @param id
     * @param carePlanItems
     * @return
     */
    @Override
    public Result updateCareItems(Long id, List<CarePlanItem> carePlanItems) {
        deleteAllCareItemsById(id);
        addCareItemById(id, carePlanItems);
        careTaskService.regenerateTasksForPlan(id);
        return Result.ok("更新成功");
    }

    /**
     * 根据计划id删除护理计划，级联删除该计划的全部任务（含已完成打卡记录）和护理项目
     * @param id
     */
    @Override
    public void deletePlanById(Long id) {
        //1.删该计划全部护理任务
        careTaskService.remove(new LambdaQueryWrapper<CareTask>().eq(CareTask::getCarePlanId, id));
        //2.删该计划全部护理项目
        deleteAllCareItemsById(id);
        //3.删计划本身
        carePlanMapper.deleteById(id);
    }

    /**
     * 批量删除护理计划，同样级联删除各计划的全部任务和护理项目
     * @param ids 计划id集合
     */
    @Override
    public void deletePlanBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        //1.删各计划全部护理任务
        careTaskService.remove(new LambdaQueryWrapper<CareTask>().in(CareTask::getCarePlanId, ids));
        //2.删各计划全部护理项目
        carePlanItemMapper.delete(new LambdaQueryWrapper<CarePlanItem>().in(CarePlanItem::getCarePlanId, ids));
        //3.删计划本身
        removeByIds(ids);
    }
}