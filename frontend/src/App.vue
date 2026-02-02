<template>
  <div class="app-container">
    <el-card class="app-card">
      <template #header>
        <div class="card-header">
          <h1>docx文档内容格式处理工具</h1>
        </div>
      </template>

      <!-- 步骤条 -->
      <el-steps :active="activeStep" finish-status="success" class="steps">
        <el-step title="上传文档"></el-step>
        <el-step title="配置格式"></el-step>
        <el-step title="下载"></el-step>
      </el-steps>

      <!-- 步骤内容 -->
      <!-- 当子组件中的文件改变时，触发自定义事件，将文件同步到App.vue -->
      <div v-if="activeStep === 0">
        <file-upload @file-selected="handleFileSelected" />
        <div class="actions">
          <el-button type="primary" :disabled="!selectedFile" @click="activeStep = 1">
            下一步
          </el-button>
        </div>
      </div>

      <div v-if="activeStep === 1">
        <format-config ref="formatConfigRef" v-model:config="formatConfigObj" />
        <div class="actions">
          <el-button @click="activeStep = 0">上一步</el-button>
          <el-button type="primary" @click="goToDownload">下一步</el-button>
        </div>
      </div>

      <div v-if="activeStep === 2">
        <div class="preview-container">
          <h2>下载处理后的文档</h2>
          <el-alert type="info" show-icon :closable="false" class="download-notice important-notice">
            <div class="prompt">
              <p>尊敬的用户：</p>
              <p>您好！在使用我们的程序处理您的docx文档时，请注意以下事项：</p>
              <p>1.
                <strong>编号样式问题</strong>：对于WPS自动生成且字体加粗（常规编号不受影响）的编号（如1. 一.
                第一章），如果存在于文档中，可能会导致所在段落的样式出现一些问题。手动输入的编号则不会有此问题。
              </p>
              <p>2. <strong>样式单位差异</strong>：您在WPS中看到的样式单位可能与程序中设置的样式单位不同。不过请放心，这些单位经过换算后，实际的值是一样的，不会对文档的实际样式效果造成影响。</p>
              <p>修改后的文档内容格式如果不符合您的预期，您可以在WPS等相关软件中再次进行调整。</p>
            </div>
            <p style="font-size: 19px; font-weight: bold;">
              如果您发现任何问题或者有任何建议，欢迎与我们联系：<strong style="color: #6CAFF3;">257181857@qq.com</strong>
            </p>
          </el-alert>
          <!-- 添加不确定进度条 -->
          <div v-if="isDownloading" class="progress-container">
            <el-progress :percentage="100" status="success" :indeterminate="true" :duration="3" :stroke-width="20"
              color="#409EFF" class="download-progress">
            </el-progress>

            <p class="processing-text">正在处理文档，请稍候...</p>
          </div>
          <div class="actions">
            <el-button type="primary" @click="downloadDocument" :loading="isDownloading" :disabled="isDownloading">
              {{ isDownloading ? '处理中...' : '下载处理后的文档' }}
            </el-button>
            <el-button @click="activeStep = 1" :disabled="isDownloading">
              返回修改配置
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, toRaw } from 'vue'
import axios from 'axios'
import FileUpload from './components/FileUpload.vue'
import FormatConfig from './components/FormatConfig.vue'
import { ElMessage } from 'element-plus'
const formatConfigRef = ref(null)
const activeStep = ref(0)
const selectedFile = ref(null)
const formatConfigObj = reactive({
  customStyles: {
    normal: {
      normalFontCn: '宋体',
      normalFontWestern: 'Times New Roman',
      normalFontComplex: 'Tahoma',
      normalFontSize: 12,
      fontColor: '#000000',//默认颜色为黑色
      fontStyle: [],
      englishFontStyle: [],//先保留，后续再考虑是否需要
      setEnglishCharStyleIndividually: false,//先保留，后续再考虑是否需要
    },
    paragraph: {
      lineSpacingType: 'exact',
      lineSpacing: 24,
      lineSpacingUnit: 'pt',
      firstLineIndent: 2,//首行缩进2个字符
      firstLineIndentUnit: 'ch',
      leftIndent: 0,
      leftIndentUnit: 'pt',
      rightIndent: 0,
      rightIndentUnit: 'pt',
      spaceBefore: 6,
      spaceBeforeUnit: 'pt',
      spaceAfter: 6,
      spaceAfterUnit: 'pt',
      alignment: 'LEFT'
    }
  },
  // 标题样式
  heading: {
    headingStyles: {
      1: {
        headingFontCn: '宋体',
        headingFontWestern: 'Times New Roman',
        headingFontComplex: 'Tahoma',
        headingFontSize: 12,
        fontColor: '#000000',
        fontStyle: [],
        englishFontStyle: [],//先保留，后续再考虑是否需要
        setEnglishCharStyleIndividually: false,//先保留，后续再考虑是否需要
        lineSpacingType: 'exact',
        lineSpacing: 24,
        lineSpacingUnit: 'pt',
        firstLineIndent: 2,
        firstLineIndentUnit: 'ch',
        leftIndent: 0,
        leftIndentUnit: 'pt',
        rightIndent: 0,
        rightIndentUnit: 'pt',
        spaceBefore: 6,
        spaceBeforeUnit: 'pt',
        spaceAfter: 6,
        spaceAfterUnit: 'pt',
        alignment: 'LEFT'
      },
      2: {
        headingFontCn: '宋体',
        headingFontWestern: 'Times New Roman',
        headingFontComplex: 'Tahoma',
        headingFontSize: 12,
        fontColor: '',//默认颜色由后端实现
        fontStyle: [],
        englishFontStyle: [],//先保留，后续再考虑是否需要
        setEnglishCharStyleIndividually: false,//先保留，后续再考虑是否需要
        lineSpacingType: 'exact',
        lineSpacing: 24,
        lineSpacingUnit: 'pt',
        firstLineIndent: 2,
        firstLineIndentUnit: 'ch',
        leftIndent: 0,
        leftIndentUnit: 'pt',
        rightIndent: 0,
        rightIndentUnit: 'pt',
        spaceBefore: 6,
        spaceBeforeUnit: 'pt',
        spaceAfter: 6,
        spaceAfterUnit: 'pt',
        alignment: 'LEFT'
      }
    },
    // 标题识别规则
    headingRules: {
      1: {
        pattern: "第[一二三四五六七八九十]+章",
      },
      2: {
        pattern: "第[一二三四五六七八九十]+章第[一二三四五六七八九十]+节",
      }
    },
    titleExamples: {
      1: {
        example: "第一章"
      },
      2: {
        example: "第一章第一节"
      }
    },
    // 是否使用自定义标题识别规则
    useCustomHeadingRules: true
  },
  // imageAlignment: 'CENTER',
  image: {
    imageAlignment: 'LEFT',
    fixedDimension: null,
    dimensionValue: null,
    showfixed: false
  },
  tableAlignment: 'CENTER'
})

const isDownloading = ref(false)

// 处理文件选择
const handleFileSelected = (file) => {
  selectedFile.value = file
}

// 下载文档
const downloadDocument = async () => {
  try {
    // 设置下载状态为 true
    isDownloading.value = true

    // 确保在下载前同步最新的配置
    if (formatConfigRef.value) {
      formatConfigRef.value.saveAllConfigs()
    }

    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('config', JSON.stringify(toRaw(formatConfigObj)))

    const response = await axios.post('http://localhost:8080/api/process', formData, {
      responseType: 'blob'
    })
    // http://47.238.88.209:8080/api/process

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `processed_${selectedFile.value.name}`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('文档处理成功')
  } catch (error) {
    console.error('处理文档时出错:', error)
    ElMessage.error('处理文档时出错，请重试')
  } finally {
    // 无论成功还是失败，都将下载状态设为 false
    isDownloading.value = false
  }
}

const goToDownload = () => {
  console.log('查看config', formatConfigObj)
  if (formatConfigRef.value) {
    formatConfigRef.value.saveAllConfigs()
    activeStep.value = 2
  }
}
</script>

<style>
.app-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.app-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.steps {
  margin: 20px 0;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.preview-container {
  margin-top: 20px;
}

.preview-frame {
  margin-top: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  height: 600px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.preview-frame iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* Estilos adicionales para mejorar el aspecto visual de los elementos de imagen */
:deep(.center-image) {
  text-align: center;
  margin: 15px auto;
  display: block;
}

:deep(.left-image) {
  float: left;
  margin: 15px 15px 15px 0;
}

:deep(.right-image) {
  float: right;
  margin: 15px 0 15px 15px;
}

/* Estilos para mejorar la visualización de tablas en la vista previa */
:deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin-bottom: 15px;
}

:deep(th),
:deep(td) {
  border: 1px solid #dcdfe6;
  padding: 8px;
}

:deep(th) {
  background-color: #f2f2f2;
  font-weight: bold;
}

.download-notice {
  margin: 15px 0;
  font-size: 25px;
  /* 增加字体大小 */
  padding: 15px;
  /* 增加内边距 */
}

/* 可以为提示信息内的段落添加额外样式 */
.download-notice p {
  line-height: 1.6;
  /* 增加行高 */
  margin-bottom: 10px;
  /* 段落间距 */
}

.important-notice {
  border-left-width: 5px;
  /* 更宽的边框 */
  background-color: #f0f9ff;
  /* 更深的背景色 */
}

.progress-container {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.download-progress {
  margin-bottom: 10px;
}

.processing-text {
  text-align: center;
  color: #606266;
  font-size: 16px;
  font-weight: bold;
  margin: 10px 0 0 0;
}

.prompt {
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  font-size: 18px;
}

.prompt p {
  margin: 5px 0;
}
</style>
