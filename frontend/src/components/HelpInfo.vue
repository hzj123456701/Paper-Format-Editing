<template>
  <div class="help-info">
    <el-collapse>
      <el-collapse-item title="图片格式设置说明" name="1">
        <div class="help-content">
          <h3>图片对齐方式</h3>
          <p>您可以选择图片在文档中的对齐方式：</p>
          <ul>
            <li><strong>左对齐</strong>：图片靠左对齐，文本流环绕右侧。</li>
            <li><strong>居中</strong>：图片在页面中央，文本在图片上下。</li>
            <li><strong>右对齐</strong>：图片靠右对齐，文本流环绕左侧。</li>
          </ul>

          <h3>最大宽度</h3>
          <p>限制图片的最大宽度（以像素为单位）。超过此宽度的图片将被等比例缩小，有助于保持文档的一致性和减少文件大小。</p>

          <h3>增强图片效果</h3>
          <p>为图片添加轻微的阴影和边框效果，使图片在文档中更加突出和美观。注意：此效果只会在Word文档中显示，HTML预览可能略有不同。</p>

          <h3>优化图片</h3>
          <p>减小图片文件大小，提高文档加载速度。对于包含多张高分辨率图片的文档特别有用。</p>

          <div class="tips-section">
            <h4>使用建议</h4>
            <ul>
              <li>对于正式文档，建议使用居中对齐以保持整洁专业的外观。</li>
              <li>对于需要与文本紧密结合的图片，可以选择左对齐或右对齐。</li>
              <li>如果文档需要打印，请确保图片的最大宽度不要超过页面实际可打印区域。</li>
              <li>如果文档中包含许多图片，启用"优化图片"选项可以显著减小文件大小。</li>
            </ul>
          </div>

          <div class="note">
            <p><strong>注意</strong>：为了获得最佳效果，建议使用分辨率适中的图片（每张图片不超过2MB）。过大的图片不仅会使文档变大，还可能导致处理速度变慢。</p>
          </div>
        </div>
      </el-collapse-item>

      <el-collapse-item title="预览与下载差异说明" name="2">
        <div class="help-content">
          <p>在线预览和下载的文档可能存在一些细微差异，这是由于以下原因造成的：</p>
          <ul>
            <li>HTML预览无法完全复制Word文档的所有格式特性，特别是一些高级格式设置。</li>
            <li>图片在HTML中的显示方式与Word文档可能略有不同，尤其是在环绕文本方面。</li>
            <li>某些特殊字体在您的浏览器中可能不可用，这会导致预览中使用替代字体。</li>
          </ul>
          <p>为了查看完整格式效果，建议下载处理后的文档并在Microsoft Word或其他兼容的文档编辑器中打开。</p>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
// No se requiere código JavaScript adicional
</script>

<style scoped>
.help-info {
  margin: 20px 0;
}

.help-content {
  padding: 10px;
  color: #333;
}

.help-content h3 {
  margin-top: 16px;
  margin-bottom: 8px;
  color: #409EFF;
}

.help-content h4 {
  margin-top: 14px;
  margin-bottom: 6px;
  color: #67C23A;
}

.help-content p {
  line-height: 1.6;
  margin-bottom: 10px;
}

.help-content ul {
  padding-left: 20px;
  margin-bottom: 15px;
}

.help-content li {
  margin-bottom: 5px;
}

.tips-section {
  background-color: #f8f8f8;
  border-left: 4px solid #67C23A;
  padding: 10px 15px;
  margin: 15px 0;
  border-radius: 0 4px 4px 0;
}

.note {
  background-color: #fff6f7;
  border-left: 4px solid #F56C6C;
  padding: 10px 15px;
  margin: 15px 0;
  border-radius: 0 4px 4px 0;
}
</style>
