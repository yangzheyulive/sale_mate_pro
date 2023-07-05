<template>
  <el-container>
    <Transition name="left-to-right">
      <el-container v-show="loading">
        <el-header>
          <div class="logo">
            <img src="~@/assets/images/logo.png" width="79" height="45"/>
          </div>
          <h2>素材库</h2>
        </el-header>
        <el-main>
          <div class="option-list">
            <!--          未购买-->
            <div ref="list1" class="material">
              <div class="title">未购买</div>
              <ul  v-if="!form1Option.visible">
                <li class="material-item" v-for="(item,index) in list1">
                  <span class="material-item-title">
                    {{item.content}}
                  </span>
                  <span class="material-item-info">
                    <div>朋友圈 | 覆盖45人</div>
                    <div class="group-icon">
                      <img
                          src="~@/assets/images/group.png"
                          width="16"
                          height="13"
                      />
                    </div>
                  </span>
                </li>
              </ul>
              <div v-if="form1Option.visible" class="material-save">
                <div class="tabs">
                  <div class="tabs-title">发送方式</div>
                  <div class="tabs-list">
                    <span class="selected">客户朋友圈</span>
                    <span>群发私聊</span>
                  </div>
                </div>
                <div class="material-option-title">文案内容</div>
                <div class="material-option-info">
                  <el-input type="textarea" autosize show-word-limit   :maxlength="240" v-if="!form1Option.gtpLoading" v-model="form1.content + '\n\n'"></el-input>
                  <span v-if="form1Option.gtpLoading">AI正在为你生成合适的文案。。</span>
                  <div @click="resetContent(form1,form1Option)" v-if="!form1Option.gtpLoading" class="reset-but">
                    重新生成
                  </div>
                </div>
                <div class="material-image-title">图片({{ form1.imagesIds.length }}/9)</div>
                <div class="material-image-list">
                  <div class="material-image-item" v-for="(item,index) in form1.imagesIds" :key="item">
                    <transition mode="out-in" name="fade">
                      <sm-image
                          :image-ids="form1.imagesIds"
                          :width="89"
                          :height="89"
                          :image-id="item"
                      />
                    </transition>
                  </div>
                  <div v-if="form1Option.uploadAiLoading" class="material-image-item">
                    <div class="option-links">
                      AI生成中···
                    </div>
                  </div>
                  <div v-if="form1Option.uploadLoading" class="material-image-item">
                    <div class="option-links">
                      图片上传中···
                    </div>
                  </div>
                  <div v-if="form1.imagesIds.length < 9" class="material-image-item">
                    <div class="image-option">
                      <span @click="generateImage(form1.imagesIds,form1Option)">
                        <img
                            src="~@/assets/images/ai-create.png"
                            width="9"
                            height="9"
                        />
                        <a>AI生成</a>
                      </span>
                      <div style="height: 10px"></div>

                        <span>

                          <img
                              src="~@/assets/images/local-update.png"
                              width="9"
                              height="9"
                          />
                          <el-upload
                              :headers="uploadOption.headers"
                              :action="uploadOption.url"
                              :show-file-list="false"
                              :on-success="handlerUploadSuccess1(form1,form1Option)"
                              :before-upload="handlerBeforeUpload1"
                          >
                          <a> 本地上传</a>
                          </el-upload>
                        </span>

                    </div>
                  </div>
                </div>
                <div @click="saveForm1" class="material-save-but">保存</div>
              </div>
              <div
                  v-if="!form1Option.visible"
                  @click="openWindow(form1,form1Option)"
                  class="material-add-but"
              >
                <span>新增素材</span>
                <span>AI自动帮您发</span>
              </div>
              <div
                  @click="openWindow(form1,form1Option)"
                  v-if="form1Option.visible"
                  class="material-add-but"
              >
                <span>返回</span>
              </div>
            </div>
            <!--          <div v-if="!repeatVisible" style="margin:0 15px"></div>-->
            <!--          促复购-->
            <div ref="list2" class="repeat">
              <div class="title">促复购</div>
              <ul v-if="!form2Option.visible" >
                <li class="material-item" v-for="(item,index) in list2">
                  <span class="material-item-title">
                    {{item.content}}
                  </span>
                  <span class="material-item-info">
                    <div>朋友圈 | 覆盖45人</div>
                    <div class="group-icon">
                      <img
                          src="~@/assets/images/group.png"
                          width="16"
                          height="13"
                      />
                    </div>
                  </span>
                </li>
              </ul>
              <div v-if="form2Option.visible" class="material-save">
                <div class="tabs">
                  <div class="tabs-title">发送方式</div>
                  <div class="tabs-list">
                    <span class="selected">客户朋友圈</span>
                    <span>群发私聊</span>
                  </div>
                </div>
                <div class="material-option-title">文案内容</div>
                <div class="material-option-info">
                  <el-input type="textarea" autosize show-word-limit   :maxlength="240" v-if="!form2Option.gtpLoading" v-model="form2.content + '\n\n'"></el-input>
                  <span v-if="form2Option.gtpLoading">AI正在为你生成合适的文案。。</span>
                  <div @click="resetContent(form2,form2Option)" v-if="!form2Option.gtpLoading" class="reset-but">
                    重新生成
                  </div>
                </div>
                <div class="material-image-title">图片({{ form2.imagesIds.length }}/9)</div>
                <div class="material-image-list">
                  <div class="material-image-item" v-for="(item,index) in form2.imagesIds" :key="item">
                    <transition mode="out-in" name="fade">
                      <sm-image
                          :image-ids="form2.imagesIds"
                          :width="89"
                          :height="89"
                          :image-id="item"
                      />
                    </transition>
                  </div>
                  <div v-if="form2Option.uploadAiLoading" class="material-image-item">
                    <div class="option-links">
                      AI生成中···
                    </div>
                  </div>
                  <div v-if="form2Option.uploadLoading" class="material-image-item">
                    <div class="option-links">
                      图片上传中···
                    </div>
                  </div>
                  <div v-if="form2.imagesIds.length < 9" class="material-image-item">
                    <div class="image-option">
                      <span @click="generateImage(form2.imagesIds,form2Option)">
                        <img
                            src="~@/assets/images/ai-create.png"
                            width="9"
                            height="9"
                        />
                        <a>AI生成</a>
                      </span>
                      <div style="height: 10px"></div>

                      <span>

                          <img
                              src="~@/assets/images/local-update.png"
                              width="9"
                              height="9"
                          />
                          <el-upload
                              :headers="uploadOption.headers"
                              :action="uploadOption.url"
                              :show-file-list="false"
                              :on-success="handlerUploadSuccess1(form2,form2Option)"
                              :before-upload="handlerBeforeUpload1"
                          >
                          <a> 本地上传</a>
                          </el-upload>
                        </span>

                    </div>
                  </div>
                </div>
                <div @click="saveForm1" class="material-save-but">保存</div>
              </div>
              <div
                  v-if="!form2Option.visible"
                  @click="openWindow(form2,form2Option)"
                  class="material-add-but"
              >
                <span>新增素材</span>
                <span>AI自动帮您发</span>
              </div>
              <div
                  @click="openWindow(form2,form2Option)"
                  v-if="form2Option.visible"
                  class="material-add-but"
              >
                返回
              </div>
            </div>
            <!--          转介绍-->
            <div ref="list3" class="introduce">
              <div class="title">转介绍</div>
              <ul v-if="!form3Option.visible" >
                <li class="material-item" v-for="(item,index) in list3">
                  <span class="material-item-title">
                    {{item.content}}
                  </span>
                  <span class="material-item-info">
                    <div>朋友圈 | 覆盖45人</div>
                    <div class="group-icon">
                      <img
                          src="~@/assets/images/group.png"
                          width="16"
                          height="13"
                      />
                    </div>
                  </span>
                </li>
              </ul>
              <div v-if="form3Option.visible" class="material-save">
                <div class="tabs">
                  <div class="tabs-title">发送方式</div>
                  <div class="tabs-list">
                    <span class="selected">客户朋友圈</span>
                    <span>群发私聊</span>
                  </div>
                </div>
                <div class="material-option-title">文案内容</div>
                <div class="material-option-info">
                  <el-input type="textarea" autosize show-word-limit   :maxlength="240" v-if="!form3Option.gtpLoading" v-model="form3.content + '\n\n'"></el-input>
                  <span v-if="form3Option.gtpLoading">AI正在为你生成合适的文案。。</span>
                  <div @click="resetContent(form3,form3Option)" v-if="!form3Option.gtpLoading" class="reset-but">
                    重新生成
                  </div>
                </div>
                <div class="material-image-title">图片({{ form3.imagesIds.length }}/9)</div>
                <div class="material-image-list">
                  <div class="material-image-item" v-for="(item,index) in form3.imagesIds" :key="item">
                    <transition mode="out-in" name="fade">
                      <sm-image
                          :image-ids="form3.imagesIds"
                          :width="89"
                          :height="89"
                          :image-id="item"
                      />
                    </transition>
                  </div>
                  <div v-if="form3Option.uploadAiLoading" class="material-image-item">
                    <div class="option-links">
                      AI生成中···
                    </div>
                  </div>
                  <div v-if="form3Option.uploadLoading" class="material-image-item">
                    <div class="option-links">
                      图片上传中···
                    </div>
                  </div>
                  <div v-if="form3.imagesIds.length < 9" class="material-image-item">
                    <div class="image-option">
                      <span @click="generateImage(form3.imagesIds,form3Option)">
                        <img
                            src="~@/assets/images/ai-create.png"
                            width="9"
                            height="9"
                        />
                        <a>AI生成</a>
                      </span>
                      <div style="height: 10px"></div>

                      <span>

                          <img
                              src="~@/assets/images/local-update.png"
                              width="9"
                              height="9"
                          />
                          <el-upload
                              :headers="uploadOption.headers"
                              :action="uploadOption.url"
                              :show-file-list="false"
                              :on-success="handlerUploadSuccess1(form3,form3Option)"
                              :before-upload="handlerBeforeUpload1"
                          >
                          <a> 本地上传</a>
                          </el-upload>
                        </span>

                    </div>
                  </div>
                </div>
                <div @click="saveForm1" class="material-save-but">保存</div>
              </div>
              <div
                  v-if="!form3Option.visible"
                  @click="openWindow(form3,form3Option)"
                  class="material-add-but"
              >
                <span>新增素材</span>
                <span>AI自动帮您发</span>
              </div>
              <div
                  @click="openWindow(form3,form3Option)"
                  v-if="form3Option.visible"
                  class="material-add-but"
              >
                返回
              </div>
            </div>
          </div>
        </el-main>
      </el-container>
    </Transition>
    <Transition name="left-to-right">
      <el-aside v-show="loading">
        <chat-common ref="chat"></chat-common>
      </el-aside>
    </Transition>
  </el-container>
</template>

<script>
import ChatCommon from "../../components/ChatCommon";
import AppConfig from "../../config";
import Vue from "vue";
import SmImage from "../../components/SmImage.vue";
import {aiImage, getList, randomImage} from "../../api/material";

export default {
  name: "material",
  components: {ChatCommon, SmImage},
  data() {
    return {
      loading: false,
      loadingAiImage1: true,
      loadingAiImage2: true,
      loadingAiImage3: true,
      uploadOption: {
        headers: {
          token: Vue.cookie.get("token"),
        },
        url: AppConfig.baseURL + "/material/updateLocal",
      },
      gpt_system: {
        "role": "system",
        "content": "九个方子是一个主打养生茶的零售品牌，由于当下年轻人生活都常常被加班、熬夜、工作压力所影响，九个方子推出了熬夜茶、纤扬茶、菊花茶、凉爽茶、红润茶、暖暖茶等产品。请以九个方子为主题，写一段100字以内,注意一定是要100字以内的客户朋友圈文案"
      },
      repeatInfo: "测试文案",
      form1Option: {
        visible: false,
        gtpLoading: true,
        uploadLoading: false,
        uploadAiLoading: false,
      },
      form2Option: {
        visible: false,
        gtpLoading: true,
        uploadLoading: false,
        uploadAiLoading: false,
      },
      form3Option: {
        visible: false,
        gtpLoading: true,
        uploadLoading: false,
        uploadAiLoading: false,
      },
      form1: {
        id:"",
        content: "",
        imagesIds: [],
        displayedContent: "",
        labelType: 0,
      },
      form2: {
        id:"",
        content: "",
        displayedContent: "",
        imagesIds: [],
        labelType: 1,
      },
      form3: {
        id:"",
        content: "",
        displayedContent: "",
        imagesIds: [],
        labelType: 3,
      },
      list1:[],
      list2:[],
      list3:[],
      pageNum1:0,
      pageNum2:0,
      pageNum3:0,
    };
  },
  methods: {
    load(list,labelType){
      const params = {
        pageNum:1,
        labelType:labelType,
        pageSize:10
      };

      getList(params).then(res=>{
        list.push(...res.data.list);
        if (labelType === 0) {
          this.count1 = res.data.totalCount;
        }
        if (labelType === 1) {
          this.count2 = res.data.totalCount;
        }
        if (labelType === 3) {
          this.count3 = res.data.totalCount;
        }
      })
    },
    openWindow(form, formOption) {
      form.imagesIds = [];
      formOption.gtpLoading = true;
      formOption.visible = !formOption.visible;
      if (formOption.visible) {
        const params = [this.gpt_system]
        this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + JSON.stringify(params))
            .then((response) => {
              let gptMsg = response.data.data.text.choices[0].message.content;
              form.displayedContent = gptMsg;
              formOption.gtpLoading = false;
            })
      }
    },
    // 上传成功
    handlerUploadSuccess1(form, formOption) {
      return (res) => {
        formOption.uploadLoading = true;
        if (res.id && res.id !== "") {
          formOption.uploadLoading = false;
          form.uploadLoading = false;
          form.imagesIds.push(res.id);
        }
      }

    },
    handlerBeforeUpload1() {
      this.form1Option.uploadLoading = true;
    },
    saveForm1() {

    },
    typeEffect(content, formName) {
      let index = 0;
      const intervalId = setInterval(() => {
        this[formName].content = content.slice(0, ++index);
        if (index === content.length) {
          clearInterval(intervalId);
          this[formName].isTyping = false;
        }
      }, 100); // Adjust the typing speed here (smaller value means faster typing)
    },
    generateImage(arr,option) {
      if (option.uploadAiLoading) {
        this.$message.warning("正在生成图片，请勿连续操作");
      }
      option.uploadAiLoading = true;
      aiImage().then((res)=>{
        if (res.status === 200) {
          arr.push(res.data.id);
        }
        // this.$message.success("生成图片成功");
      }).catch(()=>{
        this.$message.error("生成图片失败");
      }).finally(()=>{
        option.uploadAiLoading = false;

      })
    },
    resetContent(form,formOption){
      formOption.gtpLoading = true;
      const params = [this.gpt_system]
      this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + JSON.stringify(params))
          .then((response) => {
            let gptMsg = response.data.data.text.choices[0].message.content;
            form.displayedContent = gptMsg;
          })
    }
  },
  activated() {
    this.loading = true;
  },
  deactivated() {
    this.loading = false;
  },
  created() {
    this.load(this.list1,0)
    this.load(this.list2,1)
    this.load(this.list3,2)
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.chat.sendSystemMsg("何总，已为您打开“素材库”页面。 素材库的内容将会供AI选择，由AI经过对 客户的分析后，自动选取最合适该客户的 运营素材进行发送！")
      this.$refs.chat.sendButMsg([{
        content:"帮我生成一条“未购买”客户的运营内容",
        func:()=>{
          this.$refs.chat.sendUserMsg("帮我生成一条“未购买”客户的运营内容")
          const params = [this.gpt_system]
          this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + JSON.stringify(params))
              .then((response) => {
                let gptMsg = response.data.data.text.choices[0].message.content;
                this.$refs.chat.sendSystemMsg(gptMsg);
              })
        },
      },
        {
          content:"帮我生成一条“促复购”客户的运营内容",
          func:()=>{
            this.$refs.chat.sendUserMsg("帮我生成一条“促复购”客户的运营内容")
            const params = [this.gpt_system]
            this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + JSON.stringify(params))
                .then((response) => {
                  let gptMsg = response.data.data.text.choices[0].message.content;
                  this.$refs.chat.sendSystemMsg(gptMsg);
                })
          },

        },{
          content: "帮我生成一条“转介绍”客户的运营内容",
          func:()=>{
            this.$refs.chat.sendUserMsg("帮我生成一条“转介绍”客户的运营内容")
            const params = [this.gpt_system]
            this.$axios.get("https://xxw.pinbar.vip/api/list?text=" + JSON.stringify(params))
                .then((response) => {
                  let gptMsg = response.data.data.text.choices[0].message.content;
                  this.$refs.chat.sendSystemMsg(gptMsg);
                })
          },
        }])
    });
  },
  computed: {},
  watch: {
    // Watch for changes in form1.content and trigger the typing effect
    'form1.displayedContent': function (newVal) {
      if (!this.form1.isTyping && newVal) {
        this.form1.isTyping = true;
        this.typeEffect(newVal, 'form1');
      }
    },
    // Watch for changes in form2.content and trigger the typing effect
    'form2.displayedContent': function (newVal) {
      if (!this.form2.isTyping && newVal) {
        this.form2.isTyping = true;
        this.typeEffect(newVal, 'form2');
      }
    },
    // Watch for changes in form3.content and trigger the typing effect
    'form3.displayedContent': function (newVal) {
      if (!this.form3.isTyping && newVal) {
        this.form3.isTyping = true;
        this.typeEffect(newVal, 'form3');
      }
    },
    "$refs.list1.scrollTop"(){
      //触底刷新
      if(this.$refs.list1.scrollTop + this.$refs.list1.clientHeight >= this.$refs.list1.scrollHeight){
        this.load(this.list1,0)
      }

    }
  },


};
</script>

<style scoped>
.el-container .is-vertical {
  padding: 0;
}

.el-container {
  padding: 15px;
  height: 100%;
  min-width: 1300px;
}

.el-header {
  display: flex;
  align-items: center;
  padding-left: 53px;
  height: 143px !important;
  margin-bottom: 15px;
  background: #ffffff;
  border-radius: 10px;
}

.el-header .logo {
  margin-right: 28px;
}

.el-main {
  background: #ffffff;
  border-radius: 10px;
  padding: 30px;
}

.el-aside {
  /*width: 360px !important;*/
  min-width: 360px;
  width: 450px !important;
  background-color: #ffffff;
  border-radius: 10px;
  margin-left: 15px;
}

.option-list {
  display: flex;
  height: 100%;
  width: 100%;
}

.option-list > div {
  flex: 1;
  height: 100%;
  background: #eef1f8;
  border-radius: 5px 5px 5px 5px;
  padding: 0 20px;
}

.option-list > div > .title {
  padding-top: 40px;
  margin-left: 10px;
  font-size: 20px;
  /*font-family: PingFang SC-Semibold, PingFang SC;*/
  font-weight: 600;
}

.option-list.but {
}

.material {
  overflow-x: auto;
}

.option-list .material-item {
  margin-top: 15px;
  padding: 14px 20px 16px 20px;
  width: 100%;
  height: 90px;
  background: #ffffff;
  box-shadow: 0px 4px 10px 0px rgba(181, 198, 237, 0.3);
  box-sizing: border-box;
  border-radius: 11px;

  /*  只显示一行文字，超出则省略号隐藏*/
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.option-list .material-item-title {
  font-size: 16px;
  font-weight: 600;
  color: #306aff;

}

.option-list .material-item-info {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 18px;
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}

.option-list .material-item-info .group-icon {
  margin-left: 5px;
}

.option-list .material-add-but {
  display: flex;
  justify-content: center;
  flex-flow: column;
  text-align: center;
  color: #ffffff;
  margin: 25px 0;
  background: #306aff;
  border-radius: 50px 50px 50px 50px;
  width: 100%;
  height: 50px;
}

.option-list .material .material-add-but > span:nth-child(1) {
  font-size: 20px;
  font-weight: 800;
}

.option-list .material .material-add-but > span:nth-child(2) {
  font-size: 12px;
  font-weight: 400;
}

.option-list .repeat {
  margin: 0 15px;
  /*padding-bottom: 20px;*/
  overflow-y: auto;
  max-width: 677px;
}

.option-list .introduce {
  overflow-y: auto;
}

/*  促复购*/
.option-list .material-save {
  width: 100%;
  /*height: 419px;*/
  background-color: #ffffff;
  margin-top: 25px;
  box-sizing: border-box;
  box-shadow: 0px 4px 10px 0px rgba(181, 198, 237, 0.3);
  border-radius: 11px 11px 11px 11px;
  padding: 0 20px 10px;
}

.material-save .tabs {
  padding-top: 14px;
  display: flex;
  justify-content: space-between;
}

.material-save .tabs .tabs-title {
  font-size: 15px;
  font-weight: 600;
  color: #000000;
}

.material-save .tabs .tabs-list {
  font-size: 10px;
  font-weight: 500;
  color: #888888;
}

.material-save .tabs .tabs-list > span:nth-child(1) {
  border-right: 1px solid #888888;
}

.material-save .tabs .tabs-list > span {
  padding: 0 5px;
  cursor: pointer;
}

.selected {
  color: #306aff;
}

.material-option-title {
  font-size: 15px;
  font-weight: 600;
  margin-top: 23px;
}

.material-option-info {
  position: relative;
  margin-top: 5px;
  font-size: 14px;
  font-weight: 500;
  color: #888888;
}
.material-option-info .reset-but {
  right: 80px;
  bottom: 6px;
  position: absolute;
  border-radius: 30px;
  width: 94px;
  height: 30px;
  line-height: 30px;
  background: #306AFF;
  color: #FFFFFF;
  text-align: center;
}

.material-option-info textarea {
  width: 100%;
  border: none;
  outline: none;

  height: 70px;
  resize: vertical;
}

.material-image-title {
  margin-top: 18px;
  font-size: 14px;
  font-weight: 600;
  color: #161624;
}

.material-image-list {
  margin-top: 15px;
  padding-bottom: 20px;
  display: grid;
  /*  声明列的宽度  */
  grid-template-columns: repeat(3, 89px);
  /*  声明行间距和列间距  */
  grid-gap: 20px;
  /*  声明行的高度  */
  grid-template-rows: 89px 89px;
  /*  空间不够字段则换行*/
  flex-wrap: wrap;
  align-items: center;
  justify-content: start;
}

.material-save-but {
  margin: 15px auto;
  width: 92px;
  height: 30px;
  background: #306aff;
  border-radius: 50px;
  color: #ffffff;
  line-height: 30px;
  text-align: center;
}

.auto-ai-button {
  display: flex;
  flex-flow: column;
  text-align: center;
  align-items: center;
  justify-content: center;
  margin: 15px auto;
  width: 100%;
  height: 50px;
  background: #3d62fb;
  border-radius: 50px 50px 50px 50px;
}

.auto-ai-button > span:nth-child(1) {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
}

.auto-ai-button > span:nth-child(2) {
  font-size: 12px;
  font-weight: 400;
  color: #ffffff;
}

.introduce .auto-ai-button {
  margin: 15px auto;
  width: 100%;
  height: 50px;
  background: #3d62fb;
  border-radius: 50px 50px 50px 50px;
}

.left-to-right-enter-active,
.left-to-right-leave-active {
  transition: all 1s;
}

.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */
{
  transform: translateX(-100%);
  opacity: 0;
}

.material-image-item {
  display: flex;
  flex-flow: column;
  width: 89px;
  height: 89px;
  justify-content: center;
  align-items: center;
  font-size: 7px;
  font-weight: bold;

  background: #eef1f8;
  color: #306aff;
}
.image-option{
  width: 100%;
  height: 100%;
  background-color: #FFFFFF;
  display: flex;
  flex-flow: column;
}
.image-option span{
  display: flex;
  flex-flow: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  background-color: #eceff7;
}
</style>