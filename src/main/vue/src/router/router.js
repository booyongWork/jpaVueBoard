import { createRouter, createWebHistory } from 'vue-router'
import routes from './routes.js'

export const router = createRouter({
  history: createWebHistory(process.env.VUE_APP_BASE_URL),
  routes,
})

export default router;
