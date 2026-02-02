<template>
  <!-- 图片格式设置容器 -->
  <div class="image-settings">
    <!-- 标题 -->
    <h3>图片格式设置</h3>

    <!-- 提示信息 -->
    <el-alert type="warning" show-icon :closable="true" class="settings-alert">
      <template #title>
        <strong>图片格式应用提示</strong>
      </template>
      <p>图片的对齐方式不会对在一段文本中插入的图片生效</p>
    </el-alert>

    <!-- 图名设置提示 -->
    <el-alert type="info" show-icon :closable="true" class="settings-alert">
      <template #title>
        <strong>图名格式设置提示</strong>
      </template>
      <p>如果需要对图名单独设置格式，可以直接在"标题（特殊段落）设置"中配置，将段落前缀示例设置为图名的共有部分，如"图一"。</p>
      <p>系统会将所有符合该前缀的图名（如图一：图名，图二：图名）识别为特殊段落，并应用相应的格式设置。</p>
    </el-alert>


    <el-form label-width="160px" label-position="right">
      <!-- 图片对齐方式表单项 -->
      <el-form-item label="图片对齐方式" label-position="right">
        <!-- 单选框组，使用v-model绑定 -->
        <el-radio-group v-model="localConfig.imageAlignment">
          <!-- 左对齐单选框 -->
          <el-radio value="LEFT">左对齐</el-radio>
          <!-- 居中单选框 -->
          <el-radio value="CENTER">居中</el-radio>
          <!-- 右对齐单选框 -->
          <el-radio value="RIGHT">右对齐</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="固定图片高度或宽度">
        <el-switch v-model="localConfig.showfixed"></el-switch>
      </el-form-item>
      <!-- 固定尺寸选择和输入表单项，根据 showfixed 决定是否显示 -->
      <div v-if="localConfig.showfixed">
        <!-- 固定尺寸选择表单项 -->
        <el-form-item label="固定尺寸" label-position="right">
          <el-radio-group v-model="localConfig.fixedDimension">
            <el-radio value="height">固定高度</el-radio>
            <el-radio value="width">固定宽度</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 尺寸输入表单项（数字输入，带单位） -->
        <el-form-item label="固定值:">
          <div style="display: flex; align-items: center;">
            <el-input-number v-model="localConfig.dimensionValue" :min="0" :step="0.1" placeholder="请输入尺寸"
              style="width: 200px; margin-right: 8px;"></el-input-number>
            <span>厘米</span>
          </div>
        </el-form-item>
        <!-- 提醒信息 -->
        <el-form-item label="提示:">
          <div style="color: #999;">高度和宽度将会等比例缩放</div>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'

// 定义组件的 props，接收一个 config 对象
const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

// Define emits
const emit = defineEmits(['update:config', 'saveConfig'])

// 创建组件内部的响应式状态
const localConfig = reactive({
  imageAlignment: 'LEFT',
  fixedDimension: null,
  dimensionValue: null,
  showfixed: false
})

// 组件挂载时从props初始化本地状态
onMounted(() => {
  // console.log(props.config)
  // 从props.config加载初始状态
  if (props.config) {
    localConfig.imageAlignment = props.config.imageAlignment
    localConfig.fixedDimension = props.config.fixedDimension
    localConfig.dimensionValue = props.config.dimensionValue
    localConfig.showfixed = props.config.showfixed

  }
})

// 将组件内部状态同步到父组件
const syncConfigToParent = () => {
  // console.log("更新图片设置", localConfig.imageAlignment)
  emit('update:config', localConfig)
}

// 公开一个方法供父组件调用，用于"下一步"按钮
defineExpose({
  saveConfig: syncConfigToParent
})
</script>

<style scoped>
.image-settings {
  padding: 0px;
}

.settings-alert {
  margin: 10px 0 15px;
}
</style>
