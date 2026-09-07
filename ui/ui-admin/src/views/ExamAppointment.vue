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
  import examAppointmentApi from '@/api/examAppointment.js'
  import examPackageApi from '@/api/examPackage.js'
  import elderApi from '@/api/elder.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus, View} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个体检预约对象，在添加/编辑时临时保存填写的数据
  const examAppointment = ref({})

  // ================== 选项 ==================

  // 状态选项（状态：0待体检 1体检中 2已完成 3已取消 4已过期）
  const statusOptions = [
    {value: 0, label: '待体检'},
    {value: 1, label: '体检中'},
    {value: 2, label: '已完成'},
    {value: 3, label: '已取消'},
    {value: 4, label: '已过期'},
  ]

  // 明细结果状态选项（状态：0待检查 1正常 2异常 3未完成，文本型结果由人工标记）
  const resultStatusOptions = [
    {value: 1, label: '正常'},
    {value: 2, label: '异常'},
    {value: 3, label: '未完成'},
  ]

  // ================== 下拉数据 ==================

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

  // 体检套餐选项：进入页面一次加载全部上架状态套餐，供添加/编辑里"套餐下拉框"选择
  const examPackageOptions = ref([])
  const loadExamPackageOptions = () => {
    examPackageApi.listAll().then(result => {
      examPackageOptions.value = result.data
    })
  }
  loadExamPackageOptions()

  // ================== 变量 ==================

  //分页信息和搜索条件（按老人、套餐名称、状态、预约日期范围搜索）
  const examAppointmentQuery = ref({
    elderId: '',
    packageName: '',
    status: '',
    page: 1,
    limit: 10
  })

  //预约日期范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const appointmentDateRange = ref([])

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerExamAppointmentVisible = ref(false)

  //结果录入、查看对话框的弹出控制
  const drawerExamResultVisible = ref(false)
  //结果对话框模式：edit录入结果（体检中）/ view查看结果（已完成）
  const resultMode = ref('edit')
  //当前正在录入/查看结果的体检预约（行数据，用于展示老人、套餐等信息）
  const currentAppointment = ref({})
  //体检记录明细列表（含体检项目的参考范围）
  const resultItemList = ref([])

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    examAppointmentQuery.value.beginAppointmentDate = appointmentDateRange.value?.[0]
    examAppointmentQuery.value.endAppointmentDate = appointmentDateRange.value?.[1]

    examAppointmentApi.list(examAppointmentQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    examAppointmentQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    examAppointmentQuery.value = {
      elderId: '',
      packageName: '',
      status: '',
      page: 1,
      limit: 10
    }
    appointmentDateRange.value = []
    elderOptions.value = [] //清空老人搜索结果
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
      examAppointmentApi.deleteById(id).then(result => {
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
      examAppointmentApi.deleteAll(ids).then(result => {
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
    drawerExamAppointmentVisible.value = true
    title.value = '添加'
    examAppointment.value = {} //预约状态、套餐价格由后端处理
    elderOptions.value = [] //清空老人搜索结果
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (row) => {
    drawerExamAppointmentVisible.value = true
    title.value = '编辑'
    examAppointment.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    examAppointmentApi.selectById(row.id).then(result => {
      examAppointment.value = result.data
      //编辑时回显当前绑定的老人：把该老人生成一个可显示的下拉选项，这样下拉框才能显示出"姓名（身份证号）"，而不是只显示一个数字id
      if (examAppointment.value.elderId) {
        elderApi.selectById(examAppointment.value.elderId).then(res => {
          if (res.code === 1) {
            elderOptions.value = [res.data]
          }
        })
      }
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    elderId: [
      {required: true, message: '请选择老人', trigger: 'blur'}
    ],
    packageId: [
      {required: true, message: '请选择体检套餐', trigger: 'blur'}
    ],
    appointmentDate: [
      {required: true, message: '请选择预约日期', trigger: 'blur'}
    ],
    appointmentTime: [
      {required: true, message: '请选择预约时间', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (examAppointment.value.id) {//编辑
            examAppointmentApi.update(examAppointment.value.id, examAppointment.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerExamAppointmentVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            examAppointmentApi.add(examAppointment.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerExamAppointmentVisible.value = false
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

  //开始体检（待体检 → 体检中）
  const start = (row) => {
    ElMessageBox.confirm(
        `确认为老人【${row.elderName}】开始体检么?`,
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      examAppointmentApi.start(row.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //取消预约（待体检/体检中 → 已取消）
  const cancel = (row) => {
    ElMessageBox.confirm(
        `确认取消老人【${row.elderName}】的体检预约么?`,
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      examAppointmentApi.cancel(row.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //打开结果录入对话框（体检中）
  const showResultDialog = (row) => {
    resultMode.value = 'edit'
    currentAppointment.value = row
    drawerExamResultVisible.value = true
    loadResultItemList(row.id)
  }

  //打开结果查看对话框（已完成）
  const showViewDialog = (row) => {
    resultMode.value = 'view'
    currentAppointment.value = row
    drawerExamResultVisible.value = true
    loadResultItemList(row.id)
  }

  //加载体检记录明细列表（含体检项目的参考范围）
  const loadResultItemList = (id) => {
    examAppointmentApi.getAppointmentItemsById(id).then(result => {
      resultItemList.value = result.data || []
    })
  }

  //数值型结果输入后即时预览是否异常：与参考范围比对，超出范围即为异常
  const judgeNumeric = (row) => {
    if (row.resultValue === null || row.resultValue === undefined) {
      row.status = 0 //清空数值后回到待检查
      row.abnormal = 0
      return
    }
    const min = row.referenceMin
    const max = row.referenceMax
    if ((min == null || min === undefined) && (max == null || max === undefined)) {
      row.abnormal = 0 //未配置参考范围时不判定，视为正常
      row.status = 1
      return
    }
    const abnormal = (min != null && min !== undefined && row.resultValue < min)
        || (max != null && max !== undefined && row.resultValue > max)
    row.abnormal = abnormal ? 1 : 0
    row.status = abnormal ? 2 : 1
  }

  //数值型异常的方向提示：超过参考上限为偏高，低于参考下限为偏低
  const judgeDirection = (row) => {
    const min = row.referenceMin
    const max = row.referenceMax
    if (max != null && max !== undefined && row.resultValue > max) return '偏高'
    if (min != null && min !== undefined && row.resultValue < min) return '偏低'
    return '异常'
  }

  //提取明细列表，转成后端需要的ExamAppointmentItem结构（先删后插，不传明细id）
  const buildExamAppointmentItems = () => {
    return resultItemList.value.map(item => ({
      examItemId: item.examItemId,
      itemName: item.itemName,
      resultValue: item.resultValue || null,
      resultText: item.resultText || null,
      status: item.status,
      abnormal: item.abnormal,
      remark: item.remark || null
    }))
  }

  //暂存体检结果（仅限体检中状态），保存后可继续录入
  const saveResultItems = () => {
    examAppointmentApi.updateAppointmentItems(currentAppointment.value.id, buildExamAppointmentItems()).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
      } else {
        ElMessage.error(result.msg)
      }
    })
  }

  //完成体检：保存全部明细结果并流转为已完成（数值型结果由后端与参考范围比对最终判定）
  const complete = () => {
    ElMessageBox.confirm(
        `确认完成老人【${currentAppointment.value.elderName}】的体检并保存全部结果么?`,
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      examAppointmentApi.complete(currentAppointment.value.id, buildExamAppointmentItems()).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          drawerExamResultVisible.value = false
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
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('examAppointment:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('examAppointment:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="老人">
        <el-select
            v-model="examAppointmentQuery.elderId"
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
      <el-form-item label="套餐名称">
        <el-input v-model="examAppointmentQuery.packageName" placeholder="请输入套餐名称" clearable style="width: 180px"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="examAppointmentQuery.status" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预约日期">
        <el-date-picker
            v-model="appointmentDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
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
      <el-table-column prop="elderName" label="老人" width="110"/>
      <el-table-column prop="packageName" label="体检套餐" width="140" :show-overflow-tooltip="true"/>
      <el-table-column prop="appointmentDate" label="预约日期" width="110" align="center"/>
      <el-table-column prop="appointmentTime" label="预约时间" width="100" align="center"/>
      <el-table-column prop="price" label="套餐价格" width="100" align="center">
        <template #default="{ row }">
          <span>￥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{row}">
          <!-- 状态有5种，直接内联判断 -->
          <el-tag v-if="row.status === 0" type="warning">待体检</el-tag>
          <el-tag v-else-if="row.status === 1" type="primary">体检中</el-tag>
          <el-tag v-else-if="row.status === 2" type="success">已完成</el-tag>
          <el-tag v-else-if="row.status === 3" type="info">已取消</el-tag>
          <el-tag v-else type="info">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="330px" fixed="right" label="操作" v-if="hasBtnPermission('examAppointment:operation')">
        <template #default="{ row }">
          <!-- 待体检：可开始体检、编辑、取消 -->
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="start(row)" v-if="hasBtnPermission('examAppointment:start')">开始体检</el-button>
            <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row)" v-if="hasBtnPermission('examAppointment:update')">编辑</el-button>
            <el-button size="small" type="warning" @click="cancel(row)" v-if="hasBtnPermission('examAppointment:cancel')">取消</el-button>
          </template>
          <!-- 体检中：可录入结果、取消 -->
          <template v-else-if="row.status === 1">
            <el-button size="small" type="primary" @click="showResultDialog(row)" v-if="hasBtnPermission('examAppointment:complete')">录入结果</el-button>
            <el-button size="small" type="warning" @click="cancel(row)" v-if="hasBtnPermission('examAppointment:cancel')">取消</el-button>
          </template>
          <!-- 已完成：可查看结果 -->
          <template v-else-if="row.status === 2">
            <el-button size="small" type="primary" :icon="View" @click="showViewDialog(row)" v-if="hasBtnPermission('examAppointment:viewResult')">查看结果</el-button>
          </template>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('examAppointment:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="examAppointmentQuery.page"
        v-model:page-size="examAppointmentQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerExamAppointmentVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="examAppointment" :rules="dialogRules">
      <el-form-item prop="elderId" label="老人" :label-width="80">
        <el-select
            v-model="examAppointment.elderId"
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
      <el-form-item prop="packageId" label="体检套餐" :label-width="80">
        <el-select v-model="examAppointment.packageId" placeholder="请选择体检套餐" filterable style="width: 220px">
          <el-option
              v-for="item in examPackageOptions"
              :key="item.id"
              :label="`${item.name}（￥${item.price}）`"
              :value="item.id"
          />
        </el-select>
        <div class="package-tips">仅显示上架状态的套餐，价格按套餐价格快照保存</div>
      </el-form-item>
      <el-form-item prop="appointmentDate" label="预约日期" :label-width="80">
        <el-date-picker v-model="examAppointment.appointmentDate" type="date" value-format="YYYY-MM-DD" placeholder="选择预约日期" style="width: 220px"/>
      </el-form-item>
      <el-form-item prop="appointmentTime" label="预约时间" :label-width="80">
        <el-time-picker v-model="examAppointment.appointmentTime" value-format="HH:mm:ss" placeholder="选择预约时间" style="width: 220px"/>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input
            v-model="examAppointment.remark"
            autocomplete="off"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerExamAppointmentVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>

  <!--结果录入、查看弹出框-->
  <el-drawer v-model="drawerExamResultVisible" :title="resultMode === 'edit' ? '录入结果' : '查看结果'" size="65%" :close-on-click-modal="true">
    <!--体检预约基本信息-->
    <el-descriptions :column="3" border size="small" style="margin-bottom: 15px">
      <el-descriptions-item label="老人">{{ currentAppointment.elderName }}</el-descriptions-item>
      <el-descriptions-item label="体检套餐">{{ currentAppointment.packageName }}</el-descriptions-item>
      <el-descriptions-item label="预约时间">{{ currentAppointment.appointmentDate }} {{ currentAppointment.appointmentTime }}</el-descriptions-item>
    </el-descriptions>
    <!--明细结果表：数值型录数值自动判定异常，文本型录文本并人工标记状态-->
    <el-table :data="resultItemList" border size="small" style="width: 100%">
      <el-table-column prop="itemName" label="体检项目" width="140"/>
      <el-table-column label="参考范围" width="140" align="center">
        <template #default="{ row }">
          <!-- 数值型显示参考范围，文本型没有参考范围 -->
          <span v-if="row.resultType === 1">{{ row.referenceMin }} ~ {{ row.referenceMax }} {{ row.referenceUnit || '' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="检查结果" min-width="200">
        <template #default="{ row }">
          <!-- 数值型：输入数值，超出参考范围标红提示 -->
          <template v-if="row.resultType === 1">
            <el-input-number
                v-model="row.resultValue"
                :precision="2"
                controls-position="right"
                :disabled="resultMode === 'view'"
                @change="judgeNumeric(row)"
                style="width: 150px"
            />
            <span class="result-unit">{{ row.referenceUnit || '' }}</span>
            <el-tag v-if="row.status === 2" type="danger" size="small" style="margin-left: 5px">{{ judgeDirection(row) }}</el-tag>
          </template>
          <!-- 文本型：输入文本描述 -->
          <el-input
              v-else
              v-model="row.resultText"
              type="textarea"
              :rows="2"
              maxlength="1000"
              :disabled="resultMode === 'view'"
          />
        </template>
      </el-table-column>
      <el-table-column label="结论" width="150" align="center">
        <template #default="{ row }">
          <!-- 数值型结论由参考范围比对自动判定，文本型由人工标记 -->
          <el-tag v-if="row.resultType === 1" :type="row.status === 2 ? 'danger' : (row.status === 3 ? 'info' : 'success')">
            {{ resultStatusOptions.find(option => option.value === row.status)?.label || '待检查' }}
          </el-tag>
          <el-select
              v-else
              v-model="row.status"
              :disabled="resultMode === 'view'"
              style="width: 110px"
          >
            <el-option
                v-for="item in resultStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="备注" width="180">
        <template #default="{ row }">
          <el-input v-model="row.remark" maxlength="500" :disabled="resultMode === 'view'"/>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <div class="dialog-footer">
        <!-- 录入模式：可暂存、完成体检；查看模式：只有关闭 -->
        <template v-if="resultMode === 'edit'">
          <el-button @click="saveResultItems" v-if="hasBtnPermission('examAppointment:complete')">暂存结果</el-button>
          <el-button type="primary" @click="complete" v-if="hasBtnPermission('examAppointment:complete')">完成体检</el-button>
        </template>
        <el-button v-else @click="drawerExamResultVisible = false">关闭</el-button>
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

  .package-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
  }

  .result-unit {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 5px;     /* 与输入框保持间距 */
  }
</style>