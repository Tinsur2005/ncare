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
  import {
    Management,
    Promotion,
    UserFilled,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom, Plus,
    CollectionTag, SetUp,
    Document,
    HomeFilled,
    Message
  } from '@element-plus/icons-vue'
  import avatar from '@/assets/default.png'
  //条目被点击后,调用的函数
  import {useRouter} from 'vue-router'
  import {useTokenStore} from "@/store/token.js";
  import {ElMessage, ElMessageBox} from "element-plus";
  import {useUserInfoStore} from "@/store/userInfo.js";
  import userApi from "@/api/user.js";
  import {nextTick, onBeforeUnmount, ref} from "vue";

  // ============ 对象  ============

  // 用户对象：当前登录的用户封装成的对象
  const user = ref({})
  // 修改密码DTO对象：邮箱验证码修改密码时存储验证码和新密码
  const userPasswordDTO = ref({
    code: '',
    newPassword: '',
    reNewPassword: ''
  })
  // 邮箱DTO对象：绑定/更换邮箱时存储新邮箱和验证码，更换邮箱时code为新邮箱验证码之前的旧邮箱验证码，newCode为新邮箱验证码
  const emailDTO = ref({
    email: '',
    code: '',
    newCode: ''
  })
  // 获取验证码的倒计时秒数
  const countdown = ref(0)
  // 倒计时定时器
  let countdownTimer = null
  // 新邮箱验证码的倒计时秒数（更换邮箱时新邮箱的验证码单独计时，和旧邮箱互不影响）
  const countdownNew = ref(0)
  // 新邮箱验证码的倒计时定时器
  let countdownNewTimer = null

  // ============ 存储  ============
  const userInfoStore = useUserInfoStore()
  const tokenStore = useTokenStore()
  const router = useRouter()
  const resetForm = ref()
  const bindEmailForm = ref()
  const step1Form = ref()
  const step2Form = ref()

  // 菜单
  const menuData = ref([]);

  // ============ 对话框控制  ============
  //控制用户信息对话框
  const dialogFormVisible = ref(false)
  //控制修改密码对话框（邮箱验证码方式）
  const dialogResetPasswordDialog = ref(false)
  //控制绑定邮箱对话框（未绑定邮箱时使用，单步完成）
  const dialogBindEmailVisible = ref(false)
  //控制更换邮箱第一步对话框（验证旧邮箱）
  const dialogChangeEmailStep1 = ref(false)
  //控制更换邮箱第二步对话框（验证新邮箱）
  const dialogChangeEmailStep2 = ref(false)

  // ============ 方法  ============

  //邮箱脱敏显示：保留前2位，中间用****代替
  const maskEmail = (email) => {
    return email ? email.replace(/^(.{2}).*(@.*)$/, '$1****$2') : ''
  }

  //开始获取验证码倒计时（60秒内不允许再次发送）
  const startCountdown = () => {
    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
      }
    }, 1000)
  }

  //开始新邮箱验证码倒计时（60秒内不允许再次发送）
  const startCountdownNew = () => {
    countdownNew.value = 60
    countdownNewTimer = setInterval(() => {
      countdownNew.value--
      if (countdownNew.value <= 0) {
        clearInterval(countdownNewTimer)
      }
    }, 1000)
  }

  //组件销毁时清掉倒计时定时器，防止内存泄漏
  onBeforeUnmount(() => {
    clearInterval(countdownTimer)
    clearInterval(countdownNewTimer)
  })

  //重置两个验证码的倒计时：重新打开对话框时从60秒重新开始计算
  const resetCountdowns = () => {
    countdown.value = 0
    countdownNew.value = 0
    clearInterval(countdownTimer)
    clearInterval(countdownNewTimer)
  }

  //发送邮箱验证码
  //scene为BIND_EMAIL时验证码发到新填写的邮箱（绑定邮箱），其余场景由后端发到当前绑定的旧邮箱
  const sendEmailCode = (scene, email) => {
    //倒计时没结束不允许再次发送
    if (countdown.value > 0) return
    userApi.sendEmailCode({scene: scene, email: email}).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
        startCountdown()
      } else {
        ElMessage.error(result.msg)
      }
    })
  }

  //发送新邮箱的验证码（仅更换邮箱第二步使用，验证码发到新填写的邮箱）
  const sendNewEmailCode = () => {
    //倒计时没结束或新邮箱还没填写时不允许发送
    if (countdownNew.value > 0 || !emailDTO.value.email) return
    userApi.sendEmailCode({scene: 'CHANGE_EMAIL_NEW', email: emailDTO.value.email}).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
        startCountdownNew()
      } else {
        ElMessage.error(result.msg)
      }
    })
  }

  // ============ 方法  ============
  //获取用户信息
  const getUserInfo = () => {
    userApi.userInfo().then(result => {
      if(result.code === 1) {
        userInfoStore.setUserInfo(result.data.user)
        menuData.value = result.data.routerList
        userInfoStore.setBtnList(result.data.btnList)
      }
    })
  }
  getUserInfo()


  //上传图片
  const handleAvatarSuccess = (result) => {
    user.value.avatar = result.data;
  }

  //通过邮箱验证码修改密码
  const updatePasswordByEmail = async (formEl) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        ElMessageBox.confirm(
            '确定修改密码？修改后需要重新登录',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        ).then(() => {
          userApi.updatePasswordByEmail(userPasswordDTO.value).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              dialogResetPasswordDialog.value = false
              tokenStore.removeToken();
              userInfoStore.removeUserInfo();
              router.push('/login')
            } else {
              ElMessage.error(result.msg)
            }
          })
        })
      } else {
        ElMessage.error('表单验证失败');
      }
    })
  }

  //提交绑定邮箱（未绑定邮箱时使用，验证码发到新填写的邮箱）
  const submitBindEmail = async (formEl) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        userApi.bindEmail(emailDTO.value).then(result => {
          if (result.code === 1) {
            ElMessage.success(result.msg)
            dialogBindEmailVisible.value = false
            getUserInfo()
          } else {
            ElMessage.error(result.msg)
          }
        })
      } else {
        ElMessage.error('表单验证失败');
      }
    })
  }

  //更换邮箱第一步：校验旧邮箱验证码，校验通过后进入第二步验证新邮箱
  const nextChangeEmailStep = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
      if (valid) {
        //后端校验旧邮箱验证码，校验通过不消耗验证码，最后一步提交时再统一校验作废
        userApi.checkEmailCode({scene: 'CHANGE_EMAIL', code: emailDTO.value.code}).then(result => {
          if (result.code === 1) {
            dialogChangeEmailStep1.value = false
            dialogChangeEmailStep2.value = true
          } else {
            ElMessage.error(result.msg)
          }
        })
      } else {
        ElMessage.error('表单验证失败');
      }
    })
  }

  //更换邮箱第二步：提交新邮箱和新邮箱验证码，后端会再次校验新旧两个验证码后完成更换
  const submitNewEmail = async (formEl) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        userApi.updateEmail(emailDTO.value).then(result => {
          if (result.code === 1) {
            ElMessage.success(result.msg)
            dialogChangeEmailStep2.value = false
            getUserInfo()
          } else {
            ElMessage.error(result.msg)
          }
        })
      } else {
        ElMessage.error('表单验证失败');
      }
    })
  }

  //更换邮箱第二步点上一步，回到第一步重新核对旧邮箱验证码
  const backToStep1 = () => {
    dialogChangeEmailStep2.value = false
    dialogChangeEmailStep1.value = true
  }

  //修改当前登录的用户信息
  const updateUserInfo = () => {
    userApi.update(user.value.id, user.value).then(result => {
      if (result.code == 1) {
        ElMessage.success(result.msg)
        dialogFormVisible.value = false
        getUserInfo()
      }
    })
  }

  //左上角下拉菜单点击功能
  const handleCommand = (command) => {
    //判断指令
    if (command === 'logout') {
        ElMessageBox.confirm(
            '确认退出吗？',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        ).then(() => {
            //退出登录
            tokenStore.removeToken()
            userInfoStore.removeUserInfo()
            ElMessage.success('退出登录成功')
            router.push('/login')
        })
    } else if (command === 'updateUserInfo') {
      dialogFormVisible.value = true
      //这样下会有严重问题，两个数据是绑定在一起的，修改了admin里面数据，adminInfoStore.admin也会修改，
      //如果用户点击取消没有修改，就会造成adminInfoStore.admin里面数据修改了
      //admin.value = adminInfoStore.admin
      Object.assign(user.value, userInfoStore.user) //不把两个数据绑定在一起
    } else if (command === 'resetPassword') {
      //未绑定邮箱时无法通过邮箱验证码修改密码，引导先绑定邮箱
      if (!userInfoStore.user.email) {
        ElMessageBox.confirm(
            '您尚未绑定邮箱，修改密码需要先绑定邮箱，是否现在绑定？',
            '提示',
            {
              confirmButtonText: '去绑定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        ).then(() => {
          handleCommand('updateEmail')
        })
        return
      }
      dialogResetPasswordDialog.value = true
      userPasswordDTO.value = {code: '', newPassword: '', reNewPassword: ''}
      nextTick(()=>{
        resetForm.value.resetFields()
      })
    } else if (command === 'updateEmail') {
      //已绑定邮箱时走更换邮箱的两步流程，未绑定时直接弹绑定邮箱对话框
      resetCountdowns()
      emailDTO.value = {email: '', code: '', newCode: ''}
      if (userInfoStore.user.email) {
        dialogChangeEmailStep1.value = true
        nextTick(()=>{
          step1Form.value.resetFields()
        })
      } else {
        dialogBindEmailVisible.value = true
        nextTick(()=>{
          bindEmailForm.value.resetFields()
        })
      }
    } else {
      //路由
      router.push('/user/' + command)
    }
  }

  // ============ 规则校验  ============
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

  //自定义确认密码校验函数
  const rePasswordValid = (rule, value, callback) => {
    if (value == null || value == ''){
      return callback(new Error('请再次确认密码'))
    }
    if(userPasswordDTO.value.newPassword !== value) {
      return callback(new Error('两次输入的密码不一致'))
    }
    callback()
  }

  //表单校验规则
  const rules = ref({
    code: [
      {required: true, message: '请输入验证码', trigger: 'blur'},
      {min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur'}
    ],
    newPassword: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 3, max: 16, message: '密码长度必须为3~16位', trigger: 'blur'}
    ],
    reNewPassword: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {validator: rePasswordValid, trigger: 'blur' }
    ]
  })

  //绑定邮箱的表单校验规则
  const bindEmailRules = ref({
    email: [
      {required: true, message: '请输入邮箱', trigger: 'blur'},
      {type: 'email', message: '邮箱格式不正确', trigger: 'blur'}
    ],
    code: [
      {required: true, message: '请输入验证码', trigger: 'blur'},
      {min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur'}
    ]
  })

  //更换邮箱第一步的表单校验规则（只校验旧邮箱验证码）
  const step1Rules = ref({
    code: [
      {required: true, message: '请输入验证码', trigger: 'blur'},
      {min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur'}
    ]
  })

  //更换邮箱第二步的表单校验规则（校验新邮箱和新邮箱验证码）
  const step2Rules = ref({
    email: [
      {required: true, message: '请输入新邮箱', trigger: 'blur'},
      {type: 'email', message: '邮箱格式不正确', trigger: 'blur'}
    ],
    newCode: [
      {required: true, message: '请输入新邮箱验证码', trigger: 'blur'},
      {min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur'}
    ]
  })
</script>

<template>
  <!-- element-plus中的容器 -->
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <!-- 用el-scrollbar包裹菜单：菜单过长时显示细圆角滚动条，替代丑陋的原生滚动条 -->
      <el-scrollbar class="aside-scrollbar">
        <!-- element-plus的菜单标签 -->
        <el-menu :default-active="$route.path" active-text-color="#409EFF" text-color="#303133"
                 :background-color="'#fff'" router>
        <!-- 动态生成菜单 -->
        <template v-for="(menu, index) in menuData" :index="index.toString()">
          <el-sub-menu v-if="menu.children?.length>0" :index="menu.name">
            <template #title>
              <component
                  class="icons"
                  :is="menu.icon"
                  style="width: 1em; height: 1em; margin-right: 8px" >
              </component>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item v-for="(child, ind) in menu.children" :index="child.path">
              <el-icon><component :is="child.icon"></component></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon"></component></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
      </el-scrollbar>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>
        <div><strong>智慧社区养老平台后台管理系统</strong></div>
        <!-- 下拉菜单 -->
        <!-- command: 条目被点击后会触发,在事件函数上可以声明一个参数,接收条目对应的指令 -->
        <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.user.avatar?userInfoStore.user.avatar:avatar"/>
                        <span style="margin-left: 8px;">欢迎您：</span>
                        <strong>{{ userInfoStore.user.name }}</strong>
                        <el-icon>
                            <CaretBottom/>
                        </el-icon>
                    </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="updateUserInfo" :icon="User">基本资料</el-dropdown-item>
              <el-dropdown-item command="resetPassword" :icon="EditPen">修改密码</el-dropdown-item>
              <el-dropdown-item command="updateEmail" :icon="Message">{{ userInfoStore.user.email ? '更换邮箱' : '绑定邮箱' }}</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <!-- <div style="width: 1290px; height: 570px;border: 1px solid red;">
                    内容展示区
                </div> -->
        <router-view></router-view>
      </el-main>
      <!-- 底部区域 -->
      <el-footer>
        <span>Copyright © 2026 Tinsur All rights reserved</span>
        <a class="beian-link" href="https://beian.miit.gov.cn/" target="_blank" rel="nofollow">鲁ICP备2023032291号-1</a>
        <a class="beian-link" href="https://beian.mps.gov.cn/#/query/webSearch?code=37021402000083" target="_blank" rel="noreferrer">
          <img src="//www.tinsur.cn/beian.png" alt="公安备案" class="beian-ico"/>鲁公网安备37021402000083号
        </a>
      </el-footer>
    </el-container>
  </el-container>

  <!-- 修改个人信息的对话框 -->
  <el-dialog v-model="dialogFormVisible" :title="'个人信息'" width="500" :lock-scroll="false" :close-on-click-modal="false">
    <el-form :model="user">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/admin/api/upload?dir=avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}">
          <img v-if="user.avatar" :src="user.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item label="用户名" :label-width="60">
        <el-input v-model="user.name" autocomplete="off" :disabled="user.id"/>
      </el-form-item>
      <el-form-item label="姓名" :label-width="60">
        <el-input v-model="user.realName" autocomplete="off"/>
      </el-form-item>
      <!-- 邮箱只能通过下拉菜单里的绑定/更换邮箱（邮箱验证码验证）修改，这里只做展示 -->
      <el-form-item label="邮箱" :label-width="60">
        <el-input :model-value="maskEmail(userInfoStore.user.email)" placeholder="未绑定" disabled/>
      </el-form-item>
      <el-form-item label="手机号" :label-width="60">
        <el-input v-model="user.phone" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUserInfo">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 通过邮箱验证码修改密码的对话框 -->
  <el-dialog  v-model="dialogResetPasswordDialog" title="修改密码" width="500" :lock-scroll="false">
    <el-form ref="resetForm" :rules="rules" :model="userPasswordDTO">
      <el-form-item label="绑定邮箱" :label-width="100">
        <el-input :model-value="maskEmail(userInfoStore.user.email)" disabled/>
      </el-form-item>
      <el-form-item prop="code" label="邮箱验证码" :label-width="100">
        <div class="code-row">
          <el-input v-model="userPasswordDTO.code" autocomplete="off" placeholder="请输入6位验证码"/>
          <el-button type="primary" plain :disabled="countdown > 0" @click="sendEmailCode('CHANGE_PASSWORD')">
            {{ countdown > 0 ? countdown + '秒后重发' : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item prop="newPassword" label="新密码" :label-width="100">
        <el-input v-model="userPasswordDTO.newPassword" type="password" show-password autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="reNewPassword" label="重复新密码" :label-width="100">
        <el-input v-model="userPasswordDTO.reNewPassword" type="password" show-password autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogResetPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="updatePasswordByEmail(resetForm)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 绑定邮箱的对话框：未绑定邮箱时使用，验证码发到新填写的邮箱，单步完成绑定 -->
  <el-dialog v-model="dialogBindEmailVisible" title="绑定邮箱" width="500" :lock-scroll="false">
    <el-form ref="bindEmailForm" :rules="bindEmailRules" :model="emailDTO">
      <el-form-item prop="email" label="邮箱" :label-width="100">
        <el-input v-model="emailDTO.email" autocomplete="off" placeholder="请输入邮箱"/>
      </el-form-item>
      <el-form-item prop="code" label="邮箱验证码" :label-width="100">
        <div class="code-row">
          <el-input v-model="emailDTO.code" autocomplete="off" placeholder="请输入6位验证码"/>
          <el-button type="primary" plain :disabled="countdown > 0" @click="sendEmailCode('BIND_EMAIL', emailDTO.email)">
            {{ countdown > 0 ? countdown + '秒后重发' : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogBindEmailVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBindEmail(bindEmailForm)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 更换邮箱第一步的对话框：先验证旧邮箱，验证码发到当前绑定的旧邮箱，点下一步时后端校验但不作废 -->
  <el-dialog v-model="dialogChangeEmailStep1" title="更换邮箱 - 第一步 验证旧邮箱" width="500" :lock-scroll="false">
    <el-form ref="step1Form" :rules="step1Rules" :model="emailDTO">
      <el-form-item label="当前邮箱" :label-width="100">
        <el-input :model-value="maskEmail(userInfoStore.user.email)" disabled/>
      </el-form-item>
      <el-form-item prop="code" label="邮箱验证码" :label-width="100">
        <div class="code-row">
          <el-input v-model="emailDTO.code" autocomplete="off" placeholder="请输入6位验证码"/>
          <el-button type="primary" plain :disabled="countdown > 0" @click="sendEmailCode('CHANGE_EMAIL')">
            {{ countdown > 0 ? countdown + '秒后重发' : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogChangeEmailStep1 = false">取消</el-button>
        <el-button type="primary" @click="nextChangeEmailStep(step1Form)">
          下一步
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 更换邮箱第二步的对话框：填写新邮箱并验证，验证码发到新填写的邮箱，确认时后端统一校验新旧两个验证码 -->
  <el-dialog v-model="dialogChangeEmailStep2" title="更换邮箱 - 第二步 验证新邮箱" width="500" :lock-scroll="false">
    <el-form ref="step2Form" :rules="step2Rules" :model="emailDTO">
      <el-form-item prop="email" label="新邮箱" :label-width="100">
        <el-input v-model="emailDTO.email" autocomplete="off" placeholder="请输入新邮箱"/>
      </el-form-item>
      <el-form-item prop="newCode" label="邮箱验证码" :label-width="100">
        <div class="code-row">
          <el-input v-model="emailDTO.newCode" autocomplete="off" placeholder="请输入6位验证码"/>
          <el-button type="primary" plain :disabled="countdownNew > 0 || !emailDTO.email" @click="sendNewEmailCode">
            {{ countdownNew > 0 ? countdownNew + '秒后重发' : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="backToStep1">上一步</el-button>
        <el-button type="primary" @click="submitNewEmail(step2Form)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
  .layout-container {
    height: 100vh;

    .el-aside {
      background-color: #fff;
      border-right: 1px solid #f0f0f0;
      display: flex;
      flex-direction: column;

      &__logo {
        height: 120px;
        background: url('@/assets/logo.png') no-repeat center / 170px auto;
        border-bottom: 1px solid #f5f5f5;
      }

      // 侧边栏滚动区域：占据logo以下的所有剩余高度，菜单过长时出现细滚动条
      .aside-scrollbar {
        flex: 1;

        // 美化滚动条：6px细轨道、圆角浅灰滑块，悬停时加深
        :deep(.el-scrollbar__bar.is-vertical) {
          width: 6px;
          right: 2px;
        }

        :deep(.el-scrollbar__thumb) {
          background-color: #c0c4cc;
          border-radius: 3px;
        }
      }

      .el-menu {
        border-right: none;

        .el-menu-item.is-active {
          background-color: #ecf5ff;
          border-right: 3px solid var(--el-color-primary);
          font-weight: 600;
        }
      }
    }

    .el-header {
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid #f0f0f0;
      padding: 0 20px;

      .el-dropdown__box {
        display: flex;
        align-items: center;

        .el-icon {
          color: #999;
          margin-left: 10px;
        }

        &:active,
        &:focus {
          outline: none;
        }
      }
    }

    .el-main {
      background-color: #f5f7fa;
      padding: 16px;
    }

    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 20px;
      font-size: 14px;
      color: #909399;
      background-color: #fff;
      height: 44px;
      border-top: 1px solid #f0f0f0;

      .beian-link {
        color: #909399;
        text-decoration: none;

        &:hover {
          color: var(--el-color-primary);
        }
      }

      .beian-ico {
        width: 15px;
        height: auto;
        vertical-align: middle;
        margin: 0 4px 2px 0;
      }
    }
  }

  //对话框头像区域样式
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
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

  //验证码输入框和获取验证码按钮的同行布局
  .code-row {
    display: flex;
    gap: 10px;
    width: 100%;

    .el-button {
      flex-shrink: 0;
    }
  }
</style>