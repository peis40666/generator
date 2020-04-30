import {getQueryString} from "../../util/tools"
/**
 * 通用配置
 */
const state = {
  functionCode: getQueryString("FunctionCode"),
  topNavActive:"",
  secondActive:"",
  appMenus:[],
  functionArr:[]
}

const actions = {
  setTopNavActive({ commit }, res) {
    commit("setTopNavActive", res);
  },
  setSecondActive({ commit }, res) {
    commit("setSecondActive", res);
  },
  setAppMenus({ commit }, res) {
    commit("setAppMenus", res);
  },
  setFunctionArr({ commit }, res) {
    commit("setFunctionArr", res);
  }
}

const getters = {
  getTopNavActive:(state)=>state.topNavActive
}

const mutations = {
  setTopNavActive(state, res) {
    state.topNavActive = res;
  },
  setSecondActive(state, res) {
    state.secondActive = res;
  },
  setAppMenus(state, res) {
    state.appMenus = res;
  },
  setFunctionArr(state,res){
    state.functionArr = res;
  }
}

export default {
  state,
  actions,
  getters,
  mutations
}
