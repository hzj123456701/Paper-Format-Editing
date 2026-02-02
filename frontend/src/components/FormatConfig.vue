<template>
  <div class="config-container">
    <el-tabs type="border-card">
      <el-tab-pane label="正文设置">
        <div>
          <normal-text-settings ref="normalTextRef" v-model:config="localConfig.customStyles.normal" />
          <paragraph-settings ref="paragraphRef" v-model:config="localConfig.customStyles.paragraph" />
        </div>
      </el-tab-pane>
      <el-tab-pane label="标题（特殊段落）设置">
        <head-settings ref="titleRef" v-model:config="localConfig.heading" />
      </el-tab-pane>
      <el-tab-pane label="表格设置">
        <table-settings ref="tableRef" v-model:config="localConfig.tableAlignment" />
      </el-tab-pane>
      <el-tab-pane label="图片设置">
        <image-settings ref="imageRef" v-model:config="localConfig.image" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import NormalTextSettings from './NormalTextSettings.vue'
import ParagraphSettings from './ParagraphSettings.vue'
import HeadSettings from './HeadingSettings.vue'
import TableSettings from './TableSettings.vue'
import ImageSettings from './ImageSettings.vue'

// 创建子组件的引用
const normalTextRef = ref(null)//正文
const paragraphRef = ref(null)//正文段落
const titleRef = ref(null)//标题
const tableRef = ref(null)//表格
const imageRef = ref(null)//图片

// Define props
// config为app.vue中传递的配置对象
// v-model:config="localConfig"实现双向绑定，将config的值绑定到localConfig上，传递给子组件
const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

// Define emits
// 同步数据到父组件App.vue
const emit = defineEmits(['update:config'])

// Computed property for two-way binding
//访问localConfig的值时，会自动触发get方法，赋值时，会触发set方法
const localConfig = computed({
  get: () => props.config,
  set: (newVal) => emit('update:config', newVal)
})

// 保存所有子组件的配置
//在点击下一步时，调用saveAllConfigs方法，将所有子组件的配置保存到配置对象中
const saveAllConfigs = () => {
  // 调用每个组件的saveConfig方法
  console.log('保存所有子组件的配置')
  normalTextRef.value?.saveConfig?.()
  paragraphRef.value?.saveConfig?.()
  titleRef.value?.saveConfig?.()
  tableRef.value?.saveConfig?.()
  imageRef.value?.saveConfig?.()

  // 确保更新完成
  emit('update:config', localConfig.value)

  // 始终同步大标题配置到配置对象，不再检查条件
  // console.log('主动添加大标题配置到配置对象')
  // if (titleRef.value?.syncMainTitleConfig) {
  //   titleRef.value.syncMainTitleConfig()
  // }
}

// 公开saveAllConfigs方法给父组件
defineExpose({
  saveAllConfigs
})
</script>

<style scoped>
.config-container {
  margin: 20px 0;
}

/* 添加正文设置和段落设置之间的间距 */
.normal-text-settings+.paragraph-settings {
  margin-top: 30px;
}
</style>
