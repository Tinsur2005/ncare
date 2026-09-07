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
  import floorApi from '@/api/floor.js'
  import buildingApi from '@/api/building.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个楼层对象，在添加/编辑时临时保存填写的数据
  const floor = ref({})

  // ================== 下拉数据 ==================

  // 楼栋选项，供搜索下拉和编辑下拉使用
  const buildingOptions = ref([])

  //加载楼栋下拉数据
  const loadBuildingOptions = () => {
    buildingApi.listAll().then(result => {
      buildingOptions.value = result.data
    })
  }
  loadBuildingOptions()

  // ================== 变量 ==================

  //分页信息和搜索条件（按楼栋筛选）
  const floorQuery = ref({
    buildingId: '',
    page: 1,
    limit: 10
  })

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerFloorVisible = ref(false)

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    floorApi.list(floorQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    floorQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    floorQuery.value = {
      buildingId: '',
      page: 1,
      limit: 10
    }
    loadData()
  }

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除么?楼层下存在房间时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      floorApi.deleteById(id).then(result => {
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
        '您确认要删除么?楼层下存在房间时不允许删除',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      floorApi.deleteAll(ids).then(result => {
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
    drawerFloorVisible.value = true
    title.value = '添加'
    floor.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerFloorVisible.value = true
    title.value = '编辑'
    floor.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    floorApi.selectById(id).then(result => {
      floor.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    buildingId: [
      {required: true, message: '请选择所属楼栋', trigger: 'change'}
    ],
    floorNo: [
      {required: true, message: '请输入楼层号', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (floor.value.id) {//编辑
            floorApi.update(floor.value.id, floor.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerFloorVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            floorApi.add(floor.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerFloorVisible.value = false
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
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('floor:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('floor:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="所属楼栋">
        <el-select v-model="floorQuery.buildingId" placeholder="全部" clearable style="width: 180px">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
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
      <el-table-column prop="buildingName" label="所属楼栋" width="200" :show-overflow-tooltip="true"/>
      <el-table-column label="楼层号" width="120" align="center">
        <template #default="{ row }">
          <span>{{ row.floorNo }}层</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('floor:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('floor:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('floor:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="floorQuery.page"
        v-model:page-size="floorQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerFloorVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="floor" :rules="dialogRules">
      <el-form-item prop="buildingId" label="所属楼栋" :label-width="80">
        <el-select v-model="floor.buildingId" placeholder="请选择楼栋" style="width: 220px">
          <el-option
              v-for="item in buildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="floorNo" label="楼层号" :label-width="80">
        <el-input-number
            v-model="floor.floorNo"
            :min="1"
            :max="99"
            controls-position="right"
        />
        <div class="sort-tips">展示为N层</div>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input
            v-model="floor.remark"
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
        <el-button @click="drawerFloorVisible = false">取消</el-button>
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

  .sort-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 10px;    /* 与输入框保持间距 */
  }
</style>