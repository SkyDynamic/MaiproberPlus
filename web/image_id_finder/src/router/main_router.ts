import AppLayout from "@/layout/AppLayout.vue";
import MaimaiIcon from "@/components/maimai/Icon.vue"
import MaimaiPlate from "@/components/maimai/Plate.vue"

const MainRouter = {
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

export default MainRouter