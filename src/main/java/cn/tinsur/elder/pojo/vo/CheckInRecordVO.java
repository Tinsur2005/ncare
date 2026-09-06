package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.CheckInRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 入住退住办理表 视图对象（在实体基础上补充老人、客户、床位、办理人等展示字段）
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckInRecordVO extends CheckInRecord {

    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 老人联系电话
     */
    private String elderPhone;

    /**
     * 老人身份证号
     */
    private String idCardNo;

    /**
     * 客户（家属）姓名
     */
    private String familyName;

    /**
     * 客户（家属）联系电话
     */
    private String familyPhone;

    /**
     * 床位所属楼栋ID（供编辑时级联回显）
     */
    private Long buildingId;

    /**
     * 床位所属楼层ID（供编辑时级联回显）
     */
    private Long floorId;

    /**
     * 床位所属房间ID（供编辑时级联回显）
     */
    private Long roomId;

    /**
     * 床位所属楼栋名称
     */
    private String buildingName;

    /**
     * 床位所属楼层号
     */
    private Integer floorNo;

    /**
     * 床位所属房间号
     */
    private String roomNo;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 床位费（元/月）
     */
    private BigDecimal monthlyPrice;

    /**
     * 确认办理人姓名
     */
    private String handlerName;

    /**
     * 原入住单编号（退住单回显用）
     */
    private String checkInNo;
}