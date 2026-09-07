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
  import {ref} from "vue";
  import {User, Lock, House, FirstAidKit, AlarmClock} from "@element-plus/icons-vue";
  import {ElMessage} from "element-plus";
  import {useRouter} from "vue-router";
  import userApi from "@/api/user.js";
  import {useTokenStore} from "@/store/token.js";

  const tokenStore = useTokenStore();
  const router = useRouter()

  //系统logo
  import logo from '@/assets/logo.png'

  //表单校验模型
  const rules = {
    name: [
      {required: true, message: '请输入用户名', trigger: 'blur'},
      {min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur'}
    ],
    password: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
    ]
  }

  //定义数据模型
  const user = ref({
    name: '',
    password: ''
  })

  //左侧品牌展示区的社区服务亮点
  const brandFeatures = [
    {icon: AlarmClock, text: '全天候健康守护，异常情况及时预警'},
    {icon: FirstAidKit, text: '专业护理服务，护理计划一键安排'},
    {icon: House, text: '智慧社区养老，让陪伴更有温度'}
  ]

  const login = () => {

    userApi.login(user.value).then(result => {
      if(result.code == 1) {
        ElMessage.success(result.msg)
        tokenStore.setToken(result.data)
        router.push('/')
      } else {
        ElMessage.error(result.msg)
      }
    })

  }
</script>

<template>
  <div class="login-page">
    <!-- 页面背景装饰 -->
    <div class="bg-decoration bg-circle-one"></div>
    <div class="bg-decoration bg-circle-two"></div>
    <div class="bg-decoration bg-ring"></div>
    <div class="bg-decoration bg-dots bg-dots-one"></div>
    <div class="bg-decoration bg-dots bg-dots-two"></div>

    <!-- 演示系统提示 -->
    <el-alert class="demo-alert" type="warning" show-icon :closable="false" center
              title="请注意：本系统为演示系统，仅供作品演示使用，不具有任何服务性质，请勿在系统填写敏感信息！"/>

    <!-- 居中登录大卡片 -->
    <div class="login-card">
      <!-- 卡片左侧品牌展示区 -->
      <div class="card-left">
        <h2 class="welcome-title">睦邻NCare</h2>
        <p class="welcome-sub">智慧社区养老后台管理系统</p>
        <!-- 社区slogan -->
        <div class="brand-slogan">
          <h3 class="slogan-title">用心服务<br/>用爱陪伴</h3>
          <p class="slogan-sub">让每一位老人都能安享幸福晚年</p>
        </div>
        <!-- 服务亮点 -->
        <div class="brand-features">
          <div class="feature-item" v-for="feature in brandFeatures" :key="feature.text">
            <el-icon class="feature-icon" :size="18"><component :is="feature.icon"/></el-icon>
            <span>{{ feature.text }}</span>
          </div>
        </div>
      </div>

      <!-- 卡片右侧登录表单区 -->
      <div class="card-right">
        <!-- 系统logo -->
        <div class="brand-header">
          <img class="brand-logo-img" :src="logo" alt="智慧养老系统"/>
        </div>
        <el-form ref="form" size="large" autocomplete="off" :model="user" :rules="rules">
          <div class="field-label">用户名 / user name</div>
          <el-form-item prop="name">
            <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="user.name"></el-input>
          </el-form-item>
          <div class="field-label">密码 / password</div>
          <el-form-item prop="password">
            <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                      v-model="user.password" show-password></el-input>
          </el-form-item>
          <el-form-item class="flex">
            <div class="flex">
              <el-checkbox>记住我</el-checkbox>
            </div>
          </el-form-item>
          <!-- 登录按钮 -->
          <el-form-item>
            <el-button class="button" type="primary" auto-insert-space @click="login">登 录</el-button>
          </el-form-item>
        </el-form>

        <!-- 温馨提示 -->
        <div class="demo-tips">
          <p>管理员演示账号：admin/123456</p>
          <p>前台手机端演示请点击：<a href="https://ncare.tinsur.cn">手机端</a></p>
        </div>
      </div>
    </div>

    <!-- 底部备案信息 -->
    <div class="beian-footer">
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="nofollow">鲁ICP备2023032291号-1</a>
      <a href="https://beian.mps.gov.cn/#/query/webSearch?code=37021402000083" target="_blank" rel="noreferrer">鲁公网安备37021402000083号</a>
    </div>
  </div>

</template>

<style scoped>
  /* 整页浅色背景加居中大卡片布局 */
  .login-page {
    height: 100vh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    background-color: #EEF3FB;
    position: relative;
    overflow: hidden;
  }

  /* 演示提示条与登录卡片同宽 */
  .demo-alert {
    width: 1060px;
    max-width: 92vw;
  }

  /* ================== 页面背景装饰 ================== */

  .bg-decoration {
    position: absolute;
    pointer-events: none;
  }

  /* 左下角和右上角的大圆 */
  .bg-circle-one {
    width: 420px;
    height: 420px;
    border-radius: 50%;
    background-color: #E0EAF8;
    left: -140px;
    bottom: -160px;
  }

  .bg-circle-two {
    width: 360px;
    height: 360px;
    border-radius: 50%;
    background-color: #E0EAF8;
    right: -120px;
    top: -140px;
  }

  /* 空心圆环 */
  .bg-ring {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 22px solid #E0EAF8;
    right: 180px;
    top: 60px;
  }

  /* 圆点群，用径向渐变画出一组小圆点 */
  .bg-dots {
    width: 130px;
    height: 130px;
    background-image: radial-gradient(#C7D8F0 4px, transparent 4px);
    background-size: 34px 34px;
  }

  .bg-dots-one {
    left: 90px;
    top: 90px;
  }

  .bg-dots-two {
    right: 120px;
    bottom: 100px;
  }

  /* ================== 居中登录大卡片 ================== */

  .login-card {
    width: 1060px;
    max-width: 92vw;
    height: 640px;
    display: flex;
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(64, 158, 255, 0.15);
    position: relative;
  }

  /* ---------- 卡片左侧品牌展示区 ---------- */

  .card-left {
    flex: 1;
    box-sizing: border-box;
    padding: 56px 60px;
    display: flex;
    flex-direction: column;
    color: #FFFFFF;
    /* 与首页统计卡的蓝色渐变保持一致 */
    background: linear-gradient(120deg, #409eff 0%, #53a8ff 55%, #409eff 100%);
  }

  .welcome-title {
    font-size: 40px;
    letter-spacing: 4px;
    margin: 0 0 10px;
  }

  .welcome-sub {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.85);
    letter-spacing: 2px;
    margin: 0;
  }

  /* 中部slogan */
  .brand-slogan {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .slogan-title {
    font-size: 38px;
    line-height: 1.6;
    letter-spacing: 6px;
    margin: 0 0 14px;
  }

  .slogan-sub {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.85);
    letter-spacing: 2px;
    margin: 0;
  }

  /* 底部服务亮点 */
  .brand-features {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .feature-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 15px;
    color: rgba(255, 255, 255, 0.9);
  }

  .feature-icon {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    padding: 6px;
  }

  /* ---------- 卡片右侧登录表单区 ---------- */

  .card-right {
    width: 420px;
    flex-shrink: 0;
    background-color: #FFFFFF;
    box-sizing: border-box;
    padding: 56px 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  /* 顶部系统logo，与后台侧边栏顶部同一张图 */
  .brand-header {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 40px;
  }

  .brand-logo-img {
    width: 240px;
    height: auto;
  }

  /* 输入框上方的字段说明文字 */
  .field-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  /* 圆角浅灰填充风格的输入框 */
  .card-right :deep(.el-input__wrapper) {
    border-radius: 20px;
    background-color: #F5F7FA;
    box-shadow: 0 0 0 1px #F5F7FA inset;
  }

  .card-right :deep(.el-input__wrapper.is-focus) {
    background-color: #FFFFFF;
    box-shadow: 0 0 0 1px #409eff inset;
  }

  /* 圆角登录按钮 */
  .card-right .button {
    width: 100%;
    border-radius: 20px;
    letter-spacing: 4px;
  }

  /* 温馨提示 */
  .demo-tips {
    margin-top: 20px;
    text-align: center;
    font-size: 12px;
    color: #999;
    line-height: 20px;
  }

  .demo-tips a {
    color: #409eff;
    text-decoration: none;
  }

  /* 底部备案信息：小号浅灰字，弱化存在感 */
  .beian-footer {
    position: absolute;
    bottom: 10px;
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
    gap: 16px;
    font-size: 12px;
  }

  .beian-footer a {
    color: #A8ABB2;
    text-decoration: none;
  }

  .beian-footer a:hover {
    color: #909399;
  }

  /* 窄屏时隐藏左侧品牌区，只保留登录表单 */
  @media (max-width: 900px) {
    .card-left {
      display: none;
    }

    .login-card {
      width: 480px;
      height: auto;
    }
  }
</style>