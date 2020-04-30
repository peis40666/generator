import axios from 'axios'
import { getQueryString, TreeUtil } from "./util/tools"
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './vuex/store'
import citmsUI from 'citms-ui-package'
//import $ from 'jquery'
import * as tools from "./util/tools";

// 全局组件
import sliderLeft from './common/sliderLeft/sliderLeft'
import cSearch from './common/formSelectItem/formSelectItem'

import api from './api/api'

import mocker from '../static/mock/mocker'

if(window.doMock&&window.doMock === true){    
    mocker.mock(api);
}

Vue.prototype.api = api;
Vue.prototype.tools = tools;

Vue.component('slider-left', sliderLeft);
Vue.component('c-search', cSearch);
Vue.config.productionTip = false
Vue.use(citmsUI)

//window.$ = $;
window.Token = getQueryString('Token') || localStorage.getItem("TOKEN")

window.Skin = getQueryString('Skin') || localStorage.getItem("Skin")
if (Token) {
    localStorage.setItem("TOKEN", Token)
    localStorage.setItem("SKIN", Skin)
}
//获取用户信息异步处理
let initData = new Promise(resolve => {
    axios({
        url: baseURL + '/smw/UAC/GetUserInfo',
        method: 'get',
        headers: {
            "Authorization": 'Basic ' + Token
        }
    }).then(res => {
        if (res.data.code == 0) {
            //用户信息
            var data = res.data
                //打水印
            addWaterMark(data.result.userName, data.result.departmentName)
                //获取菜单权限
            axios({
                url: baseURL + '/smw/Function/GetAppPermissionFunction?appNo=' + appNo,
                method: 'get',
                headers: {
                    "Authorization": 'Basic ' + Token
                }
            }).then(fun => {
                if (fun.data.code == 0) {
                    let memus = fun.data.result.filter(item => !item.isDisabled)
                    let menuArr = [] //组织树形菜单
                    let codeArr = []
                    memus.forEach(item => {
                        let functionUrl = item.functionUrl
                        let url = functionUrl ? functionUrl.substr(2) : ""
                        codeArr.push(item.functionCode)
                        menuArr.push({
                            name: item.functionName,
                            id: item.functionCode,
                            path: "/app" + url,
                            uid: item.functionGUID,
                            parentUid: item.parentFunctionGUID
                        })
                    })
                    let menuTree = TreeUtil().arrayToTree(JSON.parse(JSON.stringify(menuArr)), null, "uid", "parentUid")
                    resolve({
                        userInfo: data.result,
                        appMenus: menuTree,
                        functionArr: codeArr
                    })
                }
            })
        } else {
            var local = window.location.href
            if (local.indexOf("/common/GoLogin") == -1 || local.indexOf("login.html") == -1) {
                if (window.top == self) {
                    window.location.href = baseURL + "/common/GoLogin?returnUri=" + local
                } else {
                    window.parent.postMessage({ type: "exit" }, '*') //是否从集成框架退出
                }
            }
        }
    }).catch(err => {
        console.log(err)
    })
})
let mainStyle = "blue"
if (Skin)
    mainStyle = Skin
    //动态插入主体样式表
let head = document.getElementsByTagName('HEAD').item(0)
var style = document.createElement('link')
style.href = '/static/skin/' + mainStyle + '-min.css'
style.rel = 'stylesheet'
style.type = 'text/css'
head.appendChild(style)

let setTopNavActive = (data, url) => {
    let urlArr = url.split("/").slice(0, 3);
    url = urlArr.join("/")
    let pData = data.filter(item => item.path == url)
    if (pData.length > 0) {
        store.dispatch('setTopNavActive', pData[0].id)
    } else {
        data.forEach(item => {
            if (item.children) {
                let cData = item.children.filter(ntem => ntem.path == url)
                if (cData.length > 0) {
                    store.dispatch('setTopNavActive', cData[0].id)
                }
            }
        })
    }
}
initData.then(res => {
    store.dispatch('setUserInfo', res.userInfo)
    store.dispatch("setAppMenus", res.appMenus)
    store.dispatch("setFunctionArr", res.functionArr)
    new Vue({
        el: '#app',
        router,
        store,
        watch: {
            '$route' (to, from) {
                setTopNavActive(res.appMenus, to.path)
            }
        },
        components: { App },
        template: '<App/>',
        mounted() {
            let nowPath = window.location.pathname
            setTopNavActive(res.appMenus, nowPath)
        }
    })

})