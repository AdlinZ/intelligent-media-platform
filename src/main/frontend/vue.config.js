module.exports = {
  publicPath: './',
  outputDir: 'dist', // 恢复默认输出目录
  assetsDir: 'static',
  indexPath: 'index.html',
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
}