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

import cn.tinsur.elder.mapper.CareItemMapper;
import cn.tinsur.elder.mapper.CarePlanItemMapper;
import cn.tinsur.elder.mapper.CarePlanMapper;
import cn.tinsur.elder.mapper.CareTaskMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.CareItem;
import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 护理任务与打卡记录表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Slf4j
@Service
public class CareTaskServiceImpl extends ServiceImpl<CareTaskMapper, CareTask> implements ICareTaskService {

    @Autowired
    private CareTaskMapper careTaskMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CarePlanMapper carePlanMapper;

    @Autowired
    private CarePlanItemMapper carePlanItemMapper;

    @Autowired
    private CareItemMapper careItemMapper;

    /**
     * 获取护理任务列表（分页），返回 CareTaskVO，并给每个 VO 填充老人姓名、执行护理员姓名。
     * 任务由「保存护理计划项目」时一次性生成整个计划周期，这里只做查询。
     * 查看范围兜底：只有 viewScope=all 且当前用户有 careTask:viewAll 权限才放开，
     * 否则一律强制 userId=当前登录用户（护工只能查看自己的任务，防止伪造参数越权）
     *
     * @param careTaskQuery
     * @param token 当前登录用户的 JWT
     * @return
     */
    @Override
    public IPage<CareTaskVO> list(CareTaskQuery careTaskQuery, String token) {
        // 0.解析当前登录用户，并判定其是否拥有 careTask:viewAll 按钮权限
        Long currentUserId = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        boolean hasViewAllPermission = userMapper.selectPermissionByUserId(currentUserId).stream()
                .anyMatch(p -> p.getType() == 2 && "careTask:viewAll".equals(p.getPermissionValue()));
        boolean viewAll = "all".equals(careTaskQuery.getViewScope()) && hasViewAllPermission;

        // 1.先查护理任务分页（按查看范围、老人、状态、计划执行日期范围筛选）
        IPage<CareTask> page = new Page<>(careTaskQuery.getPage(), careTaskQuery.getLimit());
        LambdaQueryWrapper<CareTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                //非"查看全部"一律只查自己的任务
                .eq(!viewAll, CareTask::getUserId, currentUserId)
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getElderId()), CareTask::getElderId, careTaskQuery.getElderId())
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getStatus()), CareTask::getStatus, careTaskQuery.getStatus())
                .between(!ObjectUtils.isEmpty(careTaskQuery.getBeginPlanExecuteDate())
                                && !ObjectUtils.isEmpty(careTaskQuery.getEndPlanExecuteDate()),
                        CareTask::getPlanExecuteDate, careTaskQuery.getBeginPlanExecuteDate(),
                        careTaskQuery.getEndPlanExecuteDate())
                .orderByDesc(CareTask::getPlanExecuteDate);
        IPage<CareTask> careTaskPage = careTaskMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的CareTask转成CareTaskVO
        List<CareTaskVO> careTaskVOList = careTaskPage.getRecords().stream()
                .map(careTask -> {
                    CareTaskVO vo = new CareTaskVO();
                    BeanUtils.copyProperties(careTask, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、执行护理员姓名
        fillNames(careTaskVOList);

        // 4.返回CareTaskVO类型的分页
        IPage<CareTaskVO> voPage = new Page<>(careTaskPage.getCurrent(), careTaskPage.getSize(), careTaskPage.getTotal());
        voPage.setRecords(careTaskVOList);
        return voPage;
    }

    /**
     * 分页查询某个老人的护理任务列表（前台手机端专用），返回带老人姓名、执行护理员姓名的 CareTaskVO
     * 与 list 不同：不解析后台登录用户的权限，只按老人、状态、计划执行日期范围筛选
     *
     * @param careTaskQuery 查询条件（老人、状态、计划执行日期范围、分页）
     * @return
     */
    @Override
    public IPage<CareTaskVO> listByElder(CareTaskQuery careTaskQuery) {
        // 1.先查该老人的护理任务分页（按老人、状态、计划执行日期范围筛选）
        IPage<CareTask> page = new Page<>(careTaskQuery.getPage(), careTaskQuery.getLimit());
        LambdaQueryWrapper<CareTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getElderId()), CareTask::getElderId, careTaskQuery.getElderId())
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getStatus()), CareTask::getStatus, careTaskQuery.getStatus())
                .between(!ObjectUtils.isEmpty(careTaskQuery.getBeginPlanExecuteDate())
                                && !ObjectUtils.isEmpty(careTaskQuery.getEndPlanExecuteDate()),
                        CareTask::getPlanExecuteDate, careTaskQuery.getBeginPlanExecuteDate(),
                        careTaskQuery.getEndPlanExecuteDate())
                .orderByDesc(CareTask::getPlanExecuteDate);
        IPage<CareTask> careTaskPage = careTaskMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的CareTask转成CareTaskVO
        List<CareTaskVO> careTaskVOList = careTaskPage.getRecords().stream()
                .map(careTask -> {
                    CareTaskVO vo = new CareTaskVO();
                    BeanUtils.copyProperties(careTask, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、执行护理员姓名
        fillNames(careTaskVOList);

        // 4.返回CareTaskVO类型的分页
        IPage<CareTaskVO> voPage = new Page<>(careTaskPage.getCurrent(), careTaskPage.getSize(), careTaskPage.getTotal());
        voPage.setRecords(careTaskVOList);
        return voPage;
    }

    /**
     * 查询任务详情，返回带老人姓名、执行护理员姓名的 CareTaskVO
     *
     * @param id 任务id
     * @return 带姓名的详情，查不到返回 null
     */
    @Override
    public CareTaskVO getDetail(Long id) {
        CareTask careTask = careTaskMapper.selectById(id);
        if (careTask == null) return null;
        CareTaskVO vo = new CareTaskVO();
        BeanUtils.copyProperties(careTask, vo);
        // 复用填姓名逻辑：把单条包成列表回填老人姓名、执行护理员姓名
        List<CareTaskVO> list = new ArrayList<>();
        list.add(vo);
        fillNames(list);
        return vo;
    }

    /**
     * 批量给CareTaskVO填充老人姓名、执行护理员姓名
     */
    private void fillNames(List<CareTaskVO> careTaskVOList) {
        if (careTaskVOList.isEmpty()) return;

        // 1.老人姓名：取当前页所有老人id，一次查出组装成 Map<Long, String>，再回填
        List<Long> elderIds = careTaskVOList.stream()
                .map(CareTaskVO::getElderId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!elderIds.isEmpty()) {
            Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                    .collect(Collectors.toMap(Elder::getId, Elder::getRealName));
            careTaskVOList.forEach(vo -> vo.setElderName(elderNameMap.get(vo.getElderId())));
        }

        // 2.执行护理员姓名：取当前页所有执行人id，一次查出组装成 Map<Long, String>，再回填
        List<Long> userIds = careTaskVOList.stream()
                .map(CareTaskVO::getUserId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!userIds.isEmpty()) {
            Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getRealName));
            careTaskVOList.forEach(vo -> vo.setUserName(userNameMap.get(vo.getUserId())));
        }
    }

    /**
     * 完成任务：状态置为已完成(1)，记录实际完成时间，并写入执行结果、打卡照片、备注、执行人
     * 用 updateById 只更新非空字段，不触碰其他列
     *
     * @param id       任务id
     * @param careTask 执行信息（执行结果、照片、备注、执行人）
     * @return 处理结果
     */
    @Override
    public Result complete(Long id, CareTask careTask) {
        CareTask update = new CareTask();
        update.setId(id);
        update.setStatus(1);                    //已完成为1
        update.setActualExecuteTime(new Date());//实际完成时间取当前时间
        update.setExecuteResult(careTask.getExecuteResult());
        update.setExecuteImg(careTask.getExecuteImg());
        update.setRemark(careTask.getRemark());
        update.setUserId(careTask.getUserId());
        careTaskMapper.updateById(update);
        return Result.ok("已完成打卡");
    }

    /**
     * 跳过/取消任务：状态置为已跳过(2)
     *
     * @param id 任务id
     * @return 处理结果
     */
    @Override
    public Result skip(Long id) {
        CareTask update = new CareTask();
        update.setId(id);
        update.setStatus(2); //已跳过为2
        careTaskMapper.updateById(update);
        return Result.ok("已跳过该任务");
    }

    /**
     * 按护理计划一次性生成整个计划周期内的所有任务：
     * 遍历 [startDate, endDate] 的每一天 × 每个护理项目，按执行周期+执行日判定该天是不是执行日：
     * - 每天(cycle=0)：每天都执行
     * - 每周(cycle=1)：该天星期几(周一=1~周日=7) 等于 项目的执行日才执行
     * - 每月(cycle=2)：该天几号(1~31) 等于 项目的执行日才执行
     * 是执行日则组装一条 待执行(0) 的任务（项目名冗余固化、执行人取计划的护理人员、
     * 计划执行时间取项目配置、记录来源护理项目id），全部组装完后批量插入
     */
    @Override
    public void generateTasksForPlan(CarePlan plan, List<CarePlanItem> items) {
        // 一次性生成要求计划有明确的开始/结束日期，缺失则跳过（存量数据兜底，日志提示）
        if (plan.getStartDate() == null || plan.getEndDate() == null) {
            log.warn("护理计划[id={}]缺少开始/结束日期，跳过任务生成", plan.getId());
            return;
        }

        // 1.一次查出涉及的所有护理项目id对应的项目名，组装成 Map 供写任务时取冗余名称
        Map<Long, String> careItemNameMap = items.stream()
                .map(CarePlanItem::getCareItemId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> {
                            CareItem careItem = careItemMapper.selectById(id);
                            return careItem == null ? null : careItem.getName();
                        }));

        // 2.遍历计划周期的每一天 × 每个项目，按执行日规则判定该天是不是执行日，是则组装任务
        LocalDate start = toLocalDate(plan.getStartDate());
        LocalDate end = toLocalDate(plan.getEndDate());
        List<CareTask> tasks = new ArrayList<>();
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            // 当天零点，用于和任务表的 plan_execute_date 这种 java.util.Date 比较
            Date dayDate = Date.from(day.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            for (CarePlanItem item : items) {
                if (!isTaskDay(day, item)) continue; //该天不是该项目的执行日，跳过

                CareTask task = new CareTask();
                task.setElderId(plan.getElderId());                       //老人
                task.setCarePlanId(plan.getId());                         //来源护理计划
                task.setCarePlanItemId(item.getId());                     //来源护理项目（可追溯任务来源）
                task.setCareItemId(item.getCareItemId());                 //护理项目
                task.setCareItemName(careItemNameMap.get(item.getCareItemId())); //项目名冗余，防止改名历史变动
                task.setUserId(plan.getUserId());                         //指定执行护理员=计划的护理人员
                task.setPlanExecuteDate(dayDate);                         //计划执行日期=循环到的当天
                task.setPlanExecuteTime(item.getExecuteTime());           //计划执行时间=项目配置的时间
                task.setStatus(0);                                        //待执行
                tasks.add(task);
            }
        }

        // 3.批量插入整个计划周期的任务
        saveBatch(tasks);
    }

    /**
     * 重新生成某计划的任务（方案B）：
     * 1.物理删除该计划下所有 待执行(0) 的任务，已完成/已跳过(1/2)的历史打卡记录保留不动
     * 2.查计划 + 项目列表，按新配置重新生成整个计划周期的任务
     * 创建计划、编辑计划/项目保存后都会走到这里，保证任务与计划配置一致
     */
    @Override
    public void regenerateTasksForPlan(Long planId) {
        // 1.删除该计划下所有待执行任务
        careTaskMapper.delete(new LambdaQueryWrapper<CareTask>()
                .eq(CareTask::getCarePlanId, planId)
                .eq(CareTask::getStatus, 0));

        // 2.查计划与项目列表，重新生成
        CarePlan plan = carePlanMapper.selectById(planId);
        if (plan == null) return;
        List<CarePlanItem> items = carePlanItemMapper.selectList(
                new LambdaQueryWrapper<CarePlanItem>().eq(CarePlanItem::getCarePlanId, planId));
        if (items.isEmpty()) return;
        generateTasksForPlan(plan, items);
    }

    /**
     * java.util.Date 转 LocalDate（用于遍历计划周期）
     */
    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 按执行周期+执行日判定"今天"是不是该护理项目的执行日
     *
     * @param today 今天
     * @param item  护理项目（含执行周期、执行日）
     * @return 今天是执行日返回 true，否则 false
     */
    private boolean isTaskDay(LocalDate today, CarePlanItem item) {
        int cycle = item.getExecuteCycle() == null ? 0 : item.getExecuteCycle(); //执行周期缺省按"每天"处理
        if (cycle == 0) return true; //每天
        if (item.getExecuteDay() == null) return false;
        if (cycle == 1) { //每周：今天星期几(周一=1~周日=7) 等于 执行日
            return today.getDayOfWeek().getValue() == item.getExecuteDay();
        }
        //每月：今天几号 等于 执行日
        return today.getDayOfMonth() == item.getExecuteDay();
    }
}