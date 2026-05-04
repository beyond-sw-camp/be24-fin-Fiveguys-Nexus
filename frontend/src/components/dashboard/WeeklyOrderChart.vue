<template>
  <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
    style="min-height:280px">
    <div class="flex items-center justify-between mb-5 shrink-0">
      <div>
        <h2 class="font-bold text-gray-900">주간 발주 통계</h2>
        <p class="text-xs text-gray-400 mt-0.5">이번 주 vs 지난 주 (단위: 건)</p>
      </div>
      <div class="flex items-center gap-4 text-xs text-gray-500">
        <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-orange-500 inline-block rounded"></span>이번
          주</span>
        <span class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-gray-300 inline-block rounded"></span>지난
          주</span>
      </div>
    </div>
    <div class="flex-1 min-h-0 relative" style="height:200px">
      <canvas ref="lineCanvas" style="display:block; width:100%; height:100%;"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Chart } from 'chart.js/auto'

const lineCanvas = ref(null)

const thisWeek = [32, 48, 38, 65, 52, 78, 58]
const lastWeek = [22, 38, 42, 48, 58, 52, 48]
const weekLabels = ['일', '월', '화', '수', '목', '금', '토']

onMounted(() => {
  new Chart(lineCanvas.value, {
    type: 'line',
    data: {
      labels: weekLabels,
      datasets: [
        {
          label: '이번 주',
          data: thisWeek,
          borderColor: '#f97316',
          backgroundColor: (ctx) => {
            const g = ctx.chart.ctx.createLinearGradient(0, 0, 0, ctx.chart.height)
            g.addColorStop(0, 'rgba(249,115,22,0.18)')
            g.addColorStop(1, 'rgba(249,115,22,0)')
            return g
          },
          fill: true,
          tension: 0.45,
          borderWidth: 2.5,
          pointRadius: (ctx) => ctx.dataIndex === thisWeek.indexOf(Math.max(...thisWeek)) ? 5 : 0,
          pointHoverRadius: 6,
          pointBackgroundColor: '#f97316',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
        },
        {
          label: '지난 주',
          data: lastWeek,
          borderColor: '#d1d5db',
          backgroundColor: 'transparent',
          fill: false,
          tension: 0.45,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          pointBackgroundColor: '#d1d5db',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1f2937',
          titleColor: '#f9fafb',
          bodyColor: '#d1d5db',
          padding: 10,
          cornerRadius: 10,
          callbacks: { label: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y}건` },
        },
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { color: '#9ca3af', font: { size: 12 } },
          border: { display: false },
        },
        y: {
          min: 10, max: 90,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 20 },
          border: { display: false },
        },
      },
    },
  })
})
</script>
