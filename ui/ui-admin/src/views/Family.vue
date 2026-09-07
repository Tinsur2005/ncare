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
  import familyApi from '@/api/family.js'
  import elderApi from '@/api/elder.js'
  import {computed, nextTick, ref} from 'vue'
  import {useRouter} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Plus, Delete, EditPen, View, Document} from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  import hasBtnPermission from "@/utils/btnPermission.js";
  const tokenStore = useTokenStore()
  const router = useRouter()

  // ========== 对象 ==========

  //分页信息和搜索条件
  const familyQuery = ref({
    name: '',
    realName: '',
    phone: '',
    status: '',
    page: 1,
    limit: 10
  })

  // 单个对象，用于存储当前操作的家属数据，在添加家属时用来临时保存填写的数据
  const family = ref({})

  // ============== 选项 ==============

  // 状态选项，用于搜索和新增/修改家属对话框中选择对应的状态
  const statusOptions = [
    {
      value: 0,
      label: '已停用',
    },
    {
      value: 1,
      label: '正常',
    },
  ]

  // 性别选项
  const genderOptions = [
    {
      value: 1,
      label: '男',
    },
    {
      value: 0,
      label: '女',
    },
  ]

  // 与老人的关系选项
  const relationOptions = [
    {
      value: '子女',
      label: '子女',
    },
    {
      value: '配偶',
      label: '配偶',
    },
    {
      value: '亲属',
      label: '亲属',
    },
    {
      value: '其他',
      label: '其他',
    },
  ]

  // ============== 变量 ==============

  // 标题，用于显示添加/修改了家属对话框的标题，例如“添加”、“编辑”
  const title = ref()

  //表格数据
  const list = ref([]) //表格List原始置为空
  const total = ref(0)

  //当前已经存在的所有老人组成的列表（远程搜索结果），初始化置为空List
  // 这个变量将在 loadElderOptions 方法中被赋值
  const elderOptions = ref([])

  //某个家属关联的老人id存到这个List，供添加/编辑抽屉中的"关联老人"下拉框使用，初始化置为空List
  // 打开添加/编辑抽屉时会被赋值
  const familyEldersList = ref([])

  // ========== 对话框dialog弹出控制 ==========
  const drawerDetailVisible = ref(false)  //弹出家属详情抽屉
  const drawerFamilyVisible = ref(false)  //弹出新增/编辑对话框dialog

  // 详情抽屉中的数据：当前查看的家属信息 + 该家属关联的老人列表
  const detailFamily = ref({})
  const detailElders = ref([])

  // ============== 方法 ==============
  // 显示家属详情抽屉：上面是家属信息，下面是关联老人的列表
  const showDetailDrawer = (raw) => {
    detailFamily.value = raw
    detailElders.value = [] //先清空上一次的残留，防止网络慢时显示旧数据
    familyApi.getEldersById(raw.id).then(result => {
      detailElders.value = result.data
    })
    drawerDetailVisible.value = true
  }

  // 查看合同：跳转到合同管理页面，并通过路由传参自动搜索该老人的合同（与Elder.vue的goContract一致，无合同则搜索结果为空）
  const goContract = (elder) => {
    router.push({path: '/contract', query: {elderId: elder.id}})
  }

  //远程搜索老人：根据真实姓名（可输入部分或全部）模糊搜索，供添加/编辑抽屉中"关联老人"下拉框使用
  const loadElderOptions = (query) => {
    elderApi.searchByName(query).then(result => {
      //合并时保留已关联但搜索结果里没有的老人选项，避免回显丢失
      const assigned = elderOptions.value.filter(o => familyEldersList.value.includes(o.id))
      elderOptions.value = [...assigned, ...result.data.filter(o => !assigned.some(e => e.id === o.id))]
    })
  }

  // 保存关联老人列表（修改的是elder-family中间表的数据），绑定逻辑与原来的关联老人抽屉保持一致
  const saveElders = (familyId) => {
    return familyApi.updateEldersById(familyId, familyEldersList.value)
  }


  //加载数据
  const loadData = () => {
    familyApi.list(familyQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    familyQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    familyQuery.value = {
      name: '',
      realName: '',
      phone: '',
      status: '',
      page: 1,
      limit: 10
    }
    loadData()
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
      familyApi.deleteById(id).then(result => {
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
    ids = rows.map(row => row.id)
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
      familyApi.deleteAll(ids).then(result => {
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
  const showAddDialog = () => {
    drawerFamilyVisible.value = true
    title.value = '添加'
    family.value = {}
    //新增时没有已关联的老人，清空上一次的残留并初始化下拉框选项
    elderOptions.value = []
    familyEldersList.value = []
    loadElderOptions('')
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerFamilyVisible.value = true
    title.value = '编辑'
    family.value = {}
    //编辑时初始化下拉框选项，并回显已关联的老人
    elderOptions.value = []
    familyEldersList.value = []
    loadElderOptions('')
    familyApi.getEldersById(id).then(result => {
      // 回显：把已关联的老人合并进选项，防止远程搜索下拉框里搜不到已关联的老人
      const assigned = result.data
      elderOptions.value = [...assigned, ...elderOptions.value.filter(o => !assigned.some(e => e.id === o.id))]
      familyEldersList.value = assigned.map(e => e.id)
    })
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    familyApi.selectById(id).then(result => {
      family.value = result.data
      //密码框不回显哈希值，留空表示不修改密码
      family.value.password = ''
    })
  }

  const formRef = ref()
  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (family.value.id) {
            // 编辑：密码留空由后端跳过更新，填了由后端BCrypt加密
            familyApi.update(family.value.id, family.value).then(result => {
              if (result.code === 1) {
                // 保存关联的老人（允许清空全部关联）
                saveElders(family.value.id).then(res => {
                  if (res.code === 1) {
                    ElMessage.success(result.msg)
                    drawerFamilyVisible.value = false
                    loadData()
                  } else {
                    ElMessage.error(res.msg)
                  }
                })
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {
            // 添加，后端返回新家属的id，用于新增后直接绑定老人
            familyApi.add(family.value).then(result => {
              if (result.code === 1) {
                if (familyEldersList.value.length > 0) {
                  // 有勾选老人时才保存关联
                  saveElders(result.data).then(res => {
                    if (res.code === 1) {
                      ElMessage.success(result.msg)
                      drawerFamilyVisible.value = false
                      loadData()
                    } else {
                      ElMessage.error(res.msg)
                    }
                  })
                } else {
                  ElMessage.success(result.msg)
                  drawerFamilyVisible.value = false
                  loadData()
                }
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

  //上传图片
  const handleAvatarSuccess = (result) => {
    family.value.avatar = result.data;
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

  //对话框dialog输入规则校验（编辑时密码不是必填，留空表示不修改密码）
  const dialogRules = computed(() => {
    return {
      name: [
        {required: true, message: '请输入用户名', trigger: 'blur'},
        {min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur'}
      ],
      password: [
        {required: !family.value.id, message: '请输入密码', trigger: 'blur'},
        {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
      ],
      status: [
        {required: true, message: '请选择状态', trigger: 'blur'}
      ],
      phone: [
        {required: true, message: '请输入手机号', trigger: 'blur'},
        {min: 11, max: 11, message: '手机号格式错误', trigger: 'blur'}
      ],
      realName: [
        {required: true, message: '请输入姓名', trigger: 'blur'},
        {min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur'}
      ],
    }
  })
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('family:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('family:deleteAll')">批量删除</el-button>
        </div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="用户名">
        <el-input v-model="familyQuery.name" placeholder="请输入用户名" clearable style="width: 160px"/>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="familyQuery.realName" placeholder="请输入姓名" clearable style="width: 160px"/>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="familyQuery.phone" placeholder="请输入手机号" clearable style="width: 160px"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="familyQuery.status" placeholder="请选择状态" clearable style="width: 130px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <!--表单-->
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="avatar" label="头像" width="70">
        <template #default="{row}">
          <el-avatar
              :src="row.avatar" style="max-height: 40px; max-width: 40px;"
          >{{ (row.realName || '?').slice(-1) }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="用户名" width="110" :show-overflow-tooltip="true"/>
      <el-table-column prop="realName" label="姓名" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="gender" label="性别" width="70">
        <template #default="{row}">
          <el-tag type="primary" v-if="row.gender === 1">男</el-tag>
          <el-tag type="danger" v-else-if="row.gender === 0">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="relation" label="关系" width="90" :show-overflow-tooltip="true"/>
      <el-table-column prop="phone" label="电话" :show-overflow-tooltip="true" width="115"/>
      <el-table-column label="关联老人" min-width="220">
        <template #default="{row}">
          <el-tag v-for="elder in row.elders" :key="elder.id" type="primary"
                  style="margin-right: 4px">
            {{ elder.realName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90" :resizable="false">
        <template #default="{row}">
          <el-tag type="info" v-if="row.status === 0">已停用</el-tag>
          <el-tag type="success" v-else-if="row.status === 1">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true" width="125"/>
      <el-table-column prop="createTime" label="创建时间" :show-overflow-tooltip="true" width="160"/>
      <el-table-column align="center" width="300px" fixed="right" label="操作" v-if="hasBtnPermission('family:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('family:update')">编辑</el-button>
          <el-button size="small" type="success" :icon="View" @click="showDetailDrawer(row)" v-if="hasBtnPermission('family:view')">查看</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('family:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="familyQuery.page"
        v-model:page-size="familyQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>

  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerFamilyVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="family" :rules="dialogRules">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/admin/api/upload?dir=avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="family.avatar" :src="family.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item prop="name" label="用户名" :label-width="80">
        <el-input v-model="family.name" autocomplete="off" :disabled="!!family.id"/>
      </el-form-item>
      <el-form-item prop="password" label="密码" :label-width="80">
        <el-input v-model="family.password" autocomplete="off" show-password type="password"
                  :placeholder="family.id ? '不输入即为不修改密码' : '请输入密码'"/>
      </el-form-item>
      <el-form-item prop="realName" label="姓名" :label-width="80">
        <el-input v-model="family.realName" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="gender" label="性别" :label-width="80">
        <el-select v-model="family.gender" placeholder="请选择性别" style="width: 220px">
          <el-option
              v-for="item in genderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="relation" label="关系" :label-width="80">
        <el-select v-model="family.relation" placeholder="请选择与老人的关系" style="width: 220px">
          <el-option
              v-for="item in relationOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="phone" label="手机号" :label-width="80">
        <el-input v-model="family.phone" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="关联老人" :label-width="80">
        <el-select v-model="familyEldersList"
                   multiple filterable remote :remote-method="loadElderOptions"
                   :reserve-keyword="false"
                   :loading="false" placeholder="输入老人姓名进行远程搜索"
                   style="width: 100%">
          <el-option
              v-for="elder in elderOptions"
              :key="elder.id"
              :label="elder.realName"
              :value="elder.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="family.status" placeholder="请选择状态" style="width: 220px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input v-model="family.remark" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerFamilyVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>

  <!-- 家属详情抽屉：上面是家属信息，下面是关联老人的列表 -->
  <el-drawer title="家属详情" v-model="drawerDetailVisible" size="50%" :close-on-click-modal="true">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="姓名" label-align="right" width="120">{{ detailFamily.realName }}</el-descriptions-item>
      <el-descriptions-item label="用户名" label-align="right" width="120">{{ detailFamily.name }}</el-descriptions-item>
      <el-descriptions-item label="性别" label-align="right">
        <span v-if="detailFamily.gender === 1">男</span>
        <span v-else-if="detailFamily.gender === 0">女</span>
      </el-descriptions-item>
      <el-descriptions-item label="关系" label-align="right">{{ detailFamily.relation }}</el-descriptions-item>
      <el-descriptions-item label="手机号" label-align="right">{{ detailFamily.phone }}</el-descriptions-item>
      <el-descriptions-item label="状态" label-align="right">
        <el-tag type="info" v-if="detailFamily.status === 0">已停用</el-tag>
        <el-tag type="success" v-else-if="detailFamily.status === 1">正常</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="备注" label-align="right">{{ detailFamily.remark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间" label-align="right">{{ detailFamily.createTime }}</el-descriptions-item>
    </el-descriptions>

    <p>关联老人</p>
    <el-table :data="detailElders" border style="width: 100%">
      <el-table-column prop="realName" label="老人姓名" :show-overflow-tooltip="true"/>
      <el-table-column prop="phone" label="手机号" :show-overflow-tooltip="true" width="130"/>
      <el-table-column prop="idCardNo" label="身份证号" :show-overflow-tooltip="true" width="180"/>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{row}">
          <el-tag type="info" v-if="row.status === 0">已停用</el-tag>
          <el-tag type="success" v-else-if="row.status === 1">正常</el-tag>
          <el-tag type="primary" v-else-if="row.status === 2">请假</el-tag>
          <el-tag type="danger" v-else-if="row.status === 3">退住中</el-tag>
          <el-tag type="warning" v-else-if="row.status === 4">入住中</el-tag>
          <el-tag type="info" v-else-if="row.status === 5">已退住</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" :show-overflow-tooltip="true" width="170"/>
      <!--该列里是"查看合同"按钮，跟随合同模块的操作栏权限，避免无权限时出现空操作列-->
      <el-table-column label="操作" align="center" width="150" v-if="hasBtnPermission('contract:operation')">
        <template #default="{row}">
          <el-button size="small" type="success" :icon="Document" @click="goContract(row)"
                     v-if="hasBtnPermission('contract:get')">查看合同</el-button>
        </template>
      </el-table-column>
    </el-table>
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