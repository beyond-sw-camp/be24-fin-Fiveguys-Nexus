<template>
  <div v-if="open" class="fixed inset-0 bg-gray-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-xl border border-gray-200 shadow-xl w-full max-w-sm text-center p-8">
      <div class="w-16 h-16 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-5">
        <AlertCircle class="w-8 h-8 text-red-500" />
      </div>
      <h2 class="text-xl font-bold text-gray-900 mb-2">영업을 마감하시겠습니까?</h2>
      <p class="text-sm text-gray-500 mb-8 leading-relaxed">
        마감 처리 후에는 새로운 결제를<br/>진행할 수 없습니다.<br/>
        <span class="text-orange-500 font-semibold mt-2 block">판매 데이터가 본사로 자동 전송됩니다.</span>
      </p>
      <div class="flex gap-3">
        <button type="button" @click="$emit('close')" class="flex-1 py-3 bg-white border border-gray-200 text-gray-600 rounded-xl text-sm font-bold hover:bg-gray-50 transition-colors cursor-pointer">
          취소
        </button>
        <button type="button" @click="$emit('confirm')" class="flex-1 py-3 bg-red-500 text-white rounded-xl text-sm font-bold hover:bg-red-600 shadow-sm transition-colors cursor-pointer">
          마감 승인
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { AlertCircle } from 'lucide-vue-next'

defineProps({
  open: { type: Boolean, default: false },
})

defineEmits(['close', 'confirm'])
</script>
