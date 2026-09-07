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
  import elderApi from '@/api/elder.js'
  import tagsApi from '@/api/tags.js'
  import {computed, nextTick, ref} from 'vue'
  import {useRouter} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Plus, Download, Upload, Delete, EditPen, PriceTag} from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  import eldersApi from "@/api/elder.js";
  import hasBtnPermission from "@/utils/btnPermission.js";
  const tokenStore = useTokenStore()
  const router = useRouter()

  // ========== 对象 ==========

  //分页信息和搜索条件
  const elderQuery = ref({
    name: '',
    email: '',
    page: 1,
    limit: 10
  })

  // 单个对象，用于存储当前操作的老人数据，在添加老人时用来临时保存填写的数据
  const elder = ref({})

  // ============== 变量 ==============

  // 标题，用于显示添加/修改了老人对话框的标题，例如“添加老人”、“编辑老人”
  const title = ref()

  //表格数据
  const list = ref([]) //表格List原始置为空
  const total = ref(0)

  // 创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  //当前已经存在的所有标签组成的列表，初始化置为空List
  // 这个变量将在 getTagsList 方法中被赋值
  const tagsList = ref([])

  //某个老人的标签存到这个List，供弹出对话框使用，初始化置为空List
  // 这个变量将在 showAssignedTagDialog 方法中被赋值
  const elderTagsList = ref([])

  // 状态选项，用于新增/修改老人对话框中选择对应的状态
  const statusOptions = [
    {
      value: 0,
      label: '已停用',
    },
    {
      value: 1,
      label: '正常',
    },
    {
      value: 2,
      label: '请假',
    },
    {
      value: 3,
      label: '退住中',
    },
    {
      value: 4,
      label: '入住中',
    },
    {
      value: 5,
      label: '已退住',
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

  // ========== 对话框dalog弹出控制 ==========
  const drawerTagAssignVisible = ref(false)  //弹出标注对话框dialog
  const drawerElderVisible = ref(false)  //弹出新增/编辑对话框dialog


  // ============== 方法 ==============
  // 显示已分配的标签对话框
  const showAssignedTagDialog = (raw) => {
    tagsList.value = [] //先把tagsList清空，防止网络慢的时候遗漏旧数据在对话框中
    getTagsList()   // 执行getTagsList方法来初始化tagsList,确保tagsList正确读取到数据库中全部的标签
    elder.value = raw
    eldersApi.getTagsById(raw.id).then(result => {
      elderTagsList.value = result.data
    })
    drawerTagAssignVisible.value = true
  }

  //获取标签列表
  const getTagsList = () => {
    tagsApi.listAll().then(result => {
      tagsList.value = result.data
    })
  }

  // 保存标签列表，当标注标签对话框点击保存按钮时调用此方法
  const tagsSave = () => {
    elderApi.updateTagsById(elder.value.id, elderTagsList.value).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
        drawerTagAssignVisible.value = false
        loadData()
      } else {
        ElMessage.error(result.msg)
      }
    })

  }


  //加载数据
  const loadData = () => {
    elderQuery.value.beginCreateTime = createTimeRange.value?.[0]
    elderQuery.value.endCreateTime = createTimeRange.value?.[1]

    elderApi.list(elderQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    elderQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    elderQuery.value = {
      name: '',
      email: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    loadData()
  }

  //跳转到合同管理页面，并通过路由传参自动搜索该老人在合同页面查询
  const goContract = (row) => {
    let route = {
      path: '/contract',
      query: {elderId: row.id}
    }
    router.push(route)
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
      elderApi.deleteById(id).then(result => {
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
      elderApi.deleteAll(ids).then(result => {
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
    drawerElderVisible.value = true
    title.value = '添加'
    elder.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerElderVisible.value = true
    title.value = '编辑'
    elder.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    elderApi.selectById(id).then(result => {
      elder.value = result.data
      //密码框不回显哈希值，留空表示不修改密码
      elder.value.password = ''
    })
  }

  const formRef = ref()
  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (elder.value.id) {
            // 编辑
            elderApi.update(elder.value.id, elder.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerElderVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {
            // 添加
            elderApi.add(elder.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerElderVisible.value = false
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

  //把日期格式化为 YYYY-MM-DD ，给生日字段用
  const formatDate = (value) => {
    if (!value) return ''
    return String(value).slice(0, 10)
  }

  //上传图片
  const handleAvatarSuccess = (result) => {
    elder.value.avatar = result.data;
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
      elderApi.exportExcel().then((response) => {
        //从响应头 Content-Disposition 解析后端返回的文件名,后端做过 URLEncoder.encode,需要解码
        const disposition = response.headers['content-disposition'];
        let fileName = '老人信息.xlsx'; //兜底名
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

  //对话框dialog输入规则校验（编辑时密码不是必填，留空表示不修改密码）
  const dialogRules = computed(() => {
    return {
      name: [
        {required: true, message: '请输入用户名', trigger: 'blur'},
        {min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur'}
      ],
      password: [
        {required: !elder.value.id, message: '请输入密码', trigger: 'blur'},
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
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('elder:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('elder:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right">
          <el-button type="primary" :icon="Download" @click="exportExcel" v-if="hasBtnPermission('elder:export')">导出Excel</el-button>
          <el-upload
              :icon="Upload"
              class="inline-block"
              multiple=""
              method="post"
              action="/admin/api/elders/importExcel"
              style="display:inline-block;margin-left: 12px"
              accept=".xlsx,.xls"
              :show-file-list="false"
              :on-success="importExcelSuccess"
              :headers="{Authorization: tokenStore.token}"
              name="file">
            <el-button type="primary" :icon="Upload" v-if="hasBtnPermission('elder:import')">导入Excel</el-button>
          </el-upload>
        </div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="用户名">
        <el-input v-model="elderQuery.name" placeholder="请输入用户名" clearable style="width: 200px"/>
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

    <!--表单-->
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
      <el-table-column prop="realName" label="姓名" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="gender" label="性别" width="70">
        <template #default="{row}">
          <el-tag type="primary" v-if="row.gender === 1">男</el-tag>
          <el-tag type="danger" v-else-if="row.gender === 0">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" :show-overflow-tooltip="true" width="115"/>
      <el-table-column prop="status" label="状态" min-width="100" :resizable="false">
        <template #default="{row}">
          <el-tag type="info" v-if="row.status === 0">已停用</el-tag>
          <el-tag type="success" v-else-if="row.status === 1">正常</el-tag>
          <el-tag type="primary" v-else-if="row.status === 2">请假</el-tag>
          <el-tag type="danger" v-else-if="row.status === 3">退住中</el-tag>
          <el-tag type="warning" v-else-if="row.status === 4">入住中</el-tag>
          <el-tag type="info" v-else-if="row.status === 5">已退住</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="标签" min-width="300">
        <template #default="{row}">
          <el-tag v-for="tag in row.tags" :key="tag.id" type="primary"
                  style="margin-right: 4px">
            {{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="idCardNo" label="身份证号" :show-overflow-tooltip="true" width="175"/>
      <el-table-column prop="address" label="地址" :show-overflow-tooltip="true" width="300"/>
      <el-table-column prop="birthday" label="生日" :show-overflow-tooltip="true" width="125">
        <template #default="{row}">
          {{ formatDate(row.birthday) }}
        </template>
      </el-table-column>
      <el-table-column label="合同" width="100" align="center">
        <template #default="{row}">
          <el-link type="primary" @click="goContract(row)" v-if="hasBtnPermission('contract:get')">查看合同</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true" width="125"/>
      <el-table-column prop="createTime" label="创建时间" :show-overflow-tooltip="true" width="160"/>
      <el-table-column align="center" width="250px" fixed="right" label="操作" v-if="hasBtnPermission('elder:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('elder:update')">编辑</el-button>
          <el-button size="small" type="success" :icon="PriceTag" @click="showAssignedTagDialog(row)" v-if="hasBtnPermission('elder:tag')">标注</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('elder:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="elderQuery.page"
        v-model:page-size="elderQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>

  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerElderVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="elder" :rules="dialogRules">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/admin/api/upload?dir=avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="elder.avatar" :src="elder.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item prop="name" label="用户名" :label-width="80">
        <el-input v-model="elder.name" autocomplete="off" :disabled="!!elder.id"/>
      </el-form-item>
      <el-form-item prop="password" label="密码" :label-width="80">
        <el-input v-model="elder.password" autocomplete="off" show-password type="password"
                  :placeholder="elder.id ? '不输入即为不修改密码' : '请输入密码'"/>
      </el-form-item>
      <el-form-item prop="realName" label="姓名" :label-width="80">
        <el-input v-model="elder.realName" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="gender" label="性别" :label-width="80">
        <el-select v-model="elder.gender" placeholder="请选择性别" style="width: 220px">
          <el-option
              v-for="item in genderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="phone" label="手机号" :label-width="80">
        <el-input v-model="elder.phone" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="身份证号" :label-width="80">
        <el-input v-model="elder.idCardNo" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="地址" :label-width="80">
        <el-input v-model="elder.address" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="生日" :label-width="80">
        <el-date-picker v-model="elder.birthday" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择生日"/>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="elder.status" placeholder="请选择状态" style="width: 220px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerElderVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>

  <!-- 标注标签弹出对话框dialog -->
  <el-drawer title="标签标注" v-model="drawerTagAssignVisible" size="35%" :close-on-click-modal="true">
    <el-form ref="form" :model="elder" label-width="80px">
      <el-form-item label="姓名">
        <el-input v-model="elder.realName" disabled></el-input>
      </el-form-item>
      <el-form-item label="标注列表">
        <el-checkbox-group v-model="elderTagsList">
          <el-checkbox v-for="tag in tagsList" :key="tag.id" :label="tag.id">{{tag.name}}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="tagsSave">保存</el-button>
        <el-button  @click="drawerTagAssignVisible = false">取消</el-button>
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