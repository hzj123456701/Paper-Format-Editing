<template>
  <div class="normal-text-settings">
    <h3>正文格式设置</h3>

    <el-form label-width="120px">
      <!-- 字体设置分组 -->
      <el-divider content-position="left">字体设置</el-divider>
      <!-- 免责声明 -->
      <el-alert
        title="字体选择注意事项"
        type="warning"
        :closable="false"
        description="请确保选择本机可正常使用的字体。如果选择本机未安装或未授权的字体，可能会导致文档显示异常。用户需自行承担因字体选择不当所导致的任何后果，本平台概不负责。"
        show-icon
        style="margin-bottom: 20px;"
      />
      <!-- 中文字体设置 -->
      <el-form-item label="中文字体">
        <el-select v-model="localConfig.normalFontCn" placeholder="选择中文字体">
          <el-option label="宋体" value="宋体" />
          <el-option label="黑体" value="黑体" />
          <el-option label="微软雅黑" value="微软雅黑" />
          <el-option label="仿宋" value="仿宋" />
          <el-option label="楷体" value="楷体" />
          <el-option label="等线" value="等线" />
        </el-select>
      </el-form-item>


      <el-form-item label="西文（英文）字体">
        <el-select v-model="localConfig.normalFontWestern" placeholder="选择西文字体（包括英文，数字，符号等）">
          <el-option label="默认 (Tahoma)" value="Times New Roman" />
          <el-option label="与中文字体一致" value="@same-as-chinese" />
          <el-option label="Times New Roman" value="Times New Roman" />
          <el-option label="Arial" value="Arial" />
          <el-option label="Calibri" value="Calibri" />
          <el-option label="Courier New" value="Courier New" />
          <el-option label="Georgia" value="Georgia" />
          <el-option label="Verdana" value="Verdana" />
          <el-option label="Tahoma" value="Tahoma" />
          <el-option label="Cambria" value="Cambria" />
          <el-option label="Garamond" value="Garamond" />
          <el-option label="Segoe UI" value="Segoe UI" />
        </el-select>
      </el-form-item>

      <el-form-item label="复杂文种字体">
        <el-select v-model="localConfig.normalFontComplex" placeholder="选择复杂文种字体（不确定可以直接选择默认）">
          <el-option label="默认 (Tahoma)" value="Tahoma" />
          <el-option label="与西文字体一致" value="@same-as-western" />
          <el-option label="与中文字体一致" value="@same-as-chinese" />
          <el-option label="Arial Unicode MS" value="Arial Unicode MS" />
          <el-option label="Tahoma" value="Tahoma" />
          <el-option label="Segoe UI" value="Segoe UI" />
          <el-option label="Times New Roman" value="Times New Roman" />
          <el-option label="Arial" value="Arial" />
          <el-option label="Microsoft Sans Serif" value="Microsoft Sans Serif" />
          <el-option label="Calibri" value="Calibri" />
          <el-option label="Lucida Sans Unicode" value="Lucida Sans Unicode" />
          <el-option label="Palatino Linotype" value="Palatino Linotype" />
          <el-option label="Cambria" value="Cambria" />
        </el-select>
      </el-form-item>



      <!-- 字体样式分组 -->
      <el-divider content-position="left">外观设置</el-divider>

      <!-- 字体大小设置 -->
      <el-form-item label="字体大小">
        <el-select v-model="localConfig.normalFontSize" placeholder="选择字体大小">
          <!-- 中文字号 -->
          <el-option-group label="字号">
            <el-option label="初号 (42pt)" :value="42" />
            <el-option label="小初 (36pt)" :value="36" />
            <el-option label="一号 (26pt)" :value="26" />
            <el-option label="小一 (24pt)" :value="24" />
            <el-option label="二号 (22pt)" :value="22" />
            <el-option label="小二 (18pt)" :value="18" />
            <el-option label="三号 (16pt)" :value="16" />
            <el-option label="小三 (15pt)" :value="15" />
            <el-option label="四号 (14pt)" :value="14" />
            <el-option label="小四 (12pt)" :value="12" />
            <el-option label="五号 (10.5pt)" :value="10.5" />
            <el-option label="小五 (9pt)" :value="9" />
            <el-option label="六号 (7.5pt)" :value="7.5" />
            <el-option label="小六 (6.5pt)" :value="6.5" />
          </el-option-group>
          <!-- 数字字号 -->
          <el-option-group label="数字字号">
            <el-option label="8" :value="8" />
            <el-option label="9" :value="9" />
            <el-option label="10" :value="10" />
            <el-option label="11" :value="11" />
            <el-option label="12" :value="12" />
            <el-option label="14" :value="14" />
            <el-option label="16" :value="16" />
            <el-option label="18" :value="18" />
            <el-option label="20" :value="20" />
            <el-option label="22" :value="22" />
            <el-option label="24" :value="24" />
            <el-option label="26" :value="26" />
            <el-option label="28" :value="28" />
            <el-option label="36" :value="36" />
            <el-option label="48" :value="48" />
            <el-option label="72" :value="72" />
          </el-option-group>
        </el-select>
      </el-form-item>

      <!-- 字体颜色设置 -->
      <el-form-item label="字体颜色">
        <el-color-picker v-model="localConfig.fontColor" />
        <span class="color-reset" @click="resetColor">使用默认色</span>
      </el-form-item>

      <!-- 字形设置 -->
      <el-form-item label="字形样式">
        <el-checkbox-group v-model="localConfig.fontStyle">
          <el-checkbox value="bold">加粗</el-checkbox>
          <el-checkbox value="italic">倾斜</el-checkbox>
          <el-checkbox value="underline">下划线</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <!-- 单独设置英文样式开关 -->
      <!-- <el-form-item label="单独设置英文样式" label-width="175px" style="display: flex; align-items: center;">
        <el-switch v-model="localConfig.setEnglishCharStyleIndividually"></el-switch>
      </el-form-item> -->

      <!-- 单独设置英文样式复选框，根据开关状态显示 -->
      <!-- <el-form-item label="英文样式" v-if="localConfig.setEnglishCharStyleIndividually">
        <el-checkbox-group v-model="localConfig.englishFontStyle">
          <el-checkbox value="bold">加粗</el-checkbox>
          <el-checkbox value="italic">倾斜</el-checkbox>
          <el-checkbox value="underline">下划线</el-checkbox>
        </el-checkbox-group>
      </el-form-item> -->
    </el-form>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'





// Define props
// config接收从FormatConfig组件传递的配置对象
const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

// Define emits
const emit = defineEmits(['update:config', 'saveConfig'])

// 创建组件内部的响应式状态
//正文格式的默认配置
const localConfig = reactive({
  normalFontCn: '宋体',
  normalFontWestern: 'Times New Roman',
  normalFontComplex: 'Tahoma',
  normalFontSize: 12,
  fontColor: '',//默认颜色由后端实现
  fontStyle: [],
  englishFontStyle: [],
  setEnglishCharStyleIndividually: false,
})

// 组件挂载时从props初始化本地状态
onMounted(() => {
  // 从props.config加载初始状态
  // console.log("开始加载正文配置")
  console.log(props.config)
  if (props.config) {
    if (props.config) {
      const normalStyle = props.config
      if (normalStyle.normalFontCn) localConfig.normalFontCn = normalStyle.normalFontCn
      if (normalStyle.normalFontWestern) localConfig.normalFontWestern = normalStyle.normalFontWestern
      if (normalStyle.normalFontComplex) localConfig.normalFontComplex = normalStyle.normalFontComplex
      if (normalStyle.normalFontSize) localConfig.normalFontSize = normalStyle.normalFontSize
      if (normalStyle.fontColor) localConfig.fontColor = normalStyle.fontColor
      if (normalStyle.englishFontStyle) localConfig.englishFontStyle = normalStyle.englishFontStyle
      if (normalStyle.setEnglishCharStyleIndividually) localConfig.setEnglishCharStyleIndividually = normalStyle.setEnglishCharStyleIndividually
      if (normalStyle.fontStyle) localConfig.fontStyle = normalStyle.fontStyle
    }

  }

})

// 重置颜色方法
const resetColor = () => {
  localConfig.fontColor = '#000000'
}


// 将组件内部状态同步到父组件
const syncConfigToParent = () => {
  // 处理字体设置
  const cnFont = localConfig.normalFontCn || '宋体'
  let normalFontWestern = localConfig.normalFontWestern || 'Times New Roman'
  let normalFontComplex = localConfig.normalFontComplex || 'Tahoma'

  if (normalFontWestern === '@same-as-chinese') {
    normalFontWestern = cnFont
  }
  if (normalFontComplex === '@same-as-chinese') {
    normalFontComplex = cnFont
  }
  if (normalFontComplex === '@same-as-western') {
    normalFontComplex = normalFontWestern
  }



  const configToSync = {
    // ...props.config,//对象解构赋值，将父组件传来的config对象解构赋值给configToSync
    normalFontCn: cnFont,
    normalFontWestern: normalFontWestern,
    normalFontComplex: normalFontComplex,
    normalFontSize: localConfig.normalFontSize,
    fontColor: localConfig.fontColor,
    fontStyle: localConfig.fontStyle,
    englishFontStyle: localConfig.englishFontStyle,
    setEnglishCharStyleIndividually: localConfig.setEnglishCharStyleIndividually,

  }
  // 发送更新事件，将配置同步到父组件
  //传递经过处理的configToSync对象给父组件
  emit('update:config', configToSync)
}

// 公开一个方法供父组件调用，用于"下一步"按钮
defineExpose({
  saveConfig: syncConfigToParent
})
</script>

<style scoped>
.normal-text-settings {
  padding: 0px;
}

.color-reset {
  margin-left: 10px;
  cursor: pointer;
  color: #409EFF;
  font-size: 14px;
}

.color-reset:hover {
  text-decoration: underline;
}

.preview-box {
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  padding: 20px;
  margin-top: 10px;
  background-color: #f8f8f8;
}

.preview-text {
  line-height: 1.5;
  padding: 15px;
  background-color: white;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 15px;
}

.font-info {
  background-color: white;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  line-height: 1.5;
  font-size: 14px;
}
</style>




<!-- 该组件中没有使用计算属性，在组件挂载时更新一次数据，在点击下一步时更行一次数据 -->
