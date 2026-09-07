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
package cn.tinsur.elder.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 首页看板数据VO：统计卡片数字 + 各图表数据，一次请求全部带回
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Data
public class DashboardVO {

    /**
     * 在住老人数量（状态为正常的老人，即已完成入住、占用床位的老人）
     */
    private Long checkedInElderCount;

    /**
     * 待处理求助数量（状态为未处理的求助记录数）
     */
    private Long pendingHelpCount;

    /**
     * 今日体检人次（体检日期为今天的预约记录数，不含已取消的预约）
     */
    private Long todayExamCount;

    /**
     * 今日待执行任务数
     */
    private Long todayPendingTaskCount;

    /**
     * 今日护理任务状态分布（饼图：待执行/已完成/已跳过）
     */
    private List<NameValueVO> todayTaskStatusList;

    /**
     * 近7天护理任务完成情况（柱状图：每天的待执行/已完成/已跳过数量）
     */
    private List<WeekTaskVO> weekTaskList;

    /**
     * 近7天体检预约人次（折线图：体检日期在当天的预约记录数）
     */
    private List<NameValueVO> weekExamList;

    /**
     * 各楼栋入住比例（饼图：每个楼栋占用床位的数量）
     */
    private List<NameValueVO> buildingOccupancyList;
}