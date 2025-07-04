import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { Line, Pie } from 'vue-chartjs'

// 正确导入 chart.js 模块
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement
} from 'chart.js'

// 注册Chart.js组件
ChartJS.register(
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement
)

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.component('QuillEditor', QuillEditor)
app.component('LineChart', Line)
app.component('PieChart', Pie)

app.mount('#app')