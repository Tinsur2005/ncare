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
  import familyApi from '@/api/family.js'
  import buildingApi from '@/api/building.js'
  import floorApi from '@/api/floor.js'
  import roomApi from '@/api/room.js'
  import bedApi from '@/api/bed.js'
  import contractApi from '@/api/contract.js'
  import {computed, nextTick, ref} from 'vue'
  import {useRoute, useRouter} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Upload, Document} from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  import hasBtnPermission from "@/utils/btnPermission.js";
  const tokenStore = useTokenStore()
  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //当前办理单：新建办理时为空，第一步完成后由后端返回id回填，续办时从后端加载
  const record = ref({})
  //当前步骤：新建办理从第1步开始，续办时取办理单的step
  const currentStep = ref(1)
  //是否只读（已完成或已取消的办理单只允许查看，不允许再填写）
  const isReadonly = ref(false)

  //第1步：客户与老人登记表单
  const step1 = ref({
    familyMode: 1,   //客户登记方式：0新建客户 1选择已有客户
    familyId: '',
    family: {},
    elderMode: 1,    //老人登记方式：0新建老人 1选择已有老人
    elderId: '',
    elder: {}
  })

  //第2步：入住登记表单（楼栋、楼层、房间只用于级联选择，提交时只传床位ID）
  const step2 = ref({
    buildingId: '',
    floorId: '',
    roomId: '',
    checkinDate: '',
    bedId: '',
    remark: ''
  })

  //第3步：签订合同表单（合同类型默认入住合同）
  const step3 = ref({
    contractName: '',
    contractNo: '',
    contractType: 1,
    signTime: '',
    expireTime: '',
    fileUrl: ''
  })

  // ================== 选项 ==================

  // 性别选项（0：女，1：男）
  const genderOptions = [
    {value: 0, label: '女'},
    {value: 1, label: '男'},
  ]

  // 与老人的关系选项
  const relationOptions = ['子女', '配偶', '亲属', '其他']

  // 合同类型选项（0服务合同 1入住合同 2其他）
  const contractTypeOptions = [
    {value: 0, label: '服务合同'},
    {value: 1, label: '入住合同'},
    {value: 2, label: '其他'},
  ]

  // 客户远程搜索：存放远程搜索出来的可选客户列表，供下拉框展示"姓名（联系电话）"
  const familyOptions = ref([])
  const familyLoading = ref(false)
  const loadFamilyOptions = (query) => {
    if (!query) {
      familyOptions.value = []
      return
    }
    familyLoading.value = true
    familyApi.searchByName(query).then(result => {
      familyOptions.value = result.data
    }).finally(() => {
      familyLoading.value = false
    })
  }

  // 老人远程搜索：入住办理展示全部老人，由后端附带床位占用和状态，不可办理的在选项里标注并禁选
  const elderOptions = ref([])
  const elderLoading = ref(false)
  const loadElderOptions = (query) => {
    if (!query) {
      elderOptions.value = []
      return
    }
    elderLoading.value = true
    checkInApi.listCheckInElders(query).then(result => {
      elderOptions.value = result.data || []
    }).finally(() => {
      elderLoading.value = false
    })
  }

  //老人选项展示文案：先按老人状态标注办理中的流程（退住中床位尚未释放，状态判断要在床位之前），再按床位占用标注已入住
  const elderLabel = (item) => {
    const base = `${item.realName}（${item.idCardNo}）`
    if (item.status === 4) return `${base}（入住流程中）` //入住中：正在办理入住，流程还没走完
    if (item.status === 3) return `${base}（退住流程中）` //退住中：正在办理退住，床位还没释放
    if (item.status === 0) return `${base}（已停用）`
    if (item.bedId) return `${base}（已入住）`           //正常在住：已完成入住流程，占用床位
    return base
  }

  //不可办理入住的老人：已占用床位，或处于停用(0)/入住中(4)/退住中(3)状态
  const elderDisabled = (item) => {
    return !!item.bedId || item.status === 0 || item.status === 3 || item.status === 4
  }

  //楼栋、楼层、房间选项：进入页面一次加载全部，入住登记时逐级过滤
  const buildingOptions = ref([])
  const floorOptions = ref([])
  const roomOptions = ref([])
  const loadCascadeOptions = () => {
    buildingApi.listAll().then(result => {
      buildingOptions.value = result.data
    })
    floorApi.listAll().then(result => {
      floorOptions.value = result.data
    })
    roomApi.listAll().then(result => {
      roomOptions.value = result.data
    })
  }
  loadCascadeOptions()

  //床位选项：进入页面一次加载全部床位，供入住登记的床位下拉框使用（已占用和维修中的床位在选项里标注且不允许选择）
  const bedOptions = ref([])
  const loadBedOptions = () => {
    bedApi.listAll().then(result => {
      bedOptions.value = result.data
    })
  }
  loadBedOptions()

  //选中楼栋后楼层下拉才可用，选中楼层后房间下拉才可用（与床位管理搜索栏的级联逻辑一致）
  const floorChoices = computed(() => floorOptions.value.filter(item => item.buildingId === step2.value.buildingId))
  const roomChoices = computed(() => roomOptions.value.filter(item => item.floorId === step2.value.floorId))
  const bedChoices = computed(() => bedOptions.value.filter(item => item.roomId === step2.value.roomId))

  //当前选中床位的对象，用于在入住登记页展示床位费
  const selectedBed = computed(() => bedOptions.value.find(item => item.id === step2.value.bedId))

  //床位选项文案：空闲床位展示床位费，已占用和维修中的床位在后面用括号标注状态
  const getBedLabel = (bed) => {
    let label = `${bed.bedNo}（${bed.monthlyPrice}元/月）`
    if (bed.status === 1) {
      label += '（已占用）'
    } else if (bed.status === 2) {
      label += '（维修中）'
    }
    return label
  }

  //切换楼栋后清空楼层、房间和床位
  const onBuildingChange = () => {
    step2.value.floorId = ''
    onFloorChange()
  }
  //切换楼层后清空房间和床位
  const onFloorChange = () => {
    step2.value.roomId = ''
    onRoomChange()
  }
  //切换房间后清空床位
  const onRoomChange = () => {
    step2.value.bedId = ''
  }

  // ================== 变量 ==================

  //第1步、第2步、第3步的表单校验引用
  const formRef1 = ref()
  const formRef2 = ref()
  const formRef3 = ref()

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
      //回显第2步：入住日期和备注（床位只在只读回显里展示文字，办理中不允许回头改）
      step2.value.checkinDate = record.value.checkinDate
      step2.value.remark = record.value.remark
      //回显第3步：合同数据
      step3.value.contractName = record.value.contractName || ''
      step3.value.contractNo = record.value.contractNo || ''
      step3.value.contractType = record.value.contractType ?? 1
      step3.value.signTime = record.value.signTime || ''
      step3.value.expireTime = record.value.expireTime || ''
      step3.value.fileUrl = record.value.fileUrl || ''
    })
  }

  //提交第1步：登记客户和老人，创建办理单
  const submitStep1 = () => {
    formRef1.value.validate()
        .then(() => {
          const data = {
            type: 0, //入住
            familyMode: step1.value.familyMode,
            familyId: step1.value.familyMode === 1 ? step1.value.familyId : null,
            family: step1.value.familyMode === 0 ? step1.value.family : null,
            elderMode: step1.value.elderMode,
            elderId: step1.value.elderMode === 1 ? step1.value.elderId : null,
            elder: step1.value.elderMode === 0 ? step1.value.elder : null
          }
          checkInApi.add(data).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              record.value.id = result.data
              //把办理单id写进地址栏，中途关页后从申请记录页还能续办
              router.replace({path: '/checkInProcess', query: {id: result.data}})
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

  //提交第2步：保存入住日期、床位和备注，步骤进入第3步
  const submitStep2 = () => {
    formRef2.value.validate()
        .then(() => {
          checkInApi.saveStep(record.value.id, 2, {
            checkinDate: step2.value.checkinDate,
            bedId: step2.value.bedId,
            remark: step2.value.remark
          }).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              loadRecord()
              nextTick(() => {
                formRef3.value?.clearValidate()
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

  //提交第3步：保存合同信息，步骤进入第4步（保存前先查重合同编号，避免确认入住时才报错）
  const submitStep3 = () => {
    formRef3.value.validate()
        .then(() => {
          contractApi.isExists(step3.value.contractNo).then(result => {
            if (result.data) {
              ElMessage.error('已存在相同合同编号，请修改后重试')
              return
            }
            checkInApi.saveStep(record.value.id, 3, step3.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                loadRecord()
              } else {
                ElMessage.error(result.msg)
              }
            })
          })
        })
        .catch(() => {
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //确认入住：占用床位、老人置为入住中、合同写入contract表并绑定客户老人关系
  const confirmCheckIn = () => {
    ElMessageBox.confirm(
        '确认入住后将占用所选床位并把老人状态改为入住中，是否确认？',
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

  //取消办理：取消后该办理单不再占用，已登记的数据不受影响
  const cancelRecord = () => {
    ElMessageBox.confirm(
        '取消后该办理单作废，老人将被改为已停用状态，确认取消本次入住办理么？',
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

  //上传合同文件成功后，把返回的url存到表单的fileUrl字段
  const handleFileSuccess = (result) => {
    step3.value.fileUrl = result.data;
  }
  //上传时校验合同文件的格式
  const allowedFileTypes = [
    'application/pdf', // pdf
    'application/msword', // doc
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document' // docx
  ]
  const beforeFileUpload = (rawFile) => {
    if (!allowedFileTypes.includes(rawFile.type)) {
      ElMessage.error('不支持的文件格式（仅支持pdf/word）')
      return false
    } else if (rawFile.size / 1024 / 1024 > 10) {
      ElMessage.error('上传的文件大小不允许超过10MB')
      return false
    }
    return true
  }

  //表单校验规则：新建和选择已有的字段各自只在对应方式下渲染，渲染出来的才参与校验
  const step1Rules = {
    familyId: [{required: true, message: '请选择客户', trigger: 'change'}],
    'family.name': [{required: true, message: '请输入客户登录用户名', trigger: 'blur'}],
    'family.password': [{required: true, message: '请输入客户登录密码', trigger: 'blur'}],
    'family.realName': [{required: true, message: '请输入客户姓名', trigger: 'blur'}],
    'family.gender': [{required: true, message: '请选择客户性别', trigger: 'change'}],
    'family.relation': [{required: true, message: '请选择与老人的关系', trigger: 'change'}],
    'family.phone': [{required: true, message: '请输入客户联系电话', trigger: 'blur'}],
    elderId: [{required: true, message: '请选择老人', trigger: 'change'}],
    'elder.name': [{required: true, message: '请输入老人登录用户名', trigger: 'blur'}],
    'elder.password': [{required: true, message: '请输入老人登录密码', trigger: 'blur'}],
    'elder.realName': [{required: true, message: '请输入老人姓名', trigger: 'blur'}],
    'elder.gender': [{required: true, message: '请选择老人性别', trigger: 'change'}],
    'elder.idCardNo': [{required: true, message: '请输入老人身份证号', trigger: 'blur'}],
    'elder.phone': [{required: true, message: '请输入老人联系电话', trigger: 'blur'}]
  }
  const step2Rules = {
    checkinDate: [{required: true, message: '请选择入住日期', trigger: 'change'}],
    bedId: [{required: true, message: '请选择床位', trigger: 'change'}]
  }
  const step3Rules = {
    contractName: [{required: true, message: '请输入合同名称', trigger: 'blur'}],
    contractNo: [{required: true, message: '请输入合同编号', trigger: 'blur'}],
    contractType: [{required: true, message: '请选择合同类型', trigger: 'change'}],
    signTime: [{required: true, message: '请选择签订日期', trigger: 'change'}],
    expireTime: [{required: true, message: '请选择到期日期', trigger: 'change'}]
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
      <el-step title="客户与老人登记" description="登记客户与老人信息"/>
      <el-step title="入住登记" description="选择入住日期与床位"/>
      <el-step title="签订合同" description="填写合同信息并上传附件"/>
      <el-step title="确认入住" description="核对信息并确认办理"/>
    </el-steps>

    <!--已完成或已取消的办理单只读回显，顶部给个提示条-->
    <el-alert
        v-if="isReadonly"
        :title="record.status === 1 ? '该办理单已完成，以下为办理信息回显' : '该办理单已取消，以下为已登记的信息'"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"/>

    <!--第1步：客户与老人登记-->
    <template v-if="currentStep === 1 && !isReadonly">
      <el-form ref="formRef1" :model="step1" :rules="step1Rules" label-width="110px">
        <el-divider content-position="left">客户登记</el-divider>
        <el-form-item label="客户来源">
          <el-radio-group v-model="step1.familyMode">
            <el-radio :value="1">选择已有客户</el-radio>
            <el-radio :value="0">新建客户</el-radio>
          </el-radio-group>
        </el-form-item>
        <!--选择已有客户：远程搜索-->
        <el-form-item v-if="step1.familyMode === 1" prop="familyId" label="客户">
          <el-select
              v-model="step1.familyId"
              filterable
              remote
              reserve-keyword
              clearable
              placeholder="请输入客户姓名搜索"
              :remote-method="loadFamilyOptions"
              :loading="familyLoading"
              style="width: 260px">
            <el-option
                v-for="item in familyOptions"
                :key="item.id"
                :label="`${item.realName}（${item.phone}）`"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <!--新建客户：写入family表-->
        <template v-if="step1.familyMode === 0">
          <el-form-item prop="family.name" label="登录用户名">
            <el-input v-model="step1.family.name" placeholder="客户在前台登录用的用户名" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="family.password" label="登录密码">
            <el-input v-model="step1.family.password" show-password type="password" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="family.realName" label="客户姓名">
            <el-input v-model="step1.family.realName" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="family.gender" label="性别">
            <el-select v-model="step1.family.gender" placeholder="请选择性别" style="width: 260px">
              <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value"/>
            </el-select>
          </el-form-item>
          <el-form-item prop="family.relation" label="与老人的关系">
            <el-select v-model="step1.family.relation" placeholder="请选择与老人的关系" style="width: 260px">
              <el-option v-for="item in relationOptions" :key="item" :label="item" :value="item"/>
            </el-select>
          </el-form-item>
          <el-form-item prop="family.phone" label="联系电话">
            <el-input v-model="step1.family.phone" style="width: 260px"/>
          </el-form-item>
        </template>

        <el-divider content-position="left">老人登记</el-divider>
        <el-form-item label="老人来源">
          <el-radio-group v-model="step1.elderMode">
            <el-radio :value="1">选择已有老人</el-radio>
            <el-radio :value="0">新建老人</el-radio>
          </el-radio-group>
        </el-form-item>
        <!--选择已有老人：远程搜索，已入住中和停用的老人不允许办理入住-->
        <el-form-item v-if="step1.elderMode === 1" prop="elderId" label="老人">
          <el-select
              v-model="step1.elderId"
              filterable
              remote
              reserve-keyword
              clearable
              placeholder="请输入老人姓名搜索"
              :remote-method="loadElderOptions"
              :loading="elderLoading"
              style="width: 260px">
            <el-option
                v-for="item in elderOptions"
                :key="item.elderId"
                :label="elderLabel(item)"
                :disabled="elderDisabled(item)"
                :value="item.elderId"
            />
          </el-select>
        </el-form-item>
        <!--新建老人：写入elder表，确认入住后才变入住中-->
        <template v-if="step1.elderMode === 0">
          <el-form-item prop="elder.name" label="登录用户名">
            <el-input v-model="step1.elder.name" placeholder="老人在前台登录用的用户名" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.password" label="登录密码">
            <el-input v-model="step1.elder.password" show-password type="password" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.realName" label="老人姓名">
            <el-input v-model="step1.elder.realName" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.gender" label="性别">
            <el-select v-model="step1.elder.gender" placeholder="请选择性别" style="width: 260px">
              <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value"/>
            </el-select>
          </el-form-item>
          <el-form-item prop="elder.idCardNo" label="身份证号">
            <el-input v-model="step1.elder.idCardNo" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.phone" label="联系电话">
            <el-input v-model="step1.elder.phone" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.birthday" label="出生日期">
            <el-date-picker v-model="step1.elder.birthday" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择出生日期" style="width: 260px"/>
          </el-form-item>
          <el-form-item prop="elder.address" label="家庭住址">
            <el-input v-model="step1.elder.address" style="width: 260px"/>
          </el-form-item>
        </template>
      </el-form>
      <div class="footer">
        <el-button type="primary" @click="submitStep1">下一步</el-button>
        <el-button v-if="record.id" @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkIn:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--第2步：入住登记-->
    <template v-if="currentStep === 2 && !isReadonly">
      <el-form ref="formRef2" :model="step2" :rules="step2Rules" label-width="110px">
        <el-form-item prop="checkinDate" label="入住日期">
          <el-date-picker v-model="step2.checkinDate" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择入住日期" style="width: 260px"/>
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select v-model="step2.buildingId" placeholder="请选择楼栋" clearable style="width: 260px" @change="onBuildingChange">
            <el-option v-for="item in buildingOptions" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属楼层">
          <el-select v-model="step2.floorId" placeholder="请先选择楼栋" :disabled="!step2.buildingId" clearable style="width: 260px" @change="onFloorChange">
            <el-option v-for="item in floorChoices" :key="item.id" :label="`${item.floorNo}层`" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="step2.roomId" placeholder="请先选择楼层" :disabled="!step2.floorId" clearable style="width: 260px" @change="onRoomChange">
            <el-option v-for="item in roomChoices" :key="item.id" :label="item.roomNo" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item prop="bedId" label="床位">
          <el-select v-model="step2.bedId" placeholder="请先选择房间" :disabled="!step2.roomId" clearable style="width: 260px">
            <el-option
                v-for="item in bedChoices"
                :key="item.id"
                :label="getBedLabel(item)"
                :value="item.id"
                :disabled="item.status !== 0"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedBed" label="床位费">
          <span>{{ selectedBed.monthlyPrice }}元/月</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="step2.remark" type="textarea" :rows="2" style="width: 400px"/>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-button type="primary" @click="submitStep2">下一步</el-button>
        <el-button @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkIn:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--第3步：签订合同-->
    <template v-if="currentStep === 3 && !isReadonly">
      <el-form ref="formRef3" :model="step3" :rules="step3Rules" label-width="110px">
        <el-form-item prop="contractName" label="合同名称">
          <el-input v-model="step3.contractName" style="width: 260px"/>
        </el-form-item>
        <el-form-item prop="contractNo" label="合同编号">
          <el-input v-model="step3.contractNo" style="width: 260px"/>
        </el-form-item>
        <el-form-item prop="contractType" label="合同类型">
          <el-select v-model="step3.contractType" placeholder="请选择合同类型" style="width: 260px">
            <el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item prop="signTime" label="签订日期">
          <el-date-picker v-model="step3.signTime" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择签订日期" style="width: 260px"/>
        </el-form-item>
        <el-form-item prop="expireTime" label="到期日期">
          <el-date-picker v-model="step3.expireTime" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择到期日期" style="width: 260px"/>
        </el-form-item>
        <el-form-item label="合同文件">
          <div class="contract-file-field">
            <el-upload
                action="/admin/api/upload?dir=contract"
                :show-file-list="false"
                :on-success="handleFileSuccess"
                :before-upload="beforeFileUpload"
                :headers="{Authorization: tokenStore.token}"
                name="file">
              <el-button type="primary" :icon="Upload">上传合同文件</el-button>
            </el-upload>
            <div v-if="step3.fileUrl" class="contract-file-link">
              <el-link :href="step3.fileUrl" target="_blank" :icon="Document">已上传，点击查看</el-link>
            </div>
            <div class="avatar-uploader-tips">仅支持pdf/word格式，文件大小不超过10MB</div>
          </div>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-button type="primary" @click="submitStep3">下一步</el-button>
        <el-button @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkIn:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--第4步：确认入住，汇总回显全部步骤数据-->
    <template v-if="currentStep === 4 && !isReadonly">
      <el-descriptions title="入住信息确认" :column="2" border>
        <el-descriptions-item label="客户">{{ record.familyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ record.familyPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="老人">{{ record.elderName }}（{{ record.idCardNo }}）</el-descriptions-item>
        <el-descriptions-item label="老人电话">{{ record.elderPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ formatDate(record.checkinDate) }}</el-descriptions-item>
        <el-descriptions-item label="床位">
          {{ record.buildingName ? `${record.buildingName} ${record.floorNo}层 ${record.roomNo}房 ${record.bedNo}` : '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="床位费">{{ record.monthlyPrice != null ? `${record.monthlyPrice}元/月` : '—' }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ record.contractName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ record.contractNo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">
          {{ (contractTypeOptions.find(item => item.value === record.contractType) || {}).label || '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ formatDate(record.signTime) }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ formatDate(record.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="合同文件">
          <el-link v-if="record.fileUrl" :href="record.fileUrl" target="_blank" :icon="Document">点击查看</el-link>
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ record.remark || '—' }}</el-descriptions-item>
      </el-descriptions>
      <div class="footer">
        <el-button type="primary" @click="confirmCheckIn">确认入住</el-button>
        <el-button @click="suspendProcess">暂时挂起</el-button>
        <el-button type="danger" plain @click="cancelRecord" v-if="record.id && hasBtnPermission('checkIn:cancel')">取消办理</el-button>
      </div>
    </template>

    <!--只读回显：已完成或已取消的办理单按步骤依次展示-->
    <template v-if="isReadonly">
      <el-descriptions title="客户与老人登记" :column="2" border>
        <el-descriptions-item label="客户">{{ record.familyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ record.familyPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="老人">{{ record.elderName }}（{{ record.idCardNo }}）</el-descriptions-item>
        <el-descriptions-item label="老人电话">{{ record.elderPhone || '—' }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="入住登记" :column="2" border style="margin-top: 30px">
        <el-descriptions-item label="入住日期">{{ formatDate(record.checkinDate) }}</el-descriptions-item>
        <el-descriptions-item label="床位">
          {{ record.buildingName ? `${record.buildingName} ${record.floorNo}层 ${record.roomNo}房 ${record.bedNo}` : '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="床位费">{{ record.monthlyPrice != null ? `${record.monthlyPrice}元/月` : '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ record.remark || '—' }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="签订合同" :column="2" border style="margin-top: 30px">
        <el-descriptions-item label="合同名称">{{ record.contractName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ record.contractNo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">
          {{ (contractTypeOptions.find(item => item.value === record.contractType) || {}).label || '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ formatDate(record.signTime) }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ formatDate(record.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="合同文件">
          <el-link v-if="record.fileUrl" :href="record.fileUrl" target="_blank" :icon="Document">点击查看</el-link>
          <span v-else>未上传</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="办理信息" :column="2" border style="margin-top: 30px">
        <el-descriptions-item label="办理编号">{{ record.recordNo || '—' }}</el-descriptions-item>
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

  .contract-file-field {
    display: flex;
    align-items: center;
  }

  .contract-file-link {
    margin-left: 12px;
  }

  .avatar-uploader-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 12px;
  }
</style>