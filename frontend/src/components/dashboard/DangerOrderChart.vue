<template>
  <div class="col-span-2 bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
    style="min-height:280px">
    <div class="flex items-center justify-between mb-5 shrink-0">
      <div>
        <h2 class="font-bold text-gray-900">이상 발주 통계</h2>
        <p class="text-xs text-gray-400 mt-0.5">최근 6개월 이상 발주 현황 (단위: 건)</p>
      </div>
      <div class="flex items-center gap-4 text-xs text-gray-500">
        <span class="flex items-center gap-1.5"><span
            class="w-2.5 h-2.5 rounded-sm bg-red-400 inline-block"></span>승인 대기</span>
        <span class="flex items-center gap-1.5"><span
            class="w-2.5 h-2.5 rounded-sm bg-green-400 inline-block"></span>승인</span>
        <span class="flex items-center gap-1.5"><span
            class="w-2.5 h-2.5 rounded-sm bg-gray-300 inline-block"></span>반려</span>
      </div>
    </div>
    <div class="flex-1 min-h-0 relative" style="height:200px">
      <canvas ref="barCanvas" style="display:block; width:100%; height:100%;"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Chart } from 'chart.js/auto'

const barCanvas = ref(null)

const abnormalLabels = ['11월', '12월', '1월', '2월', '3월', '4월']
const abnormalDanger = [3, 5, 2, 7, 4, 2]
const abnormalApprove = [2, 4, 2, 5, 3, 1]
const abnormalReject = [1, 1, 0, 2, 1, 1]

onMounted(() => {
  new Chart(barCanvas.value, {
    type: 'bar',
    data: {
      labels: abnormalLabels,
      datasets: [
        {
          label: '승인 대기',
          data: abnormalDanger,
          backgroundColor: '#f87171',
          borderRadius: 4,
          borderSkipped: false,
        },
        {
          label: '승인',
          data: abnormalApprove,
          backgroundColor: '#4ade80',
          borderRadius: 4,
          borderSkipped: false,
        },
        {
          label: '반려',
          data: abnormalReject,
          backgroundColor: '#d1d5db',
          borderRadius: 4,
          borderSkipped: false,
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
          min: 0,
          grid: { color: '#f3f4f6' },
          ticks: { color: '#9ca3af', font: { size: 11 }, stepSize: 2, precision: 0 },
          border: { display: false },
        },
      },
    },
  })
})
</script>
