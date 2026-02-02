<template>
  <div class="paragraph-settings">
    <h3>段落格式设置</h3>
    <el-form label-width="120px">
      <!-- 行间距设置 -->
      <el-form-item label="行间距类型">
        <el-radio-group v-model="localConfig.lineSpacingType">
          <el-radio value="multiple">多倍行距</el-radio>
          <el-radio value="exact">固定值</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 根据选择的行间距类型显示不同的设置选项 -->
      <el-form-item label="行间距">
        <div v-if="localConfig.lineSpacingType === 'multiple'" class="unit-input-group">
          <el-input-number v-model="localConfig.lineSpacing" :min="0.5" :max="10" :step="0.05" :precision="2" />
          <span class="unit">倍</span>
        </div>
        <div v-else class="unit-input-group">
          <el-input-number v-model="localConfig.lineSpacing" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.lineSpacingUnit" class="unit-select">
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 首行缩进 -->
      <el-form-item label="首行缩进">
        <div class="unit-input-group">
          <el-input-number v-model="localConfig.firstLineIndent" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.firstLineIndentUnit" class="unit-select">
            <el-option label="字符" value="ch" />
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 左侧缩进 -->
      <el-form-item label="左侧缩进">
        <div class="unit-input-group">
          <el-input-number v-model="localConfig.leftIndent" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.leftIndentUnit" class="unit-select">
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
            <el-option label="字符" value="ch" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 右侧缩进 -->
      <el-form-item label="右侧缩进">
        <div class="unit-input-group">
          <el-input-number v-model="localConfig.rightIndent" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.rightIndentUnit" class="unit-select">
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
            <el-option label="字符" value="ch" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 段前距 -->
      <el-form-item label="段前距">
        <div class="unit-input-group">
          <el-input-number v-model="localConfig.spaceBefore" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.spaceBeforeUnit" class="unit-select">
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
            <el-option label="行" value="line" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 段后距 -->
      <el-form-item label="段后距">
        <div class="unit-input-group">
          <el-input-number v-model="localConfig.spaceAfter" :min="0" :max="100" :step="0.5" />
          <el-select v-model="localConfig.spaceAfterUnit" class="unit-select">
            <el-option label="磅" value="pt" />
            <el-option label="厘米" value="cm" />
            <el-option label="毫米" value="mm" />
            <el-option label="英寸" value="in" />
            <el-option label="行" value="line" />
          </el-select>
        </div>
      </el-form-item>

      <!-- 对齐方式 -->
      <el-form-item label="对齐方式">
        <el-radio-group v-model="localConfig.alignment">
          <el-radio value="LEFT">左对齐</el-radio>
          <el-radio value="CENTER">居中</el-radio>
          <el-radio value="RIGHT">右对齐</el-radio>
          <el-radio value="JUSTIFY">两端对齐</el-radio>
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
    type: Object,
    required: true
  }
})

// Define emits
const emit = defineEmits(['update:config', 'saveConfig'])

// 创建组件内部的响应式状态
//默认配置
const localConfig = reactive({
  lineSpacingType: 'exact',
  lineSpacing: 24,
  lineSpacingUnit: 'pt',
  firstLineIndent: 0,
  firstLineIndentUnit: 'ch',
  leftIndent: 0,
  leftIndentUnit: 'pt',
  rightIndent: 0,
  rightIndentUnit: 'pt',
  spaceBefore: 0,
  spaceBeforeUnit: 'pt',
  spaceAfter: 0,
  spaceAfterUnit: 'pt',
  alignment: 'LEFT'
})

// 组件挂载时从props初始化本地状态
onMounted(() => {
  // 从props.config加载初始状态
  if (props.config) {
    const normalStyle = props.config
    if (normalStyle.lineSpacingType) localConfig.lineSpacingType = normalStyle.lineSpacingType
    if (normalStyle.lineSpacing !== undefined) localConfig.lineSpacing = normalStyle.lineSpacing
    if (normalStyle.lineSpacingUnit) localConfig.lineSpacingUnit = normalStyle.lineSpacingUnit
    if (normalStyle.firstLineIndent !== undefined) localConfig.firstLineIndent = normalStyle.firstLineIndent
    if (normalStyle.firstLineIndentUnit) localConfig.firstLineIndentUnit = normalStyle.firstLineIndentUnit
    if (normalStyle.leftIndent !== undefined) localConfig.leftIndent = normalStyle.leftIndent
    if (normalStyle.leftIndentUnit) localConfig.leftIndentUnit = normalStyle.leftIndentUnit
    if (normalStyle.rightIndent !== undefined) localConfig.rightIndent = normalStyle.rightIndent
    if (normalStyle.rightIndentUnit) localConfig.rightIndentUnit = normalStyle.rightIndentUnit
    if (normalStyle.spaceBefore !== undefined) localConfig.spaceBefore = normalStyle.spaceBefore
    if (normalStyle.spaceBeforeUnit) localConfig.spaceBeforeUnit = normalStyle.spaceBeforeUnit
    if (normalStyle.spaceAfter !== undefined) localConfig.spaceAfter = normalStyle.spaceAfter
    if (normalStyle.spaceAfterUnit) localConfig.spaceAfterUnit = normalStyle.spaceAfterUnit
    if (normalStyle.alignment) localConfig.alignment = normalStyle.alignment
  }
})

// 将组件内部状态同步到父组件
const syncConfigToParent = () => {
  // 创建一个新对象
  const configToSync = {
    ...props.config,
    lineSpacingType: localConfig.lineSpacingType,
    lineSpacing: localConfig.lineSpacing,
    lineSpacingUnit: localConfig.lineSpacingUnit,
    firstLineIndent: localConfig.firstLineIndent,
    firstLineIndentUnit: localConfig.firstLineIndentUnit,
    leftIndent: localConfig.leftIndent,
    leftIndentUnit: localConfig.leftIndentUnit,
    rightIndent: localConfig.rightIndent,
    rightIndentUnit: localConfig.rightIndentUnit,
    spaceBefore: localConfig.spaceBefore,
    spaceBeforeUnit: localConfig.spaceBeforeUnit,
    spaceAfter: localConfig.spaceAfter,
    spaceAfterUnit: localConfig.spaceAfterUnit,
    alignment: localConfig.alignment
  }


  // 发送更新事件
  emit('update:config', configToSync)
}


// 公开一个方法供父组件调用，用于"下一步"按钮
defineExpose({
  saveConfig: syncConfigToParent
})
</script>

<style scoped>
.paragraph-settings {
  padding: 0px;
}

.unit-input-group {
  display: flex;
  align-items: center;
}

.unit-select {
  width: 80px;
  margin-left: 10px;
}

.preview-box {
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  margin-top: 10px;
  background-color: #f8f8f8;
}

.preview-paragraph {
  background-color: white;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 15px;
}

.settings-info {
  background-color: white;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  line-height: 1.5;
  font-size: 14px;
}
</style>
