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

import cn.tinsur.elder.pojo.entity.Bed;
import cn.tinsur.elder.pojo.query.BedQuery;
import cn.tinsur.elder.pojo.vo.BedVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 床位表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IBedService extends IService<Bed> {

    /**
     * 分页查询床位列表（补充所属楼栋名称、楼层号和房间号）
     * @param bedQuery 查询条件（楼栋、楼层、房间、床位状态、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<BedVO> list(BedQuery bedQuery);

    /**
     * 根据ID查询床位（补充楼栋ID、楼层ID、楼栋名称、楼层号和房间号，供编辑抽屉级联回显）
     * @param id 床位ID
     * @return 床位视图对象
     */
    BedVO getVOById(Long id);

    /**
     * 获取全部床位列表（供入住办理等页面的床位下拉框使用）
     * @return 床位的List列表
     */
    List<Bed> listAll();

    /**
     * 获取全部空闲床位列表（供入住办理选床使用）
     * @return 空闲床位的List列表
     */
    List<Bed> listFree();

    /**
     * 根据老人ID查询其占用的床位（补充楼栋名称、楼层号和房间号），未入住时返回null
     * @param elderId 老人ID
     * @return 床位视图对象
     */
    BedVO getOccupiedByElderId(Long elderId);
}