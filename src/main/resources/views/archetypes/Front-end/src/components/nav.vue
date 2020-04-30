<template>
  <div id="sysmanager-app">
    <div class="top-menu bordered-b">
      <div class="logo">
        <i></i>
        <span>基础数据</span>
      </div>
      <c-nav
        class="nav-top"
        :default-active="$store.state.com.topNavActive"
        mode="horizontal"
        @select="handleSelect"
      >
        <template v-for="menu in $store.state.com.appMenus">
          <c-nav-item :key="menu.id" v-if="!menu.children" :index="menu.id">{{menu.name}}</c-nav-item>
          <c-sub-nav v-if="menu.children" :key="menu.id" :index="menu.id">
            <template slot="title">{{menu.name}}</template>
            <c-nav-item
              v-for="child in menu.children"
              :index="child.id"
              :key="child.id"
            >{{child.name}}</c-nav-item>
          </c-sub-nav>
        </template>
      </c-nav>
    </div>
      <router-view></router-view>
  </div>
</template>

<script type="text/ecmascript-6">
import api from "../api/api";
import tool from "../util/tools";
export default {
  name: "sysConfig",
  data() {
    return {
      routerNew: ""
    };
  },
  methods: {
    // 选择菜单
    handleSelect(index) {
      let menus = this.$store.state.com.appMenus;
      let pMenu = menus.filter(item => item.id === index);
      if (index.indexOf("-") === -1)
        this.$store.dispatch("setTopNavActive", index);
      if (pMenu.length > 0) {
        this.$router.push(pMenu[0].path);
        return;
      }
      menus.forEach(item => {
        if (item.children) {
          let cMenu = item.children.filter(ntem => ntem.id === index);
          if (cMenu.length > 0) {
            this.$router.push(cMenu[0].path);
          }
        }
      });
    },
    getApp() {
      //        axios.get('/api/aif/App/GetPermissionApps').then(res=>{
      //          console.log(res)
      //        });
    }
  },
  mounted() {
    this.getApp();
    //console.log(this.)
  }
};
</script>
<style scoped lang="scss">
.nav-top {
  padding-left: 180px;
}
.top-menu {
  position: relative;
}
.top-menu > .logo {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  color: #fff;
  font-size: 18px;
  span{
    padding-left:6px;
  }
}
</style>
