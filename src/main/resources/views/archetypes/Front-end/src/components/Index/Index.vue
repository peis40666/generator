<template>
  <div>
    {{msg}}
    <c-button loading size="lg">首页</c-button>
  </div>

</template>

<script type="text/ecmascript-6">
  import api from "../../api/api"
  import tool from "../../util/tools"
  export default {
    name: 'HelloWorld',
    data () {
      return {
        msg: 'Welcome to Your App'
      }
    },
    created(){
      this.$router.push("/app/fmw/company")
    },
  }
</script>
<style scoped>
</style>
