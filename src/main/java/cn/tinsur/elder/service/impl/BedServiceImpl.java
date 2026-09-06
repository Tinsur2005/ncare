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

import cn.tinsur.elder.mapper.BedMapper;
import cn.tinsur.elder.mapper.BuildingMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.FloorMapper;
import cn.tinsur.elder.mapper.RoomMapper;
import cn.tinsur.elder.pojo.entity.Bed;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.query.BedQuery;
import cn.tinsur.elder.pojo.vo.BedVO;
import cn.tinsur.elder.service.IBedService;
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
 * 床位表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements IBedService {

    /**
     * 床位状态：空闲
     */
    public static final Integer STATUS_FREE = 0;

    /**
     * 床位状态：已占用
     */
    public static final Integer STATUS_OCCUPIED = 1;

    @Autowired
    private BedMapper bedMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private FloorMapper floorMapper;
    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private ElderMapper elderMapper;

    @Override
    public IPage<BedVO> list(BedQuery bedQuery) {
        IPage<Bed> page = new Page<>(bedQuery.getPage(), bedQuery.getLimit());
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                //房间ID和床位状态直接等值查询
                .eq(!ObjectUtils.isEmpty(bedQuery.getRoomId()), Bed::getRoomId, bedQuery.getRoomId())
                .eq(!ObjectUtils.isEmpty(bedQuery.getStatus()), Bed::getStatus, bedQuery.getStatus())
                .between(!ObjectUtils.isEmpty(bedQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(bedQuery.getEndCreateTime()),
                        Bed::getCreateTime, bedQuery.getBeginCreateTime(),
                        bedQuery.getEndCreateTime())
                .orderByAsc(Bed::getRoomId)
                .orderByAsc(Bed::getBedNo);
        //楼层ID和楼栋ID需要逐级解析出房间ID集合再匹配，解析不到房间时直接返回空分页避免in空集合报错
        List<Long> roomIds = getRoomIdsByFloorOrBuilding(bedQuery.getFloorId(), bedQuery.getBuildingId());
        if (roomIds != null) {
            if (ObjectUtils.isEmpty(roomIds)) {
                return new Page<>(bedQuery.getPage(), bedQuery.getLimit());
            }
            lambdaQueryWrapper.in(Bed::getRoomId, roomIds);
        }
        IPage<Bed> bedPage = bedMapper.selectPage(page, lambdaQueryWrapper);

        //把分页结果转成VO并批量补充楼栋名称、楼层号和房间号
        IPage<BedVO> voPage = new Page<>(bedPage.getCurrent(), bedPage.getSize(), bedPage.getTotal());
        List<BedVO> voList = bedPage.getRecords().stream().map(bed -> {
            BedVO bedVO = new BedVO();
            BeanUtils.copyProperties(bed, bedVO);
            return bedVO;
        }).toList();
        fillBuildingFloorRoomInfo(voList);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public BedVO getVOById(Long id) {
        Bed bed = bedMapper.selectById(id);
        if (ObjectUtils.isEmpty(bed)) {
            return null;
        }
        BedVO bedVO = new BedVO();
        BeanUtils.copyProperties(bed, bedVO);
        fillBuildingFloorRoomInfo(List.of(bedVO));
        return bedVO;
    }

    @Override
    public List<Bed> listAll() {
        return bedMapper.selectList(new LambdaQueryWrapper<Bed>()
                .orderByAsc(Bed::getRoomId)
                .orderByAsc(Bed::getBedNo));
    }

    @Override
    public List<Bed> listFree() {
        return bedMapper.selectList(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getStatus, STATUS_FREE)
                .orderByAsc(Bed::getRoomId)
                .orderByAsc(Bed::getBedNo));
    }

    @Override
    public BedVO getOccupiedByElderId(Long elderId) {
        //在住与否以床位占用为准：查该老人占用的床位，一个老人最多占用一个床位
        Bed bed = bedMapper.selectOne(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getElderId, elderId)
                .eq(Bed::getStatus, STATUS_OCCUPIED)
                .last("limit 1"));
        if (ObjectUtils.isEmpty(bed)) {
            return null;
        }
        BedVO bedVO = new BedVO();
        BeanUtils.copyProperties(bed, bedVO);
        fillBuildingFloorRoomInfo(List.of(bedVO));
        return bedVO;
    }

    /**
     * 按楼层ID或楼栋ID解析出房间ID集合
     * @param floorId 楼层ID（可为空）
     * @param buildingId 楼栋ID（可为空）
     * @return 房间ID集合；两个参数都为空时返回null表示不需要按房间筛选
     */
    private List<Long> getRoomIdsByFloorOrBuilding(Long floorId, Long buildingId) {
        if (!ObjectUtils.isEmpty(floorId)) {
            return roomMapper.selectList(new LambdaQueryWrapper<Room>()
                            .eq(Room::getFloorId, floorId))
                    .stream().map(Room::getId).toList();
        }
        if (!ObjectUtils.isEmpty(buildingId)) {
            //先查该楼栋下的所有楼层，再查这些楼层下的所有房间
            List<Long> floorIds = floorMapper.selectList(new LambdaQueryWrapper<Floor>()
                            .eq(Floor::getBuildingId, buildingId))
                    .stream().map(Floor::getId).toList();
            if (ObjectUtils.isEmpty(floorIds)) {
                return List.of();
            }
            return roomMapper.selectList(new LambdaQueryWrapper<Room>()
                            .in(Room::getFloorId, floorIds))
                    .stream().map(Room::getId).toList();
        }
        return null;
    }

    /**
     * 批量补充VO里的所属楼栋ID、楼层ID、楼栋名称、楼层号、房间号和入住老人姓名（供编辑抽屉级联回显）
     */
    private void fillBuildingFloorRoomInfo(List<BedVO> voList) {
        if (ObjectUtils.isEmpty(voList)) {
            return;
        }
        List<Long> roomIds = voList.stream().map(BedVO::getRoomId).distinct().toList();
        Map<Long, Room> roomMap = roomMapper.selectBatchIds(roomIds).stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));
        List<Long> floorIds = roomMap.values().stream().map(Room::getFloorId).distinct().toList();
        Map<Long, Floor> floorMap = floorMapper.selectBatchIds(floorIds).stream()
                .collect(Collectors.toMap(Floor::getId, Function.identity()));
        List<Long> buildingIds = floorMap.values().stream().map(Floor::getBuildingId).distinct().toList();
        Map<Long, Building> buildingMap = buildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(Building::getId, Function.identity()));
        //收集已占用床位上的老人ID并批量查询老人姓名，没有老人时不查询
        List<Long> elderIds = voList.stream().map(BedVO::getElderId)
                .filter(elderId -> !ObjectUtils.isEmpty(elderId)).distinct().toList();
        Map<Long, Elder> elderMap = ObjectUtils.isEmpty(elderIds) ? Map.of()
                : elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(Elder::getId, Function.identity()));
        voList.forEach(vo -> {
            Room room = roomMap.get(vo.getRoomId());
            if (room != null) {
                vo.setRoomNo(room.getRoomNo());
                Floor floor = floorMap.get(room.getFloorId());
                if (floor != null) {
                    vo.setFloorId(floor.getId());
                    vo.setFloorNo(floor.getFloorNo());
                    vo.setBuildingId(floor.getBuildingId());
                    Building building = buildingMap.get(floor.getBuildingId());
                    if (building != null) {
                        vo.setBuildingName(building.getName());
                    }
                }
            }
            //空闲和维修的床位没有老人，elderId为null时不能去Map里取键
            if (!ObjectUtils.isEmpty(vo.getElderId())) {
                Elder elder = elderMap.get(vo.getElderId());
                if (elder != null) {
                    vo.setElderName(elder.getRealName());
                }
            }
        });
    }
}