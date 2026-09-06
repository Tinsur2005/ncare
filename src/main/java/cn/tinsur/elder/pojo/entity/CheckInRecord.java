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

package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 入住退住办理表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CheckInRecord implements Serializable {


    /**
     * 办理单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 办理编号（入住RZ/退住TZ+日期+ID，后端生成）
     */
    @TableField("record_no")
    private String recordNo;

    /**
     * 办理类型：0入住 1退住
     */
    private Integer type;

    /**
     * 当前步骤：入住1~4，退住1~3（每完成一步+1，支持续办）
     */
    private Integer step;

    /**
     * 状态：0办理中 1已完成 2已取消
     */
    private Integer status;

    /**
     * 老人ID（关联elder.id）
     */
    @TableField("elder_id")
    private Long elderId;

    /**
     * 客户ID（关联family.id，入住第一步登记或选择已有客户）
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 床位ID（入住分配的床位/退住释放的床位）
     */
    @TableField("bed_id")
    private Long bedId;

    /**
     * 原入住单ID（退住单关联发起退住时的入住单）
     */
    @TableField("check_in_id")
    private Long checkInId;

    /**
     * 入住日期
     */
    @TableField("checkin_date")
    private Date checkinDate;

    /**
     * 退住日期
     */
    @TableField("checkout_date")
    private Date checkoutDate;

    /**
     * 合同名称（确认入住时写入contract表）
     */
    @TableField("contract_name")
    private String contractName;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contractNo;

    /**
     * 合同类型：0服务合同 1入住合同 2其他
     */
    @TableField("contract_type")
    private Integer contractType;

    /**
     * 合同签订日期
     */
    @TableField("sign_time")
    private Date signTime;

    /**
     * 合同到期日期
     */
    @TableField("expire_time")
    private Date expireTime;

    /**
     * 合同附件地址
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 退住原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 确认办理人ID（关联user.id）
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 删除标志：0正常 1已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
