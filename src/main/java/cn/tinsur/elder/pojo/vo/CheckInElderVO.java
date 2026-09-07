package cn.tinsur.elder.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 办理老人视图对象（供入住、退住办理第一步选择老人共用，附带老人状态和床位占用信息，前端按规则标注并禁选）
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@Data
public class CheckInElderVO {

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 老人状态（0已停用 1正常 2请假 3退住中 4入住中 5已退住）
     */
    private Integer status;

    /**
     * 老人姓名
     */
    private String realName;

    /**
     * 老人身份证号
     */
    private String idCardNo;

    /**
     * 老人性别（0：女，1：男）
     */
    private Integer gender;

    /**
     * 老人联系电话
     */
    private String phone;

    /**
     * 老人占用的床位ID
     */
    private Long bedId;

    /**
     * 床位位置标签（楼栋+楼层+房间+床位拼接）
     */
    private String bedLabel;

    /**
     * 绑定的客户ID（关联family.id）
     */
    private Long familyId;

    /**
     * 绑定的客户姓名
     */
    private String familyName;

    /**
     * 原入住单ID（创建退住单时回链）
     */
    private Long checkInId;

    /**
     * 入住日期（取最近一次已完成的入住单）
     */
    private Date checkinDate;
}