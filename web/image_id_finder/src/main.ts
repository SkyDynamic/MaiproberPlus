import {createApp} from 'vue'
import App from '@/App.vue'
import router from "@/router/router.ts";

import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

import 'virtual:uno.css'

import 'normalize.css/normalize.css'
import '@/style/base.css'

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
