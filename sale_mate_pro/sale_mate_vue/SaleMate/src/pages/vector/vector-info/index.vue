<template>
  <Transition name="left-to-right">
    <el-container v-show="loading">
      <el-header>
        <div class="head-title">
          <div @click="$router.back()" class="logo"><img src="~@/assets/images/logo.png" width="79" height="45"/></div>
          <h2>客户分层</h2>
        </div>
      </el-header>

      <el-main>
        <div class="user-info">
          <div class="user-head">
            <img style="border-radius: 65px" :src="customerInfo.avatar" width="65" height="65">
          </div>
          <div class="user-info-text">
            <div class="user-name">
                <span>
                  {{ customerInfo.name }}
                </span>
              <span>{{ customerInfo.gender === 1 ? '男' : '女' }}</span>
            </div>
            <div class="user-option">
              <span>添加时间：{{ dateFormatter(customerInfo.createDate) }} 添加人：{{ customerInfo.userName }}</span>
            </div>
            <div class="user-content">
              <span>{{ customerInfo.description }}</span>
            </div>
          </div>
        </div>
        <!--      客户标签-->
        <div class="user-label">
          <div class="label-title">
            客户标签:
          </div>
          <div class="label-list">
          <span v-for=" (item,index) in tagList" :key="index" class="label-item">
            {{ item }}
          </span>
            <div v-if="tagList.length === 0" class="not-label">
              暂无标签
            </div>
          </div>
        </div>
        <!--     发送内容 -->
        <div class="send">
          <div class="send-title">
            <div class="send-title-label">发送内容:</div>
            <div class="send-title-content">AI已从 <a>素材库</a> 中，为您选择了这2周最合适的促活内容</div>
          </div>
          <div class="send-list">
            <div v-if="dataList.length === 0" class="not-data">
              <img src="~@/assets/images/not-data.gif" width="100" height="100">
              <p>暂无发送内容</p>
            </div>
            <div @click="openSendWindow(item)" :key="index" :class="['send-item',item.status===1?'selected':'']"
                 v-for="(item,index) in dataList">
              <!--          <div @click="openSendWindow(item)" :key="index"  class="send-item" v-for="(item,index) in dataList">-->
              <div class="send-ico">
                <!--                <img item.status src="~@/assets/images/send.png" width="15px" height="15px"/>-->
                <img v-if="item.status === 0" src="~@/assets/images/send.png" width="15px" height="15px"/>
                <img v-if="item.status === 1" src="~@/assets/images/selected.png" width="12px" height="12px"/>
              </div>
              <div class="send-time">
                <p>{{ item.sendDate }}</p>
                <p>{{ item.sendTime }}</p>
              </div>
              <el-divider direction="vertical"></el-divider>
              <div class="send-content">
                {{ item.content }}
              </div>
              <div class="right-icon">
                <img src="~@/assets/images/right.png" width="17px" height="14px">
              </div>
            </div>
          </div>
        </div>
        <el-dialog
            :modal="false"
            append-to-body
            width="842px"
            heiht="455px"
            :show-close="false"
            :visible.sync="visible"
            custom-class="send-save-dialog"
        >
          <div class="option-info">
            <div class="send-date">
              <el-date-picker
                  :clearable="false"
                  v-model="currentItem.sendDate"
                  align="left"
                  type="date"
                  value-format="yyyy-MM-d"
                  placeholder="选择日期">
              </el-date-picker>
            </div>

            <el-divider direction="vertical"></el-divider>
            <div class="send-time">
              <span>发送时间:</span>
              <!--            <span>{{currentItem.sendTime}}</span>-->
              <el-time-picker
                  value-format="HH:mm:ss"
                  :clearable="false"
                  v-model="currentItem.sendTime"
                  placeholder="选择时间">
              </el-time-picker>
            </div>
            <el-divider direction="vertical"></el-divider>

            <div class="send-fun">
              <span @click="currentItem.sendType = 0" :class="[currentItem.sendType === 0? 'selected':'']">客户朋友圈</span>
              <el-divider direction="vertical"></el-divider>
              <span @click="currentItem.sendType = 1" :class="[currentItem.sendType === 1? 'selected':'' ]">私聊群发</span>
            </div>
          </div>
          <div class="form-send-text">
            <span class="form-send-label">
              文案内容
            </span>
          </div>
          <div class="form-send-input">
            <el-input
                placeholder="请输入内容"
                v-model="currentItem.content"
                maxlength="230"
                show-word-limit
            ></el-input>
          </div>

          <div class="form-send-image">
            <span class="form-send-label">
              图片({{ currentItem.imageList ? currentItem.imageList.length : 0 }}/9)
            </span>
          </div>
          <div class="form-send-images-upload">


            <div v-for="(item,index) in currentItem.imageList"
                 :key="item" class="form-send-image-item">


              <sm-image :width="146" :image-ids="currentItem.imageList" :height="146" :image-id="item"/>
              <div class="image-but-list">
                <el-tooltip content="AI生成" placement="top" effect="light">

                <img @click="dev" src="~@/assets/images/ai_create.png" width="24" height="24" alt="AI生成">
                </el-tooltip>
                <el-upload

                    :headers="uploadOption.headers"
                    :action="uploadOption.url"
                    :show-file-list="false"
                    :on-success="handlerUploadSuccess1"
                    :before-upload="handlerBeforeUpload">
                  <el-tooltip content="上传" placement="top" effect="light">
                  <img src="~@/assets/images/upload.png"  @click="uploadImage(index)" width="24" height="24" alt="上传">
                  </el-tooltip>
                </el-upload>
                <el-tooltip content="删除" placement="top" effect="light">
                <img @click="removeImage(index)" src="~@/assets/images/delete.png" width="24" height="24" alt="删除">
                </el-tooltip>
              </div>
            </div>

            <el-upload
                :headers="uploadOption.headers"
                :action="uploadOption.url"
                :show-file-list="false"
                :on-success="handlerUploadSuccess"
                :before-upload="handlerBeforeUpload"
            >
              <div class="form-send-image-item form-send-image-add">
                <img width="20" height="20" src="~@/assets/images/add.png"/>
              </div>
            </el-upload>
          </div>
          <div class="dialog-but">
            <div @click="submit" class="save-but">保存</div>
            <div @click="reset" class="close-but">重新选择内容</div>
          </div>
          <div class="close-icon-but">
            <!--          <img src="~@/assets/images/close.png" width="20" height="20">-->
          </div>
        </el-dialog>
      </el-main>
    </el-container>
  </Transition>
</template>

<script>
import {getCustomer} from "../../../api/customer";
import {getMessageList, updateMessage} from "../../../api/message";
import SmImage from "../../../components/SmImage";
import Vue from "vue";
import AppConfig from "../../../config";
import {MessageBox} from "element-ui";
import {getMaterialByGroup, getMaterialByGroupImage, getMaterialByGroupText} from "../../../api/material";

export default {
  name: "vector-info",
  components: {SmImage},
  data() {
    return {
      uploadOption: {
        headers: {
          token: Vue.cookie.get("token"),
        },
        url: AppConfig.baseURL + "/material/updateLocal",
      },
      loading: true,
      visible: false,
      form: {
        sendTime: '',
      },
      customerInfo: {},
      dataList: [],
      currentItem: {
        imageList: [],
        currentClickImageIndex: -1,
      }
    };
  },
  activated() {
    this.loading = true;
    // 获取路由参数
    console.log(this.$route.query);
    const {externalUserid, clear} = this.$route.query;
    console.log(clear);
    getCustomer(externalUserid).then(res => {
      this.customerInfo = res.data;
    })
    if (clear === 'true') {
      this.dataList = [];
      return;
    }
    getMessageList(externalUserid).then(res => {
      this.dataList = res.data;
    })

  },
  mounted() {

  },
  deactivated() {
    this.loading = false;
    this.visible = false;
  },
  methods: {
    dev(){
      this.$message({
        message: '暂未开放',
        type: 'warning'
      });
    },
    reset(){

      getMaterialByGroup(this.customerInfo.labelType).then(res => {
        const groupId = res.data;
        getMaterialByGroupText(groupId).then(res => {
          this.$set(this.currentItem, 'content', res.data.content);
        })
        getMaterialByGroupImage(groupId).then(res => {
         //获取对象数组中id的字段
          const arr = [];
          for (let i = 0; i < res.data.length; i++) {
            arr.push( res.data[i].id);
          }


          this.$set(this.currentItem, 'imageList', arr);
        })
      })
    },
    uploadImage(index) {
      this.currentItem.currentClickImageIndex = index;
    },
    handlerUploadSuccess1(res) {


      console.log(this.currentItem.imageList);
      // this.currentItem.imageList[this.currentItem.currentClickImageIndex] = res.id;
      this.$set(this.currentItem.imageList, this.currentItem.currentClickImageIndex, res.id);
    },
    submit() {
      this.visible = false;
      this.currentItem.labelType = this.customerInfo.labelType;
      this.currentItem.messageId = this.currentItem.id;
      updateMessage(this.currentItem).then(res => {
        if (res.status === 200) {
          this.$message({
            message: '保存成功',
            type: 'success'
          });
          const {externalUserid} = this.$route.query;
          getMessageList(externalUserid).then(res => {
            this.dataList = res.data;
          })

        }
      })

    },
    dateFormatter: function (createDate) {
      var date = new Date(createDate); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
      let Y = date.getFullYear() + '-';
      let M = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) + '-' : date.getMonth() + 1 + '-';
      let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
      let h = date.getHours() < 10 ? '0' + date.getHours() + ':' : date.getHours() + ':';
      let m = date.getMinutes() < 10 ? '0' + date.getMinutes() + ':' : date.getMinutes() + ':';
      let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
      return Y + M + D;
    },
    openSendWindow(item) {
      //已发送的消息不能打开了
      if (item.status === 1) {
        this.$message({
          message: '已发送的消息不能修改',
          type: 'warning'
        });
      }
      this.currentItem = item;
      this.visible = true;
    },
    removeImage(index){
      this.currentItem.imageList.splice(index,1);
    },
    handlerUploadSuccess(res) {

      this.currentItem.imageList.push(res.id);
    },
    handlerBeforeUpload(file) {
      console.log(file);
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return (isJPG || isPNG) && isLt2M;
    }
  },
  computed: {
    tagList() {
      //排除为空的标签
      if (!this.customerInfo.tagName || this.customerInfo.tagName === '') {
        return [];
      }
      console.log(this.customerInfo.tagName);
      const arr = this.customerInfo.tagName.split('/')
      return arr.filter(item => item !== '');
    }
  }
}
</script>

<style scoped>
.el-container .is-vertical {
  padding: 0;
}

.el-header {
  position: relative;
  height: 142.986px !important;
  margin-bottom: 15px;
  background: #FFFFFF;
  border-radius: 10px;

}


.el-header .logo {
  margin-right: 28px;

}

.head-title {
  position: absolute;
  left: 53px;
  top: 30px;
  width: 235px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.head-title h2 {
  font-size: 32px;
  font-weight: 600;
}

.el-main {
  background: #FFFFFF;
  border-radius: 10px;
  padding: 30px 50px 0 50px;
  display: flex;
  flex-flow: column;
}

.el-form-item {
  margin-bottom: 0px !important;
}

.el-pagination {
  margin-top: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: flex-start;

}

.user-info .user-info-text {
  display: flex;
  flex-flow: column;
  align-items: flex-start;
  justify-content: flex-start;
  margin-left: 11px;
}

.user-info .user-info-text .user-name {
  display: flex;
  align-items: baseline;
  justify-content: flex-start;

}

.user-info .user-info-text .user-name span:nth-child(1) {
  margin-right: 10px;
  font-size: 20px;
  font-weight: 600;
  line-height: 20px;
}

.user-info .user-info-text .user-name span:nth-child(2) {
  font-size: 10px;
  font-weight: 500;
  line-height: 20px;
}

.user-info .user-info-text .user-option {
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}

.user-info .user-info-text .user-content {
  font-size: 12px;
  font-weight: 500;
  color: #888888;
}

/*用户标签*/
.user-label {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.user-label .label-title {
  font-size: 14px;
  font-weight: 600;

}

.user-label .label-list {
  margin-left: 22px;
}

.label-list .label-item {
  margin-right: 17px;
  height: 20px;
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  padding: 2px 9px;
  background: #306AFF;
  color: #FFFFFF;
}

.not-label {
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}

.send {
  display: flex;
  flex-flow: column;
  flex: 1;
}

/*发送内容*/
.send-title {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.send-title .send-title-label {
  font-size: 14px;
  font-weight: 600;
}

.send-title .send-title-content {
  margin-left: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}

.send-title .send-title-content a {
  color: #306AFF;
  /*  下划线*/
  text-decoration: underline;
}

.send-list {
  position: relative;
  overflow-y: auto;
  padding-left: 78px;
  margin-top: 10px;
  height: 440px;
  width: 950px;
}

.send-list .not-data {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}


.send-list .send-item {
  display: flex;
  position: relative;
  align-items: center;
  justify-content: flex-start;
  height: 61px;
  width: 790px;
  padding: 0 44px;
  border-bottom: 1px solid #E0E0E0;
}

.send-list .send-item .send-ico {
  margin-right: 23px;
}

.send-list .send-item .send-time {
  display: flex;
  flex-flow: column;
  align-items: center;
  justify-content: center;
  width: 130px;
  margin-right: 22px;

}

.send-list .send-item .send-time p {
  margin: 0;
}

.send-list .send-item .send-time p:nth-child(1) {
  font-size: 16px;
  font-weight: 600;
}

.send-list .send-item .send-time p:nth-child(2) {
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}

.send-list .send-content {
  width: 600px;
  height: 37px;
  margin-left: 22px;
  font-size: 12px;
  font-weight: 500;
  /*  只显示两行文本 超出省略号隐藏*/
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;


}

.send-list .right-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.selected {
  color: #888888 !important;
}

.send-list .send-item:hover {
  /*  放大*/
  transform: scale(1.1);
  transition: all 0.5s;
}

.send-list .send-item:active {
  /*背景变灰色*/
  background: #F5F5F5;
}

.left-to-right-enter-active, .left-to-right-leave-active {
  transition: all 1s;
}

.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */
{
  transform: translateX(-100%);
  opacity: 0;
}

/*保存弹窗*/
/deep/ .el-dialog__body {
  padding: 36px 0 0px 0 !important;
  display: flex !important;
  flex-flow: column !important;
}

/deep/ .el-dialog__header {
  padding: 0 !important;
}


.send-save-dialog .option-info {
  display: flex;
  align-items: center;

  padding: 0 30px;
}

.send-save-dialog .option-info .send-time {
  margin: 0 20px;
}

.send-save-dialog .option-info .send-time span:nth-child(1) {
  font-size: 14px;
  font-weight: 500;
  color: #161624;
}

.send-save-dialog .option-info .send-time span:nth-child(2) {
  padding: 1px 2px 1px 2px;
  margin-left: 5px;
  width: 37px;
  height: 14px;
  background: #EEF1F8;
  border-radius: 2px 2px 2px 2px;
  font-size: 10px;
  font-weight: 500;
  color: #306AFF;
}

.option-info .send-time /deep/ .el-date-editor {
  width: 55px !important;

}

.option-info .send-time /deep/ .el-date-editor input {
  border-radius: 2px 2px 2px 2px;
  padding: 0 !important;
  border: 0;
  width: 55px;
  font-size: 10px;
  height: 100% !important;
  margin-left: 10px !important;
  color: #306AFF;
  background: #EEF1F8;
  text-align: center;
}

.option-info .send-time /deep/ .el-input__prefix, el-icon-time {
  display: none !important;
}

/*-------------------------------*/
.send-save-dialog .option-info .send-date /deep/ .el-date-editor {
  width: 85px !important;

}

.send-save-dialog .option-info .send-date /deep/ .el-date-editor input {
  font-size: 14px;
  font-weight: 500;
  color: #161624;
  margin-right: 20px;
  border: none;
}

.send-save-dialog .option-info .send-date /deep/ .el-input__prefix, el-icon-time {
  display: none !important;
}

.send-save-dialog .option-info .send-date /deep/ .el-input__inner {
  padding: 0 !important;
}

.send-save-dialog .option-info .send-fun {
  line-height: 14px;
  padding: 1px 2px 1px 2px;
  margin-left: 20px;
  height: 14px;
  background: #EEF1F8;
  border-radius: 2px 2px 2px 2px;
  font-size: 10px;
  font-weight: 500;
  color: #888888;
}

.send-save-dialog .option-info .send-fun .selected {
  color: #306AFF !important;
}

.send-save-dialog .form-send-label {
  font-size: 14px;
  font-weight: 600;
  color: #161624;
}

.send-save-dialog .form-send-text {
  padding: 0 30px;
  margin-top: 25px;
}

.send-save-dialog .form-send-image {
  padding: 0 30px;
  margin-top: 25px;
}

.send-save-dialog .form-send-input {
  padding: 0 30px;
  margin-top: 12px;
}

.send-save-dialog .form-send-images-upload {
  padding: 0 30px;
  width: 100%;
  margin: 12px 0px;
  display: grid;
  grid-template-columns: repeat(4, 146px);
  /* grid-gap: 20px; */
  grid-gap: 20px 20px;
  grid-template-rows: 146px 146px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: start;
}

.send-save-dialog .form-send-image-add {
  background: #EEF1F8;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-save-dialog .form-send-image-item {
  position: relative;
  width: 146px;
  height: 146px;
}
.image-but-list{
  width: 100px;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  position: absolute;
  bottom: 0;
  right: 0;
}

.send-save-dialog .dialog-but {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #EEF1F8;
  width: 100%;
  height: 80px;
  font-size: 20px;
  font-weight: 600;
  color: #FFFFFF;
}

.send-save-dialog .dialog-but > div {
  width: 126px;
  height: 40px;
  border-radius: 100px;
  line-height: 40px;
  text-align: center;
  padding: 0 13px;
}

.send-save-dialog .dialog-but .close-but {
  background: #B5C6ED;

}

.send-save-dialog .dialog-but .save-but {
  background: #0000ff;
  margin-right: 27px;

}


</style>