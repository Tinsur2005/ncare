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
  import roomApi from '@/api/room.js'
  import buildingApi from '@/api/building.js'
  import floorApi from '@/api/floor.js'
  import {computed, nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个房间对象，在添加/编辑时临时保存填写的数据
  const room = ref({})

  // ================== 下拉数据 ==================

  // 楼栋选项，供搜索下拉和编辑下拉使用
  const buildingOptions = ref([])
  // 全部楼层列表，供搜索下拉和编辑下拉按楼栋过滤使用
  const floorAll = ref([])

  //加载楼栋和楼层的下拉数据
  const loadOptions = () => {
    buildingApi.listAll().then(result => {
      buildingOptions.value = result.data
    })
    floorApi.listAll().then(result => {
      floorAll.value = result.data
    })
  }
  loadOptions()

  //搜索栏的楼层选项，跟随搜索栏选中的楼栋过滤
  const searchFloorOptions = computed(() => {
    return floorAll.value.filter(item => !roomQuery.value.buildingId || item.buildingId === roomQuery.value.buildingId)
  })

  //编辑窗口的楼层选项，跟随编辑窗口选中的楼栋过滤
  const drawerFloorOptions = computed(() => {
    return floorAll.value.filter(item => !room.value.buildingId || item.buildingId === room.value.buildingId)
  })

  // ================== 变量 ==================

  //分页信息和搜索条件（按楼栋、楼层筛选）
  const roomQuery = ref({
    buildingId: '',
    floorId: '',
    page: 1,
    limit: 10
  })

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerRoomVisible = ref(false)

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    roomApi.list(roomQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    roomQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    roomQuery.value = {
      buildingId: '',
      floorId: '',
      page: 1,
      limit: 10
    }
    loadData()
  }

  //搜索栏切换楼栋后清空已选楼层，避免楼层和楼栋对不上
  const onSearchBuildingChange = () => {
    roomQuery.value.floorId = ''
  }

  //编辑窗口切换楼栋后清空已选楼层，避免楼层和楼栋对不上
  const onDrawerBuildingChange = () => {
    room.value.floorId = ''
  }

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除么?房间下存在床位时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      roomApi.deleteById(id).then(result => {
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
        '您确认要删除么?房间下存在床位时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      roomApi.deleteAll(ids).then(result => {
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
    drawerRoomVisible.value = true
    title.value = '添加'
    room.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerRoomVisible.value = true
    title.value = '编辑'
    room.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    roomApi.selectById(id).then(result => {
      room.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    buildingId: [
      {required: true, message: '请选择所属楼栋', trigger: 'change'}
    ],
    floorId: [
      {required: true, message: '请选择所属楼层', trigger: 'change'}
    ],
    roomNo: [
      {required: true, message: '请输入房间号', trigger: 'blur'},
      {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (room.value.id) {//编辑
            roomApi.update(room.value.id, room.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerRoomVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            roomApi.add(room.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerRoomVisible.value = false
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
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('room:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('room:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="所属楼栋">
        <el-select v-model="roomQuery.buildingId" placeholder="全部" clearable style="width: 160px" @change="onSearchBuildingChange">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层">
        <el-select v-model="roomQuery.floorId" placeholder="请先选择楼栋" :disabled="!roomQuery.buildingId" clearable style="width: 160px">
          <el-option
              v-for="item in searchFloorOptions"
              :key="item.id"
              :label="item.floorNo + '层'"
              :value="item.id"
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
      <el-table-column prop="buildingName" label="所属楼栋" width="160" :show-overflow-tooltip="true"/>
      <el-table-column label="所属楼层" width="100" align="center">
        <template #default="{ row }">
          <span>{{ row.floorNo }}层</span>
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="房间号" width="120" align="center"/>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('room:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('room:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('room:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="roomQuery.page"
        v-model:page-size="roomQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerRoomVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="room" :rules="dialogRules">
      <el-form-item prop="buildingId" label="所属楼栋" :label-width="80">
        <el-select v-model="room.buildingId" placeholder="请选择楼栋" style="width: 220px" @change="onDrawerBuildingChange">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="floorId" label="所属楼层" :label-width="80">
        <el-select v-model="room.floorId" placeholder="请先选择楼栋" style="width: 220px">
          <el-option
              v-for="item in drawerFloorOptions"
              :key="item.id"
              :label="item.floorNo + '层'"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="roomNo" label="房间号" :label-width="80">
        <el-input v-model="room.roomNo" autocomplete="off" maxlength="20"/>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input
            v-model="room.remark"
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
        <el-button @click="drawerRoomVisible = false">取消</el-button>
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
</style>