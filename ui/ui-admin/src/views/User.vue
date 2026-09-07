<!--
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
-->
<script setup>
  import userApi from '@/api/user.js'
  import {computed, nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {
    Delete,
    Edit,
    Upload,
    Download,
    Plus, EditPen, User
  } from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  import rolesApi from "@/api/roles.js";
  import hasBtnPermission from "@/utils/btnPermission.js";
  const tokenStore = useTokenStore()

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const userQuery = ref({
    name: '',
    email: '',
    page: 1,
    limit: 10
  })

  /*function loadData() {
      userApi.list(userQuery.value).then(result => {
          list.value = result.data.records
          total.value = result.data.total
      })
  }*/
  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    userQuery.value.beginCreateTime = createTimeRange.value?.[0]
    userQuery.value.endCreateTime = createTimeRange.value?.[1]

    userApi.list(userQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    userQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    userQuery.value = {
      name: '',
      email: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    loadData()
  }

  //根据id更新状态（0：停用，1：正常）
  const handleSwitchChange = (row) => {
    userApi.update(row.id, row).then(result => {
      if (result.code === 1) {
        if(row.status === 1) {
          ElMessage.success("已启用")
        } else {
          ElMessage.info("已禁用")
        }
      } else {
        ElMessage.error(result.msg)
        loadData() //失败则重新加载还原
      }
    })
  }

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除么?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      userApi.deleteById(id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  let ids = []
  const handleSelectionChange = (rows) => {
    //console.log('多选', rows)
    ids = rows.map(row => row.id)
    console.log(ids)
  }

  const deleteAll = () => {
    if(ids.length === 0){
      ElMessage.error('请选择要删除的记录')
      return
    }
    ElMessageBox.confirm(
        '您确认要删除么?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      userApi.deleteAll(ids).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }


  //添加、编辑
  const drawerUserVisible = ref(false)
  const user = ref({})
  const title = ref()

  const showAddDialog = () => {
    drawerUserVisible.value = true
    title.value = '添加'
    user.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerUserVisible.value = true
    title.value = '编辑'
    user.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    userApi.selectById(id).then(result => {
      user.value = result.data
      //密码框不回显哈希值，留空表示不修改密码
      user.value.password = ''
    })
  }

  const formRef = ref()
  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (user.value.id) {//编辑
            userApi.update(user.value.id, user.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerUserVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            userApi.add(user.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerUserVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          }
        })
        .catch(() => {
          //校验失败
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //对话框dialog输入规则校验（编辑时密码不是必填，留空表示不修改密码）
  const dialogRules = computed(() => {
    return {
      name: [
        {required: true, message: '请输入用户名', trigger: 'blur'},
        {min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur'}
      ],
      password: [
        {required: !user.value.id, message: '请输入密码', trigger: 'blur'},
        {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
      ],
      realName: [
        {required: true, message: '请输入姓名', trigger: 'blur'}
      ],
      email: [
        {type: 'email', message: '邮箱格式错误', trigger: 'blur'}
      ],
      phone: [
        {min: 11, max: 11, message: '手机号格式错误', trigger: 'blur'}
      ]
    }
  })

  //上传图片
  const handleAvatarSuccess = (result) => {
    user.value.avatar = result.data;
  }
  //上传时校验头像的文件格式
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  const beforeAvatarUpload = (rawFile) => {
    if (!allowedTypes.includes(rawFile.type)) {
      ElMessage.error('不支持的文件格式')
      return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
      ElMessage.error('上传的文件大小不允许超过2MB')
      return false
    }
    return true
  }

  //Excel导出
  const exportExcel = () => {
    ElMessageBox.confirm(
        '您确认要导出吗Excel吗？',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'info',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      userApi.exportExcel().then((response) => {
        //从响应头 Content-Disposition 解析后端返回的文件名,后端做过 URLEncoder.encode,需要解码
        const disposition = response.headers['content-disposition'];
        let fileName = '用户信息.xlsx'; //兜底名
        if (disposition) {
          fileName = decodeURIComponent(disposition.split('filename=')[1]);
        }
        //responseType 为 blob 时 result.data 本身就是 Blob,直接用即可
        //封装的Axios类里面针对responseType 设置为 blob 的响应数据直接返回全部的response，不再返回response.data，这里直接使用 response.data
        let url = window.URL.createObjectURL(response.data);
        const link = document.createElement("a"); // 创建a标签
        link.href = url;
        link.download = fileName; // 使用后端返回的文件名
        link.click();
        URL.revokeObjectURL(url);
      });
    })
  }

  //导入Excel成功后调用此处
  const importExcelSuccess = (result) => {
    if (result.code ==1) {
      ElMessage.success(result.msg)
      loadData()
    } else {
      ElMessage.error(result.msg)
    }
  }

  //角色设置
  const drawerRoleVisible = ref(false)
  const userRolesList = ref([]) // 选中的用户的角色列表
  const rolesList = ref([]) // 所有角色列表
  const showRolesDialog = (id) => {
    drawerRoleVisible.value = true
    user.value = {}
    userApi.selectById(id).then(result => {
      user.value = result.data
    })
    rolesApi.listAll().then(result => {
      rolesList.value = result.data
    })
    userApi.getRolesById(id).then(result => {
      userRolesList.value = result.data
    })
  }
  // 保存角色列表，当角色设置对话框点击保存按钮时调用此方法
  const rolesSave = () => {
    userApi.updateRolesById(user.value.id, userRolesList.value).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
        drawerRoleVisible.value = false
        loadData()
      } else {
        ElMessage.error(result.msg)
      }
    })
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('user:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('user:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right">
          <el-button type="primary" :icon="Download" @click="exportExcel" v-if="hasBtnPermission('user:export')">导出Excel</el-button>
          <el-upload
              :icon="Upload"
              class="inline-block"
              multiple=""
              method="post"
              action="/admin/api/users/importExcel"
              style="display:inline-block;margin-left: 12px"
              accept=".xlsx,.xls"
              :show-file-list="false"
              :on-success="importExcelSuccess"
              :headers="{Authorization: tokenStore.token}"
              name="file">
            <el-button type="primary" :icon="Upload" v-if="hasBtnPermission('user:import')">导入Excel</el-button>
          </el-upload>
        </div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="用户名">
        <el-input v-model="userQuery.name" placeholder="请输入用户名" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="userQuery.email" placeholder="请输入邮箱" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="创建时间">
      <el-date-picker
          v-model="createTimeRange"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
      />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <!--<el-table-column fixed prop="id" label="ID"/>-->
      <el-table-column prop="avatar" label="头像" width="70">
        <template #default="{row}">
          <el-avatar
              :src="row.avatar" style="max-height: 40px; max-width: 40px;"
          >{{ (row.realName || '?').slice(-1) }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="用户名" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="realName" label="姓名" width="100" :show-overflow-tooltip="true"/>
      <!--<el-table-column prop="password" label="密码"/>-->
      <el-table-column label="角色" min-width="50">
        <template #default="{row}">
          <el-tag v-for="role in row.roles" :key="role.id" type="primary"
                  style="margin-right: 4px">
            {{ role.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" :show-overflow-tooltip="true"/>
      <el-table-column prop="email" label="邮箱" :show-overflow-tooltip="true"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              active-text="已启用"
              inactive-text="已禁用"
              @change="handleSwitchChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column align="center" width="250px" fixed="right" label="操作" v-if="hasBtnPermission('user:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('user:update')">编辑</el-button>
          <el-button size="small" type="success" :icon="User" @click="showRolesDialog(row.id)">角色</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('user:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="userQuery.page"
        v-model:page-size="userQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerUserVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="user" :rules="dialogRules">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/admin/api/upload?dir=avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="user.avatar" :src="user.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item prop="name" label="用户名" :label-width="80">
        <el-input v-model="user.name" autocomplete="off" :disabled="user.id"/>
      </el-form-item>
      <el-form-item prop="password" label="密码" :label-width="80">
        <el-input v-model="user.password" autocomplete="off" show-password="true" type="password"
                  :placeholder="user.id ? '不输入即为不修改密码' : '请输入密码'"/>
      </el-form-item>
      <el-form-item prop="realName" label="姓名" :label-width="80">
        <el-input v-model="user.realName" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="email" label="邮箱" :label-width="80">
        <el-input v-model="user.email" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="phone" label="手机号" :label-width="80">
        <el-input v-model="user.phone" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerUserVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>


  <!-- 标角色编辑弹出对话框dialog -->
  <el-drawer title="角色设置" v-model="drawerRoleVisible" size="35%" :close-on-click-modal="true">
    <el-form ref="form" :model="user" label-width="80px">
      <el-form-item label="姓名">
        <el-input v-model="user.realName" disabled></el-input>
      </el-form-item>
      <el-form-item label="角色列表">
        <el-checkbox-group v-model="userRolesList">
          <el-checkbox v-for="role in rolesList" :key="role.id" :label="role.id">{{role.name}}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="rolesSave">保存</el-button>
        <el-button  @click="drawerRoleVisible = false">取消</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>


<style scoped>
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left,
  .header-right {
    display: flex;
    align-items: center;
  }

  .avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }

  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
  }

  .avatar-uploader-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
  }

</style>