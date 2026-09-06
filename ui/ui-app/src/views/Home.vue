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
  import announcementApi from '@/api/announcement.js'
  import newsApi from '@/api/news.js'
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

  // 是否为家属登录（家属首页展示老人卡片切换）
  const isFamily = computed(() => userInfoStore.userType === 'family')

  // ================== 选项 ==================

  // 功能入口（家属端文案与老人端略有区别），color 为入口图标的颜色
  const gridItems = computed(() => {
    if (isFamily.value) {
      return [
        {title: '代发求助', icon: 'warn-o', color: '#1989FA', path: '/help'},
        {title: '代约体检', icon: 'calendar-o', color: '#07C160', path: '/examPackageList'},
        {title: '体检记录', icon: 'records', color: '#FF976A', path: '/exam'},
        {title: '护理计划', icon: 'orders-o', color: '#EE0A24', path: '/carePlan'},
        {title: '护理任务', icon: 'todo-list-o', color: '#1989FA', path: '/careTask'},
        {title: '合同查询', icon: 'bill-o', color: '#07C160', path: '/contract'}
      ]
    }
    return [
      {title: '体检预约', icon: 'calendar-o', color: '#1989FA', path: '/examPackageList'},
      {title: 'AI对话', icon: 'chat-o', color: '#07C160', path: '/aiChat'},
      {title: '护理计划', icon: 'orders-o', color: '#FF976A', path: '/carePlan'},
      {title: '护理任务', icon: 'todo-list-o', color: '#EE0A24', path: '/careTask'},
      {title: '我的合同', icon: 'bill-o', color: '#1989FA', path: '/contract'},
      {title: '求助', icon: 'warn-o', color: '#07C160', path: '/help'}
    ]
  })

  // ================== 公告  ==================

  // 最近公告列表（首页通知条取第一条，弹层展示全部4条）
  const noticeList = ref([])
  // 是否显示最近公告弹层
  const showNotice = ref(false)

  // ================== 床位 ==================

  // 当前老人的入住床位信息（楼栋楼层房间床位），未入住时为null不显示卡片
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

  // ================== 对象（资讯） ==================

  // 最新资讯列表（首页最多展示5条，查看更多进资讯列表页）
  const newsList = ref([])
  // 资讯是否正在加载（加载中显示骨架屏占位）
  const newsLoading = ref(true)

  // ================== 下拉数据 ==================

  // 加载最近公告（已发布，最新4条）
  const loadNoticeList = () => {
    announcementApi.list({page: 1, limit: 4}).then(result => {
      noticeList.value = result.data.records
    })
  }
  loadNoticeList()

  // 加载最新资讯（已发布，按发布时间倒序，最多5条）
  const loadNewsList = () => {
    newsApi.list({page: 1, limit: 5}).then(result => {
      newsList.value = result.data.records
      newsLoading.value = false
    })
  }
  loadNewsList()

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

  //跳转到功能页面
  const goPage = (path) => {
    router.push(path)
  }

  //公告展示日期（createTime 截取 MM-DD）
  const getNoticeDate = (createTime) => {
    return createTime ? createTime.slice(5, 10) : ''
  }

  //打开最近公告弹层
  const openNotice = () => {
    showNotice.value = true
  }

  //跳转公告列表页（全部公告）
  const goNoticeList = () => {
    showNotice.value = false
    router.push('/announcement')
  }

  //跳转公告详情页
  const goNoticeDetail = (row) => {
    showNotice.value = false
    router.push({path: '/announcementDetail', query: {id: row.id}})
  }

  //资讯展示日期（createTime 截取 MM-DD）
  const getNewsDate = (createTime) => {
    return createTime ? createTime.slice(5, 10) : ''
  }

  //跳转资讯列表页
  const goNewsList = () => {
    router.push('/news')
  }

  //跳转资讯详情页
  const goNewsDetail = (row) => {
    router.push({path: '/newsDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="home">
    <!-- 顶部蓝色问候区 -->
    <div class="home-header">
      <h3 class="home-title">您好，{{ currentElder.realName }}{{ isFamily ? '的家属' : '' }}</h3>
      <p class="home-subtitle" v-if="isFamily">当前查看：{{ currentElder.realName }}（{{ getAge(currentElder.birthday) }}岁）</p>
      <p class="home-subtitle" v-else>今天也要注意身体哦</p>
    </div>

    <div class="home-body">
      <!-- 演示系统提示 -->
      <van-notice-bar class="demo-notice" left-icon="info-o" wrapable :scrollable="false">
        请注意：本系统为演示系统，仅供作品演示使用，不具有任何服务性质，请勿在系统填写敏感信息！
      </van-notice-bar>

      <!-- 公告通知条 -->
      <div class="home-notice" v-if="noticeList.length > 0" @click="openNotice">
        <van-icon name="volume-o" size="16"/>
        <span class="home-notice-text">{{ noticeList[0].title }}</span>
        <van-icon name="arrow" size="14"/>
      </div>

      <!-- 床位信息卡片（未入住时不显示） -->
      <div class="home-bed" v-if="bedInfo">
        <div class="bed-header">
          <van-icon name="location-o" size="16" color="#1989FA"/>
          <span class="bed-title">我的床位</span>
        </div>
        <div class="bed-info">
          <div class="bed-item">
            <p class="bed-label">楼栋</p>
            <p class="bed-value">{{ bedInfo.buildingName }}</p>
          </div>
          <div class="bed-item">
            <p class="bed-label">楼层</p>
            <p class="bed-value">{{ bedInfo.floorNo }}层</p>
          </div>
          <div class="bed-item">
            <p class="bed-label">房间</p>
            <p class="bed-value">{{ bedInfo.roomNo }}房</p>
          </div>
          <div class="bed-item">
            <p class="bed-label">床位</p>
            <p class="bed-value">{{ bedInfo.bedNo }}</p>
          </div>
        </div>
      </div>

      <!-- 功能入口 -->
      <div class="home-grid-card">
        <van-grid :column-num="3" :border="false">
          <van-grid-item v-for="item in gridItems" :key="item.path" @click="goPage(item.path)">
            <van-icon :name="item.icon" size="28" :color="item.color"/>
            <span class="grid-title">{{ item.title }}</span>
          </van-grid-item>
        </van-grid>
      </div>

      <!-- 最新资讯（加载中显示骨架屏占位） -->
      <div class="home-news" v-if="newsLoading || newsList.length > 0">
        <div class="section-header">
          <div class="section-title"><span class="section-bar"></span>最新资讯</div>
          <span class="section-more" @click="goNewsList">查看更多 <van-icon name="arrow"/></span>
        </div>
        <!-- 骨架屏（与资讯卡片同款外观：左图右文） -->
        <template v-if="newsLoading">
          <div class="news-skeleton" v-for="i in 3" :key="i">
            <van-skeleton title avatar avatar-shape="square" avatar-size="64px" :row="2" :row-width="['100%', '50%']"/>
          </div>
        </template>
        <template v-else>
          <div class="news-card" v-for="item in newsList" :key="item.id" @click="goNewsDetail(item)">
          <van-image class="news-cover" width="86" height="64" radius="8" fit="cover" :src="item.coverImage">
            <template #loading>
              <div class="news-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
            </template>
            <template #error>
              <div class="news-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
            </template>
          </van-image>
          <div class="news-info">
            <p class="news-title">{{ item.title }}</p>
            <p class="news-summary" v-if="item.summary">{{ item.summary }}</p>
            <p class="news-meta">{{ item.categoryName }} · {{ getNewsDate(item.createTime) }}</p>
          </div>
        </div>
        </template>
      </div>
    </div>

    <!-- 最近公告弹层（最多4条，右上角可进入全部公告列表） -->
    <van-popup v-model:show="showNotice" position="bottom" round>
      <div class="notice-popup">
        <div class="notice-popup-header">
          <span class="notice-popup-title">最近公告</span>
          <span class="notice-popup-more" @click="goNoticeList">查看更多 <van-icon name="arrow"/></span>
        </div>
        <van-empty description="暂无公告" v-if="noticeList.length === 0"/>
        <div
            class="notice-item"
            v-for="row in noticeList"
            :key="row.id"
            @click="goNoticeDetail(row)"
        >
          <p class="notice-item-title">{{ row.title }}</p>
          <p class="notice-item-date">{{ getNoticeDate(row.createTime) }}</p>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
  .home {
    min-height: 100%;
    padding-bottom: 20px;
  }

  /* 顶部蓝色问候区（底部渐变到页面底色，与白色区域平滑过渡） */
  .home-header {
    background: linear-gradient(180deg, #1989FA 0%, #5BA5FA 55%, #F5F6FA 100%);
    padding: 28px 16px 76px;
    color: #FFFFFF;
  }

  .home-title {
    font-size: 22px;
    font-weight: bold;
  }

  .home-subtitle {
    margin-top: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
  }

  /* 内容区上叠在蓝色头部上 */
  .home-body {
    padding: 0 12px;
    margin-top: -36px;
  }

  /* 小节标题（蓝色竖条 + 加粗文字） */
  .section-title {
    display: flex;
    align-items: center;
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 10px;
  }

  .section-bar {
    width: 4px;
    height: 14px;
    background-color: #1989FA;
    border-radius: 2px;
    margin-right: 6px;
  }

  /* 演示系统提示条 */
  .demo-notice {
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 12px;
  }

  /* 功能入口（在公告条和床位卡片之下） */
  .home-grid-card {
    margin-top: 12px;
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 8px 0;
  }

  .grid-title {
    margin-top: 8px;
    font-size: 13px;
    color: #323233;
  }

  /* 公告通知条（紧跟演示提示条，间距由提示条的下边距提供） */
  .home-notice {
    background-color: #E8F3FF;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #1989FA;
  }

  .home-notice-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .home-notice > .van-icon:last-child {
    flex-shrink: 0;
  }

  /* 床位信息卡片 */
  .home-bed {
    margin-top: 12px;
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 12px;
  }

  .bed-header {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .bed-title {
    font-size: 14px;
    font-weight: bold;
    color: #323233;
  }

  .bed-info {
    margin-top: 12px;
    display: flex;
  }

  .bed-item {
    flex: 1;
    text-align: center;
  }

  .bed-label {
    font-size: 12px;
    color: #999;
  }

  .bed-value {
    margin-top: 4px;
    font-size: 14px;
    font-weight: bold;
    color: #323233;
  }

  /* 最新资讯板块 */
  .home-news {
    margin-top: 12px;
  }

  /* 小节标题行（左标题 + 右查看更多） */
  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .section-more {
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    color: #1989FA;
  }

  /* 资讯骨架屏（与资讯卡片同款白卡外观） */
  .news-skeleton {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 10px;
    margin-bottom: 10px;
  }

  .news-skeleton :deep(.van-skeleton__row) {
    margin-top: 8px;
  }

  /* 资讯图文卡片 */
  .news-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 10px;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .news-cover {
    flex-shrink: 0;
  }

  .news-cover-placeholder {
    width: 86px;
    height: 64px;
    background-color: #F5F6FA;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .news-info {
    flex: 1;
    min-width: 0;
  }

  .news-title {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
    line-height: 21px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .news-summary {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
    line-height: 17px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .news-meta {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }

  /* 最近公告弹层 */
  .notice-popup {
    padding: 20px 16px 24px;
    max-height: 60vh;
    overflow-y: auto;
  }

  .notice-popup-header {
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    margin-bottom: 4px;
  }

  .notice-popup-title {
    font-size: 16px;
    font-weight: bold;
  }

  .notice-popup-more {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    color: #1989FA;
  }

  .notice-item {
    padding: 14px 0;
    border-bottom: 1px solid #F0F0F0;
  }

  .notice-item:last-child {
    border-bottom: none;
  }

  .notice-item-title {
    font-size: 15px;
    color: #323233;
    line-height: 22px;
  }

  .notice-item-date {
    margin-top: 6px;
    font-size: 12px;
    color: #999;
  }
</style>
