package cn.tinsur.elder.pojo.query;

import lombok.Data;

import java.util.Date;

@Data
public class CheckInRecordQuery {
    //办理类型：0入住 1退住
    private Integer type;
    //办理状态：0办理中 1已完成 2已取消
    private Integer status;
    private Long elderId;
    private Date beginCreateTime;
    private Date endCreateTime;
    private Integer page;
    private Integer limit;
}