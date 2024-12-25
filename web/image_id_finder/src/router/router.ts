import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainRouter from "./main_router.ts";

const routes: RouteRecordRaw[] = [
    MainRouter
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router