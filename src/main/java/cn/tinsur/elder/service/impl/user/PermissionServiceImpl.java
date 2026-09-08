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
package cn.tinsur.elder.service.impl.user;

import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.mapper.PermissionMapper;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.service.IPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionVO> selectPermissionTree() {
        //1.查找所有的权限、按sort字段排序
        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Permission::getSort);
        List<Permission> permissionList = permissionMapper.selectList(lambdaQueryWrapper);

        //2.把Permission对象转换为PermissionVO对象，便于插入ChildrenList
        List<PermissionVO> permissionVOList = permissionList.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).toList();

        //3.构建权限树形结构
            //所有一级权限
        List<PermissionVO> permissionVOTree = permissionVOList.stream()
                .filter(permissionVO -> permissionVO.getParentId() == 0)
                .map( permissionVO -> {
                    //构建children
                    permissionVO.setChildren(buildChildrenTree(permissionVO, permissionVOList));
                    return permissionVO;
                })
                .toList();
        return permissionVOTree;
    }

    /**
     * 给定任何一个List<PermissionVO>，都可以返回一个树形结构
     *
     * @param permissionVOList
     * @return
     */
    public List<PermissionVO> buildTree(List<PermissionVO> permissionVOList) {
        //所有一级分类
        List<PermissionVO> permissionVOTree = permissionVOList.stream()
                .filter(permissionVO -> permissionVO.getParentId() == 0)
                .map(permissionVO -> {
                    permissionVO.setChildren(buildChildrenTree(permissionVO, permissionVOList)); // 构建children
                    return permissionVO;
                }).toList();
        return permissionVOTree;
    }

    /**
     * 构建子节点树，需要传入父节点和所有节点列表
     * @param parentPermissionVO 父节点
     * @param permissionVOList 子节点
     * @return
     */
    private List<PermissionVO> buildChildrenTree(PermissionVO parentPermissionVO, List<PermissionVO> permissionVOList){
        // 这里必须用equals比较：id和parentId是Long包装类型，超过127后==比较的是对象引用而不是值
        return permissionVOList.stream()
                .filter(permissionVO -> parentPermissionVO.getId().equals(permissionVO.getParentId()))
                .map(permissionVO -> {
                    permissionVO.setChildren(buildChildrenTree(permissionVO, permissionVOList)); //继续递归children
                    return permissionVO;
                })
                .toList();
    }
}
