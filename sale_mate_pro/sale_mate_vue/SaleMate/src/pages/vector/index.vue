<template>
  <el-container>
<!--    <Transition name="">-->
    <keep-alive>
        <router-view path="/vector/info"></router-view>
    </keep-alive>
<!--    </Transition>-->
    <Transition name="left-to-right">
    <el-aside style="overflow: hidden">
        <ChatCommon ref="chat" v-show="loading"></ChatCommon>
    </el-aside>
    </Transition>
  </el-container>

</template>

<script>
import {getList, getSyncCustomerStatus, syncCustomer} from "../../api/customer";
import ChatCommon from "../../components/ChatCommon";
import Vue from "vue";

export default {
  name: 'vector',
  data(){
    return{
      loading: false,
      chatLoading: false,
    }
  },
  components: {
    ChatCommon
  },
  methods:{
    tableMsgCreate(){
      this.$nextTick(() => {
        this.$refs.chat.sendSystemMsg("哈喽！我是您的超级管家，您有任何问题都\n" +
            "可以咨询我哦。当然我也可以帮你出谋划\n" +
            "策，比如设计文案、策略、分析等工作。");
        this.$refs.chat.sendButMsg([
          {
            content:'介绍Salemat',
            func:()=>{
              this.$refs.chat.sendSystemMsg("Salemat是一款专注于帮助企业提升销售\n" +
                  "业绩的智能销售管理软件，通过销售管理、\n" +
                  "客户管理、销售数据分析、销售业绩分析、\n" +
                  "销售业绩预测等功能，帮助企业提升销售\n" +
                  "业绩，提高销售效率，降低销售成本。");
            }
          },
          {
            content: '生运营策策略',
            func:()=>{
              this.$refs.chat.sendUserMsg("生运营策略");
              setTimeout(()=>{
                this.$refs.chat.sendSystemMsg("好的，生成运营策略任务中．．．");
              },1000)
              setTimeout(()=>{
                this.$refs.chat.sendSystemMsg("已为您生成运营模型：未购买->促复购一>转介绍。你看查看客户详情审阅内容");
              },2000)
            }
          }
        ])

      });
    }
  },
  activated() {
    this.loading = true;
    if (!this.chatLoading) {
      this.chatLoading = true;
      this.tableMsgCreate();
    }
  },
  deactivated() {
    // console.log(this.$router.path);
    // if ()
    this.loading = false;
  },watch:{
    $route(to,from){
      console.log(to.path);
      if (to.path === '/vector') {
        this.$router.push({path: '/vector/table'});
      }

    }
  }
}
</script>

<style scoped>
.el-container {
  padding: 15px;
  height: 100%;
  min-width: 1300px;
}
.el-aside {

  /*width: 360px !important;*/
  min-width: 360px;
  width: 450px !important;
  background-color: #FFFFFF;
  border-radius: 10px;
  margin-left: 15px;
}


.left-to-right-enter-active, .left-to-right-leave-active {
  transition: all 1s;
}
.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */ {
  transform: translateX(-100%);
  opacity: 0;
}

</style>
