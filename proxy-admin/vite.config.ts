import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import viteCompression from 'vite-plugin-compression'

const VITE_API_BASE_URL: string = 'https://center.framewiki.com/'
// https://vite.dev/config/
export default defineConfig({
  base: '/',
  envDir: './env',
  envPrefix: ['VITE_'],
  plugins: [vue(),
    vueJsx(),
    vueDevTools(),
    viteCompression({
      filter: /\.(js|css|json|txt|html|ico|svg)(\?.*)?$/i, // 需要压缩的文件
      threshold: 1024, // 文件容量大于这个值进行压缩
      algorithm: 'gzip', // 压缩方式
      ext: 'gz', // 后缀名
      deleteOriginFile: false, // 压缩后是否删除压缩源文件
    })
  ],
  build: {
    sourcemap: false,
    outDir: 'dist',
    assetsDir: 'static',
    chunkSizeWarningLimit: 1024,
    assetsInlineLimit: 4096,
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: '127.0.0.1',
    port: 8080,
    proxy: {
      '/security': {
        target: VITE_API_BASE_URL,
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
      '/single': {
        target: VITE_API_BASE_URL,
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
      '/interfaces': {
        target: VITE_API_BASE_URL,
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
      '/gateway': {
        target: VITE_API_BASE_URL,
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
      '/portal': {
        target: 'http://127.0.0.1:10100/',
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
      '/bms': {
        target: VITE_API_BASE_URL,
        ws: true,
        changeOrigin: true,//允许跨域
        // rewrite: (path) => path
      },
    }
  },
})
