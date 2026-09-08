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
import cn.tinsur.elder.pojo.entity.CareItem;
import cn.tinsur.elder.pojo.query.CareItemQuery;
import cn.tinsur.elder.service.ICareItemService;
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
 * 护理项目表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class CareItemServiceImpl extends ServiceImpl<CareItemMapper, CareItem> implements ICareItemService {
    @Autowired
    private CareItemMapper careItemMapper;

    @Override
    public IPage<CareItem> list(CareItemQuery careItemQuery) {
        IPage<CareItem> page = new Page<>(careItemQuery.getPage(), careItemQuery.getLimit());
        LambdaQueryWrapper<CareItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(careItemQuery.getName()), CareItem::getName, careItemQuery.getName())
                .eq(!ObjectUtils.isEmpty(careItemQuery.getStatus()), CareItem::getStatus, careItemQuery.getStatus())
                .between(!ObjectUtils.isEmpty(careItemQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(careItemQuery.getEndCreateTime()),
                        CareItem::getCreateTime, careItemQuery.getBeginCreateTime(),
                        careItemQuery.getEndCreateTime())
                .orderByAsc(CareItem::getSort);
        return careItemMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<CareItem> listAll() {
        return careItemMapper.selectList(new LambdaQueryWrapper<CareItem>()
                .eq(CareItem::getStatus, 1)
                .orderByAsc(CareItem::getSort));
    }
}