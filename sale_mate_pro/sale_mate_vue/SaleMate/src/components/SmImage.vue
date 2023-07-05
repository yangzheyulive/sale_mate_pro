<template>
  <el-image :src="src" :preview-src-list="imageList" fit="cover" :style="{width:width+'px',height:height+'px'}"/>
</template>

<script>
import AppConfig from '../config'

export default {

  name: 'SmImage',
  props: {
    imageId: {
      type: String,
      required: true
    },
    width: {
      type: Number,
      default: 100
    },
    height: {
      type: Number,
      default: 100
    },
    imageIds: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      src: AppConfig.baseURL + "/material/getImage?imageId=" + this.imageId,
    }
  },
  watch: {
    imageId(newVal, oldVal) {
      console.log(newVal)

      this.src = AppConfig.baseURL + "/material/getImage?imageId=" + this.imageId;
    },
  }, computed: {

    imageList() {
      console.log("imageIds:",this.imageIds)
      const imageList = []
      for (let i = 0; i < this.imageIds.length; i++) {
        const imageId = this.imageIds[i];
        imageList.push(AppConfig.baseURL + "/material/getImage?imageId=" + imageId);
      }
      return imageList;
    }
  },

}
</script>

<style>

</style>