/**
 * Created by renming on 2018/1/29.
 */
import Vue from 'vue';
import axios from 'axios';
import qs from 'qs';
import router from '../router';
import store from '../vuex/store';
import * as types from '../vuex/types';
import citmsUI from "citms-ui-package"
Vue.prototype.axios = axios;
// axios 配置
axios.defaults.timeout = 15000;
// 后台接口公共前缀
axios.defaults.baseURL = baseURL;
let apiAddress = ""
    //axios.defaults.withCredentials=true
    // POST传参序列化
axios.interceptors.request.use((config) => {
    let token = Token
    apiAddress = config.url.split(baseURL)[0]
    if (token) {
        if (!config.data) {
            config.data = {};
        }
    }

    if (config.headers) {
        if (!config.headers['Content-Type']) {
            config.data = qs.stringify(config.data);
        } else {
            config.data = JSON.stringify(config.data);
        }
    } else {
        config.headers = {}
        if (config.method != "get" && config.method != "delete") {
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        } else {
            config.url = config.url + "?" + qs.stringify(config.data);
        }
        config.data = qs.stringify(config.data);
    }
    config.headers.Authorization = 'Basic ' + token
    return config;
}, (error) => {
    citmsUI.cToastMethod({
        text: '请求异常',
        type: "danger",
        placement: "top",
        closeOnClick: true
    })
        //return Promise.reject(error);
});

function loginOut(message) {
    var local = window.location.href
    if (local.indexOf("/common/GoLogin") == -1 || local.indexOf("login.html") == -1) {
        if (window.top == self) {
            citmsUI.cToastMethod({
                text: massage,
                type: "danger",
                placement: "top",
                closeOnClick: true
            })
            setTimeout(() => {
                window.location.href = baseURL + "/common/GoLogin?returnUri=" + local
            }, 3000)
        } else {
            window.top.postMessage({ type: "exit" }, '*') //是否从集成框架退出
        }
    }
}
// 返回状态判断
axios.interceptors.response.use((res) => {
    if (res.data.code != 0) {
        //请求异常情况退出
        if (res.data.code === 5000) {
            loginOut()
            return false
        } else if (res.data.code === 5001) {
            loginOut(res.data.message)
            return false
        }
        citmsUI.cToastMethod({
            text: res.data.message ? res.data.message : "操作失败",
            type: "danger",
            placement: "top",
            closeOnClick: true
        })
    }
    return res;
}, (error) => {
    let massage = "数据返回异常"
    console.log(error)
    if (error.request.status != 200)
        massage = apiAddress + "服务地址异常";
        citmsUI.cToastMethod({
            text: massage,
            type: "danger",
            placement: "top",
            closeOnClick: true
        })
});

export function fetch(url, params, method, headers) {

    method = method || "post"
    return new Promise((resolve, reject) => {
        var obj = {
            method,
            url,
            data: params,
            headers
        }
        if (method == "get") {
            delete obj.data
            obj.params = params
        }

        axios(obj).then(response => {
            resolve(response.data);
        }, err => {
            reject(err);
        }).catch((err) => {
            reject(err);
        });
    });
}
const APPJSON = { "Content-Type": "application/json;charset=UTF-8" }

export default {
    getDict(params) {
        return fetch('/smw/Dict/GetSomeDicts', params, "post", APPJSON);
    },
    getPermissionApps(params) {
        return fetch('/aif/App/GetPermissionApps', params, "get");
    },
    //获取部门
    getAllDepartment(params) {
        return fetch("/smw/Department/FindAll", params, "get")
    },
    //获取数据权限范围内的部门
    getPermissonDepartmentByFACode(params) {
        return fetch("/smw/Department/GetPermissonDepartmentByFACode", params, "get")
    },
    getUserCode() {
        return fetch('/smw/UAC/GetUserInfo', null, 'get');
    },
};