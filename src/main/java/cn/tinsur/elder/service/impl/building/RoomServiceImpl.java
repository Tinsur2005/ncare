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
import cn.tinsur.elder.mapper.RoomMapper;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.query.RoomQuery;
import cn.tinsur.elder.pojo.vo.RoomVO;
import cn.tinsur.elder.service.IRoomService;
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
 * 房间表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private FloorMapper floorMapper;
    @Autowired
    private BuildingMapper buildingMapper;

    @Override
    public IPage<RoomVO> list(RoomQuery roomQuery) {
        IPage<Room> page = new Page<>(roomQuery.getPage(), roomQuery.getLimit());
        LambdaQueryWrapper<Room> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                //楼层ID直接等值查询
                .eq(!ObjectUtils.isEmpty(roomQuery.getFloorId()), Room::getFloorId, roomQuery.getFloorId())
                .between(!ObjectUtils.isEmpty(roomQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(roomQuery.getEndCreateTime()),
                        Room::getCreateTime, roomQuery.getBeginCreateTime(),
                        roomQuery.getEndCreateTime())
                .orderByAsc(Room::getFloorId)
                .orderByAsc(Room::getRoomNo);
        //楼栋ID需要先解析出该楼栋下的楼层再匹配，楼栋下没有楼层时直接返回空分页避免in空集合报错
        if (!ObjectUtils.isEmpty(roomQuery.getBuildingId())) {
            List<Long> floorIds = getFloorIdsByBuildingId(roomQuery.getBuildingId());
            if (ObjectUtils.isEmpty(floorIds)) {
                return new Page<>(roomQuery.getPage(), roomQuery.getLimit());
            }
            lambdaQueryWrapper.in(Room::getFloorId, floorIds);
        }
        IPage<Room> roomPage = roomMapper.selectPage(page, lambdaQueryWrapper);

        //把分页结果转成VO并批量补充楼栋名称和楼层号
        IPage<RoomVO> voPage = new Page<>(roomPage.getCurrent(), roomPage.getSize(), roomPage.getTotal());
        List<RoomVO> voList = roomPage.getRecords().stream().map(room -> {
            RoomVO roomVO = new RoomVO();
            BeanUtils.copyProperties(room, roomVO);
            return roomVO;
        }).toList();
        fillBuildingAndFloorInfo(voList);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public RoomVO getVOById(Long id) {
        Room room = roomMapper.selectById(id);
        if (ObjectUtils.isEmpty(room)) {
            return null;
        }
        RoomVO roomVO = new RoomVO();
        BeanUtils.copyProperties(room, roomVO);
        fillBuildingAndFloorInfo(List.of(roomVO));
        return roomVO;
    }

    @Override
    public List<Room> listAll() {
        return roomMapper.selectList(new LambdaQueryWrapper<Room>()
                .orderByAsc(Room::getFloorId)
                .orderByAsc(Room::getRoomNo));
    }

    /**
     * 查询指定楼栋下的所有楼层ID
     */
    private List<Long> getFloorIdsByBuildingId(Long buildingId) {
        List<Floor> floorList = floorMapper.selectList(new LambdaQueryWrapper<Floor>()
                .eq(Floor::getBuildingId, buildingId));
        return floorList.stream().map(Floor::getId).toList();
    }

    /**
     * 批量补充VO里的所属楼栋ID、楼栋名称和楼层号（楼栋ID由楼层带出，供编辑抽屉级联回显）
     */
    private void fillBuildingAndFloorInfo(List<RoomVO> voList) {
        if (ObjectUtils.isEmpty(voList)) {
            return;
        }
        List<Long> floorIds = voList.stream().map(RoomVO::getFloorId).distinct().toList();
        Map<Long, Floor> floorMap = floorMapper.selectBatchIds(floorIds).stream()
                .collect(Collectors.toMap(Floor::getId, Function.identity()));
        List<Long> buildingIds = floorMap.values().stream().map(Floor::getBuildingId).distinct().toList();
        Map<Long, Building> buildingMap = buildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(Building::getId, Function.identity()));
        voList.forEach(vo -> {
            Floor floor = floorMap.get(vo.getFloorId());
            if (floor != null) {
                vo.setFloorNo(floor.getFloorNo());
                vo.setBuildingId(floor.getBuildingId());
                Building building = buildingMap.get(floor.getBuildingId());
                if (building != null) {
                    vo.setBuildingName(building.getName());
                }
            }
        });
    }
}