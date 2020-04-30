import Vue from 'vue'
import Router from 'vue-router'
import store from '../vuex/store'
import { format } from 'url';
const index = resolve => require.ensure([], () => resolve(require('@/components/Index/Index')))
const error = resolve => require.ensure([], () => resolve(require('@/components/error')))
const nav = resolve => require.ensure([], () => resolve(require('@/components/nav')))
const emptyNav = resolve => require.ensure([], () => resolve(require('@/components/emptyNav')))
const kong = resolve => require.ensure([], () => resolve(require('@/components/kong/kong')))

Vue.use(Router)
export const routeMap = [{
        path: "*",
        component: error,
    },
    {
        path: '/',
        name: 'Index',
        component: index,
        redirect:'/app/fmw/kong'
    },
    {
        path: '/fmw',
        component: emptyNav,
        meta: { requireAuth: true },
        children: [
			{
				path: 'kong',
				component: kong,
			}
        ]
    },
    {
        path: '/app/fmw',
        component: nav,
        meta: { requireAuth: true },
        children: [
            {
				path: 'kong',
				component: kong,
			}
        ]
    }
]
const router = new Router({
    routes: routeMap
});
router.beforeEach((to, from, next) => {
    if (to.matched.some(r => r.meta.requireAuth) && to.matched.some(r => store.state.com.functionArr.indexOf(r.meta.funcionCode) != -1)) {
        let token = localStorage.getItem('TOKEN');
        if (token) {
            next();
        } else {
            var local = window.location.href
            if (local.indexOf("/Common/GoLogin") == -1 || local.indexOf("login.html") == -1) {
                if (window.top == self) { //是否从集成框架退出
                    window.location.href = baseURL + "/Common/GoLogin?returnUri=" + local
                } else {
                    window.parent.postMessage({ type: "exit" }, '*')
                }
            }
        }
    } else {
        if (to.matched.some(r => r.meta.requireAuth) && to.matched.some(r => store.state.com.functionArr.indexOf(r.meta.funcionCode) == -1) && to.path != "/error") {
            next({ path: "/error", query: { back: 2 } });
        } else {
            next();
        }
    }
});
export default router