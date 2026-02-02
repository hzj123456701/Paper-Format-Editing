<template>
  <div class="table-settings">
    <h3>表格格式设置</h3>

    <!-- 提示信息 -->
    <el-alert type="warning" show-icon :closable="true" class="settings-alert">
      <template #title>
        <strong>表格格式应用提示</strong>
      </template>
    </el-alert>

    <!-- 表名设置提示 -->
    <el-alert type="info" show-icon :closable="true" class="settings-alert">
      <template #title>
        <strong>表名格式设置提示</strong>
      </template>
      <p>如果需要对表名单独设置格式，可以直接在"标题（特殊段落）设置"中配置，将段落前缀示例设置为表名的共有部分，如"表一"。</p>
      <p>系统会将所有符合该前缀的表名（如表一：表名，表二：表名）识别为特殊段落，并应用相应的格式设置。</p>
    </el-alert>

    <el-form label-width="120px">
      <el-form-item label="表格对齐方式">
        <el-radio-group v-model="localConfig.tableAlignment">
          <el-radio value="LEFT">左对齐</el-radio>
          <el-radio value="CENTER">居中</el-radio>
          <el-radio value="RIGHT">右对齐</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'

// Define props
const props = defineProps({
  config: {
    type: String,
    required: true
  }
})

// Define emits
const emit = defineEmits(['update:config', 'saveConfig'])

// 创建组件内部的响应式状态
const localConfig = reactive({
  tableAlignment: 'CENTER'
})

// 组件挂载时从props初始化本地状态
onMounted(() => {
  // console.log(props.config)
  // 从props.config加载初始状态
  if (props.config) {
    localConfig.tableAlignment = props.config
  }
})

// 将组件内部状态同步到父组件
const syncConfigToParent = () => {
  // console.log("更新表格设置", localConfig.tableAlignment)
  emit('update:config', localConfig.tableAlignment)
}

// 公开一个方法供父组件调用，用于"下一步"按钮
defineExpose({
  saveConfig: syncConfigToParent
})
</script>

<style scoped>
.table-settings {
  padding: 0px;
}

.settings-alert {
  margin: 10px 0 15px;
}
</style>
