/**
 * 日志工具类
 * 提供统一的日志记录接口
 */
class Logger {
  constructor() {
    // 在开发环境中启用调试模式
    this.debugMode = import.meta.env.MODE === 'development';
  }

  info(message) {
    if (this.debugMode) {
      console.info(`[INFO] ${message}`);
    }
  }

  warn(message) {
    console.warn(`[WARN] ${message}`);
  }

  error(message, error) {
    console.error(`[ERROR] ${message}`, error);
  }

  debug(message) {
    if (this.debugMode) {
      console.debug(`[DEBUG] ${message}`);
    }
  }
}

export const LOGGER = new Logger();
