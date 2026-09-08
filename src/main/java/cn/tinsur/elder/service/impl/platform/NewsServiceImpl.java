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
import cn.tinsur.elder.pojo.query.NewsQuery;
import cn.tinsur.elder.pojo.vo.NewsVO;
import cn.tinsur.elder.service.INewsService;
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
 * 资讯表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {
    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private NewsCategoryMapper newsCategoryMapper;

    /**
     * 获取资讯列表（分页），返回 NewsVO，并给每个 VO 填充资讯分类名称 categoryName
     * @param newsQuery
     * @return
     */
    @Override
    public IPage<NewsVO> list(NewsQuery newsQuery) {
        // 1.先查资讯分页
        IPage<News> page = new Page<>(newsQuery.getPage(), newsQuery.getLimit());
        LambdaQueryWrapper<News> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(newsQuery.getTitle()), News::getTitle, newsQuery.getTitle())
                .eq(!ObjectUtils.isEmpty(newsQuery.getCategoryId()), News::getCategoryId, newsQuery.getCategoryId())
                .eq(!ObjectUtils.isEmpty(newsQuery.getStatus()), News::getStatus, newsQuery.getStatus())
                .between(!ObjectUtils.isEmpty(newsQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(newsQuery.getEndCreateTime()),
                        News::getCreateTime, newsQuery.getBeginCreateTime(),
                        newsQuery.getEndCreateTime())
                .orderByDesc(News::getCreateTime);
        IPage<News> newsPage = newsMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的News转成NewsVO
        List<NewsVO> newsVOList = newsPage.getRecords().stream()
                .map(news -> {
                    NewsVO vo = new NewsVO();
                    BeanUtils.copyProperties(news, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上资讯分类名称
        fillCategoryName(newsVOList);

        // 4.返回NewsVO类型的分页
        IPage<NewsVO> voPage = new Page<>(newsPage.getCurrent(), newsPage.getSize(), newsPage.getTotal());
        voPage.setRecords(newsVOList);
        return voPage;
    }

    /**
     * 批量给NewsVO填充资讯分类名称
     */
    private void fillCategoryName(List<NewsVO> newsVOList) {
        if (newsVOList.isEmpty()) return;

        // 1.取当前页所有分类id去重，一次查出组装成 Map<Long, String>，再回填
        List<Long> categoryIds = newsVOList.stream()
                .map(NewsVO::getCategoryId)
                .distinct()
                .toList();
        Map<Long, String> categoryNameMap = newsCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(NewsCategory::getId, NewsCategory::getName));

        // 2.回填：每条资讯的分类名称
        newsVOList.forEach(vo -> vo.setCategoryName(categoryNameMap.get(vo.getCategoryId())));
    }
}