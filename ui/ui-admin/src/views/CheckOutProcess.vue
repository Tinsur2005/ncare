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
  import checkInApi from '@/api/checkIn.js'
  import {computed, nextTick, ref} from 'vue'
  import {useRoute, useRouter} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import hasBtnPermission from "@/utils/btnPermission.js";
  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //当前办理单：新建办理时为空，第一步完成后由后端返回id回填，续办时从后端加载
  const record = ref({})
  //当前步骤：新建办理从第1步开始，续办时取办理单的step
  const currentStep = ref(1)
  //是否只读（已完成或已取消的办理单只允许查看，不允许再填写）
  const isReadonly = ref(false)

  //第1步：选择在住老人
  const step1 = ref({
    elderId: ''
  })

  //第2步：退住登记表单
  const step2 = ref({
    checkoutDate: '',
    reason: '',
    remark: ''
  })

  // ================== 选项 ==================

  //在住老人远程搜索：返回全部可发起退住的老人，附带床位、客户和入住信息，供下拉框展示"姓名（身份证号）"，无床位的标注且不允许选择
  const inElderOptions = ref([])
  const inElderLoading = ref(false)
  const loadInElderOptions = (query) => {
    if (!query) {
      inElderOptions.value = []
      return
    }
    inElderLoading.value = true
    checkInApi.listInElders(query).then(result => {
      inElderOptions.value = result.data
    }).finally(() => {
      inElderLoading.value = false
    })
  }

  //当前选中在住老人的对象，用于在第一步回显其床位、客户和入住信息
  const selectedInElder = computed(() => inElderOptions.value.find(item => item.elderId === step1.value.elderId))

  // ================== 变量 ==================

  //第1步、第2步的表单校验引用
  const formRef1 = ref()
  const formRef2 = ref()

  // ================== 方法 ==================

  //日期展示：后端返回yyyy-MM-dd HH:mm:ss，日期字段只取前10位
  const formatDate = (date) => date ? String(date).substring(0, 10) : '—'

  //办理单编号展示
  const recordId = () => record.value.id || route.query.id

  //加载办理单详情（续办或查看时回显各步骤已保存的数据）
  const loadRecord = () => {
    checkInApi.selectById(recordId()).then(result => {
      if (result.code !== 1) {
        return
      }
      record.value = result.data
      isReadonly.value = record.value.status !== 0
      currentStep.value = record.value.step
      //回显第2步：退住日期、退住原因和备注
      step2.value.checkoutDate = record.value.checkoutDate
      step2.value.reason = record.value.reason || ''
      step2.value.remark = record.value.remark || ''
    })
  }

  //提交第1步：选择在住老人后创建退住单（后端会把老人置为退住中，防止办理期间重复发起）
  const submitStep1 = () => {
    formRef1.value.validate()
        .then(() => {
          checkInApi.add({
            type: 1, //退住
            elderId: step1.value.elderId
          }).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              record.value.id = result.data
              //把办理单id写进地址栏，中途关页后从申请记录页还能续办
              router.replace({path: '/checkOutProcess', query: {id: result.data}})
              currentStep.value = 2
              nextTick(() => {
                formRef2.value?.clearValidate()
              })
            } else {
              ElMessage.error(result.msg)
            }
          })
        })
        .catch(() => {
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //提交第2步：保存退住日期、退住原因和备注，步骤进入第3步
  const submitStep2 = () => {
    formRef2.value.validate()
        .then(() => {
          checkInApi.saveStep(record.value.id, 2, step2.value).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              loadRecord()
            } else {
              ElMessage.error(result.msg)
            }
          })
        })
        .catch(() => {
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //确认退住：释放床位、老人置为已退住
  const confirmCheckOut = () => {
    ElMessageBox.confirm(
        '确认退住后将释放老人占用的床位并把老人状态改为已退住，是否确认？',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      checkInApi.confirm(record.value.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadRecord()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //取消办理：老人会从退住中恢复为入住中，已登记的数据不受影响
  const cancelRecord = () => {
    ElMessageBox.confirm(
        '取消后该办理单作废，老人恢复为正常状态，确认取消本次退住办理么？',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '返回',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      checkInApi.cancel(record.value.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadRecord()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //暂时挂起：跳转到申请记录页，之后可从记录页继续办理
  const suspendProcess = () => {
    router.push('/checkRecord')
  }

  //表单校验规则
  const step1Rules = {
    elderId: [{required: true, message: '请选择在住老人', trigger: 'change'}]
  }
  const step2Rules = {
    checkoutDate: [{required: true, message: '请选择退住日期', trigger: 'change'}],
    reason: [{required: true, message: '请输入退住原因', trigger: 'blur'}]
  }

  //进入页面：带id为续办或查看，否则新建办理
  if (route.query.id) {
    loadRecord()
  }
</script>

<template>
  <el-card>
    <!--办理步骤条-->
    <el-steps :active="currentStep - 1" align-center finish-status="success" style="margin-bottom: 30px">
      <el-step title="选择在住老人" description="选择要办理退住的老人"/>
      <el-step title="退住登记" description="填写退住日期与原因"/>
      <el-step title="确认退住" description="核对信息并确认办理"/>
    </el-steps>

    <!--已完成或已取消的办理单只读回显，顶部给个提示条-->
    <el-alert
        v-if="isReadonly"
        :title="record.status === 1 ? '该办理单已完成，以下为办理信息回显' : '该办理单已取消，以下为已登记的信息'"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"/>

    <!--第1步：选择在住老人-->
    <template v-if="currentStep === 1 && !isReadonly">
      <el-form ref="formRef1" :model="step1" :rules="step1Rules" label-width="110px">
        <el-form-item prop="elderId" label="在住老人">
          <el-select
              v-model="step1.elderId"
              filterable
              remote
              reserve-keyword
              clearable
              placeholder="请输入老人姓名搜索"
              :remote-method="loadInElderOptions"
              :loading="inElderLoading"
              style="width: 260px">
            <el-option
                v-for="item in inElderOptions"
                :key="item.elderId"
                :label="item.bedId ? `${item.realName}（${item.idCardNo}）` : `${item.realName}（${item.idCardNo}）（无床位）`"
                :disabled="!item.bedId"
                :value="item.elderId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <!--选中老人后回显其床位、客户和入住信息，供办理前核对-->
      <el-descriptions v-if="selectedInElder" :column="2" border style="margin-top: 10px">
        <el-descriptions-item label="床位位置">{{ selectedInElder.bedLabel || '—' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ formatDate(selectedInElder.checkinDate) }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ selectedInElder.familyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="老人电话">{{ selectedInElder.phone || '—' }}</el-descriptions-item>
      </el-descriptions>
      <div class="footer">
        <el-button type="primary" @click="submitStep1">下一步</el-button>
        <el-button v-if="record.id" @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkOut:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--第2步：退住登记-->
    <template v-if="currentStep === 2 && !isReadonly">
      <el-form ref="formRef2" :model="step2" :rules="step2Rules" label-width="110px">
        <el-form-item prop="checkoutDate" label="退住日期">
          <el-date-picker v-model="step2.checkoutDate" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择退住日期" style="width: 260px"/>
        </el-form-item>
        <el-form-item prop="reason" label="退住原因">
          <el-input v-model="step2.reason" type="textarea" :rows="3" placeholder="请输入退住原因" style="width: 400px"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="step2.remark" type="textarea" :rows="2" style="width: 400px"/>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-button type="primary" @click="submitStep2">下一步</el-button>
        <el-button @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkOut:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--第3步：确认退住，汇总回显全部步骤数据-->
    <template v-if="currentStep === 3 && !isReadonly">
      <el-descriptions title="退住信息确认" :column="2" border>
        <el-descriptions-item label="老人">{{ record.elderName }}（{{ record.idCardNo }}）</el-descriptions-item>
        <el-descriptions-item label="老人电话">{{ record.elderPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="床位">
          {{ record.buildingName ? `${record.buildingName} ${record.floorNo}层 ${record.roomNo}房 ${record.bedNo}` : '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ record.familyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ formatDate(record.checkinDate) }}</el-descriptions-item>
        <el-descriptions-item label="退住日期">{{ formatDate(record.checkoutDate) }}</el-descriptions-item>
        <el-descriptions-item label="退住原因">{{ record.reason || '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ record.remark || '—' }}</el-descriptions-item>
      </el-descriptions>
      <div class="footer">
        <el-button type="primary" @click="confirmCheckOut">确认退住</el-button>
        <el-button @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkOut:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--只读回显：已完成或已取消的办理单按步骤依次展示-->
    <template v-if="isReadonly">
      <el-descriptions title="选择在住老人" :column="2" border>
        <el-descriptions-item label="老人">{{ record.elderName }}（{{ record.idCardNo }}）</el-descriptions-item>
        <el-descriptions-item label="老人电话">{{ record.elderPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="床位">
          {{ record.buildingName ? `${record.buildingName} ${record.floorNo}层 ${record.roomNo}房 ${record.bedNo}` : '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ record.familyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ formatDate(record.checkinDate) }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="退住登记" :column="2" border style="margin-top: 30px">
        <el-descriptions-item label="退住日期">{{ formatDate(record.checkoutDate) }}</el-descriptions-item>
        <el-descriptions-item label="退住原因">{{ record.reason || '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ record.remark || '—' }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="办理信息" :column="2" border style="margin-top: 30px">
        <el-descriptions-item label="办理编号">{{ record.recordNo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="原入住单编号">{{ record.checkInNo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="确认办理人">{{ record.handlerName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ record.createTime || '—' }}</el-descriptions-item>
      </el-descriptions>
    </template>

  </el-card>
</template>

<style scoped>
  .footer {
    margin-top: 30px;
  }
</style>