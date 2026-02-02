<template>
  <div class="upload-container">
    <el-upload class="upload-component" drag action="#" :auto-upload="false" :show-file-list="true" :limit="1"
      :on-change="handleFileChange" :on-exceed="handleExceed" accept=".doc,.docx">
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          请上传.docx 格式的 Word 文档
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// Define emits
const emit = defineEmits(['file-selected'])

const handleFileChange = (file) => {
  if (file.raw) {
    const fileName = file.name.toLowerCase()
    if (fileName.endsWith('.docx')) {
      emit('file-selected', file.raw)
    } else {
      ElMessage.error('请上传.docx 格式的文件')
    }
  }
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传1个文件')
}
</script>

<style scoped>
.upload-container {
  padding: 20px;
}

.upload-component {
  width: 100%;
}
</style>
