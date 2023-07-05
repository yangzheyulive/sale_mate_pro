<template>
  <div id="app">
    <keep-alive>
<!--      <Transition name="left-to-right">-->
        <router-view></router-view>
<!--      </Transition>-->
    </keep-alive>
  </div>
</template>

<script>

export default {
  name: 'app',
}
</script>

<style>
html{height: 100%;}
body{
		background-color: #EEF1F8;
		padding: 0;
		margin:0;
		height: 100%;
	}
#app{
  	width: 100%;
  	height: 100%;
  	position: relative;
}
input{

  /*去除阴影*/
  box-shadow: none;

  /*去除边框*/
  border: none;

  /*去除聚焦input的蓝色边框*/
  outline: none;

  /*禁止拖拽*/
  resize: none;

  /*去除iOS默认样式*/
  -webkit-appearance: none;

  /*去除点击时背景高亮样式*/
  -webkit-tap-highlight-color: rgba(0,0,0,0);

}

/*宽度横移*/
.left-to-right-enter-active, .left-to-right-leave-active {
  transition: all 1s;
}
.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */ {
  transform: translateX(-100%);
  opacity: 0;
}
li{
  list-style: none;
}
ul{
  padding: 0;
}
a{
  /* 鼠标指针为手指 */
  cursor: pointer;
}
</style>
