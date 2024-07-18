import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import global from "@/assets/js/global";  // 引用全局组件
// 引入echarts组件
import ECharts from 'vue-echarts'
import "echarts"

// 完整引入ElementUI
import ElementUI from 'element-ui'
// 引入ElementUI全部样式
import 'element-ui/lib/theme-chalk/index.css'
// 导入全局样式表
import './assets/css/global.css'

// 引入axios
import axios from 'axios'
// 配置请求的根路径
// axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'  // 设置默认的请求类型，不然给后端传递参数的时候会出现问题
// 给axios设置一个过滤器
axios.interceptors.request.use(config => { // config就是函数的请求对象
  if (window.sessionStorage.getItem('token')){  // 如果缓存中有token，就当做请求头发送给后台
    config.headers.token  = window.sessionStorage.getItem('token'); // 将缓存中的token提交到请求头中
  }
  return config // 固定写法，必须返回config
})
// 将axios绑定到Vue对象上的$http上，方便后面调用
Vue.prototype.$http = axios


// 全局注册组件（也可以使用局部注册）
Vue.component('ECharts', ECharts);

Vue.prototype.$global = global;  // 将自定义的global挂载到Vue上

Vue.config.productionTip = false

Vue.use(ElementUI);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
