<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>借阅趋势（最近 30 天）</template>
          <v-chart :option="trendOption" style="height: 360px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>分类借阅占比</template>
          <v-chart :option="categoryOption" style="height: 360px" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getBorrowTrend, getCategoryStats } from '@/api/statistics'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const trendOption = ref({})
const categoryOption = ref({})

onMounted(async () => {
  try {
    const trendData = (await getBorrowTrend(30)).data || []
    trendOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendData.map(d => d.date) },
      yAxis: {
        type: 'value',
        name: '借阅量(本)',
        minInterval: 1,         // 强制整数刻度，不出现小数
        min: 0,                 // Y轴从0开始
        axisLabel: {            // 标签格式化为整数
          formatter: '{value}'
        }
      },
      series: [{ data: trendData.map(d => d.count), type: 'line', smooth: true,
        areaStyle: { color: 'rgba(64,158,255,0.2)' }, itemStyle: { color: '#409EFF' } }]
    }
  } catch { /* ignore */ }

  try {
    const catData = (await getCategoryStats()).data || []
    categoryOption.value = {
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', right: 10, top: 'center' },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'],
        data: catData.map(d => ({ name: d.name, value: d.value })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
      }]
    }
  } catch { /* ignore */ }
})
</script>
