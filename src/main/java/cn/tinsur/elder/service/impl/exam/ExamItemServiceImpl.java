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

import cn.tinsur.elder.mapper.ExamItemMapper;
import cn.tinsur.elder.mapper.ExamPackageItemMapper;
import cn.tinsur.elder.pojo.entity.ExamItem;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.query.ExamItemQuery;
import cn.tinsur.elder.service.IExamItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 * 体检项目表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@Service
public class ExamItemServiceImpl extends ServiceImpl<ExamItemMapper, ExamItem> implements IExamItemService {
    @Autowired
    private ExamItemMapper examItemMapper;

    @Autowired
    private ExamPackageItemMapper examPackageItemMapper;

    @Override
    public IPage<ExamItem> list(ExamItemQuery examItemQuery) {
        IPage<ExamItem> page = new Page<>(examItemQuery.getPage(), examItemQuery.getLimit());
        LambdaQueryWrapper<ExamItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(examItemQuery.getName()), ExamItem::getName, examItemQuery.getName())
                .eq(!ObjectUtils.isEmpty(examItemQuery.getStatus()), ExamItem::getStatus, examItemQuery.getStatus())
                .between(!ObjectUtils.isEmpty(examItemQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(examItemQuery.getEndCreateTime()),
                        ExamItem::getCreateTime, examItemQuery.getBeginCreateTime(),
                        examItemQuery.getEndCreateTime())
                .orderByAsc(ExamItem::getSort);
        return examItemMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<ExamItem> listAll() {
        return examItemMapper.selectList(new LambdaQueryWrapper<ExamItem>()
                .eq(ExamItem::getStatus, 1)
                .orderByAsc(ExamItem::getSort));
    }

    @Override
    public Long countInPackage(List<Long> ids) {
        return examPackageItemMapper.selectCount(new LambdaQueryWrapper<ExamPackageItem>()
                .in(ExamPackageItem::getExamItemId, ids));
    }
}