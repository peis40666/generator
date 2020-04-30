<template>
  <div id="app">
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive" style="height:100%">
      </router-view>
    </keep-alive>
    <router-view v-if="!$route.meta.keepAlive" style="height:100%"/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="scss">
  html,body,#app{
    height: 100%;
    overflow:hidden ;
  }
  #app>div{
    height: 100%;
    & div.container{
       height: 100%;
    }
  }
  *{
    margin: 0;
    padding: 0;
  }
</style>
