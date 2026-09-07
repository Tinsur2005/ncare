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
package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.BedMapper;
import cn.tinsur.elder.mapper.BuildingMapper;
import cn.tinsur.elder.mapper.CareTaskMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.ExamAppointmentMapper;
import cn.tinsur.elder.mapper.FloorMapper;
import cn.tinsur.elder.mapper.HelpRequestMapper;
import cn.tinsur.elder.mapper.RoomMapper;
import cn.tinsur.elder.pojo.entity.Bed;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.entity.HelpRequest;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.vo.DashboardVO;
import cn.tinsur.elder.pojo.vo.NameValueVO;
import cn.tinsur.elder.pojo.vo.WeekTaskVO;
import cn.tinsur.elder.service.IDashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 首页看板 服务实现类
 * 全部复用各业务模块已有的Mapper做统计，不额外建表、不写新SQL
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private ExamAppointmentMapper examAppointmentMapper;

    @Autowired
    private CareTaskMapper careTaskMapper;

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private FloorMapper floorMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private BedMapper bedMapper;

    /**
     * 获取首页看板数据（统计卡片数字 + 各图表数据）
     *
     * @return 看板数据
     */
    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // 1.统计卡片：在住老人（状态正常）、待处理求助、今日体检人次（逻辑删除的记录会自动被排除）
        vo.setCheckedInElderCount(elderMapper.selectCount(
                new LambdaQueryWrapper<Elder>().eq(Elder::getStatus, 1)));
        vo.setPendingHelpCount(helpRequestMapper.selectCount(
                new LambdaQueryWrapper<HelpRequest>().eq(HelpRequest::getStatus, 0)));
        vo.setTodayExamCount(examAppointmentMapper.selectCount(
                new LambdaQueryWrapper<ExamAppointment>()
                        .between(ExamAppointment::getAppointmentDate, todayBegin(), weekEnd())
                        .ne(ExamAppointment::getStatus, 3)));

        // 2.一次查出近7天（含今天）的护理任务，后面"近7天柱状图"和"今日状态饼图"都从这份列表里统计，避免重复查库
        List<CareTask> weekTasks = listWeekTasks();

        // 3.近7天护理任务完成情况（柱状图）
        vo.setWeekTaskList(buildWeekTaskList(weekTasks));

        // 4.今日护理任务状态分布（饼图），并顺便取出今日待执行数给统计卡片
        List<NameValueVO> todayTaskStatusList = buildTodayTaskStatusList(weekTasks);
        vo.setTodayTaskStatusList(todayTaskStatusList);
        vo.setTodayPendingTaskCount(todayTaskStatusList.stream()
                .filter(item -> "待执行".equals(item.getName()))
                .findFirst()
                .map(NameValueVO::getValue)
                .orElse(0L));

        // 5.近7天体检预约人次（折线图）
        vo.setWeekExamList(buildWeekExamList());

        // 6.各楼栋入住比例（饼图）
        vo.setBuildingOccupancyList(buildBuildingOccupancyList());

        return vo;
    }

    /**
     * 获取今天零点的时间
     *
     * @return 今天 00:00:00
     */
    private Date todayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取近7天（含今天）的终点：今天 23:59:59
     *
     * @return 今天 23:59:59
     */
    private Date weekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayBegin());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 查询近7天（含今天）的护理任务列表
     *
     * @return 近7天的护理任务
     */
    private List<CareTask> listWeekTasks() {
        // 今天零点往前推6天得到近7天的起点（计划执行日期 >= 起点），终点为今天 23:59:59
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayBegin());
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        Date beginDate = calendar.getTime();

        LambdaQueryWrapper<CareTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.between(CareTask::getPlanExecuteDate, beginDate, weekEnd());
        return careTaskMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 统计近7天每天的任务数量，按 待执行/已完成/已跳过 三类分别计数
     * 没有任务数据的日期也补0，保证柱状图x轴连续7天
     *
     * @param weekTasks 近7天的护理任务列表
     * @return 一天一条的统计数据（按时间从早到今天排序）
     */
    private List<WeekTaskVO> buildWeekTaskList(List<CareTask> weekTasks) {
        // 按"yyyy-MM-dd"分组，key是日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, List<CareTask>> dateGroupMap = weekTasks.stream()
                .filter(task -> task.getPlanExecuteDate() != null)
                .collect(Collectors.groupingBy(task -> dateFormat.format(task.getPlanExecuteDate())));

        // 从6天前开始逐天构建，保证x轴顺序是"最早 -> 今天"
        SimpleDateFormat labelFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        List<WeekTaskVO> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            WeekTaskVO weekTaskVO = new WeekTaskVO();
            weekTaskVO.setDate(labelFormat.format(calendar.getTime()));
            // 取出这一天的任务列表，按状态分别计数（没有数据就是0）
            List<CareTask> dayTasks = dateGroupMap.getOrDefault(dateFormat.format(calendar.getTime()), Collections.emptyList());
            weekTaskVO.setPendingCount(countByStatus(dayTasks, 0));
            weekTaskVO.setCompletedCount(countByStatus(dayTasks, 1));
            weekTaskVO.setSkippedCount(countByStatus(dayTasks, 2));
            list.add(weekTaskVO);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * 统计今天的任务状态分布（饼图数据：待执行/已完成/已跳过）
     *
     * @param weekTasks 近7天的护理任务列表（从里面过滤出今天的）
     * @return 饼图数据
     */
    private List<NameValueVO> buildTodayTaskStatusList(List<CareTask> weekTasks) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayKey = dateFormat.format(new Date());
        // 从近7天列表里过滤出今天的任务
        List<CareTask> todayTasks = weekTasks.stream()
                .filter(task -> task.getPlanExecuteDate() != null
                        && todayKey.equals(dateFormat.format(task.getPlanExecuteDate())))
                .toList();
        // 按状态计数（状态：0待执行 1已完成 2已跳过）
        List<NameValueVO> list = new ArrayList<>();
        list.add(new NameValueVO("待执行", countByStatus(todayTasks, 0)));
        list.add(new NameValueVO("已完成", countByStatus(todayTasks, 1)));
        list.add(new NameValueVO("已跳过", countByStatus(todayTasks, 2)));
        return list;
    }

    /**
     * 统计近7天（含今天）每天的体检预约人次（折线图数据：体检日期在当天的预约记录数，不含已取消的预约）
     * 没有预约数据的日期也补0，保证折线图x轴连续7天
     *
     * @return 一天一条的统计数据（按时间从早到今天排序）
     */
    private List<NameValueVO> buildWeekExamList() {
        // 近7天（含今天）的体检预约，排除已取消的
        LambdaQueryWrapper<ExamAppointment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayBegin());
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        lambdaQueryWrapper.between(ExamAppointment::getAppointmentDate, calendar.getTime(), weekEnd())
                .ne(ExamAppointment::getStatus, 3);
        // 按"yyyy-MM-dd"分组计数，key是日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Long> dateCountMap = examAppointmentMapper.selectList(lambdaQueryWrapper).stream()
                .filter(appointment -> appointment.getAppointmentDate() != null)
                .collect(Collectors.groupingBy(appointment -> dateFormat.format(appointment.getAppointmentDate()), Collectors.counting()));

        // 从6天前开始逐天构建，保证x轴顺序是"最早 -> 今天"
        SimpleDateFormat labelFormat = new SimpleDateFormat("MM-dd");
        calendar.setTime(todayBegin());
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        List<NameValueVO> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(new NameValueVO(labelFormat.format(calendar.getTime()),
                    dateCountMap.getOrDefault(dateFormat.format(calendar.getTime()), 0L)));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * 统计各楼栋入住比例（饼图数据：每个楼栋占用床位的数量，即楼栋的在住人数）
     * 床位到楼栋的链路：床位的roomId -> 房间的floorId -> 楼层的buildingId
     *
     * @return 饼图数据（楼栋按sort排序）
     */
    private List<NameValueVO> buildBuildingOccupancyList() {
        // 1.楼栋、楼层、房间各查一次，在内存里建id到对象的映射
        Map<Long, Building> buildingMap = buildingMapper.selectList(null).stream()
                .collect(Collectors.toMap(Building::getId, building -> building));
        Map<Long, Floor> floorMap = floorMapper.selectList(null).stream()
                .collect(Collectors.toMap(Floor::getId, floor -> floor));
        Map<Long, Room> roomMap = roomMapper.selectList(null).stream()
                .collect(Collectors.toMap(Room::getId, room -> room));

        // 2.统计每个楼栋占用床位（状态为已占用）的数量
        Map<Long, Long> occupancyCountMap = bedMapper.selectList(null).stream()
                .filter(bed -> Integer.valueOf(1).equals(bed.getStatus()))
                .map(bed -> roomMap.get(bed.getRoomId()))
                .filter(Objects::nonNull)
                .map(room -> floorMap.get(room.getFloorId()))
                .filter(Objects::nonNull)
                .map(Floor::getBuildingId)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(buildingId -> buildingId, Collectors.counting()));

        // 3.按楼栋组装图表数据，没有在住老人的楼栋也补0
        return buildingMap.values().stream()
                .sorted(Comparator.comparing(Building::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(building -> new NameValueVO(building.getName(), occupancyCountMap.getOrDefault(building.getId(), 0L)))
                .toList();
    }

    /**
     * 统计任务列表里某个状态的数量
     *
     * @param tasks  任务列表
     * @param status 状态（0待执行 1已完成 2已跳过）
     * @return 该状态的数量
     */
    private Long countByStatus(List<CareTask> tasks, Integer status) {
        return tasks.stream().filter(task -> status.equals(task.getStatus())).count();
    }
}