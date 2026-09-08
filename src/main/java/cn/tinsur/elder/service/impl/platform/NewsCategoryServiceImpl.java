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
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
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

import cn.tinsur.elder.mapper.NewsCategoryMapper;
import cn.tinsur.elder.mapper.NewsMapper;
import cn.tinsur.elder.pojo.entity.News;
import cn.tinsur.elder.pojo.entity.NewsCategory;
import cn.tinsur.elder.pojo.query.NewsCategoryQuery;
import cn.tinsur.elder.service.INewsCategoryService;
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
 * 资讯分类表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@Service
public class NewsCategoryServiceImpl extends ServiceImpl<NewsCategoryMapper, NewsCategory> implements INewsCategoryService {
    @Autowired
    private NewsCategoryMapper newsCategoryMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public IPage<NewsCategory> list(NewsCategoryQuery newsCategoryQuery) {
        IPage<NewsCategory> page = new Page<>(newsCategoryQuery.getPage(), newsCategoryQuery.getLimit());
        LambdaQueryWrapper<NewsCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(newsCategoryQuery.getName()), NewsCategory::getName, newsCategoryQuery.getName())
                .eq(!ObjectUtils.isEmpty(newsCategoryQuery.getStatus()), NewsCategory::getStatus, newsCategoryQuery.getStatus())
                .between(!ObjectUtils.isEmpty(newsCategoryQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(newsCategoryQuery.getEndCreateTime()),
                        NewsCategory::getCreateTime, newsCategoryQuery.getBeginCreateTime(),
                        newsCategoryQuery.getEndCreateTime())
                .orderByAsc(NewsCategory::getSort);
        return newsCategoryMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<NewsCategory> listAll() {
        return newsCategoryMapper.selectList(new LambdaQueryWrapper<NewsCategory>()
                .eq(NewsCategory::getStatus, 1)
                .orderByAsc(NewsCategory::getSort));
    }

    @Override
    public Long countNews(List<Long> ids) {
        return newsMapper.selectCount(new LambdaQueryWrapper<News>()
                .in(News::getCategoryId, ids));
    }
}