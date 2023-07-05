<template>
  <div class="center">
    <div class="logo"><img src="@/assets/images/logo_l.png" height="79" width="140"/></div>
    <div v-if="!openInstall" class="wx">
      <div class="tabs">
        <div @click="dev">微信扫码登录</div>
        <div class="select">企业微信扫码登录</div>
      </div>
      <div class="login">
        <iframe width="100%" height="416" scrolling="no" referrerpolicy="origin" frameborder="no" marginwidth="0"
                marginheight="0" allowtransparency="yes" :src="callBackUrl"></iframe>
      </div>
      <el-button v-if="mode === 'dev'" style="margin:  0 auto" @click="devLogin">测试环境登录</el-button>
    </div>
    <div class="background-image">
      <img width="1409" height="553" src="@/assets/images/login_background.png"/>
    </div>
  </div>
</template>

<script>
import {install, systemLogin, verify,env} from "../api/user";
import {MessageBox,Notification} from "element-ui";
import {test} from "../api/test";
import AppConfig from "../config";
import Vue from "vue";

export default {
  name: 'WxLogin',
  data() {
    return {
      installUrl: '',
      // callBackUrl: 'https://login.work.weixin.qq.com/wwlogin/sso/login?login_type=ServiceApp&appid=ww67ff055de7bf9ee4&redirect_uri='+AppConfig.REDIRECTURI+'&state=weblogin_wwfdf5bd46d24342bd',
      callBackUrl: 'https://login.work.weixin.qq.com/wwlogin/sso/login?login_type=ServiceApp&appid=ww67ff055de7bf9ee4&redirect_uri='+AppConfig.REDIRECTURI+'&state=weblogin_wwfdf5bd46d24342bd',
      timer: null,
      mode:AppConfig.mode
    }
  },
  created() {
    if (window.location.search) {
      console.log("window.location.search:", window.location.search);
      let search = window.location.search;
      window.location.replace('/#/login' + search)
    }
    console.log("this.$route.query:", this.$route.query);
    //判断是否是安装过应用
    const authCode =this.$route.query.auth_code

    if (authCode){
      if (localStorage.authCode === authCode) {
        return;
      }
      localStorage.authCode = authCode;

      install({authCode:authCode}).then(res=>{
        if (res.status === 200){
          //保存token
          const token = res.data.token;
          const expire = res.data.expire;
          const corpid = res.data.corpid;
          //保存token
          Vue.cookie.set("token",token,expire);
          Vue.cookie.set("corpid",corpid,expire);
          this.$router.push({path:"/home"});
        }else {
          Notification.warning(res.data.msg);
        }
      });
    }

    //登录
    let scode = this.$route.query.code;
    let state = this.$route.query.state;

    if (scode) {
      if (localStorage.code === scode) {
        return;
      }
      localStorage.code = scode;
      let params = {code: scode, state: state};
      //获取用户信息
      verify(params).then(res => {
        console.log("登录～");
        if (!res.data.token) {
          //提示安装应用
          Notification.warning("请先安装授权应用");
          const  installUrl = res.data;
          //在当前跳转到安装页面
          window.location.replace(installUrl);
          return;
        }
        const token = res.data.token;
        const expire = res.data.expire;
        const corpid = res.data.corpid;
        //保存token
        Vue.cookie.set("token",token,expire);
        Vue.cookie.set("corpid",corpid,expire);
        this.$router.push({path:"/vector/table"});

      });
    }
  }
  ,beforeDestroy() {

    }, methods: {
    devLogin(){
      env().then(res=>{
        if (res.status === 200){
          const token = res.data.token;
          const expire = res.data.expire;
          //保存token
          Vue.cookie.set("token",token,expire);
          // Vue.cookie.set("corpid",corpid,expire);
          this.$router.push({path:"/vector/table"});
        }
      })
    },
    dev() {
      Notification.warning("正在开发。。。。");
    }
  }
}
</script>

<style scoped>
.center {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1500px;
  background-color: #306AFF;
}
.wx{
  position: absolute;
  z-index: 99;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 552px;
  height:552px;
  background: #FFFFFF;
  border-radius: 10px 10px 10px 10px;
  opacity: 1;
}

.logo{
  position: absolute;
  top:60px;
  left: 100px;
}
.tabs{
  padding:31px  45px 0 45px;
  display: flex;
  justify-content: space-evenly;
  text-align: center;
}
.tabs > div{
  flex: 1;
  padding-bottom: 7px;
  border-bottom: 2px solid #A8A8A8;
}
.tabs  .select{
  border-bottom: 2px solid #306AFF;
}

.background-image{
  position: fixed;
  bottom: -97px;
  left: 121px;
  /*transform: translate();*/
}
.install{
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  width: 435px;
  background: #FFFFFF;
  border-radius: 5px;
}
.install .title{
  width: 100%;
  padding-top: 15px;
  font-size: 18px;
  font-weight: 600;
  color: #306AFF;
  text-align: center;
}
.install .rqCode{
  height: 410px;
  width: 100%;
  position: relative;
  overflow: hidden;
}
.install .rqCode .wx-install-code{
  position: absolute;
  left: -3px;
  top: -95px;
}
.install .option {
  display: flex;
  flex-flow: column;
  align-items: center;
  text-align: center;
}
.install .option .info{

  padding: 14px 0;
  font-size: 13px;
  font-weight: 500;
  color: #161624;
}
.install .option .but{
  width: 144px;
  height: 25px;
  line-height: 25px;
  font-size: 10px;
  margin-top: 10px;
}
.install .option .register_but{

  background: #306AFF;
  color: #FFFFFF;
}
.install .option .retry_but{
  box-sizing: border-box;
  background: #FFFFFF;
  color: #306AFF;
  border: 1px solid #306AFF;

}
.agreement{
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 15px;
  padding-bottom: 33px;
  font-size: 10px;
}
.agreement_text{
  color: #306AFF;
}


</style>
