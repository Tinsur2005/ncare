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

import cn.tinsur.elder.mapper.BedMapper;
import cn.tinsur.elder.mapper.BuildingMapper;
import cn.tinsur.elder.mapper.CheckInRecordMapper;
import cn.tinsur.elder.mapper.ElderFamilyMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.FamilyMapper;
import cn.tinsur.elder.mapper.FloorMapper;
import cn.tinsur.elder.mapper.RoomMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.dto.CheckInAddDTO;
import cn.tinsur.elder.pojo.entity.Bed;
import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.entity.CheckInRecord;
import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ElderFamily;
import cn.tinsur.elder.pojo.entity.Family;
import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.CheckInRecordQuery;
import cn.tinsur.elder.pojo.vo.CheckInElderVO;
import cn.tinsur.elder.pojo.vo.CheckInRecordVO;
import cn.tinsur.elder.service.ICheckInRecordService;
import cn.tinsur.elder.service.IContractService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 入住退住办理表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@Service
public class CheckInRecordServiceImpl extends ServiceImpl<CheckInRecordMapper, CheckInRecord> implements ICheckInRecordService {

    /**
     * 办理类型：入住
     */
    public static final Integer TYPE_CHECK_IN = 0;

    /**
     * 办理类型：退住
     */
    public static final Integer TYPE_CHECK_OUT = 1;

    /**
     * 办理状态：办理中
     */
    public static final Integer STATUS_PROCESSING = 0;

    /**
     * 办理状态：已完成
     */
    public static final Integer STATUS_FINISHED = 1;

    /**
     * 办理状态：已取消
     */
    public static final Integer STATUS_CANCELED = 2;

    /**
     * 床位状态：空闲
     */
    public static final Integer BED_STATUS_FREE = 0;

    /**
     * 床位状态：已占用
     */
    public static final Integer BED_STATUS_OCCUPIED = 1;

    /**
     * 老人状态：已停用
     */
    public static final Integer ELDER_STATUS_DISABLED = 0;

    /**
     * 老人状态：正常
     */
    public static final Integer ELDER_STATUS_NORMAL = 1;

    /**
     * 老人状态：退住中
     */
    public static final Integer ELDER_STATUS_CHECKING_OUT = 3;

    /**
     * 老人状态：入住中（表示正在办理入住，确认入住后回到正常）
     */
    public static final Integer ELDER_STATUS_IN = 4;

    /**
     * 老人状态：已退住
     */
    public static final Integer ELDER_STATUS_CHECKED_OUT = 5;

    @Autowired
    private CheckInRecordMapper checkInRecordMapper;
    @Autowired
    private ElderMapper elderMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private ElderFamilyMapper elderFamilyMapper;
    @Autowired
    private BedMapper bedMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private FloorMapper floorMapper;
    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IContractService contractService;

    @Override
    public IPage<CheckInRecordVO> list(CheckInRecordQuery checkInRecordQuery) {
        IPage<CheckInRecord> page = new Page<>(checkInRecordQuery.getPage(), checkInRecordQuery.getLimit());
        LambdaQueryWrapper<CheckInRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                //办理类型、办理状态和老人ID直接等值查询
                .eq(!ObjectUtils.isEmpty(checkInRecordQuery.getType()), CheckInRecord::getType, checkInRecordQuery.getType())
                .eq(!ObjectUtils.isEmpty(checkInRecordQuery.getStatus()), CheckInRecord::getStatus, checkInRecordQuery.getStatus())
                .eq(!ObjectUtils.isEmpty(checkInRecordQuery.getElderId()), CheckInRecord::getElderId, checkInRecordQuery.getElderId())
                .between(!ObjectUtils.isEmpty(checkInRecordQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(checkInRecordQuery.getEndCreateTime()),
                        CheckInRecord::getCreateTime, checkInRecordQuery.getBeginCreateTime(),
                        checkInRecordQuery.getEndCreateTime())
                .orderByDesc(CheckInRecord::getCreateTime);
        IPage<CheckInRecord> recordPage = checkInRecordMapper.selectPage(page, lambdaQueryWrapper);

        //把分页结果转成VO并批量补充老人、客户、床位、办理人等展示字段
        IPage<CheckInRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(convertToVO(recordPage.getRecords()));
        return voPage;
    }

    @Override
    public CheckInRecordVO getVOById(Long id) {
        CheckInRecord checkInRecord = checkInRecordMapper.selectById(id);
        if (ObjectUtils.isEmpty(checkInRecord)) {
            return null;
        }
        List<CheckInRecordVO> voList = convertToVO(List.of(checkInRecord));
        return voList.get(0);
    }

    @Override
    public List<CheckInElderVO> listCheckInElders(String name) {
        //供入住、退住办理第一步共用：搜索范围为全部老人（不过滤状态），附带床位占用和老人状态，由前端标注并禁选不可办理的老人
        List<Elder> elders = elderMapper.selectList(new LambdaQueryWrapper<Elder>()
                .like(!ObjectUtils.isEmpty(name), Elder::getRealName, name)
                .orderByDesc(Elder::getCreateTime)
                .last("LIMIT 20"));
        if (ObjectUtils.isEmpty(elders)) {
            return List.of();
        }
        List<Long> elderIds = elders.stream().map(Elder::getId).toList();

        //批量查询老人占用的床位（一个老人只占用一个床位）
        Map<Long, Bed> bedMap = bedMapper.selectList(new LambdaQueryWrapper<Bed>()
                        .in(Bed::getElderId, elderIds)
                        .eq(Bed::getStatus, BED_STATUS_OCCUPIED))
                .stream().collect(Collectors.toMap(Bed::getElderId, Function.identity(), (first, second) -> first));
        //批量查询老人绑定的客户（一个老人可能绑定多个客户，取最早绑定的一条作为办理客户）
        Map<Long, ElderFamily> bindingMap = elderFamilyMapper.selectList(new LambdaQueryWrapper<ElderFamily>()
                        .in(ElderFamily::getElderId, elderIds)
                        .orderByAsc(ElderFamily::getId))
                .stream().collect(Collectors.toMap(ElderFamily::getElderId, Function.identity(), (first, second) -> first));
        List<Long> familyIds = bindingMap.values().stream().map(ElderFamily::getFamilyId).distinct().toList();
        Map<Long, Family> familyMap = ObjectUtils.isEmpty(familyIds) ? Map.of()
                : familyMapper.selectBatchIds(familyIds).stream()
                .collect(Collectors.toMap(Family::getId, Function.identity()));
        //批量查询老人最近一次已完成的入住单，用于回显入住日期和创建退住单时回链
        Map<Long, CheckInRecord> checkInMap = checkInRecordMapper.selectList(new LambdaQueryWrapper<CheckInRecord>()
                        .in(CheckInRecord::getElderId, elderIds)
                        .eq(CheckInRecord::getType, TYPE_CHECK_IN)
                        .eq(CheckInRecord::getStatus, STATUS_FINISHED)
                        .orderByDesc(CheckInRecord::getId))
                .stream().collect(Collectors.toMap(CheckInRecord::getElderId, Function.identity(), (first, second) -> first));

        return elders.stream().map(elder -> {
            CheckInElderVO vo = new CheckInElderVO();
            vo.setElderId(elder.getId());
            vo.setRealName(elder.getRealName());
            vo.setIdCardNo(elder.getIdCardNo());
            vo.setStatus(elder.getStatus());
            vo.setGender(elder.getGender());
            vo.setPhone(elder.getPhone());
            Bed bed = bedMap.get(elder.getId());
            if (bed != null) {
                vo.setBedId(bed.getId());
                vo.setBedLabel(getBedLabel(bed));
            }
            ElderFamily binding = bindingMap.get(elder.getId());
            if (binding != null) {
                vo.setFamilyId(binding.getFamilyId());
                Family family = familyMap.get(binding.getFamilyId());
                if (family != null) {
                    vo.setFamilyName(family.getRealName());
                }
            }
            CheckInRecord checkIn = checkInMap.get(elder.getId());
            if (checkIn != null) {
                vo.setCheckInId(checkIn.getId());
                vo.setCheckinDate(checkIn.getCheckinDate());
            }
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public Result add(CheckInAddDTO checkInAddDTO) {
        if (TYPE_CHECK_OUT.equals(checkInAddDTO.getType())) {
            return addCheckOutRecord(checkInAddDTO);
        }
        return addCheckInRecord(checkInAddDTO);
    }

    /**
     * 创建入住单：处理新建或选择已有的客户与老人，创建办理单并生成办理编号
     */
    private Result addCheckInRecord(CheckInAddDTO checkInAddDTO) {
        //处理客户：familyMode为0时新建客户写入family表，为1时校验已有客户
        Long familyId;
        if (Integer.valueOf(0).equals(checkInAddDTO.getFamilyMode())) {
            Family family = checkInAddDTO.getFamily();
            if (ObjectUtils.isEmpty(family) || ObjectUtils.isEmpty(family.getName())
                    || ObjectUtils.isEmpty(family.getPassword()) || ObjectUtils.isEmpty(family.getRealName())) {
                return Result.error("新建客户时用户名、密码和姓名不能为空");
            }
            if (isFamilyNameExists(family.getName())) {
                return Result.error("已有同名家属存在，请修改用户名后重试");
            }
            //新建客户默认正常状态
            family.setStatus(1);
            familyMapper.insert(family);
            familyId = family.getId();
        } else {
            familyId = checkInAddDTO.getFamilyId();
            if (ObjectUtils.isEmpty(familyId) || ObjectUtils.isEmpty(familyMapper.selectById(familyId))) {
                return Result.error("请选择已有客户");
            }
        }

        //处理老人：elderMode为0时新建老人写入elder表，为1时校验已有老人的状态
        Long elderId;
        Elder existElder = null;
        if (Integer.valueOf(0).equals(checkInAddDTO.getElderMode())) {
            Elder elder = checkInAddDTO.getElder();
            if (ObjectUtils.isEmpty(elder) || ObjectUtils.isEmpty(elder.getName())
                    || ObjectUtils.isEmpty(elder.getPassword()) || ObjectUtils.isEmpty(elder.getRealName())) {
                return Result.error("新建老人时用户名、密码和姓名不能为空");
            }
            if (isElderNameExists(elder.getName())) {
                return Result.error("已有同名老人存在，请修改用户名后重试");
            }
            //新建老人直接置为4入住中，表示正在办理入住，确认入住后回到1正常
            elder.setStatus(ELDER_STATUS_IN);
            elderMapper.insert(elder);
            elderId = elder.getId();
        } else {
            elderId = checkInAddDTO.getElderId();
            existElder = ObjectUtils.isEmpty(elderId) ? null : elderMapper.selectById(elderId);
            if (ObjectUtils.isEmpty(existElder)) {
                return Result.error("请选择已有老人");
            }
            if (existElder.getStatus() == 0) {
                return Result.error("该老人已停用，不允许办理入住");
            }
            if (existElder.getStatus() == ELDER_STATUS_IN) {
                return Result.error("该老人正在办理入住，不允许重复发起");
            }
            if (existElder.getStatus() == ELDER_STATUS_CHECKING_OUT) {
                return Result.error("该老人正在办理退住，请先完成或取消退住办理");
            }
        }

        //创建入住办理单：第一步完成后才落库，当前步骤直接指向第2步
        CheckInRecord checkInRecord = new CheckInRecord();
        checkInRecord.setType(TYPE_CHECK_IN);
        checkInRecord.setStep(2);
        checkInRecord.setStatus(STATUS_PROCESSING);
        checkInRecord.setElderId(elderId);
        checkInRecord.setFamilyId(familyId);
        checkInRecord.setRemark(checkInAddDTO.getRemark());
        checkInRecordMapper.insert(checkInRecord);
        generateRecordNo(checkInRecord);

        //已有老人置为4入住中，表示正在办理入住（新建老人在插入时已置为4）
        if (existElder != null) {
            existElder.setStatus(ELDER_STATUS_IN);
            elderMapper.updateById(existElder);
        }
        return Result.ok("办理单创建成功", checkInRecord.getId());
    }

    /**
     * 创建退住单：校验老人在住且没有办理中的退住单，回链原入住单并把老人置为退住中防止重复发起
     */
    private Result addCheckOutRecord(CheckInAddDTO checkInAddDTO) {
        Long elderId = checkInAddDTO.getElderId();
        Elder elder = ObjectUtils.isEmpty(elderId) ? null : elderMapper.selectById(elderId);
        if (ObjectUtils.isEmpty(elder)) {
            return Result.error("请选择在住老人");
        }
        if (elder.getStatus() == 0) {
            return Result.error("该老人已停用，不允许办理退住");
        }
        if (elder.getStatus() == ELDER_STATUS_IN) {
            return Result.error("该老人正在办理入住，不允许发起退住");
        }
        //同一老人同时只允许存在一张办理中的退住单
        Long processingCount = checkInRecordMapper.selectCount(new LambdaQueryWrapper<CheckInRecord>()
                .eq(CheckInRecord::getType, TYPE_CHECK_OUT)
                .eq(CheckInRecord::getStatus, STATUS_PROCESSING)
                .eq(CheckInRecord::getElderId, elderId));
        if (processingCount > 0) {
            return Result.error("该老人已有办理中的退住申请，请勿重复发起");
        }

        //查询老人占用的床位和最近一次已完成的入住单，用于释放床位和回链
        Bed bed = bedMapper.selectOne(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getElderId, elderId)
                .eq(Bed::getStatus, BED_STATUS_OCCUPIED)
                .last("LIMIT 1"));
        //入住完成后老人状态是1正常，是否在住以床位占用为准，没占用床位的老人不允许办理退住
        if (bed == null) {
            return Result.error("该老人未入住床位，不允许办理退住");
        }
        CheckInRecord originCheckIn = checkInRecordMapper.selectOne(new LambdaQueryWrapper<CheckInRecord>()
                .eq(CheckInRecord::getElderId, elderId)
                .eq(CheckInRecord::getType, TYPE_CHECK_IN)
                .eq(CheckInRecord::getStatus, STATUS_FINISHED)
                .orderByDesc(CheckInRecord::getId)
                .last("LIMIT 1"));
        //办理客户取老人最早绑定的家属，没有绑定时为空
        ElderFamily binding = elderFamilyMapper.selectOne(new LambdaQueryWrapper<ElderFamily>()
                .eq(ElderFamily::getElderId, elderId)
                .orderByAsc(ElderFamily::getId)
                .last("LIMIT 1"));

        //创建退住办理单：第一步完成后才落库，当前步骤直接指向第2步
        CheckInRecord checkInRecord = new CheckInRecord();
        checkInRecord.setType(TYPE_CHECK_OUT);
        checkInRecord.setStep(2);
        checkInRecord.setStatus(STATUS_PROCESSING);
        checkInRecord.setElderId(elderId);
        checkInRecord.setFamilyId(binding == null ? null : binding.getFamilyId());
        checkInRecord.setBedId(bed == null ? null : bed.getId());
        checkInRecord.setCheckInId(originCheckIn == null ? null : originCheckIn.getId());
        checkInRecord.setRemark(checkInAddDTO.getRemark());
        checkInRecordMapper.insert(checkInRecord);
        generateRecordNo(checkInRecord);

        //老人置为退住中，防止退住办理期间重复发起入住或退住
        elder.setStatus(ELDER_STATUS_CHECKING_OUT);
        elderMapper.updateById(elder);
        return Result.ok("办理单创建成功", checkInRecord.getId());
    }

    @Override
    public Result saveStep(Long id, Integer step, CheckInRecord checkInRecord) {
        CheckInRecord dbRecord = checkInRecordMapper.selectById(id);
        if (ObjectUtils.isEmpty(dbRecord)) {
            return Result.error("办理单不存在");
        }
        //只有办理中的单子可以继续填写
        if (!STATUS_PROCESSING.equals(dbRecord.getStatus())) {
            return Result.error("该办理单已结束，不允许继续操作");
        }
        //步骤号必须和办理单当前步骤一致，避免步骤错乱
        if (!dbRecord.getStep().equals(step)) {
            return Result.error("办理步骤已变化，请刷新页面后重试");
        }
        if (TYPE_CHECK_IN.equals(dbRecord.getType())) {
            if (step == 2) {
                //入住登记：必须填写入住日期和床位
                if (ObjectUtils.isEmpty(checkInRecord.getCheckinDate()) || ObjectUtils.isEmpty(checkInRecord.getBedId())) {
                    return Result.error("请填写入住日期并选择床位");
                }
                if (ObjectUtils.isEmpty(bedMapper.selectById(checkInRecord.getBedId()))) {
                    return Result.error("所选床位不存在，请重新选择");
                }
                dbRecord.setCheckinDate(checkInRecord.getCheckinDate());
                dbRecord.setBedId(checkInRecord.getBedId());
                dbRecord.setRemark(checkInRecord.getRemark());
            } else if (step == 3) {
                //签订合同：必须填写合同名称、编号、签订日期和到期日期
                if (ObjectUtils.isEmpty(checkInRecord.getContractName()) || ObjectUtils.isEmpty(checkInRecord.getContractNo())
                        || ObjectUtils.isEmpty(checkInRecord.getSignTime()) || ObjectUtils.isEmpty(checkInRecord.getExpireTime())) {
                    return Result.error("请填写完整的合同信息");
                }
                dbRecord.setContractName(checkInRecord.getContractName());
                dbRecord.setContractNo(checkInRecord.getContractNo());
                dbRecord.setContractType(ObjectUtils.isEmpty(checkInRecord.getContractType()) ? 1 : checkInRecord.getContractType());
                dbRecord.setSignTime(checkInRecord.getSignTime());
                dbRecord.setExpireTime(checkInRecord.getExpireTime());
                dbRecord.setFileUrl(checkInRecord.getFileUrl());
            }
        } else {
            if (step == 2) {
                //退住登记：必须填写退住日期和退住原因
                if (ObjectUtils.isEmpty(checkInRecord.getCheckoutDate()) || ObjectUtils.isEmpty(checkInRecord.getReason())) {
                    return Result.error("请填写退住日期和退住原因");
                }
                dbRecord.setCheckoutDate(checkInRecord.getCheckoutDate());
                dbRecord.setReason(checkInRecord.getReason());
                dbRecord.setRemark(checkInRecord.getRemark());
            }
        }
        //保存本步骤数据并把步骤+1
        dbRecord.setStep(dbRecord.getStep() + 1);
        checkInRecordMapper.updateById(dbRecord);
        return Result.ok("保存成功");
    }

    @Override
    @Transactional
    public Result confirm(Long id, Long handlerId) {
        CheckInRecord checkInRecord = checkInRecordMapper.selectById(id);
        if (ObjectUtils.isEmpty(checkInRecord)) {
            return Result.error("办理单不存在");
        }
        if (!STATUS_PROCESSING.equals(checkInRecord.getStatus())) {
            return Result.error("该办理单已结束，不允许重复确认");
        }
        if (TYPE_CHECK_IN.equals(checkInRecord.getType())) {
            return confirmCheckIn(checkInRecord, handlerId);
        }
        return confirmCheckOut(checkInRecord, handlerId);
    }

    /**
     * 确认入住：占用床位、老人置为入住中、合同写入contract表并绑定客户老人关系
     */
    private Result confirmCheckIn(CheckInRecord checkInRecord, Long handlerId) {
        //全部步骤完成后才允许确认入住
        if (checkInRecord.getStep() != 4) {
            return Result.error("请先完成前面的办理步骤");
        }
        //合同编号唯一，确认前先查重，避免插入合同时才被数据库唯一索引拦下
        Contract existContract = contractService.getOne(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getContractNo, checkInRecord.getContractNo()));
        if (!ObjectUtils.isEmpty(existContract)) {
            return Result.error("已存在相同合同编号，请修改后重试");
        }
        //床位只在确认入住时校验并锁定占用，两个办理单同床时后确认者报错
        Bed bed = bedMapper.selectById(checkInRecord.getBedId());
        if (ObjectUtils.isEmpty(bed)) {
            return Result.error("所选床位不存在，请重新选择");
        }
        if (!BED_STATUS_FREE.equals(bed.getStatus())) {
            return Result.error("该床位已被占用，请重新选择");
        }
        bed.setStatus(BED_STATUS_OCCUPIED);
        bed.setElderId(checkInRecord.getElderId());
        bedMapper.updateById(bed);

        //办理完成后老人回到1正常状态，是否在住由床位占用体现
        Elder elder = elderMapper.selectById(checkInRecord.getElderId());
        if (!ObjectUtils.isEmpty(elder)) {
            elder.setStatus(ELDER_STATUS_NORMAL);
            elderMapper.updateById(elder);
        }

        //合同写入contract表
        Contract contract = new Contract();
        contract.setContractNo(checkInRecord.getContractNo());
        contract.setElderId(checkInRecord.getElderId());
        contract.setContractName(checkInRecord.getContractName());
        contract.setContractType(checkInRecord.getContractType());
        contract.setSignTime(checkInRecord.getSignTime());
        contract.setExpireTime(checkInRecord.getExpireTime());
        contract.setFileUrl(checkInRecord.getFileUrl());
        contractService.save(contract);

        //绑定客户与老人关系，已绑定时跳过避免重复插入
        if (!ObjectUtils.isEmpty(checkInRecord.getFamilyId())) {
            Long bindingCount = elderFamilyMapper.selectCount(new LambdaQueryWrapper<ElderFamily>()
                    .eq(ElderFamily::getElderId, checkInRecord.getElderId())
                    .eq(ElderFamily::getFamilyId, checkInRecord.getFamilyId()));
            if (bindingCount == 0) {
                ElderFamily elderFamily = new ElderFamily();
                elderFamily.setElderId(checkInRecord.getElderId());
                elderFamily.setFamilyId(checkInRecord.getFamilyId());
                elderFamilyMapper.insert(elderFamily);
            }
        }

        //办理单置为已完成并记录确认办理人
        checkInRecord.setStatus(STATUS_FINISHED);
        checkInRecord.setHandlerId(handlerId);
        checkInRecordMapper.updateById(checkInRecord);
        return Result.ok("确认入住成功");
    }

    /**
     * 确认退住：释放床位、老人置为已退住
     */
    private Result confirmCheckOut(CheckInRecord checkInRecord, Long handlerId) {
        //全部步骤完成后才允许确认退住
        if (checkInRecord.getStep() != 3) {
            return Result.error("请先完成前面的办理步骤");
        }
        //释放床位为空闲，updateById会忽略null字段，elder_id要用UpdateWrapper显式更新
        Bed bed = bedMapper.selectById(checkInRecord.getBedId());
        if (!ObjectUtils.isEmpty(bed)) {
            bedMapper.update(new LambdaUpdateWrapper<Bed>()
                    .eq(Bed::getId, bed.getId())
                    .set(Bed::getStatus, BED_STATUS_FREE)
                    .set(Bed::getElderId, null));
        }

        //老人状态改为5已退住
        Elder elder = elderMapper.selectById(checkInRecord.getElderId());
        if (!ObjectUtils.isEmpty(elder)) {
            elder.setStatus(ELDER_STATUS_CHECKED_OUT);
            elderMapper.updateById(elder);
        }

        //办理单置为已完成并记录确认办理人
        checkInRecord.setStatus(STATUS_FINISHED);
        checkInRecord.setHandlerId(handlerId);
        checkInRecordMapper.updateById(checkInRecord);
        return Result.ok("确认退住成功");
    }

    @Override
    public Result cancel(Long id) {
        CheckInRecord checkInRecord = checkInRecordMapper.selectById(id);
        if (ObjectUtils.isEmpty(checkInRecord)) {
            return Result.error("办理单不存在");
        }
        //只有办理中的单子可以取消，已完成的单子不允许取消
        if (!STATUS_PROCESSING.equals(checkInRecord.getStatus())) {
            return Result.error("仅办理中的单子可以取消");
        }
        //入住单取消时把老人从入住中改为0已停用，新增和已有老人都遵循该原则（仅当老人仍是入住中时更新）
        if (TYPE_CHECK_IN.equals(checkInRecord.getType())) {
            Elder elder = elderMapper.selectById(checkInRecord.getElderId());
            if (!ObjectUtils.isEmpty(elder) && elder.getStatus() == ELDER_STATUS_IN) {
                elder.setStatus(ELDER_STATUS_DISABLED);
                elderMapper.updateById(elder);
            }
        }
        //退住单取消时把老人从退住中恢复为正常（仅当老人仍是退住中时恢复）
        if (TYPE_CHECK_OUT.equals(checkInRecord.getType())) {
            Elder elder = elderMapper.selectById(checkInRecord.getElderId());
            if (!ObjectUtils.isEmpty(elder) && elder.getStatus() == ELDER_STATUS_CHECKING_OUT) {
                elder.setStatus(ELDER_STATUS_NORMAL);
                elderMapper.updateById(elder);
            }
        }
        checkInRecord.setStatus(STATUS_CANCELED);
        checkInRecordMapper.updateById(checkInRecord);
        return Result.ok("取消成功");
    }

    /**
     * 判断家属用户名是否已存在
     */
    private boolean isFamilyNameExists(String name) {
        return familyMapper.selectCount(new LambdaQueryWrapper<Family>().eq(Family::getName, name)) > 0;
    }

    /**
     * 判断老人用户名是否已存在
     */
    private boolean isElderNameExists(String name) {
        return elderMapper.selectCount(new LambdaQueryWrapper<Elder>().eq(Elder::getName, name)) > 0;
    }

    /**
     * 保存后生成办理编号：入住RZ/退住TZ+日期+办理单ID，用自增ID拼日期保证唯一
     */
    private void generateRecordNo(CheckInRecord checkInRecord) {
        String prefix = TYPE_CHECK_IN.equals(checkInRecord.getType()) ? "RZ" : "TZ";
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        checkInRecord.setRecordNo(prefix + date + checkInRecord.getId());
        checkInRecordMapper.updateById(checkInRecord);
    }

    /**
     * 把办理单列表转成VO并批量补充老人、客户、床位位置、办理人和原入住单编号
     */
    private List<CheckInRecordVO> convertToVO(List<CheckInRecord> records) {
        if (ObjectUtils.isEmpty(records)) {
            return List.of();
        }
        List<CheckInRecordVO> voList = records.stream().map(record -> {
            CheckInRecordVO vo = new CheckInRecordVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).toList();

        //批量查询老人
        List<Long> elderIds = voList.stream().map(CheckInRecordVO::getElderId).distinct().toList();
        Map<Long, Elder> elderMap = elderMapper.selectBatchIds(elderIds).stream()
                .collect(Collectors.toMap(Elder::getId, Function.identity()));
        //批量查询客户，没有客户的单子过滤掉为空的ID
        List<Long> familyIds = voList.stream().map(CheckInRecordVO::getFamilyId)
                .filter(familyId -> !ObjectUtils.isEmpty(familyId)).distinct().toList();
        Map<Long, Family> familyMap = ObjectUtils.isEmpty(familyIds) ? Map.of()
                : familyMapper.selectBatchIds(familyIds).stream()
                .collect(Collectors.toMap(Family::getId, Function.identity()));
        //批量查询床位并逐级解析出房间、楼层和楼栋
        List<Long> bedIds = voList.stream().map(CheckInRecordVO::getBedId)
                .filter(bedId -> !ObjectUtils.isEmpty(bedId)).distinct().toList();
        Map<Long, Bed> bedMap = ObjectUtils.isEmpty(bedIds) ? Map.of()
                : bedMapper.selectBatchIds(bedIds).stream()
                .collect(Collectors.toMap(Bed::getId, Function.identity()));
        List<Long> roomIds = bedMap.values().stream().map(Bed::getRoomId).distinct().toList();
        Map<Long, Room> roomMap = ObjectUtils.isEmpty(roomIds) ? Map.of()
                : roomMapper.selectBatchIds(roomIds).stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));
        List<Long> floorIds = roomMap.values().stream().map(Room::getFloorId).distinct().toList();
        Map<Long, Floor> floorMap = ObjectUtils.isEmpty(floorIds) ? Map.of()
                : floorMapper.selectBatchIds(floorIds).stream()
                .collect(Collectors.toMap(Floor::getId, Function.identity()));
        List<Long> buildingIds = floorMap.values().stream().map(Floor::getBuildingId).distinct().toList();
        Map<Long, Building> buildingMap = ObjectUtils.isEmpty(buildingIds) ? Map.of()
                : buildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(Building::getId, Function.identity()));
        //批量查询确认办理人
        List<Long> handlerIds = voList.stream().map(CheckInRecordVO::getHandlerId)
                .filter(handlerId -> !ObjectUtils.isEmpty(handlerId)).distinct().toList();
        Map<Long, User> userMap = ObjectUtils.isEmpty(handlerIds) ? Map.of()
                : userMapper.selectBatchIds(handlerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        //批量查询原入住单编号（退住单回显用）
        List<Long> checkInIds = voList.stream().map(CheckInRecordVO::getCheckInId)
                .filter(checkInId -> !ObjectUtils.isEmpty(checkInId)).distinct().toList();
        Map<Long, CheckInRecord> checkInMap = ObjectUtils.isEmpty(checkInIds) ? Map.of()
                : checkInRecordMapper.selectBatchIds(checkInIds).stream()
                .collect(Collectors.toMap(CheckInRecord::getId, Function.identity()));

        voList.forEach(vo -> {
            Elder elder = elderMap.get(vo.getElderId());
            if (elder != null) {
                vo.setElderName(elder.getRealName());
                vo.setElderPhone(elder.getPhone());
                vo.setIdCardNo(elder.getIdCardNo());
            }
            //第一步选择已有客户才有familyId，为null时不能去Map里取键
            if (!ObjectUtils.isEmpty(vo.getFamilyId())) {
                Family family = familyMap.get(vo.getFamilyId());
                if (family != null) {
                    vo.setFamilyName(family.getRealName());
                    vo.setFamilyPhone(family.getPhone());
                }
            }
            if (!ObjectUtils.isEmpty(vo.getBedId())) {
                Bed bed = bedMap.get(vo.getBedId());
                if (bed != null) {
                    vo.setBedNo(bed.getBedNo());
                    vo.setMonthlyPrice(bed.getMonthlyPrice());
                    Room room = roomMap.get(bed.getRoomId());
                    if (room != null) {
                        vo.setRoomId(room.getId());
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
                }
            }
            if (!ObjectUtils.isEmpty(vo.getHandlerId())) {
                User user = userMap.get(vo.getHandlerId());
                if (user != null) {
                    vo.setHandlerName(user.getRealName());
                }
            }
            if (!ObjectUtils.isEmpty(vo.getCheckInId())) {
                CheckInRecord checkIn = checkInMap.get(vo.getCheckInId());
                if (checkIn != null) {
                    vo.setCheckInNo(checkIn.getRecordNo());
                }
            }
        });
        return voList;
    }

    /**
     * 拼接床位的完整位置标签：楼栋+楼层+房间+床位
     */
    private String getBedLabel(Bed bed) {
        Room room = roomMapper.selectById(bed.getRoomId());
        if (room == null) {
            return bed.getBedNo();
        }
        Floor floor = floorMapper.selectById(room.getFloorId());
        Building building = floor == null ? null : buildingMapper.selectById(floor.getBuildingId());
        StringBuilder label = new StringBuilder();
        if (building != null) {
            label.append(building.getName()).append(" ");
        }
        if (floor != null) {
            label.append(floor.getFloorNo()).append("层 ");
        }
        label.append(room.getRoomNo()).append("房 ").append(bed.getBedNo());
        return label.toString();
    }
}