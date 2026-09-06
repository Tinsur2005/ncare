package cn.tinsur.elder.pojo.dto;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Family;
import lombok.Data;

/**
 * <p>
 * 创建办理单请求对象（入住第一步登记客户和老人，退住第一步选择在住老人）
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@Data
public class CheckInAddDTO {

    /**
     * 办理类型：0入住 1退住
     */
    private Integer type;

    /**
     * 客户登记方式：0新建客户 1选择已有客户（入住办理时必传，退住办理时无需传）
     */
    private Integer familyMode;

    /**
     * 已有客户ID（familyMode为1时必传）
     */
    private Long familyId;

    /**
     * 新建客户信息（familyMode为0时必传）
     */
    private Family family;

    /**
     * 老人登记方式：0新建老人 1选择已有老人（入住办理时必传，退住办理固定选择已有老人可不传）
     */
    private Integer elderMode;

    /**
     * 已有老人ID（elderMode为1或退住办理时必传）
     */
    private Long elderId;

    /**
     * 新建老人信息（elderMode为0时必传）
     */
    private Elder elder;

    /**
     * 备注
     */
    private String remark;
}