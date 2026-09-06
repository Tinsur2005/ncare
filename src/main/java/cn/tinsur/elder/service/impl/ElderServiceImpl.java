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

import cn.tinsur.elder.exception.ServiceException;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.ElderTagMapper;
import cn.tinsur.elder.mapper.TagMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ElderTag;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.listener.ElderExcelListener;
import cn.tinsur.elder.pojo.vo.ElderExcelVO;
import cn.tinsur.elder.pojo.vo.ElderVO;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.ExcelUtil;
import cn.tinsur.elder.util.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService {
    @Autowired
    private ElderMapper elderMapper;
    @Autowired
    private ElderTagMapper elderTagMapper;
    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取老人列表（分页），返回 ElderVO，并在每个ElderVO中填充当前老人的标签列表tags
     * @param elderQuery
     * @return
     */
    @Override
    public IPage<ElderVO> list(ElderQuery elderQuery) {
        // 1.先查老人分页，这里使用的是先前的不带标签查询功能的代码
        IPage<Elder> page = new Page<>(elderQuery.getPage(), elderQuery.getLimit());
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(elderQuery.getName()),Elder::getName,elderQuery.getName())
                .between(!ObjectUtils.isEmpty(elderQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(elderQuery.getEndCreateTime()),
                        Elder::getCreateTime, elderQuery.getBeginCreateTime(),
                        elderQuery.getEndCreateTime())
                .orderByDesc(Elder::getCreateTime);
        IPage<Elder> elderPage = elderMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的Elder转成ElderVO
        List<ElderVO> elderVOList = elderPage.getRecords().stream()
                .map(elder -> {
                    ElderVO vo = new ElderVO();
                    BeanUtils.copyProperties(elder, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上标签，调用下面的fillTags方法
        fillTags(elderVOList);

        // 4.返回ElderVO类型的分页
        IPage<ElderVO> voPage = new Page<>(elderPage.getCurrent(), elderPage.getSize(), elderPage.getTotal());
        voPage.setRecords(elderVOList);
        return voPage;
    }

    /**
     * 根据id获取老人详情，ElderVO，带标签列表tags，供前台手机端个人信息页使用
     * @param id 老人id
     * @return
     */
    @Override
    public ElderVO getVOById(Long id) {
        Elder elder = elderMapper.selectById(id);
        if (ObjectUtils.isEmpty(elder)) {
            return null;
        }
        ElderVO vo = new ElderVO();
        BeanUtils.copyProperties(elder, vo);
        fillTags(List.of(vo));
        return vo;
    }

    /**
     * 批量给ElderVO填标签
     */
    private void fillTags(List<ElderVO> elderVOList) {
        if (elderVOList.isEmpty()) return;

        // 1.当前页所有老人id，使用stream流先把所有老人id取出来
        List<Long> elderIds = elderVOList.stream().map(ElderVO::getId).toList();

        // 2.一次查出这些老人的所有关联记录
        //      .in实现只查想查的elder_id所对应的标签tag
        List<ElderTag> elderTags = elderTagMapper.selectList(
                new LambdaQueryWrapper<ElderTag>().in(ElderTag::getElderId, elderIds));
        if (elderTags.isEmpty()) return;

        // 3.一次查出用到的所有标签，把我们得到的标签id转换成一个个的标签Tag对象
        //      可能有多个重复的标签id，只需要一个id获取一次就可以了，使用.distinct去重
        List<Long> tagIds = elderTags.stream().map(ElderTag::getTagId).distinct().toList();
        //      把查询结果List<Tag>组装成一个Map<Long, Tag>，key是标签id，value是标签对象本身。
        Map<Long, Tag> tagMap = tagMapper.selectBatchIds(tagIds).stream()
                .collect(Collectors.toMap(tag -> tag.getId(), t -> t));

        // 4.按elderId分组
        Map<Long, List<ElderTag>> groupByElderTag = elderTags.stream()
                .collect(Collectors.groupingBy(ElderTag::getElderId));
        //      再把每个老人名下的 tag_id 逐条翻译成 Tag 对象，收成一个列表
        //      标签被逻辑删除后绑定关系还在，map里取不到会得到null，过滤掉避免前端遍历时崩溃
        Map<Long, List<Tag>> groupByElder = new HashMap<>();
        groupByElderTag.forEach((elderId, etList) -> {
            List<Tag> tags = etList.stream()
                    .map(et -> tagMap.get(et.getTagId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            groupByElder.put(elderId, tags);
        });

        // 5.回填：没标签的老人传入空列表，保证前端 row.tags 不为 null
        elderVOList.forEach(vo -> vo.setTags(
                groupByElder.getOrDefault(vo.getId(), Collections.emptyList())));
    }

    /**
     * 根据真实姓名（realName）模糊查询老人，供合同选老人等"远程搜索"下拉框使用
     * 可输入部分姓名，也可输入全部姓名，返回匹配到的老人列表（最多返回20条）
     * @param name 老人真实姓名的关键字（可空，为空则返回最近20个老人）
     * @return
     */
    @Override
    public List<Elder> searchByName(String name) {
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                // 按真实姓名模糊匹配，可输入部分或全部（如"张"）
                .like(!ObjectUtils.isEmpty(name), Elder::getRealName, name)
                .orderByDesc(Elder::getCreateTime)
                // 最多只返回20条，避免下拉框一次加载太多
                .last("LIMIT 20");
        return elderMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据id获取标签列表
     * @param id
     * @return
     */
    @Override
    public Result<List<Tag>> getTagsById(Long id) {
        LambdaQueryWrapper<ElderTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderTag :: getElderId, id);
        List<Long> list =
                elderTagMapper.selectList(lambdaQueryWrapper)
                        .stream()
                        .map(ElderTag :: getTagId)
                        .toList();
        return Result.ok(list);
    }

    /**
     * 根据老人id删除这个老人的所有标签
     * @param id
     */
    @Override
    public void deleteAllTagsById(Long id) {
        LambdaQueryWrapper<ElderTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderTag :: getElderId, id);
        elderTagMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据老人id添加标签，传入的第二个参数应该是标签ID组成的Long数组
     * @param id
     * @param tagId
     */
    @Override
    public void addTagById(Long id, Long[] tagId) {
        for (Long tag : tagId) {
            ElderTag elderTag = new ElderTag();
            elderTag.setElderId(id);
            elderTag.setTagId(tag);
            elderTagMapper.insert(elderTag);
        }
    }

    /**
     * 根据老人id更新标签，传入的第二个参数应该是标签ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除elder-tag中间表中有关这个老人的所有数据，再根据id和tagId数组插入新的数据
     * @param id
     * @param tags
     * @return
     */
    @Override
    public Result updateTags(Long id, Long[] tags) {
        deleteAllTagsById(id);
        addTagById(id, tags);
        return Result.ok("更新成功");
    }

    /**
     * 导出老人信息表
     * @param response
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        List<Elder> list = elderMapper.selectList(null); //写null则查出所有老人
        List<ElderExcelVO> elderExcelVOList = list.stream().map(elder -> {
            ElderExcelVO elderExcelVO = new ElderExcelVO();
            BeanUtils.copyProperties(elder, elderExcelVO);
            return elderExcelVO;
        }).toList();
        ExcelUtil.exportExcel(response, elderExcelVOList, ElderExcelVO.class, "老人信息表");
    }

    /**
     * 导入老人信息表
     * @param file
     */
    @Override
    public void importExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ElderExcelVO.class, new ElderExcelListener(elderMapper)).sheet().doRead();
        } catch (ExcelDataConvertException e) {
            //单元格类型解析失败，属于格式问题
            throw new ServiceException("导入失败：Excel格式有误，请使用导出的模板文件");
        } catch (DataIntegrityViolationException e) {
            //数据写入数据库时发生约束冲突，属于数据冲突
            throw new ServiceException("导入失败：数据冲突，存在重复或不符合字段要求的数据");
        } catch (Exception e) {
            throw new ServiceException("导入失败，请检查文件内容后重试");
        }

    }
}
