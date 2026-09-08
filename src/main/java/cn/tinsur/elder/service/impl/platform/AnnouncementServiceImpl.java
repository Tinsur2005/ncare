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
package cn.tinsur.elder.service.impl.platform;

import cn.tinsur.elder.mapper.AnnouncementMapper;
import cn.tinsur.elder.pojo.entity.Announcement;
import cn.tinsur.elder.pojo.query.AnnouncementQuery;
import cn.tinsur.elder.service.IAnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> list(AnnouncementQuery announcementQuery) {
        IPage<Announcement> page = new Page<>(announcementQuery.getPage(), announcementQuery.getLimit());
        LambdaQueryWrapper<Announcement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(announcementQuery.getTitle()), Announcement::getTitle, announcementQuery.getTitle())
                .eq(!ObjectUtils.isEmpty(announcementQuery.getStatus()), Announcement::getStatus, announcementQuery.getStatus())
                .between(!ObjectUtils.isEmpty(announcementQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(announcementQuery.getEndCreateTime()),
                        Announcement::getCreateTime, announcementQuery.getBeginCreateTime(),
                        announcementQuery.getEndCreateTime())
                .orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(page, lambdaQueryWrapper);
    }
}