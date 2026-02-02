# 论文（文档）格式编辑工具

一个基于Vue 3 + Spring Boot的docx文档格式处理工具，支持对文档内容进行批量格式修改，如字体、大小、缩进、行间距、多级标题格式等。

## 项目简介

本项目提供了一个用户友好的Web界面，帮助用户快速批量处理docx文档的格式设置，特别适用于学术论文、报告等需要统一格式的文档。通过可视化的配置界面，用户可以轻松设置各级标题、正文、段落、图片、表格等元素的格式。

### 主要功能

- **📄 文档格式批量处理**：支持对文档中的不同元素进行格式设置
- **🔤 多级标题格式**：可自定义1-9级标题的字体、大小、颜色、对齐方式等
- **📝 正文格式设置**：包括普通文本、段落、图片、表格等格式配置
- **👀 实时预览**：在配置过程中实时查看格式效果
- **💾 批量导出**：支持处理后的文档下载
- **🖼️ 图片处理**：支持图片格式、大小、对齐方式设置
- **📊 表格处理**：支持表格样式、边框、对齐方式配置

## 技术栈

### 前端 (Frontend)
- **框架**：Vue 3 + Vite
- **UI组件库**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **图标库**：Element Plus Icons
- **代码规范**：ESLint + Prettier

### 后端 (Backend)
- **框架**：Spring Boot 3.4.3
- **构建工具**：Maven
- **文档处理**：Apache POI
- **Java版本**：21
- **包管理**：Maven Wrapper

## 项目结构

```
doc_format/
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── components/                # Vue组件
│   │   │   ├── FileUpload.vue         # 文件上传组件
│   │   │   ├── FormatConfig.vue       # 格式配置组件
│   │   │   ├── HeadingSettings.vue    # 标题设置组件
│   │   │   ├── NormalTextSettings.vue # 普通文本设置
│   │   │   ├── ParagraphSettings.vue  # 段落设置
│   │   │   ├── ImageSettings.vue      # 图片设置
│   │   │   ├── TableSettings.vue      # 表格设置
│   │   │   └── HelpInfo.vue          # 帮助信息
│   │   ├── stores/                    # 状态管理 (Pinia)
│   │   ├── utils/                     # 工具函数
│   │   ├── assets/                    # 静态资源
│   │   └── App.vue                    # 根组件
│   ├── public/                        # 公共资源
│   ├── package.json                   # 项目依赖配置
│   └── vite.config.js                 # Vite配置
├── backend/                           # 后端项目
│   ├── src/main/java/com/example/backend/
│   │   ├── controller/                # 控制器层
│   │   │   └── formatController.java  # 格式处理控制器
│   │   ├── formatConfig/              # 格式配置类
│   │   │   ├── CustomStyles.java      # 自定义样式
│   │   │   ├── DocumentFormatConfig.java # 文档格式配置
│   │   │   ├── Heading.java           # 标题类
│   │   │   ├── HeadingRule.java       # 标题规则
│   │   │   ├── HeadingStyle.java      # 标题样式
│   │   │   ├── ImageConfig.java       # 图片配置
│   │   │   ├── NormalConfig.java      # 普通文本配置
│   │   │   ├── Paragraph.java         # 段落类
│   │   │   ├── ParagraphConfig.java   # 段落配置
│   │   │   ├── SpecialConfig.java     # 特殊配置
│   │   │   ├── TableConfig.java       # 表格配置
│   │   │   └── TitleExample.java      # 标题示例
│   │   ├── numbering/                 # 编号处理
│   │   │   └── ParagraphNumberingInfo.java # 段落编号信息
│   │   ├── service/                   # 服务层
│   │   │   ├── DocumentFormatService.java # 文档格式服务
│   │   │   └── ImageService.java      # 图片服务
│   │   └── BackendApplication.java    # 应用启动类
│   ├── src/main/resources/            # 资源文件
│   │   └── application.properties     # 应用配置
│   ├── pom.xml                        # Maven配置
│   └── mvnw                           # Maven Wrapper
└── README.md                          # 项目说明文档
```

## 快速开始

### 环境要求

- **Node.js**: 16+ (推荐 18+)
- **Java**: 21+ (推荐 OpenJDK 21)
- **Maven**: 3.6+ (项目已包含 Maven Wrapper)
- **包管理器**: pnpm (推荐) 或 npm

### 安装和运行

#### 1. 克隆项目

```bash
git clone https://github.com/hzj123456701/Paper-Format-Editing.git
cd Paper-Format-Editing
```

#### 2. 安装依赖

**前端依赖**:
```bash
cd frontend
pnpm install  # 或 npm install
```

**后端依赖**:
```bash
cd backend
./mvnw dependency:resolve  # 或 mvn dependency:resolve
```

#### 3. 启动后端服务

```bash
cd backend
./mvnw spring-boot:run  # 或 mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 4. 启动前端服务

```bash
cd frontend
pnpm dev  # 或 npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 使用说明

注：如果是AI生成的论文，请在复制后，选择【只粘贴文本】到一个docx文档中，后续正常处理

免责声明：选字体时不要选择本机不能正常使用或未经授权的字体，可能得到的文档字体无法正常显示，其他所有后果也请自负，不过只要你的电脑上有WPS或者Word，就不用担心这个问题
1. **上传文档**：点击上传按钮选择需要处理的docx文档
2. **配置格式**：
   - 设置各级标题的字体、大小、颜色、对齐方式等
   - 配置正文、段落、图片、表格的格式
   - 实时预览格式效果
3. **下载处理结果**：确认配置后下载处理后的文档

## API接口文档

### 后端接口

#### 文档上传接口
- **URL**: `POST /api/upload`
- **参数**: Multipart file (docx文档)
- **返回**: 上传成功信息

#### 格式配置接口
- **URL**: `POST /api/format/configure`
- **参数**: 格式配置JSON对象
- **返回**: 配置结果

#### 文档处理接口
- **URL**: `POST /api/format/process`
- **参数**: 文档ID和格式配置
- **返回**: 处理后的文档下载链接

### 前端配置示例

```javascript
// 标题格式配置示例
const headingConfig = {
  level: 1,
  fontFamily: '宋体',
  fontSize: 16,
  color: '#000000',
  bold: true,
  alignment: 'center'
};

// 段落格式配置示例
const paragraphConfig = {
  lineSpacing: 1.5,
  firstLineIndent: 2,
  alignment: 'justify'
};
```

## 开发指南

### 前端开发

```bash
cd frontend
pnpm dev          # 开发模式
pnpm build        # 构建生产版本
pnpm preview      # 预览构建结果
pnpm lint         # 代码检查
pnpm format       # 代码格式化
```

### 后端开发

```bash
cd backend
./mvnw compile    # 编译项目
./mvnw test       # 运行测试
./mvnw package    # 打包项目
```

### 项目配置

#### 后端配置 (application.properties)
```properties
server.port=8080
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

#### 前端配置 (vite.config.js)
```javascript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

## 功能特性

### 标题格式设置
- 支持1-9级标题格式自定义
- 可设置字体、大小、颜色、粗体、斜体等
- 支持对齐方式设置（左对齐、居中、右对齐、两端对齐）
- 自动标题编号和层级管理

### 正文格式设置
- 普通文本格式配置（字体、大小、颜色等）
- 段落格式设置（行间距、缩进、段前段后间距）
- 图片格式处理（大小、对齐、边框等）
- 表格样式配置（边框、对齐、背景色等）

### 高级功能
- 批量处理多个文档
- 格式模板保存和加载
- 实时预览和即时反馈
- 错误处理和异常提示

## 部署说明

### 开发环境部署

1. **环境准备**
   ```bash
   # 安装 Node.js
   # 安装 Java 21
   # 安装 Maven (可选，项目包含 wrapper)
   ```

2. **启动服务**
   ```bash
   # 后端服务
   cd backend && ./mvnw spring-boot:run
   
   # 前端服务 (新终端)
   cd frontend && pnpm dev
   ```

### 生产环境部署

#### 后端部署
```bash
cd backend
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

#### 前端部署
```bash
cd frontend
pnpm build
# 将 dist 目录部署到 Web 服务器
```

### Docker 部署 (可选)

```dockerfile
# Dockerfile 示例
FROM openjdk:21-jdk-slim
COPY backend/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

### 开发环境搭建

1. Fork 本项目
2. 克隆到本地
3. 安装依赖并启动开发环境
4. 创建功能分支进行开发

### 代码规范

- 前端代码遵循 ESLint + Prettier 规范
- 后端代码遵循 Java 编码规范
- 提交信息使用约定式提交格式
- 确保代码通过所有测试

### 提交 Pull Request

1. 确保代码质量
2. 更新相关文档
3. 添加必要的测试用例
4. 描述功能变更和影响

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- **项目地址**: https://github.com/hzj123456701/Paper-Format-Editing
- **问题反馈**: 请在 GitHub Issues 中提交问题
- **功能建议**: 欢迎提交 Pull Request 或功能建议

## 更新日志

### v1.0.0 (2026-02-02)
- 初始版本发布
- 支持 docx 文档格式批量处理
- 实现多级标题格式配置
- 添加图片和表格格式处理
- 提供实时预览功能

## 注意事项

在使用本工具处理docx文档时，请注意以下事项：

1. **编号样式问题**：对于WPS自动生成且字体加粗的编号，可能会导致所在段落的样式出现一些问题
2. **样式单位差异**：WPS中看到的样式单位可能与程序中设置的样式单位不同，但实际效果相同
3. **文档兼容性**：建议在处理前备份原始文档


**温馨提示**：本工具旨在辅助文档格式处理，建议在处理重要文档前进行备份，以确保数据安全。
