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
  import {computed, ref, watch} from 'vue'
  import {useRouter} from 'vue-router'
  import {useUserInfoStore} from '@/store/userInfo.js'
  import userApi from '@/api/user.js'

  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  // 当前查看的老人：老人登录是自己，家属登录是当前选中的绑定老人
  const currentElder = computed(() => {
    if (userInfoStore.userType === 'family') {
      return userInfoStore.elders.find(item => item.id === userInfoStore.currentElderId) || {}
    }
    return userInfoStore.user
  })

  // ================== 床位 ==================

  // 当前老人的入住床位信息（楼栋楼层房间床位），未入住时为null不显示床位信息
  const bedInfo = ref(null)

  // 加载当前老人的床位信息（家属切换老人后重新加载）
  const loadBedInfo = () => {
    if (!currentElder.value.id) {
      bedInfo.value = null
      return
    }
    userApi.bedInfo(currentElder.value.id).then(result => {
      bedInfo.value = result.data
    })
  }
  loadBedInfo()

  //家属切换查看的老人后刷新床位信息
  watch(() => userInfoStore.currentElderId, loadBedInfo)

  // ================== 选项 ==================

  // 性别选项（gender：0女 1男）
  const genderOptions = [
    {value: 1, label: '男'},
    {value: 0, label: '女'},
  ]

  // 状态选项（状态：0已停用 1正常 2请假 3退住中 4入住中 5已退住）
  const statusOptions = [
    {value: 0, label: '已停用'},
    {value: 1, label: '正常'},
    {value: 2, label: '请假'},
    {value: 3, label: '退住中'},
    {value: 4, label: '入住中'},
    {value: 5, label: '已退住'},
  ]

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

  //根据性别获取展示文本
  const getGender = (value) => {
    return genderOptions.find(option => option.value === value)?.label || '-'
  }

  //根据状态获取展示文本
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value)?.label || '-'
  }
</script>

<template>
  <div class="elder-info">
    <van-nav-bar title="个人信息" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 头像卡片 -->
    <div class="info-header">
      <div class="info-avatar">
        <van-image v-if="currentElder.avatar" round width="60" height="60" fit="cover" :src="currentElder.avatar"/>
        <van-icon v-else name="user-o" size="30" color="#1989FA"/>
      </div>
      <div class="info-header-name">
        <h3>{{ currentElder.realName }}</h3>
        <p>{{ getGender(currentElder.gender) }} · {{ getAge(currentElder.birthday) }}岁</p>
      </div>
    </div>

    <!-- 基础信息 -->
    <van-cell-group inset title="基础信息" class="info-group">
      <van-cell title="姓名" :value="currentElder.realName"/>
      <van-cell title="性别" :value="getGender(currentElder.gender)"/>
      <van-cell title="出生日期" :value="currentElder.birthday ? currentElder.birthday.slice(0, 10) : '-'"/>
      <van-cell title="年龄" :value="`${getAge(currentElder.birthday)}岁`"/>
      <van-cell title="身份证号" :value="currentElder.idCardNo"/>
      <van-cell title="联系电话" :value="currentElder.phone"/>
      <van-cell title="家庭住址" :value="currentElder.address"/>
      <van-cell title="当前状态" :value="getStatus(currentElder.status)"/>
      <van-cell title="备注" :value="currentElder.remark || '-'"/>
    </van-cell-group>

    <!-- 床位信息（未入住时不显示） -->
    <van-cell-group inset title="床位信息" class="info-group" v-if="bedInfo">
      <van-cell title="楼栋" :value="bedInfo.buildingName"/>
      <van-cell title="楼层" :value="`${bedInfo.floorNo}层`"/>
      <van-cell title="房间" :value="`${bedInfo.roomNo}房`"/>
      <van-cell title="床位" :value="bedInfo.bedNo"/>
    </van-cell-group>
  </div>
</template>

<style scoped>
  .elder-info {
    min-height: 100%;
    padding: 12px 0 20px;
  }

  .info-header {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 0 16px;
    padding: 20px 16px;
    display: flex;
    align-items: center;
  }

  .info-avatar {
    width: 60px;
    height: 60px;
    background-color: #E8F3FF;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .info-header-name {
    margin-left: 14px;
  }

  .info-header-name h3 {
    font-size: 18px;
  }

  .info-header-name p {
    margin-top: 4px;
    font-size: 13px;
    color: #999;
  }

  .info-group {
    margin-top: 12px;
  }
</style>
