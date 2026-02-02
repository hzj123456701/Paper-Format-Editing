# 论文（文档）格式编辑工具

一个基于Vue 3 + Spring Boot的docx文档格式处理工具，支持对文档内容进行批量格式修改，如字体、大小、缩进、行间距、多级标题格式等。

## 项目简介

本项目提供了一个用户友好的Web界面，帮助用户快速批量处理docx文档的格式设置，特别适用于学术论文、报告等需要统一格式的文档。

### 主要功能

- **文档格式批量处理**：支持对文档中的不同元素进行格式设置
- **多级标题格式**：可自定义1-9级标题的格式
- **正文格式设置**：包括普通文本、段落、图片、表格等
- **实时预览**：在配置过程中实时查看格式效果
- **批量导出**：支持处理后的文档下载

## 技术栈

### 前端 (Frontend)
- **框架**：Vue 3 + Vite
- **UI组件**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios

### 后端 (Backend)
- **框架**：Spring Boot
- **构建工具**：Maven
- **文档处理**：Apache POI

## 项目结构

```
doc_format/
├── frontend/          # 前端项目
│   ├── src/
│   │   ├── components/    # Vue组件
│   │   │   ├── FileUpload.vue      # 文件上传组件
│   │   │   ├── FormatConfig.vue    # 格式配置组件
│   │   │   ├── HeadingSettings.vue # 标题设置组件
│   │   │   ├── NormalTextSettings.vue # 普通文本设置
│   │   │   ├── ParagraphSettings.vue  # 段落设置
│   │   │   ├── ImageSettings.vue   # 图片设置
│   │   │   ├── TableSettings.vue   # 表格设置
│   │   │   └── HelpInfo.vue        # 帮助信息
│   │   ├── stores/         # 状态管理
│   │   └── utils/          # 工具函数
│   └── package.json
├── backend/           # 后端项目
│   ├── src/main/java/
│   └── pom.xml
└── README.md
```

## 快速开始

### 环境要求

- Node.js 16+
- Java 21
- Maven 3.6+

### 安装和运行

#### 1. 克隆项目

```bash
git clone https://github.com/hzj123456701/Paper-Format-Editing.git
cd Paper-Format-Editing
```

#### 2. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 3. 启动前端服务

```bash
cd frontend
pnpm install
pnpm dev
```

前端服务将在 `http://localhost:5173` 启动

### 使用说明

1. **上传文档**：点击上传按钮选择需要处理的docx文档
2. **配置格式**：
   - 设置各级标题的字体、大小、颜色、对齐方式等
   - 配置正文、段落、图片、表格的格式
   - 实时预览格式效果
3. **下载处理结果**：确认配置后下载处理后的文档

## 功能特性

### 格式设置支持

- **标题格式**：1-9级标题的独立格式配置
- **文本格式**：字体、大小、颜色、加粗、斜体等
- **段落格式**：对齐方式、缩进、行间距、段前段后间距
- **图片格式**：大小、位置、边框等
- **表格格式**：边框样式、单元格格式等

### 用户体验

- **三步操作流程**：上传 → 配置 → 下载
- **实时预览**：配置过程中实时查看格式效果
- **智能提示**：提供使用注意事项和帮助信息
- **响应式设计**：适配不同屏幕尺寸

## 注意事项

在使用本工具处理docx文档时，请注意以下事项：

1. **编号样式问题**：对于WPS自动生成且字体加粗的编号，可能会导致所在段落的样式出现一些问题
2. **样式单位差异**：WPS中看到的样式单位可能与程序中设置的样式单位不同，但实际效果相同
3. **文档兼容性**：建议在处理前备份原始文档

## 开发指南

### 前端开发

```bash
cd frontend
pnpm install      # 安装依赖
pnpm dev          # 开发模式
pnpm build        # 构建生产版本
pnpm lint         # 代码检查
```

### 后端开发

```bash
cd backend
mvn compile       # 编译项目
mvn test          # 运行测试
mvn package       # 打包项目
```

## 贡献指南

欢迎提交Issue和Pull Request来改进本项目！

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

- 项目地址：https://github.com/hzj123456701/Paper-Format-Editing
- 问题反馈：请提交GitHub Issue

---

**温馨提示**：本工具旨在辅助文档格式处理，建议在处理重要文档前进行备份，以确保数据安全。
