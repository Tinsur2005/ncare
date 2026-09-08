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
package cn.tinsur.elder.service.impl.customer;

import cn.tinsur.elder.mapper.ElderFamilyMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.FamilyMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ElderFamily;
import cn.tinsur.elder.pojo.entity.Family;
import cn.tinsur.elder.pojo.query.FamilyQuery;
import cn.tinsur.elder.pojo.vo.FamilyVO;
import cn.tinsur.elder.service.IFamilyService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 家属表 服务实现类
 * @author Tinsur
 * @since 2026-09-01
 */
@Service
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, Family> implements IFamilyService {
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private ElderFamilyMapper elderFamilyMapper;
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 获取家属列表（分页），返回 FamilyVO，并在每个FamilyVO中填充该家属关联的老人列表elders
     * @param familyQuery
     * @return
     */
    @Override
    public IPage<FamilyVO> list(FamilyQuery familyQuery) {
        // 1.先查家属分页
        IPage<Family> page = new Page<>(familyQuery.getPage(), familyQuery.getLimit());
        LambdaQueryWrapper<Family> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(familyQuery.getName()), Family::getName, familyQuery.getName())
                .like(!ObjectUtils.isEmpty(familyQuery.getRealName()), Family::getRealName, familyQuery.getRealName())
                .like(!ObjectUtils.isEmpty(familyQuery.getPhone()), Family::getPhone, familyQuery.getPhone())
                .eq(!ObjectUtils.isEmpty(familyQuery.getStatus()), Family::getStatus, familyQuery.getStatus())
                .orderByDesc(Family::getCreateTime);
        IPage<Family> familyPage = familyMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的Family转成FamilyVO
        List<FamilyVO> familyVOList = familyPage.getRecords().stream()
                .map(family -> {
                    FamilyVO vo = new FamilyVO();
                    BeanUtils.copyProperties(family, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上关联的老人，调用下面的fillElders方法
        fillElders(familyVOList);

        // 4.返回FamilyVO类型的分页
        IPage<FamilyVO> voPage = new Page<>(familyPage.getCurrent(), familyPage.getSize(), familyPage.getTotal());
        voPage.setRecords(familyVOList);
        return voPage;
    }

    /**
     * 批量给FamilyVO填关联的老人
     */
    private void fillElders(List<FamilyVO> familyVOList) {
        if (familyVOList.isEmpty()) return;

        // 1.当前页所有家属id，使用stream流先把所有家属id取出来
        List<Long> familyIds = familyVOList.stream().map(FamilyVO::getId).toList();

        // 2.一次查出这些家属的所有关联记录
        //      .in实现只查想查的family_id所对应的关联
        List<ElderFamily> elderFamilies = elderFamilyMapper.selectList(
                new LambdaQueryWrapper<ElderFamily>().in(ElderFamily::getFamilyId, familyIds));
        if (elderFamilies.isEmpty()) {
            // 没有家属关联老人，全部传空列表，保证前端 row.elders 不为 null
            familyVOList.forEach(vo -> vo.setElders(Collections.emptyList()));
            return;
        }

        // 3.一次查出用到的所有老人，把我们得到的老人id转换成一个个的老人Elder对象
        //      可能有多个重复的老人id，只需要一个id获取一次就可以了，使用.distinct去重
        List<Long> elderIds = elderFamilies.stream().map(ElderFamily::getElderId).distinct().toList();
        //      把查询结果List<Elder>组装成一个Map<Long, Elder>，key是老人id，value是老人对象本身。
        Map<Long, Elder> elderMap = elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(elder -> elder.getId(), e -> e));

        // 4.按familyId分组
        Map<Long, List<ElderFamily>> groupByFamilyElder = elderFamilies.stream()
                .collect(Collectors.groupingBy(ElderFamily::getFamilyId));
        //      再把每个家属名下的 elder_id 逐条翻译成 Elder 对象，收成一个列表
        //      老人被逻辑删除后绑定关系还在，map里取不到会得到null，过滤掉避免前端遍历时崩溃
        Map<Long, List<Elder>> groupByFamily = new HashMap<>();
        groupByFamilyElder.forEach((familyId, efList) -> {
            List<Elder> elders = efList.stream()
                    .map(ef -> elderMap.get(ef.getElderId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            groupByFamily.put(familyId, elders);
        });

        // 5.回填：没关联老人的家属传入空列表，保证前端 row.elders 不为 null
        familyVOList.forEach(vo -> vo.setElders(
                groupByFamily.getOrDefault(vo.getId(), Collections.emptyList())));
    }

    /**
     * 根据家属id获取该家属关联的所有老人，返回由Elder对象组成的List列表
     * @param id
     * @return
     */
    @Override
    public Result<List<Elder>> getEldersById(Long id) {
        LambdaQueryWrapper<ElderFamily> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderFamily :: getFamilyId, id);
        List<Long> elderIds =
                elderFamilyMapper.selectList(lambdaQueryWrapper)
                        .stream()
                        .map(ElderFamily :: getElderId)
                        .toList();
        if (elderIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Elder> elders = elderMapper.selectBatchIds(elderIds);
        return Result.ok(elders);
    }

    /**
     * 根据家属id删除这个家属的所有老人关联
     * @param id
     */
    @Override
    public void deleteAllEldersById(Long id) {
        LambdaQueryWrapper<ElderFamily> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ElderFamily :: getFamilyId, id);
        elderFamilyMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据家属id添加老人关联，传入的第二个参数应该是老人ID组成的Long数组
     * @param id
     * @param elderId
     */
    @Override
    public void addElderById(Long id, Long[] elderId) {
        for (Long elder : elderId) {
            ElderFamily elderFamily = new ElderFamily();
            elderFamily.setFamilyId(id);
            elderFamily.setElderId(elder);
            elderFamilyMapper.insert(elderFamily);
        }
    }

    /**
     * 根据家属id更新老人关联，传入的第二个参数应该是老人ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除elder-family中间表中有关这个家属的所有数据，再根据id和elderId数组插入新的数据
     * @param id 家属id
     * @param elderIds 老人ID组成的Long数组
     * @return
     */
    @Override
    public Result updateElders(Long id, Long[] elderIds) {
        deleteAllEldersById(id);
        addElderById(id, elderIds);
        return Result.ok("更新成功");
    }

    /**
     * 删除家属（逻辑删除），并同步删除elder-family中间表中该家属的所有关联数据
     * @param id 家属id
     */
    @Override
    public void deleteFamilyById(Long id) {
        removeById(id);
        deleteAllEldersById(id);
    }

    /**
     * 批量删除家属（逻辑删除），并同步删除elder-family中间表中这些家属的所有关联数据
     * @param ids 家属id数组
     */
    @Override
    public void deleteFamilyBatch(Long[] ids) {
        removeByIds(Arrays.asList(ids));
        for (Long id : ids) {
            deleteAllEldersById(id);
        }
    }

    /**
     * 根据真实姓名（realName）模糊查询家属，供远程搜索下拉框使用
     * 可输入部分姓名，也可输入全部姓名，返回匹配到的家属列表（最多返回20条）
     * @param name 家属真实姓名的关键字（可空，为空则返回最近20个家属）
     * @return
     */
    @Override
    public List<Family> searchByName(String name) {
        LambdaQueryWrapper<Family> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                // 按真实姓名模糊匹配，可输入部分或全部（如"张"）
                .like(!ObjectUtils.isEmpty(name), Family::getRealName, name)
                .orderByDesc(Family::getCreateTime)
                // 最多只返回20条，避免下拉框一次加载太多
                .last("LIMIT 20");
        return familyMapper.selectList(lambdaQueryWrapper);
    }
}