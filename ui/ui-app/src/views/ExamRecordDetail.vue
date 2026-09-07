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
  import {useRoute, useRouter} from 'vue-router'
  import examAppointmentApi from '@/api/examAppointment.js'

  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //体检预约对象
  const appointment = ref({})
  //体检记录明细列表（含体检项目的参考范围）
  const resultItemList = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 选项 ==================

  // 状态选项（状态：0待体检 1体检中 2已完成 3已取消 4已过期），color 为状态文字的颜色
  const statusOptions = [
    {value: 0, label: '待体检', color: '#1989FA'},
    {value: 1, label: '体检中', color: '#FF976A'},
    {value: 2, label: '已完成', color: '#07C160'},
    {value: 3, label: '已取消', color: '#999999'},
    {value: 4, label: '已过期', color: '#999999'},
  ]

  // 明细结果状态选项（状态：0待检查 1正常 2异常 3未完成）
  const resultStatusOptions = [
    {value: 1, label: '正常', type: 'success'},
    {value: 2, label: '异常', type: 'danger'},
    {value: 3, label: '未完成', type: 'default'},
  ]

  // ================== 下拉数据 ==================

  //加载体检预约信息
  const loadAppointment = () => {
    return examAppointmentApi.selectById(route.query.id).then(result => {
      if (result.code === 1) {
        appointment.value = result.data
      }
    })
  }

  //加载体检记录明细列表（含体检项目的参考范围）
  const loadResultItemList = () => {
    return examAppointmentApi.getAppointmentItemsById(route.query.id).then(result => {
      resultItemList.value = result.data || []
    })
  }

  //两个请求都返回后再关闭加载动画
  Promise.all([loadAppointment(), loadResultItemList()]).finally(() => {
    loading.value = false
  })

  // ================== 方法 ==================

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value) || statusOptions[3]
  }

  //根据明细结果状态获取展示信息
  const getResultStatus = (value) => {
    return resultStatusOptions.find(option => option.value === value)
  }

  //数值型异常的方向提示：超过参考上限为偏高，低于参考下限为偏低
  const judgeDirection = (row) => {
    const min = row.referenceMin
    const max = row.referenceMax
    if (max != null && max !== undefined && row.resultValue > max) return '偏高'
    if (min != null && min !== undefined && row.resultValue < min) return '偏低'
    return '异常'
  }
</script>

<template>
  <div class="detail">
    <van-nav-bar title="体检记录详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
    <!-- 体检预约基本信息 -->
    <div class="detail-card">
      <div class="detail-top">
        <van-tag type="primary">体检</van-tag>
        <span class="detail-package">{{ appointment.packageName }}</span>
        <span class="detail-status" :style="{color: getStatus(appointment.status).color}">{{ getStatus(appointment.status).label }}</span>
      </div>
      <div class="detail-info">
        <p><van-icon name="manager-o"/><span>{{ appointment.elderName }}</span></p>
        <p><van-icon name="calendar-o"/><span>{{ appointment.appointmentDate }} {{ appointment.appointmentTime }}</span></p>
        <p v-if="appointment.remark"><van-icon name="comment-o"/><span>备注：{{ appointment.remark }}</span></p>
      </div>
      <div class="detail-footer">
        <span class="detail-price">￥{{ appointment.price }}</span>
      </div>
    </div>

    <!-- 体检结果明细（未出结果时显示空状态） -->
    <div class="detail-card" v-if="resultItemList.length > 0">
      <div class="detail-title">体检结果</div>
      <div class="result-item" v-for="row in resultItemList" :key="row.id">
        <div class="result-item-top">
          <span class="result-item-name">{{ row.itemName }}</span>
          <template v-if="row.status !== 0">
            <van-tag :type="getResultStatus(row.status).type">{{ getResultStatus(row.status).label }}</van-tag>
            <van-tag type="danger" v-if="row.status === 2 && row.resultType === 1">{{ judgeDirection(row) }}</van-tag>
          </template>
          <van-tag type="warning" v-else>待检查</van-tag>
        </div>
        <!-- 数值型结果：结果值 + 参考范围（单位用参考单位，和右侧参考范围保持一致） -->
        <template v-if="row.resultType === 1">
          <p class="result-item-value">
            {{ row.resultValue != null ? row.resultValue + (row.referenceUnit || '') : '未录入' }}
            <span class="result-item-range" v-if="row.referenceMin != null && row.referenceMax != null">
              （参考范围 {{ row.referenceMin }} ~ {{ row.referenceMax }} {{ row.referenceUnit || '' }}）
            </span>
          </p>
        </template>
        <!-- 文本型结果 -->
        <p class="result-item-value" v-else>{{ row.resultText || '未录入' }}</p>
        <p class="result-item-remark" v-if="row.remark">医生建议：{{ row.remark }}</p>
      </div>
    </div>
    <van-empty description="暂无体检结果" v-else-if="appointment.status === 0 || appointment.status === 1 || appointment.status === 3 || appointment.status === 4"/>
    </template>
  </div>
</template>

<style scoped>
  .detail {
    min-height: 100%;
    padding: 12px 12px 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  .detail-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 12px;
  }

  .detail-top {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .detail-package {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .detail-status {
    font-size: 13px;
  }

  .detail-info {
    margin-top: 4px;
  }

  .detail-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .detail-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }

  .detail-footer {
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #F0F0F0;
  }

  .detail-price {
    font-size: 18px;
    font-weight: bold;
    color: #EE0A24;
  }

  .detail-title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  /* 结果明细项 */
  .result-item {
    border-top: 1px solid #F0F0F0;
    padding: 12px 0;
  }

  .result-item-top {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .result-item-name {
    font-size: 14px;
    font-weight: bold;
    margin-right: auto;
  }

  .result-item-value {
    margin-top: 8px;
    font-size: 13px;
    color: #666;
    white-space: pre-wrap; /*保留后台录入体检结果时的换行*/
  }

  .result-item-range {
    color: #999;
    font-size: 12px;
  }

  .result-item-remark {
    margin-top: 4px;
    font-size: 12px;
    color: #EE9C01;
    white-space: pre-wrap; /*医生建议同样保留换行*/
  }
</style>
