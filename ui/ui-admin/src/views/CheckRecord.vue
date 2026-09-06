<!--
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║██████╔╝██║  ██║
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
  import checkInApi from '@/api/checkIn.js'
  import elderApi from '@/api/elder.js'
  import {ref} from 'vue'
  import {useRouter} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, Document, EditPen, SwitchButton} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";
  const router = useRouter()

  // ================== 选项 ==================

  // 办理类型选项（0入住 1退住）
  const typeOptions = [
    {value: 0, label: '入住'},
    {value: 1, label: '退住'},
  ]

  // 办理状态选项（0办理中 1已完成 2已取消）
  const statusOptions = [
    {value: 0, label: '办理中'},
    {value: 1, label: '已完成'},
    {value: 2, label: '已取消'},
  ]

  // 根据办理类型返回类型标签的展示名
  const getTypeLabel = (type) => (typeOptions.find(item => item.value === type) || {}).label || '—'

  // 根据办理状态返回状态标签的展示名和颜色
  const getStatusTag = (status) => {
    if (status === 0) return {label: '办理中', type: 'warning'}
    if (status === 1) return {label: '已完成', type: 'success'}
    return {label: '已取消', type: 'info'}
  }

  // 老人远程搜索：存放远程搜索出来的可选老人列表，供下拉框展示"姓名（身份证号）"
  const elderOptions = ref([])
  const elderLoading = ref(false)
  const loadElderOptions = (query) => {
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

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //详情抽屉里展示的办理单对象
  const detail = ref({})
  //详情抽屉的弹出控制
  const drawerDetailVisible = ref(false)

  // ================== 变量 ==================

  //分页信息和搜索条件（按类型、状态、老人、时间范围筛选）
  const checkInRecordQuery = ref({
    type: '',
    status: '',
    elderId: '',
    page: 1,
    limit: 10
  })

  //创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    checkInRecordQuery.value.beginCreateTime = createTimeRange.value?.[0]
    checkInRecordQuery.value.endCreateTime = createTimeRange.value?.[1]

    checkInApi.list(checkInRecordQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    checkInRecordQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    checkInRecordQuery.value = {
      type: '',
      status: '',
      elderId: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    loadData()
  }

  //继续办理：办理中的单子跳转到对应流程页续办
  const continueProcess = (row) => {
    if (row.type === 0) {
      router.push({path: '/checkInProcess', query: {id: row.id}})
    } else {
      router.push({path: '/checkOutProcess', query: {id: row.id}})
    }
  }

  //取消办理：办理中的单子取消后作废，退住单会同时把老人恢复为入住中
  const cancelRecord = (row) => {
    ElMessageBox.confirm(
        row.type === 0 ? '取消后该入住办理单作废，老人将被改为已停用状态，确认取消么？' : '取消后该退住办理单作废，老人恢复为正常状态，确认取消么？',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '返回',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      checkInApi.cancel(row.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //查看详情：打开抽屉回显办理单的全部信息
  const showDetail = (row) => {
    checkInApi.selectById(row.id).then(result => {
      if (result.code === 1) {
        detail.value = result.data
        drawerDetailVisible.value = true
      } else {
        ElMessage.error(result.msg)
      }
    })
  }

  //日期展示：后端返回yyyy-MM-dd HH:mm:ss，日期字段只取前10位
  const formatDate = (date) => date ? String(date).substring(0, 10) : '—'

  //床位位置展示：楼栋+楼层+房间+床位
  const formatBed = (row) => row.buildingName ? `${row.buildingName} ${row.floorNo}层 ${row.roomNo}房 ${row.bedNo}` : '—'

  //根据id删除（仅已完成或已取消的办理单允许删除）
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
      checkInApi.deleteById(id).then(result => {
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
        '您确认要删除么?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      checkInApi.deleteAll(ids).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('record:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="办理类型">
        <el-select v-model="checkInRecordQuery.type" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="办理状态">
        <el-select v-model="checkInRecordQuery.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="老人">
        <el-select
            v-model="checkInRecordQuery.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名搜索"
            :remote-method="loadElderOptions"
            :loading="elderLoading"
            style="width: 180px">
          <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="`${item.realName}（${item.idCardNo}）`"
              :value="item.id"
          />
        </el-select>
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
      <el-table-column prop="recordNo" label="办理编号" width="160" :show-overflow-tooltip="true"/>
      <el-table-column label="类型" width="80" align="center">
        <template #default="{row}">
          <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ getTypeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="老人" width="120">
        <template #default="{row}">
          {{ row.elderName || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="客户" width="120">
        <template #default="{row}">
          {{ row.familyName || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="床位" min-width="170" :show-overflow-tooltip="true">
        <template #default="{row}">
          {{ formatBed(row) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}">
          <el-tag :type="getStatusTag(row.status).type">{{ getStatusTag(row.status).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前步骤" width="90" align="center">
        <template #default="{row}">
          {{ row.status === 0 ? `第${row.step}步` : '—' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160" align="center">
        <template #default="{row}">
          {{ row.createTime }}
        </template>
      </el-table-column>
      <el-table-column align="center" width="300px" fixed="right" label="操作" v-if="hasBtnPermission('record:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="continueProcess(row)" v-if="row.status === 0">继续办理</el-button>
          <el-button
              size="small"
              type="warning"
              :icon="SwitchButton"
              @click="cancelRecord(row)"
              v-if="row.status === 0 && hasBtnPermission(row.type === 0 ? 'checkIn:cancel' : 'checkOut:cancel')">取消</el-button>
          <el-button size="small" :icon="Document" @click="showDetail(row)">详情</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)"
                     v-if="row.status !== 0 && hasBtnPermission('record:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="checkInRecordQuery.page"
        v-model:page-size="checkInRecordQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>

  <!--详情抽屉-->
  <el-drawer v-model="drawerDetailVisible" title="办理详情" size="45%" :close-on-click-modal="true">
    <el-descriptions title="基本信息" :column="1" border v-if="detail.id">
      <el-descriptions-item label="办理编号">{{ detail.recordNo || '—' }}</el-descriptions-item>
      <el-descriptions-item label="办理类型">{{ getTypeLabel(detail.type) }}</el-descriptions-item>
      <el-descriptions-item label="办理状态">
        <el-tag :type="getStatusTag(detail.status).type">{{ getStatusTag(detail.status).label }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="老人">{{ detail.elderName || '—' }}（{{ detail.idCardNo || '—' }}）</el-descriptions-item>
      <el-descriptions-item label="老人电话">{{ detail.elderPhone || '—' }}</el-descriptions-item>
      <el-descriptions-item label="客户">{{ detail.familyName || '—' }}</el-descriptions-item>
      <el-descriptions-item label="客户电话">{{ detail.familyPhone || '—' }}</el-descriptions-item>
      <el-descriptions-item label="床位">{{ formatBed(detail) }}</el-descriptions-item>
    </el-descriptions>
    <!--入住单展示入住登记和合同信息，退住单展示退住登记信息-->
    <el-descriptions v-if="detail.type === 0" title="入住登记" :column="1" border style="margin-top: 30px">
      <el-descriptions-item label="入住日期">{{ formatDate(detail.checkinDate) }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ detail.remark || '—' }}</el-descriptions-item>
    </el-descriptions>
    <el-descriptions v-if="detail.type === 0" title="签订合同" :column="1" border style="margin-top: 30px">
      <el-descriptions-item label="合同名称">{{ detail.contractName || '—' }}</el-descriptions-item>
      <el-descriptions-item label="合同编号">{{ detail.contractNo || '—' }}</el-descriptions-item>
      <el-descriptions-item label="合同类型">{{ (typeOptions.find(item => item.value === detail.contractType) || {}).label || '—' }}</el-descriptions-item>
      <el-descriptions-item label="签订日期">{{ formatDate(detail.signTime) }}</el-descriptions-item>
      <el-descriptions-item label="到期日期">{{ formatDate(detail.expireTime) }}</el-descriptions-item>
      <el-descriptions-item label="合同文件">
        <el-link v-if="detail.fileUrl" :href="detail.fileUrl" target="_blank" :icon="Document">点击查看</el-link>
        <span v-else>未上传</span>
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions v-if="detail.type === 1" title="退住登记" :column="1" border style="margin-top: 30px">
      <el-descriptions-item label="退住日期">{{ formatDate(detail.checkoutDate) }}</el-descriptions-item>
      <el-descriptions-item label="退住原因">{{ detail.reason || '—' }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ detail.remark || '—' }}</el-descriptions-item>
      <el-descriptions-item label="原入住单编号">{{ detail.checkInNo || '—' }}</el-descriptions-item>
    </el-descriptions>
    <el-descriptions title="办理信息" :column="1" border style="margin-top: 30px">
      <el-descriptions-item label="确认办理人">{{ detail.handlerName || '—' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</el-descriptions-item>
    </el-descriptions>
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