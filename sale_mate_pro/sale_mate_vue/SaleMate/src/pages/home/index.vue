<template>
  <div id="home">
    <Transition name="top-to-bottom">
        <chat v-show="loading" ></chat>
    </Transition>
    <div>
      <img src="">
    </div>

  </div>
</template>

<script>
import Chat from "../../components/Chat";
import {verify} from "../../api/user";
import {test} from "../../api/test";
import ChatVector from "../../components/ChatCommon";
export default {
  name: 'home',
  components: {ChatVector, Chat},
  data(){
	  return{
      loading: false,
	  }
  },
  methods:{

  },
  created() {

  },
  activated() {
    this.loading = true;
  },
  deactivated() {
    this.loading = false;
  },

  watch: {
    info_list(newVal, oldVal) {
      this.$nextTick(() => {
        this.$refs.lefScroll.scrollTop = this.$refs.lefScroll.scrollHeight;
      });
    },
  }
}
</script>

<style scoped>
#home{
  position: relative;
	margin: 0 auto;
	padding: 15px;
  width: 100%;
	min-width: 100vh;
  height: 100%;
	background-color: #EEF1F8;
	display: flex;
	box-sizing: border-box;
}



</style>
<style>
	.v-tour div.v-step{ background-color:#306AFF; width:132px; font-size: 12px;} 
	div.v-step__buttons button.v-step__button,div.v-step__buttons button.v-step__button:hover{ background-color: #fff; color:#306AFF; }
	.el-popover {
	  background: #EEF1F8;
	}
	.el-popper[x-placement^=top] .popper__arrow::after {
	  border-top-color: #EEF1F8;
	}
	.el-popper[x-placement^=top] .popper__arrow {
	  border-top-color: #EEF1F8;
	} 
	.el-popper[x-placement^=bottom] .popper__arrow::after {
	  border-bottom-color: #EEF1F8;
	}
	.el-popper[x-placement^=bottom] .popper__arrow {
	  border-bottom-color: #EEF1F8;
	}
  .el-row{
    width: 100%;
  }
  .top-to-bottom-enter-active, .top-to-bottom-leave-active {
    transition: all 1s;
  }
  .top-to-bottom-enter, .top-to-bottom-leave-to /* .fade-leave-active below version 2.1.8 */ {
    transform: translateY(-100%);
    opacity: 0;
  }

</style>
