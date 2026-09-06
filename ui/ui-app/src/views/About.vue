<!--
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║██████╔╝██║  ██║
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
  import logo from '@/assets/logo.png'

  const router = useRouter()

  // ================== 常量 ==================

  // 系统信息（版本号更新时改这里）
  const systemName = '睦邻智慧社区养老平台'
  const systemVersion = 'v1.0.0'
  const servicePhone = '400-123-4567'
  const systemIntro = '睦邻NCare智慧社区养老平台是面向社区养老场景的一站式服务平台，为老人提供体检预约、AI健康对话、护理计划、护理任务、合同查询和一键求助等服务，为家属提供远程代办和实时了解老人动态的通道，让老人安心、家属放心。'
</script>

<template>
  <div class="about">
    <van-nav-bar title="关于我们" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 品牌区：LOGO、系统名称、版本号、口号 -->
    <div class="about-brand">
      <img class="about-logo" :src="logo" :alt="systemName"/>
      <h3 class="about-name">{{ systemName }}</h3>
      <p class="about-slogan">让每一位老人都能安享幸福晚年</p>
    </div>

    <!-- 系统信息 -->
    <van-cell-group inset title="系统信息">
      <van-cell title="系统名称" :value="systemName"/>
      <van-cell title="联系电话" :value="servicePhone"/>
      <van-cell title="系统版本" :value="systemVersion"/>
    </van-cell-group>

    <!-- 系统介绍 -->
    <van-cell-group inset title="系统介绍">
      <van-cell :value="systemIntro" class="about-intro"/>
    </van-cell-group>
  </div>
</template>

<style scoped>
  .about {
    min-height: 100%;
    padding-bottom: 20px;
  }

  /* 品牌区（居中展示LOGO、名称、版本号和口号） */
  .about-brand {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 36px 24px 28px;
  }

  .about-logo {
    width: 190px;
    height: auto;
  }

  .about-name {
    margin-top: 14px;
    font-size: 18px;
    font-weight: bold;
    color: #323233;
  }

  .about-version {
    margin-top: 8px;
  }

  .about-slogan {
    margin-top: 10px;
    font-size: 13px;
    color: #999;
  }

  /* 系统介绍：左标题右内容布局下文字较长，改为上下排列并控制行高 */
  .about-intro :deep(.van-cell__value) {
    flex: 1;
    font-size: 13px;
    color: #666;
    line-height: 22px;
    text-align: justify;
  }
</style>
