<template>
  <div class="chat">
    <div class="header">
      <div class="title">肖老板</div>
      <div class="content">【私域运营专家】有任何问题，都可以问我哦！</div>
      <el-divider></el-divider>
    </div>
    <!-- 系统消息 -->
    <div ref="chatScroll"  class="chat-list">
      <div v-for="(item,index) in msgList" :key="index" class="chat-msg">
      <div v-if="item.role === 'system'" class="system">
        <img src="~@/assets/images/root_head.png" height="40" width="40" />
        <div class="msg"><div>{{item.content}}</div></div>
      </div>
      <!-- 用户消息 -->
      <div  v-if="item.role === 'user'" class="user">
        <div class="msg">
          <div>
            {{item.content}}
          </div>
        </div>
        <img src="~@/assets/images/avatar.png" height="40" width="40" />
      </div>
      <!-- 咨询按钮 -->
      <div v-if="item.role === 'but'" class="but">
        <span>尝试询问</span>
        <div @click="item.func" v-for=" (item,index) in item.butList" :key="index">{{item.content}}</div>
        <!-- <div>用户分层功能模块有什么作用？</div> -->
      </div>
    </div>
    
    </div>
    <div class="send-form">
        <div @keyup.enter="keydown" class="send-form-input">
          <input placeholder="在这里输入您要咨询或执行的任务" />
          <img src="~@/assets/images/send_btn.png" height="24" width="24" />
        </div>
        <div class="menu-but">
        <el-popover width="120px">
          <div class="menu-list">

            <div class="menu-item"  @click="pushPage('/vector/table')">
              <img src="~@/assets/images/active.png" width="24" height="24"/>
              <span>客户分层</span>
            </div>
             <el-tooltip  effect="dark" content="暂未开放" placement="left">
            <div class="menu-item" @click="pushPage('/material')">
              <img src="~@/assets/images/vector.png"  width="24" height="24"/>
              <span>素材库</span>
            </div>
             </el-tooltip>
            <el-tooltip  effect="dark" content="暂未开放" placement="left">
            <div class="menu-item">
              <img src="~@/assets/images/tjt.png" width="24" height="24"/>
              <span>数据看板</span>
            </div>
            </el-tooltip>

            <div @click="loginout" class="menu-item">
              <img src="~@/assets/images/exit.png" width="24" height="24"/>
              <span>退出登录</span>
            </div>
          </div>
          <div  slot="reference">
            <img src="~@/assets/images/menu_btn.png" width="62" height="30" />
          </div>
        </el-popover>
        </div>
       
      </div>
  </div>
</template>
<script>
import { syncCustomer } from "../api/customer";
import Vue from "vue";
import {MessageBox} from "element-ui";

export default {
  name: "ChatCommon",
  props:{
    callback:{
      type:Function,
      default:()=>{}
    }
  },
  data() {
    return {
      msgList:[],
      gpt_system: {
        "role": "system",
        "content": "你现在要扮演的角色是私域专家肖老板，你是一个擅长私域运营的专家，你将在对话中给出大家建议，并帮大家进行SCRM系统的功能操作。第一句话请给出欢迎语打个招呼!SCRM系统功能包含客户分层、客户促活、数据看版等功能，你需要根据客户的回复进行理解，如果客户的语义有明确的操作功能的指令，请将对应功能的代号回复出来:代号“1”: 打开客户分层页面;代号“2”: 打开客户促活页面;代号“3”: 打开数据看版页面;代号“0”: 回到首页;代号“客户名称: 某客户”: 查询某个客户的名称(例如查询张三的信息，代号“客户名称: 张三”);代号“备注: 某客户”: 查询某个客户的备注(例如查询张三的备注，代号“备注: 张三”);代号“描述: 某客户”查询某个客户的描述(例如查询张三的描述，代号“描述: 张三”);如果只是在咨询，而不是操作，请不要说出代号!"
      },
    };
  },
  methods: {

  loginout: function () {
    MessageBox.confirm('确定退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      Vue.cookie.delete('token')
      this.$router.push('/login');
    }).catch(() => {
    });
  },
    pushPage: function (url) {
          // this.isLoadPage = true;
          // //跳转路径相同返回home
          // if (this.$route.path === url) {
          //   this.isLoadPage = false;
          //   this.$router.push('/home');
          //   return;
          // }
          this.$router.push(url);
        },
    keydown(e){
      if(e.target.value){
        let list = [];
        if (this.callback) {
         var res = this.callback(e.target.value);
         if(res){
           this.sendUserMsg(e.target.value)
           e.target.value = ""
           return;
         }
        }
        this.sendUserMsg(e.target.value)
        e.target.value = ""

        list.push(this.gpt_system);
        for(let i = 0;i<this.msgList.length;i++){
          if(this.msgList[i].role === "user"){
            list.push({
              "role": "user",
              "content": this.msgList[i].content
            })
          }
          if (this.msgList[i].role === "system") {
            list.push({
              "role": "system",
              "content": this.msgList[i].content
            })
          }
        }

        let params = JSON.stringify(list)
        this.$axios.get("https://xxw.pinbar.vip/api/list?text="+params)
            .then((response) => {
              let gptMsg = response.data.data.text.choices[0].message.content;
              this.sendSystemMsg(gptMsg)
            })
      }
    },
      sendSystemMsg(content){
        console.log(content)
          this.msgList.push({role:"system",content:content})
        },
        sendUserMsg(content,callBack){
          this.msgList.push({role:"user",content:content})
        if (callBack) {
          callBack()
          }
        },
        sendButMsg(butList){
          /**
           * butList:按钮列表
           * {
           *  content:''
           *  func:()=>{}
           * }
           */
          this.msgList.push({role:"but",butList:butList})
        },
    
  },
  activated() {
    console.log(this.$refs.chatScroll.scrollTop)
    this.$refs.chatScroll.scrollTop = this.$refs.chatScroll.scrollHeight;

  },
  watch: {
    msgList: {
      handler: function (val, oldVal) {
        console.log(this.$refs.chatScroll.scrollTop)
        this.$nextTick(() => {

          this.$refs.chatScroll.scrollTop = this.$refs.chatScroll.scrollHeight + 30;
        });
      },
      deep: true,
    },
  },
};
</script>

<style scoped>
.chat {
  display: flex;
  flex-flow: column;
  width: 100%;
  height: 100%;
}

.header {
  position: relative;
  padding: 33px 22px 0 22px;
}
.header .title {
  font-size: 32px;
  font-family: PingFang SC-Semibold, PingFang SC;
  font-weight: 600;
  color: #161624;
}
.header .content {
  margin-top: 8px;
  font-size: 12px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #888888;
}
.chat-list {
  overflow-y: auto;
  position: relative;
  flex: 1;
  padding: 0 13px;
  padding-bottom: 30px;
}
.chat-list .chat-msg{
  width: 100%;
}
.system {
  margin-top: 23px;
  padding-right: 50px;
  display: flex;
}
.system img {
  margin-right: 10px;
}
.system .msg {
  justify-content: flex-start;
  align-items: center;
  padding: 13px 13px;
  background: #eef1f8;
  border-radius: 6px 6px 6px 6px;
  display: inline-block;
  /* line-height: 51px; */
  display: flex;

  font-size: 14px;
}
.user {
  display: flex;
  justify-content: flex-end;
  margin-top: 23px;
  padding-left: 50px;
}
.user img {
  margin-left: 10px;
}
.user .msg {
  justify-content: flex-end;
  align-items: center;
  padding: 13px 13px;
  color: #eef1f8;
  background: #306aff;
  border-radius: 6px 6px 6px 6px;
  display: inline-block;
  /* line-height: 51px; */
  display: flex;

  font-size: 14px;
}
/* 按钮消息 */
.chat-list .but {
 
  display: flex;
  flex-flow: column;
  justify-content: center;
  align-items: flex-start;
  padding: 0 46px;
}
.chat .but > span {
  margin-top: 18px;
  font-size: 16px;
  font-family: PingFang SC-Semibold, PingFang SC;
  font-weight: 600;
  color: #161624;
}
.chat .but > div {
  margin-top: 15px;
  text-align: center;
  width: 100%;
  font-size: 14px;
  padding: 10px 13px;
  border-radius: 101px 101px 101px 101px;
  border: 1px solid #a8a8a8;
}
.send-form {
  position:sticky;
  bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  background-color: #FFFFFF;
}
.send-form .send-form-input {
  margin:0 13px ;
  position: relative;
  display: flex;
  flex: 1;
}
.send-form .send-form-input img{
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 3px;
}
.send-form .send-form-input input {
  flex: 1;
  padding: 4px 46px 4px 10px;
  border-radius: 50px;
  border: 1px solid #a8a8a8;
  font-size: 14px;
  height: 22px;
}
.send-form .meun-btn{
  width: 62px;
  height: 30px;
  margin-right: 13px;
}
.menu-but{
  margin-right: 13px;
}
.menu-list{
  /*width: 120px;*/
  background: #EEF1F8;
}
.menu-list .menu-item{
  padding: 0 20px;
  display: flex;
  font-size: 14px;
  text-align: center;
  height: 52px;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  border-bottom: 1px solid #A8A8A8;;
}
</style>