<script setup>
import { ref, watch, onMounted } from 'vue'
import { Ckeditor as CkeditorComponent } from '@ckeditor/ckeditor5-vue'
import ClassicEditor from '@ckeditor/ckeditor5-build-classic'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Nhập nội dung chi tiết tại đây...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editor = ref(ClassicEditor)
const editorData = ref(props.modelValue || '')

// CKEditor 5 configuration
const editorConfig = ref({
  toolbar: {
    items: [
      'heading', '|',
      'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', '|',
      'imageUpload', 'insertTable', 'mediaEmbed', '|',
      'undo', 'redo'
    ]
  },
  placeholder: props.placeholder
})

// Sync internal editor data with outside props updates
watch(() => props.modelValue, (newVal) => {
  if (newVal !== editorData.value) {
    editorData.value = newVal || ''
  }
})

// Sync internal changes to outside modelValue
watch(editorData, (newVal) => {
  emit('update:modelValue', newVal)
})
</script>

<template>
  <div class="ckeditor-wrapper bg-white border border-gray-250 rounded-2xl overflow-hidden shadow-sm flex flex-col">
    <ckeditor-component
      :editor="editor"
      v-model="editorData"
      :config="editorConfig"
    />
  </div>
</template>

<style>
/* Custom styling to match our Premium Design System */
.ck-editor__editable_inline {
  min-height: 250px;
  max-height: 400px;
  padding: 1rem !important;
  font-size: 14px;
  color: #374151; /* gray-700 */
}

/* Remove default focusing borders for a cleaner look */
.ck.ck-editor__main>.ck-editor__editable:not(.ck-focused) {
  border-color: transparent !important;
}
.ck.ck-editor__main>.ck-editor__editable.ck-focused {
  border-color: transparent !important;
  box-shadow: none !important;
}

/* Style the toolbar to match our grey sidebar colors */
.ck.ck-editor__top .ck-sticky-panel .ck-toolbar {
  border: none !important;
  background-color: #f9fafb !important; /* gray-50 */
  border-bottom: 1px solid #e5e7eb !important; /* gray-200 */
  padding: 0.5rem !important;
}

/* Custom list bullet style within CKEditor editable preview */
.ck-content ul {
  list-style-type: disc !important;
  padding-left: 1.5rem !important;
  margin-bottom: 0.75rem !important;
}
.ck-content ol {
  list-style-type: decimal !important;
  padding-left: 1.5rem !important;
  margin-bottom: 0.75rem !important;
}
.ck-content blockquote {
  border-left: 4px solid #7c3aed !important; /* purple-600 */
  background-color: #faf5ff !important; /* purple-50/50 */
  padding: 0.75rem 1rem !important;
  font-style: italic !important;
  border-radius: 0 8px 8px 0 !important;
  margin-bottom: 0.75rem !important;
}
.ck-content h2 {
  font-size: 1.25rem !important;
  font-weight: 800 !important;
  color: #1f2937 !important;
  margin-top: 1.25rem !important;
  margin-bottom: 0.5rem !important;
}
.ck-content h3 {
  font-size: 1.1rem !important;
  font-weight: 700 !important;
  color: #374151 !important;
  margin-top: 1rem !important;
  margin-bottom: 0.5rem !important;
}
</style>
