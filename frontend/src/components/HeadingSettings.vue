<template>
  <div class="title-settings">
    <!-- 全局标题规则控制 -->
    <div class="global-rule-control">
      <el-switch v-model="localConfig.useCustomHeadingRules" active-text="使用标题（特殊段落）规则" size="large"
        class="rule-main-switch" />
      <h3>标题（特殊段落）设置</h3>
    </div>

    <!-- 使用说明提示信息 -->
    <el-alert v-if="localConfig.useCustomHeadingRules" type="info" show-icon :closable="true" class="settings-alert">
      <template #title>
        <strong>如何设置特殊段落格式</strong>
      </template>
      <p>对于需要区别于正文设置的段落，只需输入段落的前缀示例，系统将根据此前缀示例自动识别文档中的对应段落。</p>
      <p>如需对多个不同的段落应用相同格式，请输入这些段落所共有的前缀部分。</p>
      <p>当有多个级别的段落设置时，系统会按照级别数从大到小依次识别段落。</p>
    </el-alert>

    <!-- headingRules -->
    <el-alert v-if="!localConfig.useCustomHeadingRules" type="warning" title="标题（特殊段落）规则已禁用"
      description="您已禁用所有标题（特殊段落）规则，系统将不会自动识别文档中的标题（特殊段落）" show-icon :closable="false" class="settings-alert" />

    <!-- 当标题规则禁用时显示的提示信息 -->
    <div v-if="!localConfig.useCustomHeadingRules" class="disabled-settings-guide">
      <el-empty description="标题（特殊段落）规则已禁用" :image-size="150">
        <template #description>
          <div>
            <p>您已禁用标题（特殊段落）规则功能，所有标题（特殊段落）设置都已隐藏</p>
            <p>要进行标题（特殊段落）样式设置，请先开启"使用标题（特殊段落）规则"开关</p>
          </div>
        </template>
        <el-button type="primary" @click="localConfig.useCustomHeadingRules = true;">开启标题（特殊段落）规则</el-button>
      </el-empty>
    </div>
    <!-- <label for=""></label> -->
    <!-- 标题级别选择栏 -->
    <div class="level-control" v-if="localConfig.useCustomHeadingRules">
      <el-radio-group v-model="activeHeadingTab" size="large" class="heading-level-tabs">
        <el-radio-button v-for="level in getTitleLevels()" :key="level" :value="level"
          :disabled="!localConfig.useCustomHeadingRules">
          {{ level }}级标题（特殊段落）
        </el-radio-button>

        <el-button type="primary" @click="addTitleLevel"
          :disabled="getMaxLevel() >= 7 || !localConfig.useCustomHeadingRules" class="add-level-btn">
          <el-icon>
            <Plus />
          </el-icon>
        </el-button>
      </el-radio-group>
    </div>
    <!-- 标题级别配置区域 -->
    <div v-for="level in getTitleLevels()" :key="level"
      v-show="activeHeadingTab === level && localConfig.useCustomHeadingRules" class="heading-level-section">
      <div class="heading-level-header">
        <h4>{{ level }}级标题（特殊段落）配置</h4>
        <div class="heading-controls">
          <el-button type="danger" size="small" icon="Delete" circle @click="removeAndSwitchTitleLevel(level)"
            :disabled="getTitleLevels().some(l => l > level)" title="删除此级标题（特殊段落）">
          </el-button>
        </div>
      </div>

      <el-divider></el-divider>

      <!-- 只有当规则启用或全局规则关闭时才显示设置内容 -->
      <div v-if="localConfig.useCustomHeadingRules">
        <el-form label-width="120px">
          <el-form-item label="段落前缀示例">
            <el-input v-model="localConfig.titleExamples[level].example" placeholder="输入标题（特殊段落）前缀示例，如：1.1.1"
              @input="updateTitlePattern(level)" />
            <div class="help-text">
              <i class="el-icon-info"></i>
              例如输入"第一章"，系统将识别所有以"第x章"开头的段落
            </div>
          </el-form-item>

          <el-form-item label="识别规则">
            <el-input v-model="localConfig.headingRules[level].pattern" readonly placeholder="自动生成的识别规则" />
            <el-tooltip content="识别规则用于自动识别文档中的标题（特殊段落）" placement="top">
              <el-icon class="info-icon">
                <InfoFilled />
              </el-icon>
            </el-tooltip>
          </el-form-item>

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
            <el-select v-model="localConfig.headingStyles[level].headingFontCn" placeholder="选择中文字体">
              <el-option label="宋体" value="宋体" />
              <el-option label="黑体" value="黑体" />
              <el-option label="微软雅黑" value="微软雅黑" />
              <el-option label="仿宋" value="仿宋" />
              <el-option label="楷体" value="楷体" />
              <el-option label="等线" value="等线" />
            </el-select>
          </el-form-item>
          <el-form-item label="西文（英文）字体">
            <el-select v-model="localConfig.headingFontWestern" placeholder="选择西文字体（包括英文，数字，符号等）">
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
            <el-select v-model="localConfig.headingFontComplex" placeholder="选择复杂文种字体（不确定可以直接选择默认）">
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
            <el-select v-model="localConfig.headingStyles[level].headingFontSize" placeholder="选择字体大小">
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
            </el-select>
          </el-form-item>

          <!-- 字体颜色设置 -->
          <el-form-item label="字体颜色">
            <el-color-picker v-model="localConfig.headingStyles[level].fontColor" />
            <span class="color-reset" @click="resetTitleLevelColor(level)">使用默认色</span>
          </el-form-item>
          <!-- 字形设置 -->
          <el-form-item label="字形样式">
            <el-checkbox-group v-model="localConfig.headingStyles[level].fontStyle">
              <el-checkbox value="bold">加粗</el-checkbox>
              <el-checkbox value="italic">倾斜</el-checkbox>
              <el-checkbox value="underline">下划线</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <!-- 单独设置英文样式开关
          <el-form-item label="单独设置英文样式" label-width="175px" style="display: flex; align-items: center;">
            <el-switch v-model="localConfig.headingStyles[level].setEnglishCharStyleIndividually"></el-switch>
          </el-form-item>

           单独设置英文样式复选框，根据开关状态显示 -->
          <!--<el-form-item label="英文样式" v-show="localConfig.headingStyles[level].setEnglishCharStyleIndividually">
            <el-checkbox-group v-model="localConfig.headingStyles[level].englishFontStyle">
              <el-checkbox value="bold">加粗</el-checkbox>
              <el-checkbox value="italic">倾斜</el-checkbox>
              <el-checkbox value="underline">下划线</el-checkbox>
            </el-checkbox-group>
          </el-form-item>  -->

          <!-- 段落设置分组 -->
          <el-divider content-position="left">段落设置</el-divider>

          <!-- 行间距设置 -->
          <el-form-item label="行间距类型">
            <el-radio-group v-model="localConfig.headingStyles[level].lineSpacingType">
              <el-radio value="multiple">多倍行距</el-radio>
              <el-radio value="exact">固定值</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 根据选择的行间距类型显示不同的设置选项 -->
          <el-form-item label="行间距">
            <div v-if="localConfig.headingStyles[level].lineSpacingType === 'multiple'" class="unit-input-group">
              <el-input-number v-model="localConfig.headingStyles[level].lineSpacing" :min="0.5" :max="10" :step="0.05"
                :precision="2" />
              <span class="unit">倍</span>
            </div>
            <div v-else class="unit-input-group">
              <el-input-number v-model="localConfig.headingStyles[level].lineSpacing" :min="0" :max="100" :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].lineSpacingUnit" class="unit-select">
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
              <el-input-number v-model="localConfig.headingStyles[level].firstLineIndent" :min="0" :max="100"
                :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].firstLineIndentUnit" class="unit-select">
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
              <el-input-number v-model="localConfig.headingStyles[level].leftIndent" :min="0" :max="100" :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].leftIndentUnit" class="unit-select">
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
              <el-input-number v-model="localConfig.headingStyles[level].rightIndent" :min="0" :max="100" :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].rightIndentUnit" class="unit-select">
                <el-option label="磅" value="pt" />
                <el-option label="厘米" value="cm" />
                <el-option label="毫米" value="mm" />
                <el-option label="英寸" value="in" />
                <el-option label="字符" value="ch" />
              </el-select>
            </div>
          </el-form-item>

          <!-- 对齐方式 -->
          <el-form-item label="对齐方式">
            <el-radio-group v-model="localConfig.headingStyles[level].alignment">
              <el-radio value="LEFT">左对齐</el-radio>
              <el-radio value="CENTER">居中</el-radio>
              <el-radio value="RIGHT">右对齐</el-radio>
              <el-radio value="JUSTIFY">两端对齐</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 段前距 -->
          <el-form-item label="段前距">
            <div class="unit-input-group">
              <el-input-number v-model="localConfig.headingStyles[level].spaceBefore" :min="0" :max="100" :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].spaceBeforeUnit" class="unit-select">
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
              <el-input-number v-model="localConfig.headingStyles[level].spaceAfter" :min="0" :max="100" :step="0.5" />
              <el-select v-model="localConfig.headingStyles[level].spaceAfterUnit" class="unit-select">
                <el-option label="磅" value="pt" />
                <el-option label="厘米" value="cm" />
                <el-option label="毫米" value="mm" />
                <el-option label="英寸" value="in" />
                <el-option label="行" value="line" />
              </el-select>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { InfoFilled, Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

// Define props
const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

// Define emits
const emit = defineEmits(['update:config', 'saveConfig'])

// 当前活动标题级别标签
const activeHeadingTab = ref('1');

// 创建组件内部的响应式状态
const localConfig = reactive({
  headingStyles: {},
  headingRules: {},
  titleExamples: {},
  useCustomHeadingRules: true
})

// 获取标题级别
const getTitleLevels = () => {
  return Object.keys(localConfig.headingStyles).sort((a, b) => parseInt(a) - parseInt(b))
}

// 获取最大标题级别
const getMaxLevel = () => {
  const levels = getTitleLevels()
  return levels.length > 0 ? parseInt(levels[levels.length - 1], 10) : 0
}

// 默认标题规则配置
const defaultHeadingConfig = {
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
    },
    2: {
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
    }
  },
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
  }
}

// 组件挂载时从props初始化本地状态
onMounted(() => {
  // 从props.config加载初始状态
  // console.log("开始加载标题配置")
  // console.log(props.config)

  if (props.config) {
    // 浅拷贝对象，避免直接引用，这里直接引用也行，即localConfig.headingStyles = props.config.headingStyles子组件和父组件指向同一块内存
    if (props.config.headingStyles) {
      localConfig.headingStyles = { ...props.config.headingStyles }
    }
    if (props.config.headingRules) {
      localConfig.headingRules = { ...props.config.headingRules }
    }
    if (props.config.titleExamples) {
      localConfig.titleExamples = { ...props.config.titleExamples }
    }
    if (props.config.useCustomHeadingRules !== undefined) {
      localConfig.useCustomHeadingRules = props.config.useCustomHeadingRules
    }
  }

  // 确保至少有一个级别的标题
  if (Object.keys(localConfig.headingStyles).length === 0) {
    localConfig.headingStyles['1'] = {
      headingFontCn: '宋体',
      headingFontWestern: 'Times New Roman',
      headingFontComplex: 'Tahoma',
      headingFontSize: 12,
      fontColor: '#000000',
      fontStyle: [],
      englishFontStyle: [],
      setEnglishCharStyleIndividually: false,
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
    }
  }
})

// 添加标题级别
const addTitleLevel = () => {
  const currentLevels = getTitleLevels()
  const newLevel = parseInt(currentLevels[currentLevels.length - 1], 10) + 1
  const maxTitleLevel = 7

  if (newLevel <= maxTitleLevel) {
    localConfig.headingStyles[newLevel] = {
      headingFontCn: '宋体',
      headingFontWestern: 'Times New Roman',
      headingFontComplex: 'Tahoma',
      headingFontSize: 12,
      fontColor: '#000000',
      fontStyle: [],
      englishFontStyle: [],
      setEnglishCharStyleIndividually: false,
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
    }

    // 初始化规则和示例
    localConfig.headingRules[newLevel] = {
      pattern: "第[一二三四五六七八九十]+章"
    }
    localConfig.titleExamples[newLevel] = {
      example: "第一章"
    }

    // 切换到新增的级别
    activeHeadingTab.value = newLevel.toString()

    // 同步到父组件
    syncConfigToParent()
  }
}

// 删除标题级别并切换
const removeAndSwitchTitleLevel = (level) => {
  // 确保不能删除有更高级别的标题
  if (getTitleLevels().some(l => parseInt(l) > parseInt(level))) {
    ElMessageBox.alert('不能删除此级标题，请先删除更高级别的标题', '操作提示', {
      confirmButtonText: '确定',
      type: 'warning'
    })
    return
  }

  const isLastLevel = Object.keys(localConfig.headingStyles).length === 1

  // 弹出确认对话框
  const confirmMessage = isLastLevel
    ? '确定要删除最后一级标题规则吗？这将关闭标题规则功能。'
    : '确定要删除此级标题规则吗？'

  ElMessageBox.confirm(confirmMessage, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (isLastLevel) {
      // 关闭标题规则功能并重置为默认状态
      localConfig.useCustomHeadingRules = false
      localConfig.headingStyles = { ...defaultHeadingConfig.headingStyles }
      localConfig.headingRules = { ...defaultHeadingConfig.headingRules }
      localConfig.titleExamples = { ...defaultHeadingConfig.titleExamples }
    } else {
      // 删除对应级别
      delete localConfig.headingStyles[level]
      delete localConfig.headingRules[level]
      delete localConfig.titleExamples[level]

      // 切换到最后一个级别
      const levels = getTitleLevels()
      if (levels.length > 0) {
        activeHeadingTab.value = levels[levels.length - 1]
      } else {
        activeHeadingTab.value = '1'
      }
    }

    // 同步到父组件
    syncConfigToParent()
  }).catch(() => {
    // 用户点击取消，不做任何操作
  })
}

// 重置标题颜色
const resetTitleLevelColor = (level) => {
  if (localConfig.headingStyles[level]) {
    localConfig.headingStyles[level].fontColor = '#000000'
  }
}

// 更新标题识别规则
const updateTitlePattern = (level) => {
  // 实现标题识别规则的生成逻辑
  // 这里可以根据用户输入的示例生成正则表达式
  let regexPattern = ''
  const text = localConfig.titleExamples[level].example
  if (text) {
    const numberPattern = /\d/
    const chineseNumberPattern = /[一二三四五六七八九十]/

    for (let i = 0; i < text.length; i++) {
      const char = text[i]
      if (numberPattern.test(char)) {
        if (i === 0 || !numberPattern.test(text[i - 1])) {
          regexPattern += '\\d+'
        }
      } else if (chineseNumberPattern.test(char)) {
        if (i === 0 || !chineseNumberPattern.test(text[i - 1])) {
          regexPattern += '[一二三四五六七八九十]+'
        }
      } else {
        const escapedChar = char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
        regexPattern += escapedChar
      }
    }
  }
  localConfig.headingRules[level].pattern = regexPattern


  // 同步到父组件
  syncConfigToParent()
}

// 将组件内部状态同步到父组件
const syncConfigToParent = () => {
  // 创建一个新对象来避免引用问题
  const configToSync = {
    headingStyles: { ...localConfig.headingStyles },
    headingRules: { ...localConfig.headingRules },
    titleExamples: { ...localConfig.titleExamples },
    useCustomHeadingRules: localConfig.useCustomHeadingRules
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
.title-settings {
  padding: 0px;
}

.level-control {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.heading-level-tabs {
  display: flex;
  align-items: center;
}

.add-level-btn {
  margin-left: 10px;
}

.main-title-section,
.heading-level-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f0f9ff;
}

.main-title-header,
.heading-level-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-title-header h4,
.heading-level-header h4 {
  margin: 0;
  color: #409EFF;
  font-size: 16px;
}

.main-title-content {
  margin-top: 15px;
}

.unit-input-group {
  display: flex;
  align-items: center;
}

.unit-select {
  width: 80px;
  margin-left: 10px;
}

.color-reset {
  margin-left: 10px;
  color: #409EFF;
  cursor: pointer;
  font-size: 12px;
}

.color-reset:hover {
  text-decoration: underline;
}

.preview-box {
  margin-top: 15px;
  padding: 15px;
  border: 1px dashed #ccc;
  border-radius: 4px;
  background-color: #fafafa;
}

.preview-text {
  margin-bottom: 10px;
  padding: 10px;
  background-color: white;
  border: 1px solid #eee;
  min-height: 30px;
}

.settings-info {
  font-size: 12px;
  color: #666;
  display: flex;
  flex-wrap: wrap;
}

.settings-info div {
  margin-right: 15px;
}

.unit {
  margin-left: 10px;
  color: #666;
}

.info-icon {
  margin-left: 8px;
  color: #909399;
  cursor: help;
}

.global-rule-control {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.settings-alert {
  margin-top: 10px;
  margin-bottom: 15px;
}

.heading-controls {
  display: flex;
  align-items: center;
}

.rule-switch {
  margin-right: 25px;
}

.rule-hint {
  color: #e6a23c;
  font-size: 12px;
  margin-right: 10px;
  white-space: nowrap;
}

.rule-main-switch {
  margin-right: 15px;
  --el-switch-on-color: #409EFF;
  --el-switch-size: 24px;
}

.rule-main-switch :deep(.el-switch__label) {
  font-size: 16px;
  font-weight: bold;
}

.heading-controls .el-button {
  margin-left: 10px;
}

.disabled-settings-guide {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f0f9ff;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.action-buttons .el-button {
  margin-left: 10px;
}

.rule-hint-icon {
  margin-left: 8px;
  color: #e6a23c;
  cursor: help;
}

.help-text {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  padding-left: 2px;
}
</style>
