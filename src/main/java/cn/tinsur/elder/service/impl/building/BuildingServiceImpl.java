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
package cn.tinsur.elder.service.impl.building;

import cn.tinsur.elder.mapper.BuildingMapper;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.query.BuildingQuery;
import cn.tinsur.elder.service.IBuildingService;
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
 * 楼栋表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements IBuildingService {
    @Autowired
    private BuildingMapper buildingMapper;

    @Override
    public IPage<Building> list(BuildingQuery buildingQuery) {
        IPage<Building> page = new Page<>(buildingQuery.getPage(), buildingQuery.getLimit());
        LambdaQueryWrapper<Building> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(buildingQuery.getName()), Building::getName, buildingQuery.getName())
                .between(!ObjectUtils.isEmpty(buildingQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(buildingQuery.getEndCreateTime()),
                        Building::getCreateTime, buildingQuery.getBeginCreateTime(),
                        buildingQuery.getEndCreateTime())
                .orderByAsc(Building::getSort);
        return buildingMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<Building> listAll() {
        return buildingMapper.selectList(new LambdaQueryWrapper<Building>()
                .orderByAsc(Building::getSort));
    }
}