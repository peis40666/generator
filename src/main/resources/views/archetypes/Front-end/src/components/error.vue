<template>
  <div class=""></div>
</template>
<script type="text/ecmascript-6">
  import api from "../api/api"
  import qs from "qs"
  export default{
    mounted(){
      let queryStr = qs.stringify(this.$route.query)
      if(queryStr.length)
        queryStr = "?"+queryStr
      api.getPermissionApps().then(res=>{
        if(!res.code){
          let SysConfig = res.result.filter(item=>item.appId == "SysConfig")
          res.result.forEach(item=>{
            if(item.appId == "SysConfig"){
              let url = item.appUrl.split("/#/")[0]
              window.location.href = url+"/error.html"+queryStr
            }
            if(item.menus){
              item.menus.forEach(ntem=>{
                if(ntem.appId == "SysConfig"){
                  let url = ntem.appUrl.split("/#/")[0]
                  window.location.href = url+"/error.html"+queryStr
                }
              })
            }
          })
        }
      })
    }
  }
</script>
