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
package cn.tinsur.elder.service.impl.care;

import cn.tinsur.elder.mapper.CareLevelMapper;
import cn.tinsur.elder.pojo.entity.CareLevel;
import cn.tinsur.elder.pojo.query.CareLevelQuery;
import cn.tinsur.elder.service.ICareLevelService;
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
 * 护理等级表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class CareLevelServiceImpl extends ServiceImpl<CareLevelMapper, CareLevel> implements ICareLevelService {
    @Autowired
    private CareLevelMapper careLevelMapper;

    @Override
    public IPage<CareLevel> list(CareLevelQuery careLevelQuery) {
        IPage<CareLevel> page = new Page<>(careLevelQuery.getPage(), careLevelQuery.getLimit());
        LambdaQueryWrapper<CareLevel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(careLevelQuery.getName()), CareLevel::getName, careLevelQuery.getName())
                .eq(!ObjectUtils.isEmpty(careLevelQuery.getStatus()), CareLevel::getStatus, careLevelQuery.getStatus())
                .between(!ObjectUtils.isEmpty(careLevelQuery.getBeginCreateTime())
                                && !ObjectUtils.isEmpty(careLevelQuery.getEndCreateTime()),
                        CareLevel::getCreateTime, careLevelQuery.getBeginCreateTime(),
                        careLevelQuery.getEndCreateTime())
                .orderByAsc(CareLevel::getSort);
        return careLevelMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<CareLevel> listAll() {
        return careLevelMapper.selectList(new LambdaQueryWrapper<CareLevel>()
                .eq(CareLevel::getStatus, 1)
                .orderByAsc(CareLevel::getSort));
    }
}