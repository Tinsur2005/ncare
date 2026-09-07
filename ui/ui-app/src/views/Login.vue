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
  import {ref} from 'vue'
  import {showToast, showSuccessToast} from 'vant'
  import {useRouter} from 'vue-router'
  import userApi from '@/api/user.js'
  import {useTokenStore} from '@/store/token.js'
  import {useUserInfoStore} from '@/store/userInfo.js'
  //系统logo
  import logo from '@/assets/logo.png'

  const tokenStore = useTokenStore()
  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  //表单校验模型
  const rules = {
    name: [
      {required: true, message: '请输入用户名'},
      {min: 4, max: 16, message: '长度在 4 到 16 个字符'}
    ],
    password: [
      {required: true, message: '请输入密码'},
      {min: 6, max: 16, message: '长度在 6 到 16 个字符'}
    ]
  }

  //定义数据模型（userType 与选中的登录Tab对应：elder老人 / family家属）
  const userType = ref('elder')
  const user = ref({
    name: '',
    password: ''
  })

  const formRef = ref()

  const login = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          userApi.login({...user.value, userType: userType.value}).then(result => {
            if (result.code === 1) {
              // 登录成功：保存token，再查询登录用户信息（老人自己的档案 / 家属绑定的老人列表）
              showSuccessToast(result.msg)
              tokenStore.setToken(result.data)
              userApi.userInfo().then(res => {
                if (res.code === 1) {
                  userInfoStore.setUserInfo(res.data.user)
                  userInfoStore.setUserType(res.data.userType)
                  userInfoStore.setElders(res.data.elders)
                  // 老人登录时保存绑定的家属/监护人（家属登录时为null）
                  userInfoStore.setFamily(res.data.family || null)
                  // 家属默认选中第一位绑定老人，老人登录则置空
                  userInfoStore.setCurrentElderId(
                      res.data.userType === 'family' && res.data.elders.length > 0 ? res.data.elders[0].id : null)
                  router.push('/home')
                }
              })
            } else {
              showToast(result.msg)
            }
          })
        })
        .catch(() => {
          //校验失败
          showToast('请检查表单填写是否正确')
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
    <van-notice-bar class="demo-notice" left-icon="info-o" wrapable :scrollable="false">
      请注意：本系统为演示系统，仅供作品演示使用，不具有任何服务性质，请勿在系统填写敏感信息！
    </van-notice-bar>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 系统logo -->
      <img class="brand-logo" :src="logo" alt="智慧养老系统"/>
      <p class="brand-slogan">让每一位老人都能安享幸福晚年</p>

      <!-- 登录表单 -->
      <!-- van-form 的校验规则配置在每个 van-field 上，通过 formRef.validate() 整体校验 -->
      <van-form ref="formRef" autocomplete="off">
        <van-tabs v-model:active="userType" shrink>
          <van-tab title="老人登录" name="elder"></van-tab>
          <van-tab title="家属登录" name="family"></van-tab>
        </van-tabs>
        <div class="field-list">
          <van-field
              v-model="user.name"
              name="name"
              left-icon="contact"
              placeholder="请输入用户名"
              :rules="rules.name"
          />
          <van-field
              v-model="user.password"
              name="password"
              left-icon="lock"
              type="password"
              placeholder="请输入密码"
              :rules="rules.password"
          />
        </div>
        <!-- 登录按钮 -->
        <div class="login-button">
          <van-button round block type="primary" native-type="button" @click="login">登 录</van-button>
        </div>
      </van-form>

      <!-- 温馨提示 -->
      <div class="demo-tips">
        <p>老人演示账号：yaozhenfu/123456</p>
        <p>家属演示账号：zhangsan/123456</p>
        <p>后端管理页面演示请点击：<a href="https://ncare.tinsur.cn/admin">管理后台</a></p>
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
  /* 整页浅色背景加居中登录卡片布局，仿照后台登录页风格 */
  .login-page {
    min-height: 100vh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    padding: 24px;
    background-color: #EEF3FB;
    position: relative;
    overflow: hidden;
  }

  /* 演示系统提示条与登录卡片同宽 */
  .demo-notice {
    width: 100%;
    max-width: 400px;
    border-radius: 8px;
    overflow: hidden;
  }

  /* ================== 页面背景装饰 ================== */

  .bg-decoration {
    position: absolute;
    pointer-events: none;
  }

  /* 左下角和右上角的大圆 */
  .bg-circle-one {
    width: 280px;
    height: 280px;
    border-radius: 50%;
    background-color: #DEEDFD;
    left: -100px;
    bottom: -110px;
  }

  .bg-circle-two {
    width: 240px;
    height: 240px;
    border-radius: 50%;
    background-color: #DEEDFD;
    right: -90px;
    top: -100px;
  }

  /* 空心圆环 */
  .bg-ring {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    border: 14px solid #DEEDFD;
    right: 60px;
    top: 120px;
  }

  /* 圆点群，用径向渐变画出一组小圆点 */
  .bg-dots {
    width: 100px;
    height: 100px;
    background-image: radial-gradient(#C9DEF8 3px, transparent 3px);
    background-size: 26px 26px;
  }

  .bg-dots-one {
    left: 40px;
    top: 50px;
  }

  .bg-dots-two {
    right: 50px;
    bottom: 60px;
  }

  /* ================== 登录卡片 ================== */

  .login-card {
    width: 100%;
    max-width: 400px;
    box-sizing: border-box;
    padding: 32px 24px 24px;
    border-radius: 20px;
    background-color: #FFFFFF;
    box-shadow: 0 12px 40px rgba(25, 137, 250, 0.12);
    position: relative;
  }

  /* 顶部系统logo，与后台登录页同一张图 */
  .brand-logo {
    display: block;
    width: 190px;
    height: auto;
    margin: 0 auto 10px;
  }

  .brand-slogan {
    text-align: center;
    font-size: 13px;
    color: #999;
    letter-spacing: 1px;
    margin: 0 0 20px;
  }

  /* 老人/家属切换Tab */
  .login-card :deep(.van-tabs) {
    margin: 0 8px;
  }

  .login-card :deep(.van-tab--active) {
    color: #1989FA;
  }

  /* 圆角浅灰填充风格的输入框 */
  .field-list {
    margin: 16px 8px 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .field-list :deep(.van-field) {
    background-color: #F5F6FA;
    border-radius: 22px;
    padding: 9px 14px;
    overflow: hidden;
  }

  .field-list :deep(.van-field__left-icon) {
    color: #1989FA;
    margin-right: 8px;
  }

  /* 登录按钮 */
  .login-button {
    margin: 24px 8px 0;
  }

  /* 温馨提示 */
  .demo-tips {
    margin-top: 16px;
    text-align: center;
    font-size: 12px;
    color: #999;
    line-height: 20px;
  }

  /* 底部备案信息：小号浅灰字，弱化存在感 */
  .beian-footer {
    position: absolute;
    bottom: calc(10px + env(safe-area-inset-bottom));
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
    gap: 12px;
    font-size: 11px;
  }

  .beian-footer a {
    color: #C0C4CC;
    text-decoration: none;
  }

  .beian-footer a:active {
    color: #909399;
  }
</style>