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
import cn.tinsur.elder.mapper.FloorMapper;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.query.FloorQuery;
import cn.tinsur.elder.pojo.vo.FloorVO;
import cn.tinsur.elder.service.IFloorService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 楼层表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class FloorServiceImpl extends ServiceImpl<FloorMapper, Floor> implements IFloorService {
    @Autowired
    private FloorMapper floorMapper;
    @Autowired
    private BuildingMapper buildingMapper;

    @Override
    public IPage<FloorVO> list(FloorQuery floorQuery) {
        IPage<Floor> page = new Page<>(floorQuery.getPage(), floorQuery.getLimit());
        LambdaQueryWrapper<Floor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!ObjectUtils.isEmpty(floorQuery.getBuildingId()), Floor::getBuildingId, floorQuery.getBuildingId())
                .between(!ObjectUtils.isEmpty(floorQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(floorQuery.getEndCreateTime()),
                        Floor::getCreateTime, floorQuery.getBeginCreateTime(),
                        floorQuery.getEndCreateTime())
                .orderByAsc(Floor::getBuildingId)
                .orderByAsc(Floor::getFloorNo);
        IPage<Floor> floorPage = floorMapper.selectPage(page, lambdaQueryWrapper);

        //把分页结果转成VO并批量补充楼栋名称
        IPage<FloorVO> voPage = new Page<>(floorPage.getCurrent(), floorPage.getSize(), floorPage.getTotal());
        List<FloorVO> voList = floorPage.getRecords().stream().map(floor -> {
            FloorVO floorVO = new FloorVO();
            BeanUtils.copyProperties(floor, floorVO);
            return floorVO;
        }).toList();
        fillBuildingNames(voList);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<Floor> listAll() {
        return floorMapper.selectList(new LambdaQueryWrapper<Floor>()
                .orderByAsc(Floor::getBuildingId)
                .orderByAsc(Floor::getFloorNo));
    }

    /**
     * 批量补充VO里的所属楼栋名称
     */
    private void fillBuildingNames(List<FloorVO> voList) {
        if (ObjectUtils.isEmpty(voList)) {
            return;
        }
        List<Long> buildingIds = voList.stream().map(FloorVO::getBuildingId).distinct().toList();
        Map<Long, Building> buildingMap = buildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(Building::getId, Function.identity()));
        voList.forEach(vo -> {
            Building building = buildingMap.get(vo.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        });
    }
}