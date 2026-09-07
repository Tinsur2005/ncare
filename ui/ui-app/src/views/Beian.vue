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
  import {useRouter} from 'vue-router'

  const router = useRouter()

  // ================== 常量 ==================

  // 备案信息（备案号变更时改这里）
  const icpNo = '鲁ICP备2023032291号-1'
  const icpUrl = 'https://beian.miit.gov.cn/'
  const publicSecurityNo = '鲁公网安备37021402000083号'
  const publicSecurityUrl = 'https://beian.mps.gov.cn/#/query/webSearch?code=37021402000083'
</script>

<template>
  <div class="beian">
    <van-nav-bar title="备案信息" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 顶部提示 -->
    <div class="beian-tip">
      <van-icon name="shield-o" class="beian-tip-icon"/>
      <span>备案号仅供合规查验，本平台仅供作品演示，不具备任何服务性质</span>
    </div>

    <!-- ICP备案 -->
    <a class="beian-card" :href="icpUrl" target="_blank" rel="nofollow">
      <div class="beian-card-info">
        <p class="beian-card-label">工业和信息化部ICP备案</p>
        <p class="beian-card-value">{{ icpNo }}</p>
      </div>
      <van-icon name="arrow" color="#C8C9CC"/>
    </a>

    <!-- 公安备案 -->
    <a class="beian-card" :href="publicSecurityUrl" target="_blank" rel="noreferrer">
      <div class="beian-card-info">
        <p class="beian-card-label">公安联网备案</p>
        <p class="beian-card-value">{{ publicSecurityNo }}</p>
      </div>
      <van-icon name="arrow" color="#C8C9CC"/>
    </a>
  </div>
</template>

<style scoped>
  .beian {
    min-height: 100%;
    padding-bottom: 20px;
  }

  /* 顶部提示条（浅蓝底，说明备案可查验） */
  .beian-tip {
    margin: 12px 12px 0;
    padding: 12px 14px;
    background-color: #E8F3FF;
    border-radius: 12px;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 12px;
    line-height: 18px;
    color: #1989FA;
  }

  .beian-tip-icon {
    margin-top: 2px;
    font-size: 14px;
    flex-shrink: 0;
  }

  /* 备案卡片（整卡可点击跳转备案官网） */
  .beian-card {
    margin: 12px 12px 0;
    padding: 16px;
    background-color: #FFFFFF;
    border-radius: 12px;
    display: flex;
    align-items: center;
    text-decoration: none;
  }

  .beian-card:active {
    background-color: #F5F6FA;
  }

  .beian-card-info {
    flex: 1;
    min-width: 0;
    margin: 0 12px;
  }

  .beian-card-label {
    font-size: 12px;
    color: #999;
  }

  .beian-card-value {
    margin-top: 4px;
    font-size: 15px;
    font-weight: bold;
    color: #323233;
  }
</style>