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
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.dto.CheckInAddDTO;
import cn.tinsur.elder.pojo.entity.CheckInRecord;
import cn.tinsur.elder.pojo.query.CheckInRecordQuery;
import cn.tinsur.elder.pojo.vo.CheckInElderVO;
import cn.tinsur.elder.pojo.vo.CheckInRecordVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 入住退住办理表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
public interface ICheckInRecordService extends IService<CheckInRecord> {

    /**
     * 分页查询办理单列表（VO里补充老人、客户、床位、办理人等展示字段）
     * @param checkInRecordQuery 查询条件
     * @return 办理单VO分页
     */
    IPage<CheckInRecordVO> list(CheckInRecordQuery checkInRecordQuery);

    /**
     * 根据ID查询办理单详情
     * @param id 办理单ID
     * @return 办理单VO
     */
    CheckInRecordVO getVOById(Long id);

    /**
     * 远程搜索在住老人（仅status=4入住中），供退住办理第一步选择老人使用
     * @param name 老人真实姓名关键字（可空）
     * @return 在住老人VO列表（附带床位、客户和入住信息）
     */
    List<CheckInElderVO> listInElders(String name);

    /**
     * 第一步完成时创建办理单（入住：登记客户和老人；退住：选择在住老人并把老人置为退住中）
     * @param checkInAddDTO 创建办理单请求对象
     * @return 创建结果，成功时data为新办理单ID
     */
    Result add(CheckInAddDTO checkInAddDTO);

    /**
     * 保存第n步的办理数据并把步骤+1
     * @param id 办理单ID
     * @param step 要保存的步骤号
     * @param checkInRecord 本步骤填写的字段
     * @return 保存结果
     */
    Result saveStep(Long id, Integer step, CheckInRecord checkInRecord);

    /**
     * 确认入住或确认退住，按办理类型走不同的事务
     * @param id 办理单ID
     * @param handlerId 确认办理人ID
     * @return 确认结果
     */
    Result confirm(Long id, Long handlerId);

    /**
     * 取消办理（入住单直接置为已取消，退住单需同时把老人从退住中恢复为入住中）
     * @param id 办理单ID
     * @return 取消结果
     */
    Result cancel(Long id);
}