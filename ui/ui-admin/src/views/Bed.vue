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
  import bedApi from '@/api/bed.js'
  import buildingApi from '@/api/building.js'
  import floorApi from '@/api/floor.js'
  import roomApi from '@/api/room.js'
  import elderApi from '@/api/elder.js'
  import {computed, nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个床位对象，在添加/编辑时临时保存填写的数据
  const bed = ref({})

  // ================== 选项 ==================

  // 床位状态选项（0空闲 1已占用 2维修中），type为el-tag的type
  const statusOptions = [
    {value: 0, label: '空闲', type: 'success'},
    {value: 1, label: '已占用', type: 'warning'},
    {value: 2, label: '维修中', type: 'info'},
  ]

  // ================== 下拉数据 ==================

  // 楼栋选项，供搜索下拉和编辑下拉使用
  const buildingOptions = ref([])
  // 全部楼层列表，供搜索下拉和编辑下拉按楼栋过滤使用
  const floorAll = ref([])
  // 全部房间列表，供搜索下拉和编辑下拉按楼层过滤使用
  const roomAll = ref([])

  //加载楼栋、楼层、房间的下拉数据
  const loadOptions = () => {
    buildingApi.listAll().then(result => {
      buildingOptions.value = result.data
    })
    floorApi.listAll().then(result => {
      floorAll.value = result.data
    })
    roomApi.listAll().then(result => {
      roomAll.value = result.data
    })
  }
  loadOptions()

  // 老人远程搜索：存放远程搜索出来的可选老人列表，供下拉框展示"姓名（身份证号）"
  const elderOptions = ref([])
  // 是否正在加载远程搜索结果（控制下拉框的loading转圈）
  const elderLoading = ref(false)
  // 远程搜索方法：根据用户输入的老人姓名（可部分可全部）去后端模糊搜索老人
  const loadElderOptions = (query) => {
    // 没有输入内容时不搜索，直接清空列表
    if (!query) {
      elderOptions.value = []
      return
    }
    elderLoading.value = true
    elderApi.searchByName(query).then(result => {
      elderOptions.value = result.data
    }).finally(() => {
      elderLoading.value = false
    })
  }

  //搜索栏的楼层选项，跟随搜索栏选中的楼栋过滤
  const searchFloorOptions = computed(() => {
    return floorAll.value.filter(item => !bedQuery.value.buildingId || item.buildingId === bedQuery.value.buildingId)
  })

  //搜索栏的房间选项，跟随搜索栏选中的楼层过滤
  const searchRoomOptions = computed(() => {
    return roomAll.value.filter(item => !bedQuery.value.floorId || item.floorId === bedQuery.value.floorId)
  })

  //编辑窗口的楼层选项，跟随编辑窗口选中的楼栋过滤
  const drawerFloorOptions = computed(() => {
    return floorAll.value.filter(item => !bed.value.buildingId || item.buildingId === bed.value.buildingId)
  })

  //编辑窗口的房间选项，跟随编辑窗口选中的楼层过滤
  const drawerRoomOptions = computed(() => {
    return roomAll.value.filter(item => !bed.value.floorId || item.floorId === bed.value.floorId)
  })

  // ================== 变量 ==================

  //分页信息和搜索条件（按楼栋楼层房间和状态筛选）
  const bedQuery = ref({
    buildingId: '',
    floorId: '',
    roomId: '',
    status: '',
    page: 1,
    limit: 10
  })

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerBedVisible = ref(false)

  // ================== 方法 ==================

  //根据选项获取展示文本
  const getLabel = (options, value) => {
    return options.find(option => option.value === value)?.label || '-'
  }

  //根据选项获取el-tag的type
  const getTagType = (options, value) => {
    return options.find(option => option.value === value)?.type || 'info'
  }

  //加载数据
  const loadData = () => {
    bedApi.list(bedQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    bedQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    bedQuery.value = {
      buildingId: '',
      floorId: '',
      roomId: '',
      status: '',
      page: 1,
      limit: 10
    }
    loadData()
  }

  //搜索栏切换楼栋后清空已选楼层和房间，避免级联选项对不上
  const onSearchBuildingChange = () => {
    bedQuery.value.floorId = ''
    bedQuery.value.roomId = ''
  }

  //搜索栏切换楼层后清空已选房间，避免房间和楼层对不上
  const onSearchFloorChange = () => {
    bedQuery.value.roomId = ''
  }

  //编辑窗口切换楼栋后清空已选楼层和房间，避免级联选项对不上
  const onDrawerBuildingChange = () => {
    bed.value.floorId = ''
    bed.value.roomId = ''
  }

  //编辑窗口切换楼层后清空已选房间，避免房间和楼层对不上
  const onDrawerFloorChange = () => {
    bed.value.roomId = ''
  }

  //编辑窗口切换状态后：改成空闲或维修时清空已选老人，避免非占用床位还挂着老人
  const onStatusChange = () => {
    if (bed.value.status !== 1) {
      bed.value.elderId = ''
      nextTick(() => {
        formRef.value?.clearValidate('elderId')
      })
    }
  }

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除么?床位已被占用时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      bedApi.deleteById(id).then(result => {
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
    if (ids.length === 0) {
      ElMessage.error('请选择要删除的记录')
      return
    }
    ElMessageBox.confirm(
        '您确认要删除么?床位已被占用时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      bedApi.deleteAll(ids).then(result => {
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
    drawerBedVisible.value = true
    title.value = '添加'
    bed.value = {status: 0} //状态默认空闲
    elderOptions.value = [] //清空老人搜索结果
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerBedVisible.value = true
    title.value = '编辑'
    bed.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    bedApi.selectById(id).then(result => {
      bed.value = result.data
      //编辑时回显当前入住的老人：把该老人生成一个可显示的下拉选项，这样下拉框才能显示出"姓名（身份证号）"，而不是只显示一个数字id
      if (bed.value.elderId) {
        elderApi.selectById(bed.value.elderId).then(res => {
          if (res.code === 1) {
            elderOptions.value = [res.data]
          }
        })
      }
    })
  }

  const formRef = ref()

  //入住老人校验：状态为已占用时必须选择老人，空闲和维修时不需要
  const validateElderId = (rule, value, callback) => {
    if (bed.value.status === 1 && !value) {
      callback(new Error('床位状态为已占用时必须选择入住老人'))
    } else {
      callback()
    }
  }

  //对话框dialog输入规则校验
  const dialogRules = {
    buildingId: [
      {required: true, message: '请选择所属楼栋', trigger: 'change'}
    ],
    floorId: [
      {required: true, message: '请选择所属楼层', trigger: 'change'}
    ],
    roomId: [
      {required: true, message: '请选择所属房间', trigger: 'change'}
    ],
    bedNo: [
      {required: true, message: '请输入床位号', trigger: 'blur'},
      {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
    ],
    elderId: [
      {validator: validateElderId, trigger: 'change'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (bed.value.id) {//编辑
            bedApi.update(bed.value.id, bed.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerBedVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            bedApi.add(bed.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerBedVisible.value = false
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
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('bed:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('bed:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="所属楼栋">
        <el-select v-model="bedQuery.buildingId" placeholder="全部" clearable style="width: 140px" @change="onSearchBuildingChange">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层">
        <el-select v-model="bedQuery.floorId" placeholder="请先选择楼栋" :disabled="!bedQuery.buildingId" clearable style="width: 140px" @change="onSearchFloorChange">
          <el-option
              v-for="item in searchFloorOptions"
              :key="item.id"
              :label="item.floorNo + '层'"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属房间">
        <el-select v-model="bedQuery.roomId" placeholder="请先选择楼层" :disabled="!bedQuery.floorId" clearable style="width: 140px">
          <el-option
              v-for="item in searchRoomOptions"
              :key="item.id"
              :label="item.roomNo"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="bedQuery.status" placeholder="全部" clearable style="width: 140px">
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
      <el-table-column prop="buildingName" label="所属楼栋" width="140" :show-overflow-tooltip="true"/>
      <el-table-column label="所属楼层" width="90" align="center">
        <template #default="{ row }">
          <span>{{ row.floorNo }}层</span>
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="所属房间" width="90" align="center"/>
      <el-table-column prop="bedNo" label="床位号" width="90" align="center"/>
      <el-table-column prop="monthlyPrice" label="床位费" width="100" align="center">
        <template #default="{ row }">
          <span>￥{{ row.monthlyPrice }}/月</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getTagType(statusOptions, row.status)">{{ getLabel(statusOptions, row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="elderName" label="入住老人" width="100" align="center">
        <template #default="{row}">
          {{ row.elderName || '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('bed:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('bed:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('bed:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="bedQuery.page"
        v-model:page-size="bedQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerBedVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="bed" :rules="dialogRules">
      <el-form-item prop="buildingId" label="所属楼栋" :label-width="80">
        <el-select v-model="bed.buildingId" placeholder="请选择楼栋" style="width: 220px" @change="onDrawerBuildingChange">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="floorId" label="所属楼层" :label-width="80">
        <el-select v-model="bed.floorId" placeholder="请先选择楼栋" style="width: 220px" @change="onDrawerFloorChange">
          <el-option
              v-for="item in drawerFloorOptions"
              :key="item.id"
              :label="item.floorNo + '层'"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="roomId" label="所属房间" :label-width="80">
        <el-select v-model="bed.roomId" placeholder="请先选择楼层" style="width: 220px">
          <el-option
              v-for="item in drawerRoomOptions"
              :key="item.id"
              :label="item.roomNo"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="bedNo" label="床位号" :label-width="80">
        <el-input v-model="bed.bedNo" autocomplete="off" maxlength="20" placeholder="例如：1床"/>
      </el-form-item>
      <el-form-item label="床位费" :label-width="80">
        <el-input-number
            v-model="bed.monthlyPrice"
            :min="0"
            :precision="2"
            :step="10"
            controls-position="right"
            style="width: 220px"
        />
        <div class="price-tips">元/月</div>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="bed.status" placeholder="请选择状态" style="width: 220px" @change="onStatusChange">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <!--入住老人：状态为已占用时必填，远程搜索老人姓名选择-->
      <el-form-item prop="elderId" label="入住老人" :label-width="80" v-if="bed.status === 1">
        <el-select
            v-model="bed.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名搜索"
            :remote-method="loadElderOptions"
            :loading="elderLoading"
            style="width: 220px">
          <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="`${item.realName}（${item.idCardNo}）`"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input
            v-model="bed.remark"
            autocomplete="off"
            type="textarea"
            :rows="3"
            maxlength="255"
            show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerBedVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
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

  .price-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 10px;    /* 与输入框保持间距 */
  }
</style>