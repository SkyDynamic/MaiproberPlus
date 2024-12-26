import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import AppLayout from "@/layout/AppLayout.vue";
import MaimaiIcon from "@/components/maimai/Icon.vue"
import MaimaiPlate from "@/components/maimai/Plate.vue"

const routes: RouteRecordRaw[] = [
    {
        path: '',
        component: AppLayout,
        children: [
            {
                path: '',
                redirect: '/maimai-icon'
            },
        {
            path: '/maimai-icon',
            component: MaimaiIcon
        },
        {
            path: "/maimai-plate",
            component: MaimaiPlate
        }
    ]
}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router