import type {RouteRecordRaw} from 'vue-router'
import {createRouter, createWebHistory} from 'vue-router'

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
                redirect: '/maimai',
                children: [
                    {
                        path: 'maimai',
                        redirect: '/maimai/icon',
                        children: [
                            {
                                path: 'icon',
                                component: MaimaiIcon
                            },
                            {
                                path: "plate",
                                component: MaimaiPlate
                            }
                        ]
                    }
                ]
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router