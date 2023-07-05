<template>
  <el-container>
    <el-header>
      <div class="head-logo">
        <img src="~@/assets/images/logo.png" width="79" height="45"/>
      </div>
      <div class="head-title">
        <div class="title">肖老板</div>
        <div class="content">【私域运营专家】有任何问题，都可以问我哦！</div>
      </div>
    </el-header>
    <el-main>
      <div class="content" ref="chatScroll" style="height: 100%;width: 100%;overflow-y: auto">
        <div style="width: 100%" :key="index" v-for="(item,index) in msgList">
          <div :key="index" v-if="item.role==='system'" class="system-msg">
            <div class="system-msg-box">
              <div class="msg-head">
                <img src="~@/assets/images/root_head.png" width="37" height="37">
              </div>
              <div class="msg-text">
                <div>
                  {{ item.content }}
                </div>
              </div>
            </div>
          </div>
          <!--        用户消息-->
          <div v-if="item.role === 'user'" class="user-msg">
            <div class="right">
              <div class="msg-text">
                {{ item.content }}
              </div>
              <div class="image-box">
                <img src="~@/assets/images/avatar.png" width="52" height="52">
              </div>
            </div>
          </div>
          <!--        机器人消息-->
          <div v-if="item.role === 'but'">
            <div class="system-xw">尝试询问</div>
            <div @click="but.func" v-for="(but ,index2)  in item.list" :key="index2" class="msg-but">
              <div class="msg-but-text">
                {{ but.content }}
              </div>
              <img src="~@/assets/images/right.png" width="20" height="20"/>
            </div>
          </div>

          <div v-if="item.role === 'task'" class="task">
            <div class="head">
              <img src="~@/assets/images/right.png" width="52" height="52">
            </div>
            <div class="task-option">
              <span class="task-msg">好的，请先确定以下生成策略的信息！</span>
              <Transition name="left-to-right">
                <div v-if="item.task1" :class="['prd-fun-task',item.start1?'start-task':'']">
                  <p>生成用户运营模型</p>
                  <p>加微信 -> 未购买 -> 促复购 -> 转介绍</p>
                  <div v-if="item.start1" class="end"><img src="~@/assets/images/end.png" width="14" height="14"></div>
                  <div v-if="!item.start1" class="end"><img src="~@/assets/images/selected2.png" width="14" height="14">
                  </div>
<!--                  <div v-if="item.start1" class="right"><img src="~@/assets/images/right3.png" width="8" height="13">-->
<!--                  </div>-->
<!--                  <div v-if="!item.start1" class="right"><img src="~@/assets/images/right2.png" width="8" height="13">-->
<!--                  </div>-->
                </div>
              </Transition>
              <Transition name="left-to-right">
                <div v-if="item.task2" :class="['prd-task-item',item.start2 ? 'start-task':'']">
                  <img v-if="item.start2" src="~@/assets/images/end.png" width="14" height="14">
                  <img v-if="!item.start2" src="~@/assets/images/selected2.png" width="14" height="14">
                  <div class="item-info"> 生成“未购买”客户策略</div>
                  <span :class="item.start2 ? 'start-task':''" @click="toCusomerInfo(0,item)">查看客户详情</span>
                  <img v-if="!item.start2" src="~@/assets/images/right2.png" width="8" height="13">
                  <img v-if="item.start2" src="~@/assets/images/right3.png" width="8" height="13">
                </div>
              </Transition>
              <Transition name="left-to-right">
                <div v-if="item.task3" :class="['prd-task-item',item.start3 ? 'start-task':'']">
                  <img v-if="item.start3" src="~@/assets/images/end.png" width="14" height="14">
                  <img v-if="!item.start3" src="~@/assets/images/selected2.png" width="14" height="14">
                  <div class="item-info"> 生成“促复购”客户策略</div>
                  <span :class="item.start3 ? 'start-task':''" @click="toCusomerInfo(1,item)">查看客户详情</span>
                  <img v-if="!item.start3" src="~@/assets/images/right2.png" width="8" height="13">
                  <img v-if="item.start3" src="~@/assets/images/right3.png" width="8" height="13">
                </div>
              </Transition>
              <Transition name="left-to-right">
                <div v-if="item.task4" :class="['prd-task-item',item.start4 ? 'start-task':'']">
                  <img v-if="item.start4" src="~@/assets/images/end.png" width="14" height="14">
                  <img v-if="!item.start4" src="~@/assets/images/selected2.png" width="14" height="14">
                  <div class="item-info"> 生成“转介绍”客户策略</div>
                  <span :class="item.start4 ? 'start-task':''" @click="toCusomerInfo(2,item)">查看客户详情</span>
                  <img v-if="!item.start4" src="~@/assets/images/right2.png" width="8" height="13">
                  <img v-if="item.start4" src="~@/assets/images/right3.png" width="8" height="13">
                </div>
              </Transition>
              <!-- but box 为按钮撑开空间 -->
              <div class="but-box">

              </div>

            </div>
            <div v-if="item.task4 && !item.stop" @click="item.startFun(index)" class="option-but-start">
              开始执行
            </div>
            <div v-if="item.stop " @click="item.endFun(index)" class="option-but-end">
              停止执行
            </div>
            <div v-if="!item.task4" class="option-but-end">
              正在执行
            </div>
          </div>

          <div class="send-form">
            <div class="send-input">
              <input v-model="inputText" placeholder="在这⾥输⼊您要咨询或执⾏的任务" @keyup.enter="keydown"/>
              <div @click="keydown" class="send-but">
                <img src="~@/assets/images/send_btn.png" width="31" height="31">
              </div>
            </div>
            <div>
            </div>
            <div class="menu-but">
              <el-popover width="120px">
                <div class="menu-list">

                  <div class="menu-item" @click="pushPage('/vector/table')">
                    <img src="~@/assets/images/active.png" width="24" height="24"/>
                    <span>客户分层</span>
                  </div>
                  <el-tooltip effect="dark" content="暂未开放" placement="left">
                    <div class="menu-item" @click="pushPage('/material')">
<!--                    <div class="menu-item">-->
                      <img src="~@/assets/images/vector.png" width="24" height="24"/>
                      <span>素材库</span>
                    </div>
                  </el-tooltip>
                  <el-tooltip effect="dark" content="暂未开放" placement="left">
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
                <div slot="reference">
                  <img src="~@/assets/images/menu.png" width="16" height="10">
                  功能菜单
                </div>
              </el-popover>
            </div>
          </div>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import Vue from "vue";
import {MessageBox} from "element-ui";
import {getList} from "../api/customer";

export default {
  name: "Chat",
  data() {
    return {
      msgList: [],
      gpt_system: {
        "role": "system",
        "content": "你现在要扮演的角色是私域专家肖老板，你是一个擅长私域运营的专家，你将在对话中给出大家建议，并帮大家进行SCRM系统的功能操作。第一句话请给出欢迎语打个招呼!SCRM系统功能包含客户分层、客户促活、数据看版等功能，你需要根据客户的回复进行理解，如果客户的语义有明确的操作功能的指令，请将对应功能的代号回复出来:代号“1”: 打开客户分层页面;代号“2”: 打开客户促活页面;代号“3”: 打开数据看版页面;代号“0”: 回到首页;代号“客户名称: 某客户”: 查询某个客户的名称(例如查询张三的信息，代号“客户名称: 张三”);代号“备注: 某客户”: 查询某个客户的备注(例如查询张三的备注，代号“备注: 张三”);代号“描述: 某客户”查询某个客户的描述(例如查询张三的描述，代号“描述: 张三”);如果只是在咨询，而不是操作，请不要说出代号!"
      },
      inputText: "",
    }
  }, methods: {
    keydown() {
      if (this.inputText) {
        this.sendUserMsg(this.inputText)
        this.inputText = ""
        let list = [];
        list.push(this.gpt_system);
        for (let i = 0; i < this.msgList.length; i++) {
          if (this.msgList[i].role == "user") {
            list.push({
              "role": "user",
              "content": this.msgList[i].content
            })
          }
          if (this.msgList[i].role == "system") {
            list.push({
              "role": "system",
              "content": this.msgList[i].content
            })
          }
        }

        let params = JSON.stringify(list)
        this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + params)
            .then((response) => {
              let gptMsg = response.data.data.text.choices[0].message.content;
              this.sendSystemMsg(gptMsg)
            })
      }
    },
    sendSystemMsg(content) {
      this.msgList.push({role: "system", content: content})
    },
    sendUserMsg(content) {
      this.msgList.push({role: "user", content: content})
    },
    sendButMsg(list) {
      this.msgList.push({role: "but", list: list})
    },
    sendTaskMsg(index) {
      this.msgList.push({
        role: "task"
        , start1: true
        , start2: true
        , start3: true
        , start4: true
        , task1: false
        , task2: false
        , task3: false
        , task4: false
        , taskStatus: true,
        ok: false,
        stop: false,
        clear: false
        , startFun: (index) => {
          this.sendUserMsg("开始执行")
          setTimeout(() => {
            this.sendSystemMsg("收到，已为您自动化执行策略。")
            this.msgList[index].stop = true;
            if (this.msgList[index].task1) {
              setTimeout(() => {
                this.msgList[index].start1 = false;
                this.msgList[index].task2 = true;
              }, 500)
            }

            if (this.msgList[index].task2) {
              setTimeout(() => {
                this.msgList[index].start2 = false;
                this.msgList[index].task3 = true;
              }, 1500)
            }


            setTimeout(() => {
              if (this.msgList[index].task4) {
                this.msgList[index].start4 = false;
                this.msgList[index].ok = true;
              }
            }, 3500)

          }, 1000)

        },
        endFun: (index) => {
          this.sendUserMsg("停止执行")
          setTimeout(() => {
            this.msgList[index].taskStatus = false;
            this.msgList[index].clear = true;
            this.sendSystemMsg("收到，已为您停⽌执⾏策略")
            clearTimeout(this.msgList[index].timer1)
            clearTimeout(this.msgList[index].timer2)
            clearTimeout(this.msgList[index].timer3)
            clearTimeout(this.msgList[index].timer4)

          }, 1000)
        }
      })
      const taskId = this.msgList.length - 1;
      this.msgList[taskId].task1 = true;
      this.msgList[taskId].timer1 = setTimeout(() => {
        this.msgList[taskId].start1 = false;
        this.msgList[taskId].task2 = true;
      }, 500)
      this.msgList[taskId].timer2 = setTimeout(() => {
        this.msgList[taskId].start2 = false;
        this.msgList[taskId].task3 = true;

      }, 1500)
      this.msgList[taskId].timer3 = setTimeout(() => {
        this.msgList[taskId].start3 = false;
        this.msgList[taskId].task4 = true;

      }, 4000)
      this.msgList[taskId].timer4 = setTimeout(() => {
        this.msgList[taskId].start4 = false;
        this.msgList[taskId].ok = true;
      }, 6000)

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
    checkLoadPage: function () {
      // if (this.$route.path === '/home') {
      //   this.isLoadPage = false;
      // } else {
      //   this.isLoadPage = true;
      // }
    },
    stopNavCallBack: function () {
      this.popPanelVisible = false;
    },
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
    },toCusomerInfo(labelType,item){
      const queryParams ={
            labelType:labelType,
            pageNum:1,
            pageSize:8
      }
      getList(queryParams).then(response=>{
        //随机获取一个数组元素
        const list = response.data.list;
        const index = Math.floor(Math.random() * list.length);
        const external_userid =  list[index].externalUserid;
        this.$router.push('/vector/info?externalUserid='+external_userid + '&clear=' + item.clear);
      })



    }
  },
  created() {
    this.checkLoadPage();
    this.sendSystemMsg("哈喽！我是您的超级管家，您有任何问题都可以咨询我哦。 当然我也可以帮你出谋划策，比如设计文案、策略、分析等工作。")

    this.sendButMsg([{
      content: "介绍Salemate",
      func: () => {
        this.sendUserMsg("介绍Salemate");
        setTimeout(() => {
          this.sendSystemMsg("您好！SaleMate是一款“对话式”的AI营销工具，可以帮助企业自动的\n" +
              "更新客户信息、生成客户促活的策略、以及回复客户咨询。做到让每\n" +
              "一个商家都能0门槛使用，让AI员工24小时的为您打工！")
          this.sendButMsg([{
            content: "生成客户运营策略", func: (index) => {
              //生产工作信息
              setTimeout(() => {
                this.sendTaskMsg(index)
              }, 2000)
            }
          }])
        }, 2000)
      }
    },
      //介绍
      {
        content: "我应该如何使用",
        func: () => {
          this.sendUserMsg("我应该如何使用");
          setTimeout(() => {
            this.sendSystemMsg("您好！SaleMate是一款“对话式”的AI营销工具，可以帮助企业自动的 更新客户信息、生成客户促活的策略、以及回复客户咨询。做到让每 一个商家都能0门槛使用，让AI员工24小时的为您打工！")
            this.sendButMsg([{
              content: "生成客户运营策略", func: (index) => {
                //生产工作信息
                setTimeout(() => {
                  this.sendTaskMsg(index)
                }, 2000)
              }
            }])
          }, 2000)
        }
      }

    ])

  }, activated() {
    setTimeout(() => {
      this.$nextTick(() => {
        this.$refs.chatScroll.scrollTop = this.$refs.chatScroll.scrollHeight;
      });
    }, 1000)

  }
  , watch: {
    //  监视路由变化
    $route(to, from) {
      this.checkLoadPage();
    },
    msgList: {
      handler: function (newVal, oldVal) {
        this.$nextTick(() => {
          this.$refs.chatScroll.scrollTop = this.$refs.chatScroll.scrollHeight;
        });

      },
      deep: true
    }
  }
}

</script>

<style scoped>
.el-container .is-vertical {
  padding: 0;
}

.el-container {
  height: 100%;
  min-width: 1300px;
}

.el-header {
  position: relative;
  display: flex;
  align-items: center;
  padding-left: 53px;
  height: 143px !important;
  margin-bottom: 15px;
  background: #FFFFFF;
  border-radius: 10px;
}

.el-header .logo {
  margin-right: 28px;

}

.el-main {

  position: relative;
  padding-top: 30px;
  background: #FFFFFF;
  border-radius: 10px;
  padding-left: 115px !important;
  padding-right: 331px !important;
  padding-bottom: 70px !important;
}

.head-logo {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.head-title {
  text-align: center;
  margin: 0 auto;
}

.head-title .title {
  font-size: 32px;
  font-weight: 600;
  color: #161624;
}

.head-title .content {
  font-size: 16px;
  font-weight: 500;
  color: #888888;
}


.system-msg {
  margin-top: 18px;
  display: inline-block;
  align-items: center;
  justify-content: flex-start;
  /*width: 100%;*/
  padding: 10px 97px 10px 10px;
  background: #EEF1F8;
  border-radius: 10px 10px 10px 10px;
  /*margin-top: 20px;*/
  font-size: 14px;
  font-weight: 500;
}

.system-msg .menu-item {

}

.system-msg .msg-head {

}

.system-msg .msg-text {
  display: flex;
  align-items: center;
  padding-left: 20px
}

.system-msg-box {
  display: flex;
}

.msg-but {
  width: 840px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  margin-top: 18px;

  box-sizing: border-box;
  border: 1px solid #A8A8A8;
  font-size: 14px;
  font-weight: 500;
  color: #161624;
  border-radius: 50px;
  height: 36px;
  line-height: 36px;
}

.system-xw {
  margin-top: 20px;
  font-size: 20px;
  font-family: PingFang SC-Semibold, PingFang SC;
  font-weight: 600;

}


.user-msg {
  margin-top: 18px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.user-msg .right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: #306AFF;
  color: #FFFFFF;
  padding: 10px 10px 10px 10px;
  border-radius: 10px 10px 10px 10px;
}

.user-msg .right .image-box {
  height: 100%;
}

.user-msg .right .msg-text {
  text-indent: 36px;
  font-size: 14px;
}

.user-msg .right img {
  margin-left: 30px;
}

.send-form {
  /*background-color: #FFFFFF;*/
  min-width: 900px;
  position: fixed;
  bottom: 25px;
  width: calc(100% - 445px);

}

.send-form .send-input input {

  text-indent: 21px;
  width: 100%;
  height: 36px;
  border-radius: 36px 36px 36px 36px;
  border: 1px solid #A8A8A8;
  box-sizing: border-box;
}

.send-input {
  position: relative;
}

.send-input .send-but {
  z-index: 999999;
  width: 31px;
  height: 31px;
  position: absolute;
  top: 50%;
  right: 3px;
  transform: translateY(-50%);
}

.menu-but {
  position: absolute;
  top: 50%;
  text-align: center;
  transform: translateY(-50%);
  right: -220px;
  width: 175px;
  height: 36px;
  background: #306AFF;
  border-radius: 36px 36px 36px 36px;
  color: #FFFFFF;
  line-height: 36px;
}

.menu-list {
  /*width: 120px;*/
  background: #EEF1F8;
}

.menu-list .menu-item {
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

.left-to-right-enter-active, .left-to-right-leave-active {
  transition: all 1s;
}

.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */
{
  transform: translateX(-100%);
  opacity: 0;
}

.right-to-left-enter-active, .right-to-left-leave-active {
  transition: all 1s;
}

.right-to-left-enter, .right-to-left-leave-to /* .fade-leave-active below version 2.1.8 */
{
  transform: translateX(100%);
  opacity: 0;
}

/*策略执行*/
.task {
  position: relative;
  display: flex;
  padding: 10px;
  margin-top: 15px;
  width: 420px;
  background: #EEF1F8;
  border-radius: 7px 7px 7px 7px;

}

.task .head {
  margin-right: 30px;
}

.task .prd-fun-task {
  position: relative;
  display: flex;
  flex-flow: column;
  justify-content: center;
  margin-top: 10px;
  padding: 0 10px;
  width: 301px;
  height: 62px;
  background: #FFFFFF;
  border-radius: 4px 4px 4px 4px;

}

.prd-fun-task .end {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: 10px;
}

.prd-fun-task .right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 10px;
}

.task .prd-fun-task p:nth-child(1) {
  padding: 0 30px;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.task .prd-fun-task p:nth-child(2) {
  padding: 0 30px;
  margin: 0;
  font-size: 10px;
  font-weight: 400;
}

.task .task-msg {
  font-size: 14px;
  font-weight: 500;
}

.prd-task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  padding: 0 10px;
  width: 301px;
  height: 44px;
  background: #FFFFFF;
  border-radius: 4px 4px 4px 4px;
}

.prd-task-item .item-info {
  padding: 0 10px;
  font-weight: 500;
}

.task .prd-task-item > span {
  font-size: 10px;
  /*padding-right: 20px;*/
  color: #306AFF;
}

.task .but-box {
  width: 100%;
  height: 90px;
}

.option-but-start {
  position: absolute;
  transform: translateX(-50%);
  left: 50%;
  bottom: 50px;
  margin-top: 15px;
  color: #FFFFFF;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  line-height: 29px;
  width: 139px;
  height: 29px;
  background: #306AFF;
  border-radius: 71px 71px 71px 71px;
}

.option-but-end {
  position: absolute;
  transform: translateX(-50%);
  left: 50%;
  bottom: 10px;
  margin-top: 15px;
  box-sizing: border-box;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  line-height: 29px;
  width: 139px;
  height: 29px;
  border-radius: 71px 71px 71px 71px;
  opacity: 1;
  border: 1px solid #306AFF;
}

/* 滚动条样式设置  */
.content::-webkit-scrollbar {
  width: 0;
}

.content::-webkit-scrollbar-thumb {
  border-radius: 1px;
  box-shadow: inset 0 0 1px rgba(0, 0, 0, 0.2);
  background: rgba(0, 0, 0, 0.2);
}

.content::-webkit-scrollbar-track {
  box-shadow: inset 0 0 1px rgba(0, 0, 0, 0.2);
  border-radius: 0;
  background: rgba(0, 0, 0, 0.1);

}

.start-task {
  color: #888888 !important;
}


</style>