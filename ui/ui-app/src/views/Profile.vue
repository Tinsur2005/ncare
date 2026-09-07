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
  import {computed, ref} from 'vue'
  import {showConfirmDialog, showToast} from 'vant'
  import {useRouter} from 'vue-router'
  import {useTokenStore} from '@/store/token.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const tokenStore = useTokenStore()
  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  // 当前登录用户（家属端展示"与老人的关系"，老人端展示"老人标注"）
  const user = computed(() => userInfoStore.user)
  const isFamily = computed(() => userInfoStore.userType === 'family')

  // 当前查看的老人（家属端为选中的绑定老人）
  const currentElder = computed(() => {
    return userInfoStore.elders.find(item => item.id === userInfoStore.currentElderId) || {}
  })

  // 是否显示切换老人弹层
  const showElderPicker = ref(false)

  // 我的页面菜单（家属端展示当前选中老人的信息和合同，老人端固定展示自己的；关于我们、备案信息两端共用）
  const menus = computed(() => {
    if (isFamily.value) {
      const name = currentElder.value.realName || '老人'
      return [
        {title: `${name}的信息`, icon: 'user-circle-o', path: '/elderInfo'},
        {title: `${name}的合同`, icon: 'bill-o', path: '/contract'},
        {title: '关于我们', icon: 'info-o', path: '/about'},
        {title: '备案信息', icon: 'shield-o', path: '/beian'}
      ]
    }
    return [
      {title: '个人信息', icon: 'user-circle-o', path: '/elderInfo'},
      {title: '我的合同', icon: 'bill-o', path: '/contract'},
      {title: '关于我们', icon: 'info-o', path: '/about'},
      {title: '备案信息', icon: 'shield-o', path: '/beian'}
    ]
  })

  // ================== 方法 ==================

  //根据出生日期计算年龄
  const getAge = (birthday) => {
    if (!birthday) {
      return ''
    }
    const birth = new Date(birthday)
    const now = new Date()
    let age = now.getFullYear() - birth.getFullYear()
    // 未过生日时年龄减一
    if (now.getMonth() < birth.getMonth() || (now.getMonth() === birth.getMonth() && now.getDate() < birth.getDate())) {
      age = age - 1
    }
    return age
  }

  //打开切换老人弹层
  const openElderPicker = () => {
    showElderPicker.value = true
  }

  //选择当前查看的老人并关闭弹层
  const selectElder = (elder) => {
    userInfoStore.setCurrentElderId(elder.id)
    showElderPicker.value = false
    showToast(`已切换到 ${elder.realName}`)
  }

  //退出登录
  const logout = () => {
    showConfirmDialog({
      title: '提示',
      message: '确认退出登录么?',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      tokenStore.removeToken()
      userInfoStore.removeUserInfo()
      userInfoStore.removeUserType()
      userInfoStore.removeElders()
      userInfoStore.removeCurrentElderId()
      showToast('已退出登录')
      router.push('/login')
    }).catch(() => {
      //取消退出
    })
  }
</script>

<template>
  <div class="profile">
    <van-nav-bar title="我的" :fixed="true" placeholder/>

    <!-- 用户信息卡 -->
    <div class="profile-header">
      <div class="profile-avatar">
        <van-image v-if="user.avatar" round width="62" height="62" fit="cover" :src="user.avatar"/>
        <van-icon v-else name="manager" size="34" color="#FFFFFF"/>
      </div>
      <div class="profile-user-info">
        <h3>{{ user.realName }}</h3>
        <p>{{ isFamily ? user.relation : '老人' }}</p>
      </div>
    </div>

    <!-- 家属：切换老人入口（底部抽屉选择） -->
    <div class="profile-card" v-if="isFamily && userInfoStore.elders.length > 0">
      <van-cell
          title="切换老人"
          icon="exchange"
          is-link
          :value="currentElder.realName"
          @click="openElderPicker"
      />
    </div>

    <!-- 功能菜单 -->
    <div class="profile-card">
      <van-cell
          v-for="menu in menus"
          :key="menu.path"
          :title="menu.title"
          :icon="menu.icon"
          is-link
          :to="menu.path"
      />
    </div>

    <!-- 退出登录 -->
    <div class="profile-logout">
      <van-button round block plain color="#EE0A24" @click="logout">退出登录</van-button>
    </div>

    <!-- 切换老人弹出框 -->
    <van-popup v-model:show="showElderPicker" position="bottom" round>
      <div class="elder-popup">
        <div class="elder-popup-title">切换老人</div>
        <div
            class="elder-item"
            :class="{'elder-item-active': elder.id === userInfoStore.currentElderId}"
            v-for="elder in userInfoStore.elders"
            :key="elder.id"
            @click="selectElder(elder)"
        >
          <!-- 有头像显示头像，无头像回退默认icon -->
          <van-image v-if="elder.avatar" class="elder-item-avatar" round width="40" height="40" fit="cover" :src="elder.avatar"/>
          <div v-else class="elder-item-avatar elder-item-avatar-fallback">
            <van-icon name="user-o" size="20" color="#999"/>
          </div>
          <div class="elder-item-info">
            <p class="elder-item-name">{{ elder.realName }}（{{ getAge(elder.birthday) }}岁）</p>
            <p class="elder-item-phone">{{ elder.phone }}</p>
          </div>
          <van-icon v-if="elder.id === userInfoStore.currentElderId" name="success" size="18" color="#1989FA"/>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
  .profile {
    min-height: 100%;
    padding: 0 0 20px;
  }

  /* 用户信息卡（蓝色渐变，与首页头部同款配色） */
  .profile-header {
    background: linear-gradient(135deg, #1989FA 0%, #5BA5FA 100%);
    border-radius: 12px;
    margin: 12px 12px 0;
    padding: 26px 20px;
    display: flex;
    align-items: center;
  }

  .profile-avatar {
    width: 62px;
    height: 62px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .profile-user-info {
    margin-left: 16px;
    color: #FFFFFF;
  }

  .profile-user-info h3 {
    font-size: 20px;
    font-weight: bold;
  }

  .profile-user-info p {
    margin-top: 6px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
  }

  /* 白卡列表（菜单/绑定老人） */
  .profile-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 12px 12px 0;
    overflow: hidden;
  }

  .profile-card :deep(.van-cell) {
    font-size: 15px;
    padding: 16px;
  }

  .profile-card :deep(.van-cell__left-icon) {
    font-size: 18px;
    color: #666;
    margin-right: 10px;
  }

  /* 退出登录（红色描边胶囊按钮） */
  .profile-logout {
    margin: 28px 16px 0;
  }

  /* 切换老人弹层 */
  .elder-popup {
    padding: 20px 16px 24px;
    max-height: 60vh;
    overflow-y: auto;
  }

  .elder-popup-title {
    font-size: 16px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 4px;
  }

  .elder-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 8px;
    border-bottom: 1px solid #F0F0F0;
  }

  .elder-item:last-child {
    border-bottom: none;
  }

  .elder-item-active {
    background-color: #E8F3FF;
    border-radius: 8px;
    border-bottom-color: transparent;
  }

  .elder-item-avatar {
    flex-shrink: 0;
  }

  .elder-item-avatar-fallback {
    width: 40px;
    height: 40px;
    background-color: #F5F6FA;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .elder-item-info {
    flex: 1;
    min-width: 0;
  }

  .elder-item-name {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
  }

  .elder-item-phone {
    margin-top: 2px;
    font-size: 12px;
    color: #999;
  }
</style>