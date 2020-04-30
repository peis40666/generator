<template>
  <div class="searchform" ref="searchform">
    <transition v-for="(obj,index) in searchBoxs" :key="index" :name="obj.id">
      <div class="itemPanel" v-show="item[index].show">
        <!-- 时间 -->
        <div v-if="obj.type&&obj.type==='datetime'" class="itemBox date">
          <div class="dateBox fl">
            <span class="itemLabel fl">{{obj.title}}</span>
            <c-datepicker
              v-model="obj.dateTime"
              width="340px"
              :timePicker="true"
              format="YYYY-MM-DD HH:mm:ss"
              :readonly="true"
              @change="changeDateTime($event,obj.dateTime,item[index],index)"
            ></c-datepicker>
            <a @click="chooseTime" class="btn">确定</a>
          </div>
          <div class="item" :class="[item[index].expand ?'active':'']">
            <div class="myBox" :ref="obj.id">
              <a
                :class="[obj.checkAll ?'on':'']"
                v-if="obj.isShowAll"
                @click="getRadioValue(index,null,item,obj)"
              >全部</a>
              <a
                :class="[(singelCheckIndex[index] === $index || (defalutFlag&&searchBoxs[index].defalut[0]===key.value))?'on':'']"
                v-for="(key,$index) in obj.data"
                :id="key.value"
                :key="$index"
                @click="getRadioValue(index,$index,key,obj)"
              >{{key.text}}</a>
            </div>
          </div>
          <!-- 单项收缩按钮 -->
          <div
            class="arrow"
            @click="expand(index,item,item[index].expand)"
            v-show="item[index].shoWIcon"
          >
            <i
              class="iconfont"
              :class="[item[index].expand ?'icon-jiantou_yemian_xiangxia':'icon-jiantou_yemian_xiangyou']"
            ></i>
          </div>
        </div>
        <!-- 其他 -->
        <div v-else class="itemBox">
          <span class="itemLabel fl">{{obj.title}}</span>
          <!-- 单选样式 -->
          <div v-if="!obj.isMultiple" class="item" :class="[item[index].expand ?'active':'']">
            <div class="myBox" :ref="obj.id">
              <a
                :class="[obj.checkAll ?'on':'']"
                v-if="obj.isShowAll"
                @click="getRadioValue(index,null,item,obj)"
              >全部</a>
              <a
                :class="[singelCheckIndex[index] === $index ?'on':'']"
                v-for="(key,$index) in obj.data"
                :id="key.value"
                :key="$index"
                @click="getRadioValue(index,$index,key,obj)"
              >{{key.text}}</a>
            </div>
          </div>
          <!-- 复选样式 -->
          <div v-else class="item" :class="[item[index].expand ?'active':'']">
            <div class="myBox" :ref="obj.id">
              <c-checkbox-group v-model="item[index].check">
                <a v-if="obj.isShowAll" class="checkAll" @click="chooseAll(obj.id,item)">
                  <span class="c-checkbox-inner" :class="[item[index].checkAll ?'on':'']">
                    <i
                      :class="[item[index].checkAll ?'c-fa-check':'']"
                      class="c-fa c-fa-heckbox-checked c-checkbox-inner-check"
                    ></i>
                  </span>
                  <span class="txt">全部</span>
                </a>
                <c-checkbox   @change="change"
                  v-for="(key,index) in obj.data"
                  :label="key.value"
                  :key="index"
                >{{key.text}}</c-checkbox>
              </c-checkbox-group>
            </div>
          </div>
          <!-- 单项收缩按钮 -->
          <div
            class="arrow"
            @click="expand(index,item,item[index].expand)"
            v-show="item[index].shoWIcon"
          >
            <i
              class="iconfont"
              :class="[item[index].expand ?'icon-jiantou_yemian_xiangxia':'icon-jiantou_yemian_xiangyou']"
            ></i>
          </div>
        </div>
      </div>
    </transition>
    <!-- 按钮 -->
    <p class="btnBg"  v-if="expandRow<searchBoxs.length">
      <a class="arrowDown" @click="handerSilder">
        <i class="iconfont" :class="[expandAll ?'icon-shouqi1':'icon-zhankai2']"></i>
      </a>
    </p>
  </div>
</template>
<script>
import moment from "moment";
export default {
  data() {
    return {
      flag:false, //防止多次发送请求
      singelCheckIndex: [], //存储单选框状态
      itemHeight: [],
      item: [],
      expandAll: false,
      expandRow: 2,
      searchBoxs: [],
      dataTime: [], //存储时间数组
      defalutFlag:true
    };
  },
  props: ["data"],
  watch: {
    // 复选框全选/反选
    item: {
      handler: function(val, oldVal) {
        let _this = this;
        this.searchBoxs.forEach(function(item, index) {
          if (item.data.length === val[index].check.length) {
            _this.$set(val[index], "checkAll", true);
          } else {
            _this.$set(val[index], "checkAll", false);
          }
        });
      },
      deep: true
    },
    searchBoxs(val) {
        let _item = [];
        
        //console.log(this.item)
        if (this.data) {
        this.expandRow = this.data.expandRow;
        this.searchBoxs = this.data.searchBoxs;
        var searchBoxs = val;
        //debugger
        for (var i = 0; i < searchBoxs.length; i++) {
          if (searchBoxs[i].defalut.length > 0) {
            searchBoxs[i].checkAll = false;
          }
          if (searchBoxs[i].type === "datetime") {
            this.getDate(searchBoxs[i], i, true); //时间控件初始默认值
          }
          //console.log(searchBoxs[i].dateTime)
          _item.push({
            id: searchBoxs[i].id, //列表ID
            check: searchBoxs[i].defalut, //是否选择
            dateTime: searchBoxs[i].dateTime, //时间
            checkAll: searchBoxs[i].checkAll, // 全选
            isMultiple: searchBoxs[i].isMultiple, // 单选/多选
            expand: false, //是否展开
            show: false, //列表是否显示
            shoWIcon: false //展开按钮是否显示
          });
           

          if (i < this.expandRow) {
            _item[i].show = true;
          }
        }
       
      }
       
      this.item = _item

    }
  },
  methods: {
    getStyle(element, style) {
      let val = "";
      
      if (element.currentStyle) {
        val = element.currentStyle[style];
      } else {
        val = getComputedStyle(element, false)[style];
      }

      if (val === "auto" && (style === "width" || style === "height")) {
          
        var rect = element.getBoundingClientRect();
        if (style === "width") {
          val =  rect.right - rect.left + "px";
        } else {
          val = rect.bottom - rect.top + "px";
        }
       
      }
      
      return val;
    },
    getElOutHeight(el) {
      
      let h = this.getStyle(el, "height");
      return h.split("px")[0];
    },
    getParam() {
      //构建筛选数据
      let filterArray = this.item;
      let filterData = {};
      if (filterArray) {
        for (let i = 0, len = filterArray.length; i < len; i++) {
          let obj = filterArray[i];
          if (obj.dateTime) {
            let dataTimeArr = obj.dateTime.split(" ~ ");
            filterData[obj.id + "Start"] = dataTimeArr[0];
            filterData[obj.id + "End"] = dataTimeArr[1];
          } else {
            if (obj.check && obj.check.length > 0 && obj.checkAll == false) {
              obj.isMultiple ? filterData[obj.id] = obj.check : filterData[obj.id] = obj.check.join("");
            }
          }
        }
      }
      return filterData;
    },
    change(currentValue) { 

        this.$nextTick(()=>{
            this.$emit("search", this.getParam());
        }); 
    },
    // 复选框全选
    chooseAll(id, item) {
      var searchBoxs = this.searchBoxs;
      for (var i = 0; i < searchBoxs.length; i++) {
        var arr = [];
        if (!this.item[i].checkAll && this.item[i].id === id) {
          for (var j = 0; j < searchBoxs[i].data.length; j++) {
            arr.push(searchBoxs[i].data[j].value);
          }
          this.$set(this.item[i], "check", arr);
          this.$set(this.item[i], "checkAll", true);
        } else {
          if (this.item[i].id === id) {
            this.$set(this.item[i], "check", []);
            this.$set(this.item[i], "checkAll", false);
          }
        }
      }
      this.change();
    },
    //单选点击事件
    getRadioValue(parentIndex, currentIndex, current, parent) {
      var arr = [];
      if (currentIndex || currentIndex === 0) {
        //非全选的时候
        arr.push(current.value);
        parent.checkAll = false;
        this.getDate(current, parentIndex,true);
        this.$set(this.singelCheckIndex, parentIndex, currentIndex);
      } else {
        parent.checkAll = true;
        parent.dateTime=""
        this.$set(this.singelCheckIndex, parentIndex, "");
      }
      this.defalutFlag=false
      this.$set(this.item[parentIndex], "check", arr);
      this.$set(this.item[parentIndex], "dateTime", parent.dateTime);
      this.$emit("search", this.getParam());
    },
    changeDateTime(currentValue,oldValue, item, index) {
      item.check = [];
      item.dateTime = currentValue;
      this.$set(this.singelCheckIndex, index, "");
    },
    chooseTime(){
     this.$emit("search", this.getParam());
    },
    //当前列表展开事件
    expand(index, obj, flag) {
      this.$set(obj[index], "expand", !flag);
    },
    // 收缩事件
    handerSilder() {
      let _this = this;
      let flag=false
      let h=this.$refs.searchform.offsetHeight
      this.expandAll = !this.expandAll  
       if (this.expandAll) {
        this.item.forEach(item => {
          item.show = true;
        });
        h=this.$refs.searchform.offsetHeight
        flag=true
      } else {
        this.item.forEach((item, index) => {
          if (index < this.expandRow) {
            item.show = true;
          } else {
            item.show = false;
          }
        h=this.$refs.searchform.offsetHeight
         flag=false
        });
      }
      this.$nextTick(() => {
         this.getObjHeight(); 
      });
      if(this.data.handerSilderEvent&&typeof this.data.handerSilderEvent==="function"){
          this.data.handerSilderEvent(h,flag)
      }
    },
    //获取列表项实际高度
    getObjHeight() {
      this.itemHeight = [];
      for (var i = 0; i < this.item.length; i++) {
        var id = this.item[i].id;
        //var obj=$(this.$refs[id][0])
        //this.itemHeight.push({"id":id,"height":$(obj).outerHeight()})
        var el = this.$refs[id][0];
        this.itemHeight.push({ id: id, height: this.getElOutHeight(el) });
      }
      if (this.itemHeight.length > 0) {
        this.itemHeight.forEach((obj, index) => {
          if (obj.height > 30) {
            this.$set(this.item[index], "shoWIcon", true);
          }
        });
      }
    },
    //计算时间
    getDate(obj, index) {
      if(!obj.checkAll){
        // 间隔分钟
        if (obj.time === "m") {
          this.searchBoxs[index].dateTime = `${moment()
            .subtract(obj.num, "minute")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment().format(
            "YYYY-MM-DD HH:mm:ss"
          )}`
        }
        // 间隔小时
        if (obj.time === "h") {
          this.searchBoxs[index].dateTime = `${moment()
            .subtract(obj.num, "hours")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment().format(
            "YYYY-MM-DD HH:mm:ss"
          )}`;
        }
        // 今天
        if (obj.time === "now") {
          this.searchBoxs[index].dateTime = `${moment()
            .startOf("day")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment()
            .endOf("day")
            .format("YYYY-MM-DD HH:mm:ss")}`;
        }
        //昨天
        if (obj.time === "yd") {
          this.searchBoxs[index].dateTime = `${moment()
            .subtract(1, "day")
            .startOf("day")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment()
            .subtract(1, "day")
            .endOf("day")
            .format("YYYY-MM-DD HH:mm:ss")}`;
        }
        // 间隔天
        if (obj.time === "d") {
          this.searchBoxs[index].dateTime = `${moment()
            .subtract(obj.num, "day")
            .startOf("day")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment()
            .endOf("day")
            .format("YYYY-MM-DD HH:mm:ss")}`;
        }
        // 间隔月
        if (obj.time === "month") {
          this.searchBoxs[index].dateTime = `${moment()
            .subtract(obj.num, "month")
            .startOf("day")
            .format("YYYY-MM-DD HH:mm:ss")} ~ ${moment()
            .endOf("day")
            .format("YYYY-MM-DD HH:mm:ss")}`;
        }
      }else{
         this.searchBoxs[index].dateTime=""
      }
      
    }
  },
  created() {
    if (this.data) {
      this.expandRow = this.data.expandRow;
      this.searchBoxs = this.data.searchBoxs;
      var searchBoxs = this.searchBoxs;
      for (var i = 0; i < searchBoxs.length; i++) {
        if (searchBoxs[i].defalut.length > 0) {
          searchBoxs[i].checkAll = false;
        }
        if (searchBoxs[i].type === "datetime") {
          
          this.getDate(searchBoxs[i], i, true); //时间控件初始默认值
        }
        this.item.push({
          id: searchBoxs[i].id, //列表ID
          check: searchBoxs[i].defalut, //是否选择
          dateTime: searchBoxs[i].dateTime, //时间
          checkAll: searchBoxs[i].checkAll, // 全选
          isMultiple: searchBoxs[i].isMultiple, // 单选/多选
          expand: false, //是否展开
          show: false, //列表是否显示
          shoWIcon: false //展开按钮是否显示
        });
        

        if (i < this.expandRow) {
          this.$set(this.item[i], "show", true);
        }
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      //this.getObjHeight();
      setTimeout(this.getObjHeight,500);
      this.flag=true
    });
  }
};
</script>
<style lang="scss" scoped>
.fl {
  float: left;
}

.searchform {
  height: auto;
  background: #181e3e;
}
.itemBox {
  border-bottom: 1px dashed #303455;
  padding: 10px 20px;
  font-size: 14px;
  line-height: 26px;
  font-family: "微软雅黑";
  height: auto;
  position: relative;
}
.dateBox {
  display: flex;
  margin-right: 20px;
}
.itemBox span.itemLabel {
  display: block;
  width: 80px;
  margin-right: 10px;
  text-align: right;
}
.item {
  margin-left: 10px;
  font-size: 12px;
  height: 28px;
  overflow: hidden;
}
.item.active {
  height: auto;
}
.item a {
  padding: 0px 10px;
  display: inline-block;
  color: #fff;
  line-height: 28px;
  top: 1px;
  position: relative;
  border-radius: 2px;
  height: 28px;
}
.item a.on {
  background: linear-gradient(#089ab6, #2261dc);
}
.item a span.txt {
  position: relative;
  top: -2px;
}
.searchform >>> .c-checkbox-inner {
  top: -2px;
}
.c-checkbox-label {
  margin-top: -5px;
}
.searchform >>> .item .c-checkbox-inner-check {
  top: -5px;
}
.searchform >>> .item .c-checkbox-label span {
  vertical-align: text-bottom;
}
.searchform .checkAll .c-checkbox-inner {
  top: 6px;
  margin-right: 5px;
  margin-left: -10px;
}
.searchform .item .checkAll .c-checkbox-inner-check {
  top: -8px;
}
.c-checkbox-inner.on {
  background-color: #1dabe9;
}
.arrow {
  position: absolute;
  right: 10px;
  top: 12px;
  color: #1ca3de;
}
.dateBox a.btn{
 background: linear-gradient(#089ab6, #2261dc);
 display:inline-block;
 padding:0 10px;
 color:#fff;
 
}

.btnBg {
  background: #030124;
  height: 20px;
  position: relative;
  margin-top: -1px;
}
.arrowDown {
  display: block;
  width: 40px;
  height: 20px;
  background: #131737;
  margin: 0 auto;
  text-align: center;
}
.arrowDown:hover {
  color: #06c;
}
.itemBox {
  border-bottom: 1px dashed #303455;
  padding: 10px 20px;
  font-size: 14px;
  line-height: 26px;
  font-family: "微软雅黑";
  height: auto;
  position: relative;
}
.dateBox {
  display: flex;
  margin-right: 20px;
}
.itemBox span.itemLabel {
  color: #5782b2;
  display: block;
  width: 90px;
  margin-right: 10px;
  text-align: right;
}
.item {
  margin-left: 10px;
  font-size: 12px;
  height: 28px;
  overflow: hidden;
}
.item.active {
  height: auto;
}
.item a {
  margin-right: 10px;
  padding: 0px 10px;
  display: inline-block;
  color: #fff;
  line-height: 28px;
  top: 1px;
  position: relative;
  border-radius: 2px;
  height: 28px;
}
.item a.on {
  background: linear-gradient(#089ab6, #2261dc);
}
.item a span.txt {
  position: relative;
  top: -2px;
}
.searchform >>> .c-checkbox-inner {
  top: -2px;
}
.c-checkbox-label {
  margin-top: -5px;
  margin-right: 20px;
}
.searchform >>> .item .c-checkbox-inner-check {
  top: -5px;
}
.searchform >>> .item .c-checkbox-label span {
  vertical-align: text-bottom;
}
.searchform .checkAll .c-checkbox-inner {
  top: 6px;
  margin-right: 5px;
  margin-left: -10px;
}
.searchform .item .checkAll .c-checkbox-inner-check {
  top: -8px;
}
.c-checkbox-inner.on {
  background-color: #1dabe9;
}
.arrow {
  position: absolute;
  right: 10px;
  top: 12px;
  color: #1ca3de;
}
.btnBg {
  background: #030124;
  height: 20px;
  position: relative;
  margin-top: -1px;
}
.arrowDown {
  display: block;
  width: 40px;
  height: 20px;
  background: #131737;
  margin: 0 auto;
  text-align: center;
}
.arrowDown:hover {
  color: #06c;
}

</style>